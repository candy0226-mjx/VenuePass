package com.jxbg.ticket.service.impl;

import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.service.FileUploadService;
import com.jxbg.ticket.vo.FileUploadVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final long MAX_REVIEW_IMAGE_SIZE = 5 * 1024 * 1024L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${ticket.upload.review-image-path:uploads/reviews}")
    private String reviewImagePath;

    @Value("${ticket.upload.review-image-url-prefix:/review-images}")
    private String reviewImageUrlPrefix;

    @Override
    public FileUploadVO uploadReviewImage(MultipartFile file) {
        validateImage(file);
        String extension = getExtension(file.getOriginalFilename());
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path targetDir = Path.of(reviewImagePath, datePath).toAbsolutePath().normalize();
        Path targetFile = targetDir.resolve(fileName).normalize();
        if (!targetFile.startsWith(targetDir)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件名不合法");
        }

        try {
            Files.createDirectories(targetDir);
            file.transferTo(targetFile);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "点评图片上传失败");
        }

        String urlPrefix = reviewImageUrlPrefix.endsWith("/") ? reviewImageUrlPrefix.substring(0, reviewImageUrlPrefix.length() - 1) : reviewImageUrlPrefix;
        String url = urlPrefix + "/" + datePath + "/" + fileName;
        return new FileUploadVO(fileName, url, file.getSize());
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "上传文件不能为空");
        }
        if (file.getSize() > MAX_REVIEW_IMAGE_SIZE) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "点评图片不能超过5MB");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "仅支持 jpg、jpeg、png、gif、webp 图片");
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank() || !originalFilename.contains(".")) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件名不合法");
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
