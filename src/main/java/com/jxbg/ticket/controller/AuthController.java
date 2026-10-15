package com.jxbg.ticket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxbg.ticket.annotation.RateLimit;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.dto.LoginRequest;
import com.jxbg.ticket.entity.User;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.security.JwtUtil;
import com.jxbg.ticket.security.LoginUser;
import com.jxbg.ticket.service.UserService;
import com.jxbg.ticket.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "用户认证", description = "登录和当前用户信息接口")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码登录，成功后返回 JWT token。")
    @RateLimit(limit = 5, windowSeconds = 60, message = "登录尝试过于频繁，请稍后再试")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .eq(User::getStatus, 1));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return ApiResult.success(new LoginVO(user.getId(), user.getUsername(), user.getNickname(), user.getRole(), token));
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户", description = "根据 Authorization 中的 JWT token 返回当前登录用户。")
    public ApiResult<User> me(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResult.success(userService.getById(loginUser.getUserId()));
    }
}
