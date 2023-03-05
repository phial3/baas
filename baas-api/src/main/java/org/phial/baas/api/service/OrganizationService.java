package org.phial.baas.api.service;

import org.phial.baas.api.domain.Node;
import org.phial.baas.api.domain.Organization;

public interface OrganizationService {

    Node getCANode(String domain);

    Organization getByDomainOrNull(String orgDomain);
}
