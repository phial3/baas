package org.phial.baas.fabric.deploy;

import org.phial.baas.api.util.YamlUtil;
import org.phial.baas.fabric.deploy.yaml.GenesisBlockYaml;
import org.phial.baas.fabric.entity.CANodeDomain;
import org.phial.baas.fabric.entity.NodeDomain;
import org.phial.baas.fabric.entity.OrderNodeDomain;
import org.phial.baas.fabric.entity.PeerNodeDomain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenesisBlockYamlUtil {

    public static String generateGenesisYaml(List<NodeDomain> nodes) {

        List<CANodeDomain> cas = new LinkedList<>();
        List<PeerNodeDomain> peers = new LinkedList<>();
        List<OrderNodeDomain> orders = new LinkedList<>();
        for (NodeDomain nodeDomain : nodes) {
            if (nodeDomain instanceof CANodeDomain) {
                cas.add((CANodeDomain)nodeDomain);
            } else if (nodeDomain instanceof PeerNodeDomain) {
                peers.add((PeerNodeDomain)nodeDomain);
            } else {
                orders.add((OrderNodeDomain)nodeDomain);
            }
        }
        Map<String, CANodeDomain> orgCaNodes = cas.stream().collect(Collectors.toMap(caNode -> caNode.getFabricOrg().getDomain(), caNode -> caNode));


        GenesisBlockYaml genesisBlockYaml = new GenesisBlockYaml();
        genesisBlockYaml.initPolicies();
        genesisBlockYaml.initOrder();
        for (OrderNodeDomain order : orders) {
            genesisBlockYaml.addOrder(order, orgCaNodes.get(order.getFabricOrg().getDomain()));
        }

        for (PeerNodeDomain peer : peers) {
            genesisBlockYaml.addPeerOrganization(orgCaNodes.get(peer.getFabricOrg().getDomain()));
        }

        return YamlUtil.toYaml(genesisBlockYaml.getYaml());
    }

}
