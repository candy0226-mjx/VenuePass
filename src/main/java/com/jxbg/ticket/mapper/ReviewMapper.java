package com.jxbg.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxbg.ticket.entity.Review;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ReviewMapper extends BaseMapper<Review> {

    @Update("""
            UPDATE review
            SET like_count = like_count + 1
            WHERE id = #{id}
              AND deleted = 0
            """)
    int incrementLikeCount(@Param("id") Long id);

    @Update("""
            UPDATE review
            SET like_count = IF(like_count > 0, like_count - 1, 0)
            WHERE id = #{id}
              AND deleted = 0
            """)
    int decrementLikeCount(@Param("id") Long id);
}
