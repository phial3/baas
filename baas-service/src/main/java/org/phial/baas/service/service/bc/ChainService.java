package org.phial.baas.service.service.bc;


import org.phial.baas.service.domain.entity.bc.Chain;

public interface ChainService {
    Chain getByChainIdOrNull(String chainId);
}
