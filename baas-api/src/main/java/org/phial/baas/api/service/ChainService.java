package org.phial.baas.api.service;

import org.phial.baas.api.domain.entity.Chain;

public interface ChainService {
    Chain getByChainIdOrNull(String chainId);

    void deploy();

    void undeploy();

    void restart();
}
