package org.phial.baas.fabric.deploy;

import com.umetrip.blockchain.fabric.config.apollo.ApolloConfig;
import com.umetrip.blockchain.fabric.deploy.yaml.CaConfigYaml;
import com.umetrip.blockchain.fabric.deploy.yaml.YamlDumper;
import com.umetrip.blockchain.fabric.domain.entity.FabricNode;
import com.umetrip.blockchain.fabric.domain.entity.FabricOrg;
import com.umetrip.blockchain.fabric.domain.node.NodeDomain;

import java.util.HashMap;
import java.util.Map;

public class NodeYamlUtil {

    public static Map<String, String> generateFabricCaConfigYaml(NodeDomain nodeDomain) {

        FabricNode caNode = nodeDomain.getFabricNode();
        FabricOrg org = nodeDomain.getFabricOrg();

        String imageVersion = ApolloConfig.getImagesName("ca").split(":")[1];
        CaConfigYaml caConfigYaml = new CaConfigYaml(imageVersion, false, caNode.getRpcPort());
        caConfigYaml.setOperations(9943L);
        caConfigYaml.setBccsp("SW", "SHA2", 256);
        caConfigYaml.setSigning("876000h");
        caConfigYaml.setDB(ApolloConfig.getDatasource());
        //暂时没用
        caConfigYaml.setCsr(org.getDomain(), org.getCountry(), "", "", "", "", "");

        String yaml = YamlDumper.getInstance().dump(caConfigYaml.getYaml());
        Map<String, String> result = new HashMap<>();
        result.put("fabric-ca-server-config.yaml", yaml);
        return result;
    }
}
