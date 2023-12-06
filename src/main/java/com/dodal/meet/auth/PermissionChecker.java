package com.dodal.meet.auth;

import org.springframework.stereotype.Component;

@Component
public interface PermissionChecker<T> {

    boolean hasPermission(T t);
}
