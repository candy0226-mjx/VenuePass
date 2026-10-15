package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.dto.CreateOrderRequest;
import com.jxbg.ticket.dto.PayOrderRequest;
import com.jxbg.ticket.entity.CheckinCode;
import com.jxbg.ticket.entity.OrderItem;
import com.jxbg.ticket.entity.PaymentRecord;
import com.jxbg.ticket.entity.ReserveSession;
import com.jxbg.ticket.entity.ReservationOrder;
import com.jxbg.ticket.entity.Seat;
import com.jxbg.ticket.entity.User;
import com.jxbg.ticket.enums.CheckinCodeStatus;
import com.jxbg.ticket.enums.OrderStatus;
import com.jxbg.ticket.enums.OperationResult;
import com.jxbg.ticket.enums.PaymentStatus;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.mapper.ReservationOrderMapper;
import com.jxbg.ticket.mq.OrderTimeoutMessageProducer;
import com.jxbg.ticket.service.CheckinCodeService;
import com.jxbg.ticket.service.HotStatsService;
import com.jxbg.ticket.service.OrderItemService;
import com.jxbg.ticket.service.OperationLogService;
import com.jxbg.ticket.service.OrderStateMachine;
import com.jxbg.ticket.service.PaymentRecordService;
import com.jxbg.ticket.service.ReservationOrderService;
import com.jxbg.ticket.service.ReservationStockService;
import com.jxbg.ticket.service.ReserveSessionService;
import com.jxbg.ticket.service.SeatService;
import com.jxbg.ticket.service.UserService;
import com.jxbg.ticket.service.UserNotificationService;
import com.jxbg.ticket.vo.CheckinCodeVO;
import com.jxbg.ticket.vo.OrderDetailVO;
import com.jxbg.ticket.vo.OrderItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationOrderServiceImpl extends ServiceImpl<ReservationOrderMapper, ReservationOrder> implements ReservationOrderService {

    private static final Duration SUBMIT_TOKEN_TIMEOUT = Duration.ofMinutes(10);
    private static final String SUBMIT_TOKEN_KEY_PREFIX = "order:submit:token:";

    @Value("${ticket.order.expire-minutes:15}")
    private int orderExpireMinutes;

    private final UserService userService;
    private final ReserveSessionService reserveSessionService;
    private final SeatService seatService;
    private final OrderItemService orderItemService;
    private final CheckinCodeService checkinCodeService;
    private final OperationLogService operationLogService;
    private final PaymentRecordService paymentRecordService;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final OrderTimeoutMessageProducer orderTimeoutMessageProducer;
    private final UserNotificationService userNotificationService;
    private final ReservationStockService reservationStockService;
    private final HotStatsService hotStatsService;

    @Override
    public String generateSubmitToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(buildSubmitTokenKey(token), "1", SUBMIT_TOKEN_TIMEOUT);
        return token;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(CreateOrderRequest request) {
        checkAndConsumeSubmitToken(request.getSubmitToken());

        User user = userService.getById(request.getUserId());
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在或已禁用");
        }

        ReserveSession session = reserveSessionService.getById(request.getSessionId());
        if (session == null || session.getStatus() == null || session.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "场次不存在或不可预约");
        }
        if (session.getStartTime() != null && !session.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "场次已开始，不能预约");
        }

        List<Long> seatIds = request.getSeatIds().stream()
                .distinct()
                .sorted()
                .toList();

        List<Seat> seats = seatService.listByIds(seatIds);
        if (seats.size() != seatIds.size()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "部分座位不存在");
        }
        boolean hasInvalidSeat = seats.stream()
                .anyMatch(seat -> seat.getStatus() == null || seat.getStatus() != 1 || !session.getVenueId().equals(seat.getVenueId()));
        if (hasInvalidSeat) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "座位不可用或不属于当前场馆");
        }

        reservationStockService.tryReserve(session.getId(), seatIds);
        registerReleaseRedisStockAfterRollback(session.getId(), seatIds);

        List<RLock> lockedSeats = lockSeats(session.getId(), seatIds);
        registerUnlockAfterTransaction(lockedSeats);

        Set<Long> reservedSeatIds = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getSessionId, session.getId())
                        .eq(OrderItem::getActiveFlag, 1))
                .stream()
                .map(OrderItem::getSeatId)
                .collect(java.util.stream.Collectors.toSet());
        boolean hasReservedSeat = seatIds.stream().anyMatch(reservedSeatIds::contains);
        if (hasReservedSeat) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        int availableStock = session.getAvailableStock() == null ? 0 : session.getAvailableStock();
        if (availableStock < seatIds.size()) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED, "当前场次可预约资源不足");
        }

        BigDecimal totalAmount = seats.stream()
                .map(Seat::getBasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ReservationOrder order = new ReservationOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        order.setActivityId(session.getActivityId());
        order.setSessionId(session.getId());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING.name());
        order.setExpireTime(LocalDateTime.now().plusMinutes(orderExpireMinutes));
        save(order);

        List<OrderItem> items = seats.stream()
                .map(seat -> {
                    OrderItem item = new OrderItem();
                    item.setOrderId(order.getId());
                    item.setSessionId(session.getId());
                    item.setSeatId(seat.getId());
                    item.setSeatNo(seat.getSeatNo());
                    item.setPrice(seat.getBasePrice());
                    item.setActiveFlag(1);
                    return item;
                })
                .toList();
        orderItemService.saveBatch(items);

        ReserveSession updateSession = new ReserveSession();
        updateSession.setId(session.getId());
        updateSession.setAvailableStock(availableStock - seatIds.size());
        reserveSessionService.updateById(updateSession);
        hotStatsService.recordOrderCreated(session.getId(), seatIds);
        sendCloseOrderMessageAfterCommit(order.getId());
        return order.getId();
    }

    private void checkAndConsumeSubmitToken(String submitToken) {
        Boolean deleted = stringRedisTemplate.delete(buildSubmitTokenKey(submitToken));
        if (!Boolean.TRUE.equals(deleted)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "请勿重复提交或下单令牌已过期");
        }
    }

    private String buildSubmitTokenKey(String submitToken) {
        return SUBMIT_TOKEN_KEY_PREFIX + submitToken;
    }

    private List<RLock> lockSeats(Long sessionId, List<Long> seatIds) {
        List<RLock> lockedSeats = new ArrayList<>();
        for (Long seatId : seatIds) {
            String key = buildSeatLockKey(sessionId, seatId);
            RLock lock = redissonClient.getLock(key);
            boolean locked;
            try {
                locked = lock.tryLock(0, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                releaseLocks(lockedSeats);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取资源锁被中断");
            }
            if (!locked) {
                releaseLocks(lockedSeats);
                throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED, "资源正在被预约，请稍后再试");
            }
            lockedSeats.add(lock);
        }
        return lockedSeats;
    }

    private void registerUnlockAfterTransaction(List<RLock> lockedSeats) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            releaseLocks(lockedSeats);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单事务未开启");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                releaseLocks(lockedSeats);
            }
        });
    }

    private void registerReleaseRedisStockAfterRollback(Long sessionId, List<Long> seatIds) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            reservationStockService.release(sessionId, seatIds);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单事务未开启");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    reservationStockService.release(sessionId, seatIds);
                }
            }
        });
    }

    private void releaseRedisStockAfterCommit(Long sessionId, List<Long> seatIds) {
        if (seatIds.isEmpty()) {
            return;
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            reservationStockService.release(sessionId, seatIds);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                reservationStockService.release(sessionId, seatIds);
            }
        });
    }

    private void releaseLocks(List<RLock> lockedSeats) {
        for (int i = lockedSeats.size() - 1; i >= 0; i--) {
            RLock lock = lockedSeats.get(i);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String buildSeatLockKey(Long sessionId, Long seatId) {
        return "lock:reserve:session:" + sessionId + ":seat:" + seatId;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long id) {
        ReservationOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);
        List<OrderItemVO> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, id))
                .stream()
                .map(item -> {
                    OrderItemVO itemVO = new OrderItemVO();
                    BeanUtils.copyProperties(item, itemVO);
                    return itemVO;
                })
                .toList();
        vo.setItems(items);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        ReservationOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        OrderStateMachine.checkCanCancel(order.getStatus());

        List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, id)
                .eq(OrderItem::getActiveFlag, 1));

        ReservationOrder updateOrder = new ReservationOrder();
        updateOrder.setId(id);
        updateOrder.setStatus(OrderStatus.CANCELLED.name());
        updateOrder.setCancelledAt(LocalDateTime.now());
        updateById(updateOrder);

        orderItemService.update(new LambdaUpdateWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, id)
                .set(OrderItem::getActiveFlag, null));
        releaseRedisStockAfterCommit(order.getSessionId(), items.stream().map(OrderItem::getSeatId).toList());

        ReserveSession session = reserveSessionService.getById(order.getSessionId());
        if (session != null) {
            int availableStock = session.getAvailableStock() == null ? 0 : session.getAvailableStock();
            ReserveSession updateSession = new ReserveSession();
            updateSession.setId(session.getId());
            updateSession.setAvailableStock(availableStock + items.size());
            reserveSessionService.updateById(updateSession);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closeTimeoutOrders(int batchSize) {
        List<ReservationOrder> timeoutOrders = page(new Page<>(1, batchSize), new LambdaQueryWrapper<ReservationOrder>()
                .eq(ReservationOrder::getStatus, OrderStatus.PENDING.name())
                .lt(ReservationOrder::getExpireTime, LocalDateTime.now())
                .orderByAsc(ReservationOrder::getExpireTime)).getRecords();
        int closedCount = 0;
        for (ReservationOrder order : timeoutOrders) {
            if (closeTimeoutOrder(order)) {
                closedCount++;
            }
        }
        return closedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeTimeoutOrder(Long id) {
        ReservationOrder order = getById(id);
        if (order == null) {
            return false;
        }
        if (!OrderStateMachine.canCloseTimeout(order.getStatus())) {
            return false;
        }
        if (order.getExpireTime() == null || order.getExpireTime().isAfter(LocalDateTime.now())) {
            return false;
        }
        return closeTimeoutOrder(order);
    }

    private boolean closeTimeoutOrder(ReservationOrder order) {
        List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId())
                .eq(OrderItem::getActiveFlag, 1));

        boolean updated = update(new LambdaUpdateWrapper<ReservationOrder>()
                .eq(ReservationOrder::getId, order.getId())
                .eq(ReservationOrder::getStatus, OrderStatus.PENDING.name())
                .set(ReservationOrder::getStatus, OrderStatus.CANCELLED.name())
                .set(ReservationOrder::getCancelledAt, LocalDateTime.now()));
        if (!updated) {
            return false;
        }

        orderItemService.update(new LambdaUpdateWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId())
                .eq(OrderItem::getActiveFlag, 1)
                .set(OrderItem::getActiveFlag, null));
        releaseRedisStockAfterCommit(order.getSessionId(), items.stream().map(OrderItem::getSeatId).toList());

        ReserveSession session = reserveSessionService.getById(order.getSessionId());
        if (session != null) {
            int availableStock = session.getAvailableStock() == null ? 0 : session.getAvailableStock();
            ReserveSession updateSession = new ReserveSession();
            updateSession.setId(session.getId());
            updateSession.setAvailableStock(availableStock + items.size());
            reserveSessionService.updateById(updateSession);
        }
        recordLog(null, "SYSTEM", "TIMEOUT_CLOSE_ORDER", "ORDER", order.getId(),
                "系统关闭超时未支付订单，订单号：" + order.getOrderNo(), OperationResult.SUCCESS.name());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckinCodeVO payOrder(Long id, PayOrderRequest request) {
        ReservationOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        if (OrderStatus.PAID.name().equals(order.getStatus())) {
            return getExistingCheckinCode(id);
        }
        OrderStateMachine.checkCanPay(order.getStatus());
        if (order.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单已超时，不能支付");
        }

        ReservationOrder updateOrder = new ReservationOrder();
        updateOrder.setId(id);
        updateOrder.setStatus(OrderStatus.PAID.name());
        updateOrder.setPaidAt(LocalDateTime.now());
        updateById(updateOrder);

        createPaymentRecord(order, request);

        CheckinCode code = checkinCodeService.getOne(new LambdaQueryWrapper<CheckinCode>()
                .eq(CheckinCode::getOrderId, id));
        if (code == null) {
            ReserveSession session = reserveSessionService.getById(order.getSessionId());
            code = new CheckinCode();
            code.setOrderId(id);
            code.setCode(generateCheckinCode());
            code.setStatus(CheckinCodeStatus.VALID.name());
            code.setExpiredAt(session == null ? order.getExpireTime().plusDays(1) : session.getEndTime());
            checkinCodeService.save(code);
        }

        CheckinCodeVO vo = new CheckinCodeVO();
        BeanUtils.copyProperties(code, vo);
        List<Long> seatIds = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, id))
                .stream()
                .map(OrderItem::getSeatId)
                .toList();
        hotStatsService.recordOrderPaid(order.getSessionId(), seatIds);
        userNotificationService.createAndPush(order.getUserId(),
                "支付成功",
                "订单 " + order.getOrderNo() + " 已支付成功，请凭核销码入场。",
                "ORDER_PAY",
                "ORDER",
                order.getId());
        return vo;
    }

    private void recordLog(Long operatorId, String operatorRole, String operationType,
                           String businessType, Long businessId, String content, String result) {
        operationLogService.record(operatorId, operatorRole, operationType, businessType, businessId, content, result);
    }

    private void sendCloseOrderMessageAfterCommit(Long orderId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            sendCloseOrderMessage(orderId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendCloseOrderMessage(orderId);
            }
        });
    }

    private void sendCloseOrderMessage(Long orderId) {
        try {
            orderTimeoutMessageProducer.sendCloseOrderMessage(orderId);
        } catch (Exception ex) {
            log.error("send order timeout close message failed, orderId={}", orderId, ex);
        }
    }

    private void createPaymentRecord(ReservationOrder order, PayOrderRequest request) {
        PaymentRecord paymentRecord = paymentRecordService.getOne(new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getOrderId, order.getId()));
        if (paymentRecord != null) {
            return;
        }

        String paymentNo = request == null || request.getPaymentNo() == null || request.getPaymentNo().isBlank()
                ? generatePaymentNo()
                : request.getPaymentNo();
        PaymentRecord samePaymentNoRecord = paymentRecordService.getOne(new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getPaymentNo, paymentNo));
        if (samePaymentNoRecord != null && !order.getId().equals(samePaymentNoRecord.getOrderId())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "支付流水号已存在");
        }

        paymentRecord = new PaymentRecord();
        paymentRecord.setOrderId(order.getId());
        paymentRecord.setOrderNo(order.getOrderNo());
        paymentRecord.setPaymentNo(paymentNo);
        paymentRecord.setAmount(order.getTotalAmount());
        paymentRecord.setStatus(PaymentStatus.SUCCESS.name());
        paymentRecord.setPaidAt(LocalDateTime.now());
        paymentRecordService.save(paymentRecord);
    }

    private CheckinCodeVO getExistingCheckinCode(Long orderId) {
        CheckinCode code = checkinCodeService.getOne(new LambdaQueryWrapper<CheckinCode>()
                .eq(CheckinCode::getOrderId, orderId));
        if (code == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "核销码不存在");
        }
        CheckinCodeVO vo = new CheckinCodeVO();
        BeanUtils.copyProperties(code, vo);
        return vo;
    }

    private String generateOrderNo() {
        return "R" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String generateCheckinCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private String generatePaymentNo() {
        return "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
