package org.phial.baas.manager.service.bc;

import org.phial.baas.manager.config.cache.CacheClient;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.service.domain.entity.bc.ChainNode;
import org.phial.baas.service.service.bc.ChainNodeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author admin
 */
@Component
public class ChainNodeBusiness extends ConsoleBaseBusiness<ChainNode> implements ChainNodeService {

    @Resource
    private ConsoleSessionManager sessionManager;

    @Resource
    private CacheClient cacheClient;

}
