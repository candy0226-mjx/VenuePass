package com.jxbg.ticket.aspect;

import com.jxbg.ticket.annotation.OperationLogRecord;
import com.jxbg.ticket.common.ApiResult;
import com.jxbg.ticket.common.OperationLogContext;
import com.jxbg.ticket.enums.OperationResult;
import com.jxbg.ticket.security.LoginUser;
import com.jxbg.ticket.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Around("@annotation(operationLogRecord)")
    public Object record(ProceedingJoinPoint joinPoint, OperationLogRecord operationLogRecord) throws Throwable {
        Object result = null;
        try {
            result = joinPoint.proceed();
            saveLog(joinPoint, operationLogRecord, result, OperationResult.SUCCESS.name(), operationLogRecord.content());
            return result;
        } catch (Throwable ex) {
            saveLog(joinPoint, operationLogRecord, result, OperationResult.FAILED.name(), ex.getMessage());
            throw ex;
        } finally {
            OperationLogContext.clear();
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, OperationLogRecord operationLogRecord,
                         Object result, String operationResult, String content) {
        try {
            LoginUser loginUser = getCurrentLoginUser();
            Long operatorId = loginUser == null ? null : loginUser.getUserId();
            String operatorRole = loginUser == null ? "SYSTEM" : loginUser.getRole();
            Long businessId = resolveBusinessId(joinPoint, operationLogRecord.businessId(), result);

            operationLogService.record(operatorId, operatorRole, operationLogRecord.operationType(),
                    operationLogRecord.businessType(), businessId, content, operationResult);
        } catch (Exception ex) {
            log.warn("save operation log failed", ex);
        }
    }

    private LoginUser getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            return null;
        }
        return loginUser;
    }

    private Long resolveBusinessId(ProceedingJoinPoint joinPoint, String expression, Object result) {
        Long contextBusinessId = OperationLogContext.getBusinessId();
        if (contextBusinessId != null) {
            return contextBusinessId;
        }
        if (expression == null || expression.isBlank()) {
            return null;
        }

        Object value = parseExpression(joinPoint, expression, result);
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Long.valueOf(text);
        }
        return null;
    }

    private Object parseExpression(ProceedingJoinPoint joinPoint, String expression, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
            context.setVariable("a" + i, args[i]);
        }
        String[] parameterNames = signature.getParameterNames();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        context.setVariable("result", result);
        if (result instanceof ApiResult<?> apiResult) {
            context.setVariable("data", apiResult.getData());
        }
        return expressionParser.parseExpression(expression).getValue(context);
    }
}
