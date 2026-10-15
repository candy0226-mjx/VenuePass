package com.jxbg.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jxbg.ticket.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @TableId
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String role;

    private Integer status;
}
