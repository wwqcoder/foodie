package cn.wwq.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    public final static Logger LOGGER = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * AOP 通知
     *  1。前置通知：在方法调用之前通知
     *  2。后置通知：在方法正常调用之后通知
     *  3。环绕通知：在方法调用之前和之后，都分别可以执行的通知
     *  4。异常通知：如果在方法调用过程中发生异常，则通知
     *  5。最终通知：在方法调用之后执行
     */

    /**
     * 切面表达式
     * execution
     * 1.* 方法的返回值
     * 2。包名代表aop监控的类所在的包
     * 3。..代表该包以及子包下的所有类方法
     * 4。*代表类名 ，*代表所有类
     * 5。*（..） 方法名(任意参数)
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* cn.wwq.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.info("=======开始执行 {}.{}=======",
                joinPoint.getTarget().getClass(),
                joinPoint.getSignature().getName());

        //记录开始时间
        long begin = System.currentTimeMillis();
        //执行目标 service
        Object result = joinPoint.proceed();
        //记录开始时间
        long end = System.currentTimeMillis();

        long takeTime = end - begin;

        if (takeTime > 3000){
            LOGGER.error("========执行结束，耗时 {} 毫秒========",takeTime);
        }else if (takeTime > 2000){
            LOGGER.warn("========执行结束，耗时 {} 毫秒========",takeTime);
        }else{
            LOGGER.info("========执行结束，耗时 {} 毫秒========",takeTime);
        }

        return result;
    }
}
