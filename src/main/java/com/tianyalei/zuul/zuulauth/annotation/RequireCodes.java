package com.tianyalei.zuul.zuulauth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需求的权限.
 * @author wuweifeng wrote on 2019/8/9.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireCodes {
    String[] value() default "";

    Logical logical() default Logical.AND;
}
