package org.phial.baas.chainmaker;

import org.phial.baas.api.BaasApiApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BaasChainmakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaasApiApplication.class, args);
    }

}
