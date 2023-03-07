package org.phial.baas.fabric.deploy;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.phial.baas.api.constant.CryptoEnum;
import org.phial.baas.api.domain.entity.Organization;
import org.phial.baas.fabric.entity.NodeDomain;

import java.util.List;

@Data
@AllArgsConstructor
public class CryptogenBatch {

    private Organization organization;

    private CryptoEnum.CryptoUserType type;

    private List<NodeDomain> nodeDomainList;
}
