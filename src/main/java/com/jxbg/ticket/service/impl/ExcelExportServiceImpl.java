package com.jxbg.ticket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxbg.ticket.entity.ReservationOrder;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.service.ExcelExportService;
import com.jxbg.ticket.service.ReservationOrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportServiceImpl implements ExcelExportService {

    private static final long EXPORT_LIMIT = 5000;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReservationOrderService reservationOrderService;

    @Override
    public void exportMyOrders(Long userId, String status, HttpServletResponse response) {
        List<ReservationOrder> orders = reservationOrderService.page(new Page<>(1, EXPORT_LIMIT),
                new LambdaQueryWrapper<ReservationOrder>()
                        .eq(ReservationOrder::getUserId, userId)
                        .eq(status != null && !status.isBlank(), ReservationOrder::getStatus, status)
                        .orderByDesc(ReservationOrder::getCreateTime)).getRecords();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("我的订单");
            writeRows(workbook, sheet,
                    List.of("订单ID", "订单号", "活动ID", "场次ID", "金额", "状态", "过期时间", "支付时间", "取消时间", "创建时间"),
                    orders.stream()
                            .map(order -> List.of(
                                    value(order.getId()),
                                    value(order.getOrderNo()),
                                    value(order.getActivityId()),
                                    value(order.getSessionId()),
                                    value(order.getTotalAmount()),
                                    value(order.getStatus()),
                                    value(order.getExpireTime()),
                                    value(order.getPaidAt()),
                                    value(order.getCancelledAt()),
                                    value(order.getCreateTime())))
                            .toList());
            writeWorkbook(response, workbook, "我的订单.xlsx");
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "导出订单失败");
        }
    }

    private void writeRows(Workbook workbook, Sheet sheet, List<String> headers, List<List<String>> rows) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 18 * 256);
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            List<String> values = rows.get(rowIndex);
            for (int colIndex = 0; colIndex < values.size(); colIndex++) {
                row.createCell(colIndex).setCellValue(values.get(colIndex));
            }
        }
    }

    private void writeWorkbook(HttpServletResponse response, Workbook workbook, String filename) throws IOException {
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
        workbook.write(response.getOutputStream());
    }

    private String value(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.format(DATE_TIME_FORMATTER);
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.toPlainString();
        }
        return String.valueOf(value);
    }
}
