package com.eli.product.elimvc.annotation;

import java.lang.annotation.*;

/**
 * @author eli
 * @description:
 */
@Target(ElementType.FIELD) //用于描述属性
@Retention(RetentionPolicy.RUNTIME)//运行时有效
@Documented
public @interface Qualifier {
    String value() default "";
}
