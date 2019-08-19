package com.tianyalei.zuul.zuulauth.annotation;

import com.tianyalei.zuul.zuulauth.config.ZuulBlackListConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启白名单（指ip白名单）
 * @author wuweifeng wrote on 2019-08-14.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ZuulBlackListConfigure.class)
public @interface EnableBlackList {

}