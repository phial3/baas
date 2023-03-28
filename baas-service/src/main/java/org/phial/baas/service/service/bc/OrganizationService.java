package org.phial.baas.service.service.bc;


import org.phial.baas.service.domain.entity.bc.P2pNode;
import org.phial.baas.service.domain.entity.rbac.Organization;

public interface OrganizationService {

    P2pNode getCANode(String domain);

    Organization getByDomainOrNull(String orgDomain);
}
