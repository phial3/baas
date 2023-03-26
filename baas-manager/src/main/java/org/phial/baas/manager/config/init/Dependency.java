package org.phial.baas.manager.config.init;


public @interface Dependency {

    String method();

    //    Class<? extends DataController> type();
    Class<?> type();

}
