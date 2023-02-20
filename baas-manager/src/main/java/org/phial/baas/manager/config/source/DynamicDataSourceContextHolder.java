package org.phial.baas.manager.config.source;

import lombok.extern.slf4j.Slf4j;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Slf4j
public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static String getCurrentSource() {
        String sourceKey = CONTEXT_HOLDER.get();
        log.info("DynamicDataSourceContextHolder getCurrentSource source:{}", sourceKey);
        return sourceKey;
    }

    public static void setCurrentSource(String source) {
        log.info("DynamicDataSourceContextHolder setCurrentSource source:{}", source);
        CONTEXT_HOLDER.set(source);
    }
}
