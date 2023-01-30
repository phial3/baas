package org.phial.baas;

import com.dtp.core.spring.EnableDynamicTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDynamicTp
@SpringBootApplication
public class BaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaasApplication.class, args);
    }

}
