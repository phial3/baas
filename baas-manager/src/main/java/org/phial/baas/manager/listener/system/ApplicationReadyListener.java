package org.phial.baas.manager.listener.system;

import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationReadyListener {
    void applicationReady(ConfigurableApplicationContext context);
}
