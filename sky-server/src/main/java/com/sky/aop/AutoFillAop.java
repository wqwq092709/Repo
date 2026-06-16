package com.sky.aop;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import com.sky.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.First;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

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
    public void autoFill(JoinPoint joinPoint) throws Exception{

        MethodSignature signature =(MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        Object[] args = joinPoint.getArgs();
        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();
        Long user = BaseContext.getCurrentId();
        OperationType operationType = annotation.value();
        if(operationType == OperationType.UPDATE){

            Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime",LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser",Long.class);

            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,user);

        }else if (operationType == OperationType.INSERT){
            Method setCreateTime = entity.getClass().getMethod("setCreateTime",LocalDateTime.class);
            Method setCreateUser = entity.getClass().getMethod("setCreateUser",Long.class);
            Method setUpdateTime = entity.getClass().getMethod("setUpdateTime",LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getMethod("setUpdateUser",Long.class);

            setCreateTime.invoke(entity,now);
            setCreateUser.invoke(entity,user);
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,user);
        }else if(operationType == OperationType.CHANGE_STATUS){
            log.info("状态修改日志...");
            Method setUpdateTime = entity.getClass().getMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getMethod("setUpdateUser", Long.class);

            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,user);
        }

    }
}
