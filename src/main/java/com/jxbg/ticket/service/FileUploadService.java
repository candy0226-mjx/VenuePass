package com.jxbg.ticket.service;

import com.jxbg.ticket.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    FileUploadVO uploadReviewImage(MultipartFile file);
}
