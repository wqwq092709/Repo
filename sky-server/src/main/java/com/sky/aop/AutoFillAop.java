package com.sky.aop;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Aspect
@Component
public class AutoFillAop {

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void pointCut(){};

    /**
     * 自动填充公共字段
     */
    @Before("pointCut()")
    public void autoFill(JoinPoint joinPoint) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        Object arg = joinPoint.getArgs()[0];
        OperationType operationType = annotation.value();

        if (arg instanceof Collection) {
            for (Object item : (Collection<?>) arg) {
                fillEntity(item, operationType);
            }
        } else {
            fillEntity(arg, operationType);
        }
    }

    private void fillEntity(Object entity, OperationType operationType) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Long user = BaseContext.getCurrentId();

        if (operationType == OperationType.INSERT) {
            Method setCreateTime = entity.getClass().getMethod("setCreateTime", LocalDateTime.class);
            Method setCreateUser = entity.getClass().getMethod("setCreateUser", Long.class);
            Method setUpdateTime = entity.getClass().getMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getMethod("setUpdateUser", Long.class);
            setCreateTime.invoke(entity, now);
            setCreateUser.invoke(entity, user);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, user);
        } else {
            Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, user);
        }
    }
}
