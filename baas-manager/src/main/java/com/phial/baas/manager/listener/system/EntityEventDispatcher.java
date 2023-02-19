package com.phial.baas.manager.listener.system;

import com.phial.baas.api.domain.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class EntityEventDispatcher implements ApplicationReadyListener {

    private Collection<EntityEventListener> listeners;


    @Override
    public void applicationReady(ConfigurableApplicationContext context) {
        listeners = context.getBeansOfType(EntityEventListener.class).values();
        log.info("::::::::Find Dispatcher::::::::EntityEventListeners: {}", listeners.size());
    }

    public void emitEvent(final Entity event) {
        listeners.stream().forEach(e -> {
            if (e.support(event)) {
            }
        });
    }
}
