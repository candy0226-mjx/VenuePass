package com.jxbg.ticket.controller;

import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.entity.Venue;
import com.jxbg.ticket.security.LoginUser;
import com.jxbg.ticket.service.HotStatsService;
import com.jxbg.ticket.service.ReviewService;
import com.jxbg.ticket.service.VenueService;
import com.jxbg.ticket.vo.SeatHotVO;
import com.jxbg.ticket.vo.VenueBrowseHistoryVO;
import com.jxbg.ticket.vo.VenueHotVO;
import com.jxbg.ticket.vo.ReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/venues")
@Tag(name = "场馆管理", description = "场馆查询、浏览历史、热门场馆和场馆点评接口")
public class VenueController {

    private final VenueService venueService;
    private final ReviewService reviewService;
    private final HotStatsService hotStatsService;

    @GetMapping
    @Operation(summary = "查询场馆列表")
    public ApiResult<List<Venue>> list() {
        return ApiResult.success(venueService.list());
    }

    @PostMapping
    @Operation(summary = "新增场馆", description = "管理员新增场馆。")
    public ApiResult<Long> create(@RequestBody Venue venue) {
        return ApiResult.success(venueService.createVenue(venue));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询场馆详情", description = "查询场馆详情，并记录用户浏览历史和热门分值。")
    public ApiResult<Venue> detail(@AuthenticationPrincipal LoginUser loginUser,
                                   @PathVariable @Positive(message = "场馆ID必须大于0") Long id) {
        return ApiResult.success(venueService.getVenueDetail(loginUser.getUserId(), id));
    }

    @GetMapping("/hot")
    @Operation(summary = "查询热门场馆", description = "基于 Redis ZSet 统计的热门场馆，包含浏览、下单、支付、核销、点评等行为权重。")
    public ApiResult<List<VenueHotVO>> hot(@RequestParam(defaultValue = "10") @Min(value = 1, message = "数量必须大于0") @Max(value = 100, message = "数量不能超过100") Integer limit) {
        return ApiResult.success(hotStatsService.listHotVenues(limit));
    }

    @GetMapping("/hot-seats")
    @Operation(summary = "查询热门资源", description = "基于 Redis ZSet 统计的热门座位、场地或房间资源。")
    public ApiResult<List<SeatHotVO>> hotSeats(@RequestParam(defaultValue = "10") @Min(value = 1, message = "数量必须大于0") @Max(value = 100, message = "数量不能超过100") Integer limit) {
        return ApiResult.success(hotStatsService.listHotSeats(limit));
    }

    @GetMapping("/history")
    @Operation(summary = "查询浏览历史", description = "查询当前用户最近浏览过的场馆。")
    public ApiResult<List<VenueBrowseHistoryVO>> history(@AuthenticationPrincipal LoginUser loginUser,
                                                         @RequestParam(defaultValue = "20") @Min(value = 1, message = "数量必须大于0") @Max(value = 100, message = "数量不能超过100") Integer limit) {
        return ApiResult.success(venueService.listBrowseHistory(loginUser.getUserId(), limit));
    }

    @GetMapping("/{id}/reviews")
    @Operation(summary = "查询场馆公开点评", description = "只返回审核通过且未删除的点评。")
    public ApiResult<PageResult<ReviewVO>> listReviews(@PathVariable @Positive(message = "场馆ID必须大于0") Long id,
                                                       @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于0") Long pageNo,
                                                       @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") Long pageSize) {
        return ApiResult.success(reviewService.listVenueReviews(id, pageNo, pageSize));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改场馆", description = "管理员修改场馆信息，并清理对应缓存。")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody Venue venue) {
        venueService.updateVenue(id, venue);
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "禁用场馆", description = "管理员禁用场馆，并清理对应缓存。")
    public ApiResult<Void> disable(@PathVariable Long id) {
        venueService.disableVenue(id);
        return ApiResult.success();
    }
}
