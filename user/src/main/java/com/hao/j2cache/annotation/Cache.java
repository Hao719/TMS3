package com.hao.j2cache.annotation;

import java.lang.annotation.*;

/**
 * 加入缓存注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    String region() default "";
    String key() default "";
    String params() default "";
}
