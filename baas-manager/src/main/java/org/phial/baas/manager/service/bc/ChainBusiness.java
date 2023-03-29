package org.phial.baas.manager.service.bc;

import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.service.domain.entity.bc.Chain;
import org.phial.baas.service.service.bc.ChainService;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Component
public class ChainBusiness extends ConsoleBaseBusiness<Chain> implements ChainService {

    @Override
    public Chain getByChainIdOrNull(String chainId) {
        return dao().queryOne(
                QueryBuilder.custom(Chain.class)
                        .andEquivalent("chainId", chainId)
                        .build()
        );
    }
}
