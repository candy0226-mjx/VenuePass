package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.dto.CreateReviewRequest;
import com.jxbg.ticket.entity.ReservationOrder;
import com.jxbg.ticket.entity.ReserveSession;
import com.jxbg.ticket.entity.Review;
import com.jxbg.ticket.entity.ReviewImage;
import com.jxbg.ticket.entity.ReviewLike;
import com.jxbg.ticket.entity.User;
import com.jxbg.ticket.enums.OrderStatus;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.mapper.ReviewMapper;
import com.jxbg.ticket.service.ReservationOrderService;
import com.jxbg.ticket.service.ReserveSessionService;
import com.jxbg.ticket.service.HotStatsService;
import com.jxbg.ticket.service.ReviewImageService;
import com.jxbg.ticket.service.ReviewLikeService;
import com.jxbg.ticket.service.ReviewService;
import com.jxbg.ticket.service.UserService;
import com.jxbg.ticket.service.UserNotificationService;
import com.jxbg.ticket.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    private static final String REVIEW_LIKE_KEY_PREFIX = "review:like:";
    private static final int REVIEW_STATUS_HIDDEN = 0;
    private static final int REVIEW_STATUS_APPROVED = 1;
    private static final int REVIEW_STATUS_PENDING = 2;
    private static final int REVIEW_STATUS_REJECTED = 3;

    private final ReservationOrderService reservationOrderService;
    private final ReserveSessionService reserveSessionService;
    private final ReviewImageService reviewImageService;
    private final ReviewLikeService reviewLikeService;
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserNotificationService userNotificationService;
    private final HotStatsService hotStatsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(Long userId, CreateReviewRequest request) {
        ReservationOrder order = reservationOrderService.getById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能点评自己的订单");
        }
        if (!OrderStatus.CHECKED_IN.name().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "订单核销完成后才能点评");
        }

        boolean reviewed = exists(new LambdaQueryWrapper<Review>()
                .eq(Review::getOrderId, order.getId())
                .eq(Review::getActiveFlag, 1));
        if (reviewed) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "该订单已发布过点评");
        }

        List<String> imageUrls = request.getImageUrls() == null ? List.of() : request.getImageUrls();
        Review review = new Review();
        review.setUserId(userId);
        review.setOrderId(order.getId());
        review.setVenueId(resolveVenueId(order));
        review.setActivityId(order.getActivityId());
        review.setSessionId(order.getSessionId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setLikeCount(0);
        review.setImageCount(imageUrls.size());
        review.setStatus(REVIEW_STATUS_PENDING);
        review.setActiveFlag(1);
        save(review);

        if (!imageUrls.isEmpty()) {
            List<ReviewImage> images = new ArrayList<>();
            for (int i = 0; i < imageUrls.size(); i++) {
                ReviewImage image = new ReviewImage();
                image.setReviewId(review.getId());
                image.setImageUrl(imageUrls.get(i));
                image.setSort(i + 1);
                images.add(image);
            }
            reviewImageService.saveBatch(images);
        }
        hotStatsService.recordReviewCreated(review.getVenueId());
        notifyAdminsReviewPending(review);
        return review.getId();
    }

    @Override
    public ReviewVO getReviewDetail(Long id) {
        Review review = getById(id);
        if (review == null || review.getStatus() == null || review.getStatus() != REVIEW_STATUS_APPROVED
                || review.getActiveFlag() == null || review.getActiveFlag() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "点评不存在");
        }
        ReviewVO vo = buildReviewVO(review, loadReviewImages(List.of(id)));
        User user = userService.getById(review.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
        }
        return vo;
    }

    @Override
    public PageResult<ReviewVO> listVenueReviews(Long venueId, Long pageNo, Long pageSize) {
        Page<Review> page = page(PageResult.page(pageNo, pageSize), new LambdaQueryWrapper<Review>()
                .eq(Review::getVenueId, venueId)
                .eq(Review::getStatus, REVIEW_STATUS_APPROVED)
                .eq(Review::getActiveFlag, 1)
                .orderByDesc(Review::getCreateTime));
        List<Review> reviews = page.getRecords();
        if (reviews.isEmpty()) {
            return new PageResult<>(List.of(), page.getTotal(), page.getCurrent(), page.getSize());
        }

        List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
        Map<Long, List<String>> imageMap = loadReviewImages(reviewIds);
        Map<Long, User> userMap = userService.listByIds(reviews.stream()
                        .map(Review::getUserId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<ReviewVO> records = reviews.stream()
                .map(review -> {
                    ReviewVO vo = buildReviewVO(review, imageMap);
                    User user = userMap.get(review.getUserId());
                    if (user != null) {
                        vo.setUsername(user.getUsername());
                        vo.setNickname(user.getNickname());
                    }
                    return vo;
                })
                .toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<ReviewVO> listMyReviews(Long userId, Long pageNo, Long pageSize) {
        Page<Review> page = page(PageResult.page(pageNo, pageSize), new LambdaQueryWrapper<Review>()
                .eq(Review::getUserId, userId)
                .eq(Review::getActiveFlag, 1)
                .orderByDesc(Review::getCreateTime));
        List<Review> reviews = page.getRecords();
        if (reviews.isEmpty()) {
            return new PageResult<>(List.of(), page.getTotal(), page.getCurrent(), page.getSize());
        }

        List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
        Map<Long, List<String>> imageMap = loadReviewImages(reviewIds);
        User user = userService.getById(userId);
        List<ReviewVO> records = reviews.stream()
                .map(review -> {
                    ReviewVO vo = buildReviewVO(review, imageMap);
                    if (user != null) {
                        vo.setUsername(user.getUsername());
                        vo.setNickname(user.getNickname());
                    }
                    return vo;
                })
                .toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public PageResult<ReviewVO> listPendingReviews(Long pageNo, Long pageSize) {
        Page<Review> page = page(PageResult.page(pageNo, pageSize), new LambdaQueryWrapper<Review>()
                .eq(Review::getStatus, REVIEW_STATUS_PENDING)
                .eq(Review::getActiveFlag, 1)
                .orderByAsc(Review::getCreateTime));
        return buildReviewPage(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveReview(Long reviewId) {
        Review review = getActiveReview(reviewId);
        if (!Integer.valueOf(REVIEW_STATUS_PENDING).equals(review.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "只有待审核点评可以通过");
        }
        update(new LambdaUpdateWrapper<Review>()
                .eq(Review::getId, review.getId())
                .eq(Review::getStatus, REVIEW_STATUS_PENDING)
                .eq(Review::getActiveFlag, 1)
                .set(Review::getStatus, REVIEW_STATUS_APPROVED));
        userNotificationService.createAndPush(review.getUserId(),
                "点评审核通过",
                "你提交的点评已审核通过，其他用户现在可以看到。",
                "REVIEW_APPROVE",
                "REVIEW",
                review.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectReview(Long reviewId) {
        Review review = getActiveReview(reviewId);
        if (!Integer.valueOf(REVIEW_STATUS_PENDING).equals(review.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "只有待审核点评可以驳回");
        }
        update(new LambdaUpdateWrapper<Review>()
                .eq(Review::getId, review.getId())
                .eq(Review::getStatus, REVIEW_STATUS_PENDING)
                .eq(Review::getActiveFlag, 1)
                .set(Review::getStatus, REVIEW_STATUS_REJECTED));
        userNotificationService.createAndPush(review.getUserId(),
                "点评审核未通过",
                "你提交的点评未通过审核，可以修改内容后重新提交。",
                "REVIEW_REJECT",
                "REVIEW",
                review.getId());
    }

    private void notifyAdminsReviewPending(Review review) {
        userService.list(new LambdaQueryWrapper<User>()
                        .eq(User::getRole, "ADMIN")
                        .eq(User::getStatus, 1))
                .forEach(admin -> userNotificationService.createAndPush(admin.getId(),
                        "新的点评待审核",
                        "用户提交了新的点评，请及时审核。",
                        "REVIEW_SUBMIT",
                        "REVIEW",
                        review.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMyReview(Long userId, Long reviewId) {
        Review review = getActiveReview(reviewId);
        if (!userId.equals(review.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能删除自己的点评");
        }

        update(new LambdaUpdateWrapper<Review>()
                .eq(Review::getId, review.getId())
                .eq(Review::getActiveFlag, 1)
                .set(Review::getStatus, REVIEW_STATUS_HIDDEN)
                .set(Review::getActiveFlag, null));
        reviewLikeService.update(new LambdaUpdateWrapper<ReviewLike>()
                .eq(ReviewLike::getReviewId, review.getId())
                .eq(ReviewLike::getActiveFlag, 1)
                .set(ReviewLike::getActiveFlag, null));
        stringRedisTemplate.delete(buildReviewLikeKey(review.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeReview(Long userId, Long reviewId) {
        Review review = getNormalReview(reviewId);
        String key = buildReviewLikeKey(review.getId());
        String userIdText = String.valueOf(userId);
        Boolean likedInRedis = stringRedisTemplate.opsForSet().isMember(key, userIdText);
        if (Boolean.TRUE.equals(likedInRedis)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "已点赞，请勿重复操作");
        }

        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setReviewId(review.getId());
        reviewLike.setUserId(userId);
        reviewLike.setActiveFlag(1);
        try {
            reviewLikeService.save(reviewLike);
        } catch (DuplicateKeyException ex) {
            stringRedisTemplate.opsForSet().add(key, userIdText);
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "已点赞，请勿重复操作");
        }

        baseMapper.incrementLikeCount(review.getId());
        stringRedisTemplate.opsForSet().add(key, userIdText);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelLike(Long userId, Long reviewId) {
        Review review = getNormalReview(reviewId);
        ReviewLike reviewLike = reviewLikeService.getOne(new LambdaQueryWrapper<ReviewLike>()
                .eq(ReviewLike::getReviewId, review.getId())
                .eq(ReviewLike::getUserId, userId)
                .eq(ReviewLike::getActiveFlag, 1));
        if (reviewLike == null) {
            stringRedisTemplate.opsForSet().remove(buildReviewLikeKey(review.getId()), String.valueOf(userId));
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR, "还未点赞，不能取消");
        }

        reviewLikeService.update(new LambdaUpdateWrapper<ReviewLike>()
                .eq(ReviewLike::getId, reviewLike.getId())
                .set(ReviewLike::getActiveFlag, null));
        baseMapper.decrementLikeCount(review.getId());
        stringRedisTemplate.opsForSet().remove(buildReviewLikeKey(review.getId()), String.valueOf(userId));
    }

    private Review getNormalReview(Long reviewId) {
        Review review = getById(reviewId);
        if (review == null || review.getStatus() == null || review.getStatus() != REVIEW_STATUS_APPROVED
                || review.getActiveFlag() == null || review.getActiveFlag() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "点评不存在");
        }
        return review;
    }

    private Review getActiveReview(Long reviewId) {
        Review review = getById(reviewId);
        if (review == null || review.getActiveFlag() == null || review.getActiveFlag() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "点评不存在");
        }
        return review;
    }

    private String buildReviewLikeKey(Long reviewId) {
        return REVIEW_LIKE_KEY_PREFIX + reviewId + ":users";
    }

    private ReviewVO buildReviewVO(Review review, Map<Long, List<String>> imageMap) {
        ReviewVO vo = new ReviewVO();
        BeanUtils.copyProperties(review, vo);
        vo.setStatusName(resolveStatusName(review.getStatus()));
        vo.setImageUrls(imageMap.getOrDefault(review.getId(), List.of()));
        return vo;
    }

    private PageResult<ReviewVO> buildReviewPage(Page<Review> page) {
        List<Review> reviews = page.getRecords();
        if (reviews.isEmpty()) {
            return new PageResult<>(List.of(), page.getTotal(), page.getCurrent(), page.getSize());
        }

        List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
        Map<Long, List<String>> imageMap = loadReviewImages(reviewIds);
        Map<Long, User> userMap = userService.listByIds(reviews.stream()
                        .map(Review::getUserId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<ReviewVO> records = reviews.stream()
                .map(review -> {
                    ReviewVO vo = buildReviewVO(review, imageMap);
                    User user = userMap.get(review.getUserId());
                    if (user != null) {
                        vo.setUsername(user.getUsername());
                        vo.setNickname(user.getNickname());
                    }
                    return vo;
                })
                .toList();
        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private String resolveStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case REVIEW_STATUS_APPROVED -> "已通过";
            case REVIEW_STATUS_PENDING -> "待审核";
            case REVIEW_STATUS_REJECTED -> "已驳回";
            case REVIEW_STATUS_HIDDEN -> "已隐藏";
            default -> "未知";
        };
    }

    private Map<Long, List<String>> loadReviewImages(List<Long> reviewIds) {
        return reviewImageService.list(new LambdaQueryWrapper<ReviewImage>()
                        .in(ReviewImage::getReviewId, reviewIds)
                        .orderByAsc(ReviewImage::getSort))
                .stream()
                .collect(Collectors.groupingBy(ReviewImage::getReviewId,
                        Collectors.mapping(ReviewImage::getImageUrl, Collectors.toList())));
    }

    private Long resolveVenueId(ReservationOrder order) {
        ReserveSession session = reserveSessionService.getById(order.getSessionId());
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "场次不存在");
        }
        return session.getVenueId();
    }
}
