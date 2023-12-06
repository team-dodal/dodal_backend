package com.dodal.meet.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoleChecker {
    Class<?> clazz() default PermissionChecker.class;

    String[] condition();
}
