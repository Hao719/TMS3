package com.hao.j2cache.aop.processor;

import com.hao.j2cache.annotation.Cache;
import com.hao.j2cache.annotation.CacheEvictor;
import com.hao.j2cache.model.AnnotationInfo;
import com.hao.j2cache.utiles.SpringApplicationContextUtils;
import net.oschina.j2cache.CacheChannel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象注解处理器
 */
public abstract class AbstractCacheAnnotationProcessor {
    protected CacheChannel cacheChannel;

    /**
     * 初始化公共属性，供子类使用
     */
    public AbstractCacheAnnotationProcessor() {
        ApplicationContext applicationContext = SpringApplicationContextUtils.getApplicationContext();
        cacheChannel = applicationContext.getBean(CacheChannel.class);
    }

    /**
     * 封装注解信息
     *
     * @param proceedingJoinPoint
     * @param cache
     * @return
     */
    protected List<AnnotationInfo<Cache>> getAnnotationInfo(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        List<String> keys = generateKey(proceedingJoinPoint, cache);
        List<AnnotationInfo<Cache>> annotationInfos = keys.stream().map(key -> {
            AnnotationInfo<Cache> annotationInfo = new AnnotationInfo<>();
            annotationInfo.setAnnotation(cache);
            annotationInfo.setRegion(cache.region());
            annotationInfo.setKey(key);
            return annotationInfo;
        }).collect(Collectors.toList());
        return annotationInfos;
    }

    /**
     * 动态解析注解信息，生成key
     *
     * @param proceedingJoinPoint
     * @param cache
     * @return
     */
    protected List<String> generateKey(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        String key = cache.key();
        List<String> keys = new ArrayList<>();
        if (!StringUtils.hasText(key)) {
            //如果当前key为空串，重新设置当前可以为：目前controller类名:方法名
            //获得类名
            String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
            //获得方法前面对象
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            //获得当前拦截到的controller方法名
            String methodName = signature.getMethod().getName();
            key = className + ":" + methodName;
            keys.add(key);
        }
//        key = CacheKeyBuilder.generate(key, cache.params(), proceedingJoinPoint.getArgs());
        Object[] args = proceedingJoinPoint.getArgs();
        String[] dest = new String[args.length];
        System.arraycopy(args, 0, dest, 0, args.length);
        System.out.println(dest[0]);
//        for (Object key2 : list.get(0)) {
//            keys.add(cache.key() + ":" + key2);
//        }
        return keys;
    }

    /**
     * 抽象方法，处理缓存操作，具体应该由子类具体实现
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    public abstract Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;

    /**
     * 获取Cache注解处理器
     *
     * @param proceedingJoinPoint 切点
     * @param cache               缓存注解
     * @return 注解处理器
     */
    public static CachesAnnotationProcessor getCacheProcessor(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        return new CachesAnnotationProcessor(proceedingJoinPoint, cache);
    }

    /**
     * 获取CacheEvictor注解处理器
     *
     * @param proceedingJoinPoint 切点
     * @param cacheEvictor        消除注解
     * @return 注解处理器
     */
    public static CacheEvictorAnnotationProcessor getCacheEvictorProcessor(ProceedingJoinPoint proceedingJoinPoint, CacheEvictor cacheEvictor) {
        return new CacheEvictorAnnotationProcessor(proceedingJoinPoint, cacheEvictor);
    }
}
