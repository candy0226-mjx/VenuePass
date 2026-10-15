package com.jxbg.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxbg.ticket.entity.PaymentRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    @Select("""
            SELECT COALESCE(SUM(amount), 0)
            FROM payment_record
            WHERE deleted = 0
              AND status = #{status}
              AND paid_at >= #{startTime}
              AND paid_at < #{endTime}
            """)
    BigDecimal sumAmountByStatusAndPaidAtBetween(@Param("status") String status,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);
}
