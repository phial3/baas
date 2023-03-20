package org.phial.baas.service.service;


import org.phial.baas.service.domain.entity.Node;
import org.phial.baas.service.domain.entity.Organization;

public interface OrganizationService {

    Node getCANode(String domain);

    Organization getByDomainOrNull(String orgDomain);
}
