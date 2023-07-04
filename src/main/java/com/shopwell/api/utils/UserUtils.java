package com.shopwell.api.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {
    public static <T> T getAuthenticatedCustomer(Class<T> type){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && type.isAssignableFrom(authentication.getPrincipal().getClass())) {
            return type.cast(authentication.getPrincipal());
        }
        return null;
    }
}
