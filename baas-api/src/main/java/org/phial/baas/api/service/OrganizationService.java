package org.phial.baas.api.service;

import org.phial.baas.api.domain.entity.Node;
import org.phial.baas.api.domain.entity.Organization;

public interface OrganizationService {

    Node getCANode(String domain);

    Organization getByDomainOrNull(String orgDomain);
}
