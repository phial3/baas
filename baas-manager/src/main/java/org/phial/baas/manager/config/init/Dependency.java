package org.phial.baas.manager.config.init;


import org.phial.baas.manager.controller.DataController;

public @interface Dependency {

    String method();

    Class<? extends DataController> type();

}
