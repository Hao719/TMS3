package com.hao.j2cache.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.lang.annotation.Annotation;

/**
 * Cache信息包装
 */
@Data
public class AnnotationInfo<T extends Annotation> {
    private T annotation;
    private String key;
    private String region;


    public String toString() {
        if(annotation == null){
            return null;
        }
        return JSONObject.toJSONString(this);
    }
}
