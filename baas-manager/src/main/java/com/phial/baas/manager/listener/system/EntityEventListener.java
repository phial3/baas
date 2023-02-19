package com.phial.baas.manager.listener.system;

import com.phial.baas.api.domain.Entity;

public interface EntityEventListener {
    void onEntityChange(Entity event);

    boolean support(Entity event);
}
