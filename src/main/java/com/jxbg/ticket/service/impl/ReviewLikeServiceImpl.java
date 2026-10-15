package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.ReviewLike;
import com.jxbg.ticket.mapper.ReviewLikeMapper;
import com.jxbg.ticket.service.ReviewLikeService;
import org.springframework.stereotype.Service;

@Service
public class ReviewLikeServiceImpl extends ServiceImpl<ReviewLikeMapper, ReviewLike> implements ReviewLikeService {
}
