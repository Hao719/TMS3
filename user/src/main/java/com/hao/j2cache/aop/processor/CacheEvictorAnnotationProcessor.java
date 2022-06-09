package com.hao.j2cache.aop.processor;

import com.hao.j2cache.annotation.Cache;
import com.hao.j2cache.annotation.CacheEvictor;
import com.hao.j2cache.model.AnnotationInfo;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * CacheEvictor注解处理器
 */
public class CacheEvictorAnnotationProcessor extends AbstractCacheAnnotationProcessor{
    private List<AnnotationInfo<Cache>> annotationInfos = new ArrayList<>();

    /**
     * 初始化处理器，同时将相关的对象进行初始化
     * @param proceedingJoinPoint
     * @param cacheEvictor
     */
    public CacheEvictorAnnotationProcessor(ProceedingJoinPoint proceedingJoinPoint, CacheEvictor cacheEvictor) {
        super();
        Cache[] caches = cacheEvictor.value();
        for (Cache cache:caches){
            //创建注解封装信息对象并添加到集合
            //annotationInfos.add(getAnnotationInfo(proceedingJoinPoint, cache));
        }
    }

    /**
     * 清楚缓存处理逻辑
     * @param proceedingJoinPoint 切点
     * @return 处理结果
     */
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        //如果有缓存数据则删除并放行
        annotationInfos.forEach(annotationInfo ->{
//            Map map = JSON.parseObject(annotationInfo.getKey(), Map.class);
//            System.out.println(m);
            if(cacheChannel.exists(annotationInfo.getRegion(),annotationInfo.getKey())){
                //清理缓存数据
                cacheChannel.evict(annotationInfo.getRegion(),annotationInfo.getKey());
            }
        });
        //如果没有缓存数据直接放行
        return invoke(proceedingJoinPoint);
    }

    /**
     * 放行,调用目标方法
     * @param proceedingJoinPoint
     * @return
     */
    private Object invoke(ProceedingJoinPoint proceedingJoinPoint)throws Throwable{
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }
}
