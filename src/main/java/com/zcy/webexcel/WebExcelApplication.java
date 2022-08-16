package com.zcy.webexcel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
public class WebExcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebExcelApplication.class, args);
    }

}
