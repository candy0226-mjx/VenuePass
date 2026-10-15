package com.jxbg.ticket.common;

public final class OperationLogContext {

    private static final ThreadLocal<Long> BUSINESS_ID = new ThreadLocal<>();

    private OperationLogContext() {
    }

    public static void setBusinessId(Long businessId) {
        BUSINESS_ID.set(businessId);
    }

    public static Long getBusinessId() {
        return BUSINESS_ID.get();
    }

    public static void clear() {
        BUSINESS_ID.remove();
    }
}
