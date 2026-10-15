package com.jxbg.ticket.controller;

import com.jxbg.ticket.annotation.OperationLogRecord;
import com.jxbg.ticket.annotation.RateLimit;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.dto.CreateReviewRequest;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.security.LoginUser;
import com.jxbg.ticket.service.ReviewService;
import com.jxbg.ticket.vo.ReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "点评管理", description = "用户点评、管理员审核、点赞和取消点赞接口")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "发布点评", description = "用户订单核销完成后，可以发布评分和点评内容。")
    @RateLimit(limit = 5, windowSeconds = 60, message = "发布点评过于频繁，请稍后再试")
    @OperationLogRecord(operationType = "CREATE_REVIEW", businessType = "REVIEW",
            businessId = "#data", content = "用户发布订单点评")
    public ApiResult<Long> create(@AuthenticationPrincipal LoginUser loginUser,
                                  @Valid @RequestBody CreateReviewRequest request) {
        return ApiResult.success(reviewService.createReview(loginUser.getUserId(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询点评详情", description = "只允许查看审核通过的公开点评。")
    public ApiResult<ReviewVO> detail(@PathVariable @Positive(message = "点评ID必须大于0") Long id) {
        return ApiResult.success(reviewService.getReviewDetail(id));
    }

    @GetMapping("/my")
    @Operation(summary = "查询我的点评")
    public ApiResult<PageResult<ReviewVO>> myReviews(@AuthenticationPrincipal LoginUser loginUser,
                                                     @RequestParam(defaultValue = "1") @Positive(message = "页码必须大于0") Long pageNo,
                                                     @RequestParam(defaultValue = "10") @Positive(message = "每页数量必须大于0") Long pageSize) {
        return ApiResult.success(reviewService.listMyReviews(loginUser.getUserId(), pageNo, pageSize));
    }

    @GetMapping("/pending")
    @Operation(summary = "查询待审核点评", description = "管理员查询用户提交后待审核的点评。")
    public ApiResult<PageResult<ReviewVO>> pending(@AuthenticationPrincipal LoginUser loginUser,
                                                   @RequestParam(defaultValue = "1") @Positive(message = "页码必须大于0") Long pageNo,
                                                   @RequestParam(defaultValue = "10") @Positive(message = "每页数量必须大于0") Long pageSize) {
        assertAdmin(loginUser);
        return ApiResult.success(reviewService.listPendingReviews(pageNo, pageSize));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "审核通过点评", description = "管理员审核通过后，点评会在场馆点评列表展示。")
    @OperationLogRecord(operationType = "APPROVE_REVIEW", businessType = "REVIEW",
            businessId = "#p1", content = "管理员审核通过点评")
    public ApiResult<Void> approve(@AuthenticationPrincipal LoginUser loginUser,
                                   @PathVariable @Positive(message = "点评ID必须大于0") Long id) {
        assertAdmin(loginUser);
        reviewService.approveReview(id);
        return ApiResult.success();
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "驳回点评")
    @OperationLogRecord(operationType = "REJECT_REVIEW", businessType = "REVIEW",
            businessId = "#p1", content = "管理员驳回点评")
    public ApiResult<Void> reject(@AuthenticationPrincipal LoginUser loginUser,
                                  @PathVariable @Positive(message = "点评ID必须大于0") Long id) {
        assertAdmin(loginUser);
        reviewService.rejectReview(id);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除我的点评")
    @OperationLogRecord(operationType = "DELETE_REVIEW", businessType = "REVIEW",
            businessId = "#p1", content = "用户删除自己的点评")
    public ApiResult<Void> delete(@AuthenticationPrincipal LoginUser loginUser,
                                  @PathVariable @Positive(message = "点评ID必须大于0") Long id) {
        reviewService.deleteMyReview(loginUser.getUserId(), id);
        return ApiResult.success();
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞点评", description = "使用 review_like 表和 Redis 做点赞去重。")
    @RateLimit(limit = 20, windowSeconds = 60, message = "点赞操作过于频繁，请稍后再试")
    @OperationLogRecord(operationType = "LIKE_REVIEW", businessType = "REVIEW",
            businessId = "#p1", content = "用户点赞点评")
    public ApiResult<Void> like(@AuthenticationPrincipal LoginUser loginUser,
                                @PathVariable @Positive(message = "点评ID必须大于0") Long id) {
        reviewService.likeReview(loginUser.getUserId(), id);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞")
    @RateLimit(limit = 20, windowSeconds = 60, message = "取消点赞操作过于频繁，请稍后再试")
    @OperationLogRecord(operationType = "CANCEL_LIKE_REVIEW", businessType = "REVIEW",
            businessId = "#p1", content = "用户取消点赞点评")
    public ApiResult<Void> cancelLike(@AuthenticationPrincipal LoginUser loginUser,
                                      @PathVariable @Positive(message = "点评ID必须大于0") Long id) {
        reviewService.cancelLike(loginUser.getUserId(), id);
        return ApiResult.success();
    }

    private void assertAdmin(LoginUser loginUser) {
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只有管理员可以审核点评");
        }
    }
}
