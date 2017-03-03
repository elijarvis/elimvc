package com.eli.product.elimvc.annotation;

import java.lang.annotation.*;

/**
 * @author eli
 * @description: 服务层注解
 */
@Target({ElementType.TYPE, ElementType.METHOD}) //用于描述类和方法
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented
public @interface RequestMapping {
    String value() default "";
}
