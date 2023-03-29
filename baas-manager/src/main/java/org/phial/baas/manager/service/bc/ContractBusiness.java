package org.phial.baas.manager.service.bc;

import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.service.domain.entity.bc.SmartContract;
import org.phial.baas.service.service.bc.ContractService;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Component
public class ContractBusiness extends ConsoleBaseBusiness<SmartContract> implements ContractService {

    @Override
    public void invokeContract() {

    }

    @Override
    public void queryContract() {

    }
}
