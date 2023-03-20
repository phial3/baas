package org.phial.baas.fabric.deploy;

import com.umetrip.blockchain.fabric.constants.enums.CryptoEnum;
import com.umetrip.blockchain.fabric.domain.entity.FabricOrg;
import com.umetrip.blockchain.fabric.domain.node.NodeDomain;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CryptogenBatch {

    private FabricOrg fabricOrg;

    private CryptoEnum.CryptoUserType type;

    private List<NodeDomain> nodeDomainList;
}
