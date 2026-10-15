package com.jxbg.ticket.controller;

import com.jxbg.ticket.annotation.RateLimit;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.service.FileUploadService;
import com.jxbg.ticket.vo.FileUploadVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/uploads")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/review-images")
    @RateLimit(limit = 20, windowSeconds = 60, message = "上传图片过于频繁，请稍后再试")
    public ApiResult<FileUploadVO> uploadReviewImage(@RequestPart("file") MultipartFile file) {
        return ApiResult.success(fileUploadService.uploadReviewImage(file));
    }
}
