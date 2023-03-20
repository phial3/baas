package org.phial.baas.manager.config.redis;

import lombok.Data;

@Data
public class RedisEvent {
    private Integer eventType;

    private String source;

    private String content;

    private Integer sender;

}
