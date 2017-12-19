/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2017/11/14
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.holy.demo.constant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2017/11/14
 * Author: Holy.Han
 * modification:
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bind {
    int value();
    boolean click() default false;
}
