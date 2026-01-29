package com.sxps.xsps.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AfterSaleLogRecord {

    String value();              // 操作内容
    String operator() default "merchant"; // user / merchant / system
}
