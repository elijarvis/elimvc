package com.eli.product.elimvc.annotation;

import java.lang.annotation.*;

/**
 * @author eli
 * @description: 控制层注解
 */
@Target(ElementType.TYPE) //用于描述类
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented
public @interface Controller {
    String value() default "";
}
