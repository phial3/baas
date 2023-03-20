package org.phial.baas.manager.listener.redis;

import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gyf
 * @date 2022/12/12
 */
public class RedisMessageListener implements MessageListener<String> {

    static Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);

    private final Map<String, Long> messageUnDuplicate = new ConcurrentHashMap<>();

    @Override
    public void onMessage(CharSequence channel, String msg) {
        removeOldMsgCache();
        logger.info("onMessage:{}", msg);
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
