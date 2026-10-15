package com.jxbg.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.entity.UserNotification;

public interface UserNotificationService extends IService<UserNotification> {

    Long createAndPush(Long userId, String title, String content, String type, String businessType, Long businessId);

    PageResult<UserNotification> listMyNotifications(Long userId, Integer readFlag, Long pageNo, Long pageSize);

    long countUnread(Long userId);

    void markRead(Long userId, Long id);

    void markAllRead(Long userId);
}
