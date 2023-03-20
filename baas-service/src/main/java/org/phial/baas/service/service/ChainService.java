package org.phial.baas.service.service;


import org.phial.baas.service.domain.entity.Chain;

public interface ChainService {
    Chain getByChainIdOrNull(String chainId);

    void deploy();

    void undeploy();

    void restart();
}
