package com.dodal.meet.aop;


import com.dodal.meet.controller.UserController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        StringBuilder sb = new StringBuilder("##### Start Method Name ").append(method.getName() +" #####\n");

        Object[] args = joinPoint.getArgs();

        if (args.length == 0) {
            sb.append("No Param\n");
        }

        for (int i = 0; i < args.length; i++) {
            if (!ObjectUtils.isEmpty(args[i]) && !args[i].getClass().getSimpleName().equals("UsernamePasswordAuthenticationToken")) {
                sb.append("[").append(i+1).append("] ").append("Type : ").append(args[i].getClass().getSimpleName()).append(", ").append("Value : ").append(args[i]).append("\n");
            }
            else {
                sb.append("[").append(i+1).append("] ").append("Type : null, ").append("Value : null\n");
            }
        }

        log.info(sb.toString());
    }

    // @AfterReturning 메서드가 예외 없이 성공적으로 끝났을 때
    @AfterReturning(value = "pointCut()", returning = "returnObj")
    public void afterReturnLog(JoinPoint joinPoint, Object returnObj) {
        Method method = getMethod(joinPoint);

        StringBuilder sb = new StringBuilder("##### End Method Name ").append(method.getName() +" #####\n");

        if (!ObjectUtils.isEmpty(returnObj)) {
            sb.append("Type : ").append(returnObj.getClass().getSimpleName()).append(", ").append("Value : ").append(returnObj).append("\n");
        }

        log.info(sb.toString());
    }

    // @AfterThrowing 메서드 실행 중 예외가 발생했을 때
    @AfterThrowing(value = "pointCut()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        Method method = getMethod(joinPoint);

        StringBuilder sb = new StringBuilder("##### Error Method Name ").append(method.getName() +" #####\n");

        sb.append("Error : ").append(ex.getMessage()).append("\n");

        log.error(sb.toString());
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info(signature.getClass().toString());
        return signature.getMethod();
    }
}
