package com.hao.j2cache.aop;

import com.hao.j2cache.annotation.Cache;
import com.hao.j2cache.annotation.CacheEvictor;
import com.hao.j2cache.aop.processor.AbstractCacheAnnotationProcessor;
import com.hao.j2cache.aop.processor.CacheEvictorAnnotationProcessor;
import com.hao.j2cache.aop.processor.CachesAnnotationProcessor;
import com.hao.j2cache.utiles.SpringApplicationContextUtils;
import org.aopalliance.intercept.Interceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 缓存操作拦截器
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)//指定使用cglib方式为controller创建代理对象，代理对象其实是目标对象的子类
@Import(SpringApplicationContextUtils.class)
public class CacheMethodInterceptor implements Interceptor {

    /**
     * 拦截单个Cache注解的方法以便实现缓存
     *
     * @param proceedingJoinPoint 切点
     * @return 结果
     * @throws Throwable 异常
     */
    @Around("@annotation(com.hao.j2cache.annotation.Cache)")//环绕通知
    public Object invokeCacheAllMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("缓存拦截器cache");
        //获得方法前面对象
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        //获得当前拦截到的controller方法对象
        Method method = signature.getMethod();
        //获得方法上cache注解信息
        Cache cache = AnnotationUtils.findAnnotation(method, Cache.class);
        //需要进行设置缓存数据处理
        if(cache != null){
            CachesAnnotationProcessor cacheProcessor = AbstractCacheAnnotationProcessor.getCacheProcessor(proceedingJoinPoint, cache);
            return cacheProcessor.process(proceedingJoinPoint);
        }
        //没有获取到cache注解信息直接放行
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }

    /**
     * 拦截单个CacheEvictor注解的方法以便实现清理缓存
     *
     * @param proceedingJoinPoint 切点
     * @return 结果
     * @throws Throwable 异常
     */
    @Around("@annotation(com.hao.j2cache.annotation.CacheEvictor)")//环绕通知
    public Object invokeCacheEvictorAllMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("缓存拦截器CacheEvictor");
        //获得方法前面对象
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        //获得当前拦截到的controller方法对象
        Method method = signature.getMethod();
        //获得方法上cache注解信息
        CacheEvictor cacheEvictor = AnnotationUtils.findAnnotation(method, CacheEvictor.class);

        //需要进行设置缓存数据处理
        if(cacheEvictor != null){
            CacheEvictorAnnotationProcessor cacheEvictorProcessor = AbstractCacheAnnotationProcessor.getCacheEvictorProcessor(proceedingJoinPoint, cacheEvictor);
            return cacheEvictorProcessor.process(proceedingJoinPoint);
        }
        //没有获取到cache注解信息直接放行
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }
}