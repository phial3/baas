package org.phial.baas.service.listener.system;

import lombok.extern.slf4j.Slf4j;
import org.phial.baas.service.domain.entity.Entity;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class EntityEventDispatcher implements ApplicationReadyListener {

    private Collection<EntityEventListener> listeners;

    @Resource
    private ThreadPoolExecutor executor;

    @Override
    public void applicationReady(ConfigurableApplicationContext context) {
        listeners = context.getBeansOfType(EntityEventListener.class).values();
        log.info("::::::::Find Dispatcher::::::::EntityEventListeners: {}", listeners.size());
    }

    public void emitEvent(final Entity event) {
        listeners.forEach(e -> {
            if (e.support(event)) {
                executor.execute(() -> e.onEntityChange(event));
            }
        });
    }
}
