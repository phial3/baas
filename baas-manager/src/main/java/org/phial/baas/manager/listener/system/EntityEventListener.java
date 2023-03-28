package org.phial.baas.manager.listener.system;


import org.phial.baas.manager.service.EntityEvent;

public interface EntityEventListener {
    void onEntityChange(EntityEvent event);

    boolean support(EntityEvent event);
}
