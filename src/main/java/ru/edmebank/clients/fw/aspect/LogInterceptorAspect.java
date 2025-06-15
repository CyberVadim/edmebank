package ru.edmebank.clients.fw.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static java.util.Arrays.stream;
import static ru.edmebank.clients.fw.log.sensitive.SensitiveDataMasker.mask;

@Aspect
public class LogInterceptorAspect {
    
    @Pointcut("call(* org.slf4j.Logger.info(..)) || " +
            "call(* org.slf4j.Logger.error(..)) || " +
            "call(* org.slf4j.Logger.warn(..))")
    public void loggerMethods() {}
    
    @Around("loggerMethods()")
    public Object maskSensitiveLogData(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length <= 1) return joinPoint.proceed();
        Object[] maskedArgs = new Object[args.length];
        maskedArgs[0] = args[0];
        for (int i = 1; i < args.length; i++) {
            maskedArgs[i] = maskArgument(args[i]);
        }
        return joinPoint.proceed(maskedArgs);
    }
    
    private Object maskArgument(Object arg) {
        if (arg instanceof Object[] array) {
            return stream(array)
                    .map(this::maskArgument)
                    .toArray();
        }
        return mask(arg);
    }
}
