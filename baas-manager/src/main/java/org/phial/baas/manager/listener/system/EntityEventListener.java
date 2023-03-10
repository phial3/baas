package org.phial.baas.manager.listener.system;

import org.phial.baas.api.domain.entity.Entity;

public interface EntityEventListener {
    void onEntityChange(Entity event);

    boolean support(Entity event);
}
