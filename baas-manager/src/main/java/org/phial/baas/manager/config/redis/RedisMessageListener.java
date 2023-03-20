package org.phial.baas.manager.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Slf4j
public class RedisMessageListener implements MessageListener<String> {

    private final Map<String, Long> messageUnDuplicate = new ConcurrentHashMap<>();

    @Override
    public void onMessage(CharSequence channel, String msg) {
        removeOldMsgCache();
        log.info("onMessage:{}", msg);
    }

    private void removeOldMsgCache() {
        Set<String> keySet = messageUnDuplicate.keySet();
        long current = System.currentTimeMillis();
        for (String key : keySet) {
            if (current - 10000 > messageUnDuplicate.get(key)) {
                messageUnDuplicate.remove(key);
            }
        }
    }
}
