package org.phial.baas.service.listener.system;

import org.mayanjun.mybatisx.api.entity.Entity;

public interface EntityEventListener {
    void onEntityChange(Entity event);

    boolean support(Entity event);
}
