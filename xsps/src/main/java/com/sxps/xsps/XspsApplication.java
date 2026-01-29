package com.sxps.xsps;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;


//开启定时任务
@EnableKnife4j
@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class XspsApplication {

    public static void main(String[] args) {
        SpringApplication.run(XspsApplication.class, args);
    }

}
