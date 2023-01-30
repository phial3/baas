package org.phial.baas.listener.system;


import org.phial.baas.domain.Entity;

public interface EntityEventListener {
    void onEntityChange(Entity event);

    boolean support(Entity event);
}
