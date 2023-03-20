package org.phial.baas.service.listener.system;

import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationReadyListener {
    void applicationReady(ConfigurableApplicationContext context);
}
