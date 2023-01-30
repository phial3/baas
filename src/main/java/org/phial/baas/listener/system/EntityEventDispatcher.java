package org.phial.baas.listener.system;

import org.phial.baas.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class EntityEventDispatcher implements ApplicationReadyListener {

    private static final Logger logger = LoggerFactory.getLogger(EntityEventDispatcher.class);

    private Collection<EntityEventListener> listeners;

    private ThreadPoolTaskExecutor executor;

    public EntityEventDispatcher(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void applicationReady(ConfigurableApplicationContext context) {
        listeners = context.getBeansOfType(EntityEventListener.class).values();
        logger.info("::::::::Find Dispatcher::::::::EntityEventListeners: {}", listeners.size());
    }

    public void emitEvent(final Entity event) {
        listeners.stream().forEach(e -> {
            if (e.support(event)) {
                executor.submit(() -> e.onEntityChange(event));
            }
        });
    }
}
