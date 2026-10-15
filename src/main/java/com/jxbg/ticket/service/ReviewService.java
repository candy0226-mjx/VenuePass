package com.jxbg.ticket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxbg.ticket.common.PageResult;
import com.jxbg.ticket.dto.CreateReviewRequest;
import com.jxbg.ticket.entity.Review;
import com.jxbg.ticket.vo.ReviewVO;

public interface ReviewService extends IService<Review> {

    Long createReview(Long userId, CreateReviewRequest request);

    ReviewVO getReviewDetail(Long id);

    PageResult<ReviewVO> listVenueReviews(Long venueId, Long pageNo, Long pageSize);

    PageResult<ReviewVO> listMyReviews(Long userId, Long pageNo, Long pageSize);

    PageResult<ReviewVO> listPendingReviews(Long pageNo, Long pageSize);

    void approveReview(Long reviewId);

    void rejectReview(Long reviewId);

    void deleteMyReview(Long userId, Long reviewId);

    void likeReview(Long userId, Long reviewId);

    void cancelLike(Long userId, Long reviewId);
}
