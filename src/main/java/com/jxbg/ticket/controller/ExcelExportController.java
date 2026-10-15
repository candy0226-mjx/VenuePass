package com.jxbg.ticket.controller;

import com.jxbg.ticket.security.LoginUser;
import com.jxbg.ticket.service.ExcelExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/export")
public class ExcelExportController {

    private final ExcelExportService excelExportService;

    @GetMapping("/my-orders")
    public void exportMyOrders(@AuthenticationPrincipal LoginUser loginUser,
                               @RequestParam(required = false) String status,
                               HttpServletResponse response) {
        excelExportService.exportMyOrders(loginUser.getUserId(), status, response);
    }
}
