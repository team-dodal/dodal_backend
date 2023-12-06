package com.dodal.meet.aop;

import com.dodal.meet.exception.DodalApplicationException;
import com.dodal.meet.exception.ErrorCode;
import com.dodal.meet.auth.PermissionChecker;
import com.dodal.meet.auth.RoleChecker;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final List<PermissionChecker> checkers;

    @Before("@annotation(roleChecker)")
    public void checkHostRole(JoinPoint joinPoint, RoleChecker roleChecker) {
        PermissionChecker pc = checkers.stream()
                .filter(c -> c.getClass().isAssignableFrom(roleChecker.clazz()))
                .findFirst()
                .orElseThrow(() -> new DodalApplicationException(ErrorCode.PERMISSION_CHECK_ERROR));
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        IntStream.range(0, parameterNames.length)
                .filter(i -> Arrays.asList(roleChecker.condition()).contains(parameterNames[i]))
                .findFirst()
                .ifPresent(i -> {
                    if (!pc.hasPermission(parameterValues[i])) {
                        throw new DodalApplicationException(ErrorCode.UNAUTHORIZED_ROOM_HOST);
                    }
                });
    }
}
