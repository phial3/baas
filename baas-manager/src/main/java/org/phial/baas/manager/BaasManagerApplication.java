package org.phial.baas.manager;

import com.dtp.core.spring.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDynamicTp
@SpringBootApplication
public class BaasManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaasManagerApplication.class, args);
    }

}
