package org.phial.baas.fabric.deploy;

import org.phial.baas.api.domain.entity.Node;
import org.phial.baas.api.domain.entity.Organization;
import org.phial.baas.api.util.YamlUtil;
import org.phial.baas.fabric.deploy.yaml.CaConfigYaml;
import org.phial.baas.fabric.entity.NodeDomain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NodeYamlUtil {

    @Value("${baas.fabric.image.ca}")
    private String imageCa;

    @Value("${baas.fabric.image.db}")
    private String database;

    public Map<String, String> generateCaConfigYaml(NodeDomain nodeDomain) {

        Node caNode = nodeDomain.getFabricNode();
        Organization org = nodeDomain.getFabricOrg();

        String imageVersion = imageCa.split(":")[1];
        CaConfigYaml caConfigYaml = new CaConfigYaml(imageVersion, false, caNode.getRpcPort());
        caConfigYaml.setOperations(9943L);
        caConfigYaml.setBccsp("SW", "SHA2", 256);
        caConfigYaml.setSigning("876000h");
        caConfigYaml.setDB(database);

        //暂时没用
        caConfigYaml.setCsr(org.getDomain(), org.getCountry(), "", "", "", "", "");

        String yaml = YamlUtil.toYaml(caConfigYaml.getYaml());
        Map<String, String> result = new HashMap<>();
        result.put("fabric-ca-server-config.yaml", yaml);
        return result;
    }
}
