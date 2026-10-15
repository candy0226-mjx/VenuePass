package com.jxbg.ticket.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {

    private static final long DEFAULT_PAGE_NO = 1;
    private static final long DEFAULT_PAGE_SIZE = 10;
    private static final long MAX_PAGE_SIZE = 100;

    private List<T> records;

    private Long total;

    private Long pageNo;

    private Long pageSize;

    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    public static <T> Page<T> page(Long pageNo, Long pageSize) {
        long safePageNo = pageNo == null || pageNo < 1 ? DEFAULT_PAGE_NO : pageNo;
        long safePageSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        return new Page<>(safePageNo, safePageSize);
    }
}
