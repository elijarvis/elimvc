package com.eli.product.elimvc.annotation;

import java.lang.annotation.*;

/**
 * @author eli
 * @description: 服务层注解
 */
@Target(ElementType.PARAMETER) //用于描述参数
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented
public @interface RequestParam {
    String value() default "";
}
