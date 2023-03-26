package org.phial.baas.manager.config.init;

import org.mayanjun.mybatisx.dal.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CachedAspect<T extends Annotation> {

    private Map<Method, T> cache = new HashMap<>();

    private Class<T> annotationType;

    protected T annotation(Method method) {
        return cache.computeIfAbsent(method, m -> m.getAnnotation(annotationType()));
    }

    protected Class<T> annotationType() {
        if (this.annotationType != null) return annotationType;
        annotationType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return annotationType;
    }

}
