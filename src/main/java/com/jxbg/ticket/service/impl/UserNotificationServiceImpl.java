package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.entity.UserNotification;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.mapper.UserNotificationMapper;
import com.jxbg.ticket.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationServiceImpl extends ServiceImpl<UserNotificationMapper, UserNotification>
        implements UserNotificationService {

    private static final int UNREAD = 0;
    private static final int READ = 1;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Long createAndPush(Long userId, String title, String content, String type, String businessType, Long businessId) {
        UserNotification notification = new UserNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setBusinessType(businessType);
        notification.setBusinessId(businessId);
        notification.setReadFlag(UNREAD);
        save(notification);
        push(notification);
        return notification.getId();
    }

    @Override
    public PageResult<UserNotification> listMyNotifications(Long userId, Integer readFlag, Long pageNo, Long pageSize) {
        Page<UserNotification> page = page(PageResult.page(pageNo, pageSize), new LambdaQueryWrapper<UserNotification>()
                .eq(UserNotification::getUserId, userId)
                .eq(readFlag != null, UserNotification::getReadFlag, readFlag)
                .orderByDesc(UserNotification::getCreateTime)
                .orderByDesc(UserNotification::getId));
        return PageResult.of(page);
    }

    @Override
    public long countUnread(Long userId) {
        return count(new LambdaQueryWrapper<UserNotification>()
                .eq(UserNotification::getUserId, userId)
                .eq(UserNotification::getReadFlag, UNREAD));
    }

    @Override
    public void markRead(Long userId, Long id) {
        UserNotification notification = getById(id);
        if (notification == null || !userId.equals(notification.getUserId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }
        update(new LambdaUpdateWrapper<UserNotification>()
                .eq(UserNotification::getId, id)
                .eq(UserNotification::getUserId, userId)
                .eq(UserNotification::getReadFlag, UNREAD)
                .set(UserNotification::getReadFlag, READ)
                .set(UserNotification::getReadAt, LocalDateTime.now()));
        pushUnreadCount(userId);
    }

    @Override
    public void markAllRead(Long userId) {
        update(new LambdaUpdateWrapper<UserNotification>()
                .eq(UserNotification::getUserId, userId)
                .eq(UserNotification::getReadFlag, UNREAD)
                .set(UserNotification::getReadFlag, READ)
                .set(UserNotification::getReadAt, LocalDateTime.now()));
        pushUnreadCount(userId);
    }

    private void push(UserNotification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(notification.getUserId()),
                    "/queue/notifications",
                    notification);
            pushUnreadCount(notification.getUserId());
        } catch (Exception ex) {
            log.warn("push user notification failed, userId={}, notificationId={}",
                    notification.getUserId(), notification.getId(), ex);
        }
    }

    private void pushUnreadCount(Long userId) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/notification-count", countUnread(userId));
    }
}
