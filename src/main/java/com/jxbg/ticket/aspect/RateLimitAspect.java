package com.jxbg.ticket.aspect;

import com.jxbg.ticket.annotation.RateLimit;
import com.jxbg.ticket.exception.BusinessException;
import com.jxbg.ticket.exception.ErrorCode;
import com.jxbg.ticket.security.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class RateLimitAspect {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate:limit:";

    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(rateLimit)")
    public Object limit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = buildRateLimitKey(joinPoint);
        if (isLimited(key, rateLimit)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, rateLimit.message());
        }
        return joinPoint.proceed();
    }

    private boolean isLimited(String key, RateLimit rateLimit) {
        try {
            Long count = stringRedisTemplate.opsForValue().increment(key);
            if (count != null && count == 1L) {
                stringRedisTemplate.expire(key, Duration.ofSeconds(rateLimit.windowSeconds()));
            }
            return count != null && count > rateLimit.limit();
        } catch (Exception ex) {
            log.warn("rate limit check failed, key={}", key, ex);
            return false;
        }
    }

    private String buildRateLimitKey(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return RATE_LIMIT_KEY_PREFIX + joinPoint.getSignature().toShortString();
        }

        HttpServletRequest request = attributes.getRequest();
        String operator = resolveOperator(request);
        return RATE_LIMIT_KEY_PREFIX + operator + ":" + request.getMethod() + ":" + request.getRequestURI();
    }

    private String resolveOperator(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            return "user:" + loginUser.getUserId();
        }
        return "ip:" + getClientIp(request);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
