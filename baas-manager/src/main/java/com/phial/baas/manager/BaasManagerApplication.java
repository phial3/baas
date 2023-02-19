package com.phial.baas.manager;

import com.dtp.core.spring.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableDynamicTp
@SpringBootApplication
public class BaasManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaasManagerApplication.class, args);
    }

}
