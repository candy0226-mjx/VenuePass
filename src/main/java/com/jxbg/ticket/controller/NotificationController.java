package com.jxbg.ticket.controller;

import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.entity.UserNotification;
import com.jxbg.ticket.security.LoginUser;
import com.jxbg.ticket.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "站内通知", description = "用户站内消息、未读数和已读状态管理")
public class NotificationController {

    private final UserNotificationService userNotificationService;

    @GetMapping
    @Operation(summary = "我的通知列表", description = "查询当前登录用户收到的站内通知。readFlag 可选：0未读，1已读。")
    public ApiResult<PageResult<UserNotification>> list(@AuthenticationPrincipal LoginUser loginUser,
                                                        @RequestParam(required = false) Integer readFlag,
                                                        @RequestParam(required = false) Long pageNo,
                                                        @RequestParam(required = false) Long pageSize) {
        return ApiResult.success(userNotificationService.listMyNotifications(loginUser.getUserId(), readFlag, pageNo, pageSize));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "未读通知数", description = "查询当前登录用户未读通知数量。")
    public ApiResult<Long> unreadCount(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResult.success(userNotificationService.countUnread(loginUser.getUserId()));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记单条已读", description = "将当前用户的一条通知标记为已读。")
    public ApiResult<Void> markRead(@AuthenticationPrincipal LoginUser loginUser,
                                    @PathVariable @Positive(message = "通知ID必须大于0") Long id) {
        userNotificationService.markRead(loginUser.getUserId(), id);
        return ApiResult.success();
    }

    @PutMapping("/read-all")
    @Operation(summary = "全部标记已读", description = "将当前用户的所有未读通知标记为已读。")
    public ApiResult<Void> markAllRead(@AuthenticationPrincipal LoginUser loginUser) {
        userNotificationService.markAllRead(loginUser.getUserId());
        return ApiResult.success();
    }
}
