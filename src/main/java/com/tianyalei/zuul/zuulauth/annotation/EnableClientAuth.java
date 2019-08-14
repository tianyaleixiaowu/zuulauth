package com.tianyalei.zuul.zuulauth.annotation;

import com.tianyalei.zuul.zuulauth.config.ClientRequestMappingConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 决定客户端是否开启上传mapping信息的开关，默认是上传。
 * 有些情况下，可能修改了代码里的接口权限后，不希望覆盖原来的redis信息，就可以关闭
 * @author wuweifeng wrote on 2019-08-14.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ClientRequestMappingConfigure.class)
public @interface EnableClientAuth {
    boolean value() default true;
}