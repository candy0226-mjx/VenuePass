package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxbg.ticket.entity.ReviewImage;
import com.jxbg.ticket.mapper.ReviewImageMapper;
import com.jxbg.ticket.service.ReviewImageService;
import org.springframework.stereotype.Service;

@Service
public class ReviewImageServiceImpl extends ServiceImpl<ReviewImageMapper, ReviewImage> implements ReviewImageService {
}
