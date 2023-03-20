package org.phial.baas.manager.listener.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
public class RedisEvent {

    private Integer eventType;

    private String source;

    private String content;

    private Integer sender;



    @AllArgsConstructor
    public enum RedisEventType {
        BLOCK_LISTEN_RELEASE(0),
        EVENT_LISTEN_RELEASE(1),
        CHAIN_ADD(2),
        CHAIN_REMOVE(3),
        CHAIN_ORG_ADD(4),
        CHAIN_ORG_REMOVE(5);

        @Getter
        private final Integer code;

        public static RedisEventType getEventType(Integer code) {
            for (RedisEventType event : RedisEventType.values()) {
                if (event.getCode().equals(code)) {
                    return event;
                }
            }
            throw new RuntimeException("no RedisEventType node:" + code);
        }
    }

    @Data
    public static class SubscribeEvent {
        String chainId;
        String eventName;
        String contractName;
    }

    @Data
    public static class ChainEvent {
        String chainId;
        List<String> orgIds;
    }


    public static RedisEvent createSubscribeEndEvent(String chainId, String eventName, String contractName, RedisEventType eventType, String source) {
        RedisEvent event = new RedisEvent();
        event.setEventType(eventType.getCode());
        event.setSource(source);
        JSONObject content = new JSONObject();
        content.put("chainId", chainId);
        content.put("eventName", eventName);
        content.put("contractName", contractName);
        event.setContent(JSON.toJSONString(content));
        return event;
    }

    public static RedisEvent createChainOrgEvent(String chainId, List<String> orgIds, RedisEventType eventType, String source) {
        RedisEvent event = new RedisEvent();
        event.setEventType(eventType.getCode());
        event.setSource(source);
        JSONObject content = new JSONObject();
        content.put("chainId", chainId);
        content.put("orgIds", orgIds);
        event.setContent(JSON.toJSONString(content));
        return event;
    }

    public SubscribeEvent parseSubscribeEvent() {
        return JSON.parseObject(content, SubscribeEvent.class);
    }

    public ChainEvent parseChainOrgEvent() {
        return JSON.parseObject(content, ChainEvent.class);
    }


}
