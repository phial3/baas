package org.phial.baas.service.listener.system;

import org.mayanjun.mybatisx.api.entity.Entity;
import org.phial.baas.service.service.EntityEvent;

public interface EntityEventListener {
    void onEntityChange(EntityEvent event);

    boolean support(EntityEvent event);
}
