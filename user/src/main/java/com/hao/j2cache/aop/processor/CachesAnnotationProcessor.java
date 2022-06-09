package com.hao.j2cache.aop.processor;

import com.hao.j2cache.annotation.Cache;
import com.hao.j2cache.model.AnnotationInfo;
import com.hao.j2cache.model.CacheHolder;
import net.oschina.j2cache.CacheObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Cache注解处理器
 */
public class CachesAnnotationProcessor extends AbstractCacheAnnotationProcessor{
    private static final Logger logger = LoggerFactory.getLogger(CachesAnnotationProcessor.class);
    private final List<AnnotationInfo<Cache>> annotationInfos;

    /**
     * 初始化处理器，同时将相关的对象进行初始化
     * @param proceedingJoinPoint
     * @param cache
     */
    public CachesAnnotationProcessor(ProceedingJoinPoint proceedingJoinPoint, Cache cache) {
        super();
        //创建注解封装信息对象
        annotationInfos = getAnnotationInfo(proceedingJoinPoint,cache);
    }

    /**
     * 缓存处理逻辑
     * @param proceedingJoinPoint 切点
     * @return 处理结果
     */
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        boolean existsCache = false;
        boolean exists = false;
        //获取缓存数据
        List<CacheHolder> cacheHolders = getCache(annotationInfos);
        for (CacheHolder cacheHolder : cacheHolders) {
            if(cacheHolder.isExistsCache()){
                //如果缓存数据存在则直接返回（相当于controller的目标方法没有执行）
                result = cacheHolder.getValue();
                existsCache = true;
            }
            exists = true;
        }
        if(exists){
            //如果缓存数据不存在，放行调用controller的目标方法
            result = invoke(proceedingJoinPoint);
        }
        //将目标方法返回的数据载入缓存
        for (AnnotationInfo<Cache> annotationInfo : annotationInfos) {
            exists = cacheChannel.exists(annotationInfo.getRegion(), annotationInfo.getKey());
            if(!exists){
                setCache(result);
            }
        }
        //将结果返回
        return result;
    }

    /**
     * 获取缓存数据
     * @param annotationInfos
     * @return
     */
    private List<CacheHolder> getCache(List<AnnotationInfo<Cache>> annotationInfos){
        ArrayList<CacheHolder> cacheHolders = new ArrayList<>();
        for (AnnotationInfo<Cache> annotationInfo : annotationInfos) {
            boolean exists = cacheChannel.exists(annotationInfo.getRegion(), annotationInfo.getKey());
            if(exists){
                CacheObject cacheObject = cacheChannel.get(annotationInfo.getRegion(), annotationInfo.getKey());
                cacheHolders.add(CacheHolder.newResult(cacheObject.getValue(),exists));
            }else {
                cacheHolders.add(CacheHolder.newResult(null,exists));
            }
        }
        return cacheHolders;
    }

    /**
     * 放行,调用目标方法
     * @param proceedingJoinPoint
     * @return
     */
    private Object invoke(ProceedingJoinPoint proceedingJoinPoint)throws Throwable{
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }

    /**
     * 设置缓存数据
     * @param result
     */
    private void setCache(Object result){
        for (AnnotationInfo<Cache> annotationInfo : annotationInfos) {
            cacheChannel.set(annotationInfo.getRegion(),annotationInfo.getKey(),result);
        }
    }
}
