package org.phial.baas.fabric.deploy;

import com.umetrip.blockchain.fabric.deploy.yaml.GenesisBlockYaml;
import com.umetrip.blockchain.fabric.deploy.yaml.YamlDumper;
import com.umetrip.blockchain.fabric.domain.node.CANodeDomain;
import com.umetrip.blockchain.fabric.domain.node.NodeDomain;
import com.umetrip.blockchain.fabric.domain.node.OrderNodeDomain;
import com.umetrip.blockchain.fabric.domain.node.PeerNodeDomain;

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
            CANodeDomain caNodeDomain = orgCaNodes.get(order.getFabricOrg().getDomain());
            if (caNodeDomain == null) {
                throw new RuntimeException("order找不到指定的CANodeDomain");
            }
            genesisBlockYaml.addOrder(order, caNodeDomain);
        }

        for (PeerNodeDomain peer : peers) {
            genesisBlockYaml.addPeerOrganization(orgCaNodes.get(peer.getFabricOrg().getDomain()));
        }

        return YamlDumper.getInstance().dump(genesisBlockYaml.getYaml());
    }

}
