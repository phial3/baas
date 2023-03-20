package org.phial.baas.service.listener.system;


import org.phial.baas.service.domain.entity.Entity;

public interface EntityEventListener {
    void onEntityChange(Entity event);

    boolean support(Entity event);
}
