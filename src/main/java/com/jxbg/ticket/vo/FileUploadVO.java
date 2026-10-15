package com.jxbg.ticket.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadVO {

    private String fileName;

    private String url;

    private Long size;
}
