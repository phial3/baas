package org.phial.baas.manager.service.bc;

import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.service.domain.entity.bc.P2pNode;
import org.phial.baas.service.domain.entity.rbac.Organization;
import org.phial.baas.service.service.bc.OrganizationService;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Component
public class OrganizationBusiness extends ConsoleBaseBusiness<Organization> implements OrganizationService {


    @Override
    public P2pNode getCANode(String domain) {
        return null;
    }

    @Override
    public Organization getByDomainOrNull(String orgDomain) {
        return null;
    }
}
