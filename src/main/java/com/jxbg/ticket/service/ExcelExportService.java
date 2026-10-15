package com.jxbg.ticket.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ExcelExportService {

    void exportMyOrders(Long userId, String status, HttpServletResponse response);
}
