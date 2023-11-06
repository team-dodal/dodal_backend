package com.dodal.meet.aop;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class ControllerLogAop {

    // 메소드 실행 조인 포인트 매칭 - execution(접근제어자? 반환타입 선언타입? 메서드이름(파라미터) 예외?)
    // @Pointcut("execution(* com.dodal.meet.controller..*.*(..))")
    @Pointcut("execution(* com.dodal.meet.controller..*.*(..)) && within(com.dodal.meet.controller.*)")
    private void pointCut() {}

    // 메서드가 실행되기 전일 때
    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.info("##################### Start method name = {} #####################", method.getName());

        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            log.info("no param");
        }
        for (Object arg : args) {
            if (!ObjectUtils.isEmpty(arg)) {
                log.info("param : {}, value : {}", arg.getClass().getSimpleName(), arg);
            }else {
                log.info("param : null, value : null");
            }
        }
    }

    // @AfterReturning 메서드가 예외 없이 성공적으로 끝났을 때
    @AfterReturning(value = "pointCut()", returning = "returnObj")
    public void afterReturnLog(JoinPoint joinPoint, Object returnObj) {
        Method method = getMethod(joinPoint);
        log.info("##################### End method name : {} #####################", method.getName());
        if (!ObjectUtils.isEmpty(returnObj)) {
            log.info("return : {}, value : {}", returnObj.getClass().getSimpleName(), returnObj);
        }
    }

    // @AfterThrowing 메서드 실행 중 예외가 발생했을 때
    @AfterThrowing(value = "pointCut()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        Method method = getMethod(joinPoint);
        log.error("##################### Error method name : {} #####################", method.getName());

        log.error("Error : {} ", ex.getMessage());
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info(signature.getClass().toString());
        return signature.getMethod();
    }
}
