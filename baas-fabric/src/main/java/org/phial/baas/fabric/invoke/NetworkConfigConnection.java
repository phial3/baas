package org.phial.baas.fabric.invoke;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umetrip.blockchain.fabric.constants.CommonConstant;
import com.umetrip.blockchain.fabric.constants.enums.NodeEnum;
import com.umetrip.blockchain.fabric.domain.chain.ChainFactory;
import com.umetrip.blockchain.fabric.domain.entity.FabricChain;
import com.umetrip.blockchain.fabric.domain.entity.FabricChainNode;
import com.umetrip.blockchain.fabric.domain.entity.FabricNode;
import com.umetrip.blockchain.fabric.domain.entity.FabricOrg;
import com.umetrip.blockchain.fabric.domain.node.CANodeDomain;
import com.umetrip.blockchain.fabric.domain.node.NodeDomain;
import com.umetrip.blockchain.fabric.service.manager.FabricChainNodeService;
import com.umetrip.blockchain.fabric.service.manager.FabricNodeService;
import com.umetrip.blockchain.fabric.service.manager.FabricOrgService;
import org.phial.baas.service.domain.entity.ChainNode;
import org.phial.baas.service.service.ChainNodeService;
import org.phial.baas.service.service.NodeService;
import org.phial.baas.service.service.OrganizationService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * client: 客户端配置。其中的organization属性为必需项，指定到organizations配置中的某一个具体的组织。
 * channels: 通道配置。所有的通道都配置在这里，每一个通道里都要配置排序节点和peer节点。
 * organizations: 组织配置。配置所有的组织的mspId，ca实体及admin用户的证书等。
 * orderers: 排序节点配置，url，证书等。
 * peers: Peer节点配置，url，grpc属性，证书等。
 * certificateAuthorities: ca节点配置。
 * <p>
 * 注意：
 * 1. 由于所有的证书都是颁发给带域名的url的，而我们通常会使用IP来访问，所以必须要配置hostnameOverride属性指定到相关的url
 */
@Service
public class NetworkConfigConnection {

    @Resource
    private OrganizationService organizationService;

    @Resource
    private NodeService nodeService;

    @Resource
    private ChainNodeService chainNodeService;

    private boolean usePem = true;

    private static final String cryptoConfigFilePath = "";

    public JSONObject buildConnectionJson() {
        JSONObject connection = new JSONObject();
        connection.put("name", "fabric-network");
        connection.put("version", "1.0.0");
        connection.put("channels", buildChannels());
        connection.put("certificateAuthorities", buildCertificateAuthorities());
        connection.put("organizations", buildOrganizations());
        connection.put("client", buildClient());
        connection.put("peers", buildPeers());
        connection.put("orderers", buildOrderers());
        return connection;
    }


    // channelId : org :: nodeList
    public Map<String, Map<String, List<FabricNode>>> getChannelOrgNodesMap() {
        List<FabricChainNode> chainNodeList = fabricChainNodeService.listAllByCondition(FabricChainNode.builder().build());

        // 根据channelId 分组
        Map<String, List<FabricChainNode>> channelMap = chainNodeList.stream().collect(Collectors.groupingBy(FabricChainNode::getChainName));

        Map<String, Map<String, List<FabricNode>>> retMap = new HashMap<>();
        for (Map.Entry<String, List<FabricChainNode>> entry : channelMap.entrySet()) {
            String channelId = entry.getKey();

            ChainFactory chainFactory = ChainFactory.getInstance();
            FabricChain chain = chainFactory.selectChain(channelId);
            if (chain == null || chain.getIsuse() == 0 || !chain.getNetworkStatus().equals("ONLINE")) {
                // 跳过禁用的链
                continue;
            }

            // 链中的节点ID,包含order和peer
            List<Long> nodeIdList = entry.getValue().stream().map(FabricChainNode::getNodeId).collect(Collectors.toList());

            List<FabricNode> nodeList = fabricNodeService.getByIds(nodeIdList)
                    .stream()
                    .filter(n -> n.getIsuse() == 1 && n.getStatus().equals(NodeEnum.NodeStatus.ONLINE.getStatus()))
                    .collect(Collectors.toList());

            if (nodeList.isEmpty()) {
                // 没有可用的节点
                continue;
            }
            Map<String, List<FabricNode>> orgNodesMap = nodeList.stream().collect(Collectors.groupingBy(FabricNode::getOrgDomain));
            retMap.put(channelId, orgNodesMap);
        }

        return retMap;
    }

    private JSONObject buildCertificateAuthorityNode(FabricOrg org) {
        JSONObject node = new JSONObject();

        CANodeDomain caNodeDomain = org.selectCANode();
        Assert.notNull(caNodeDomain, "ca节点不存在！org=" + org.getDomain());

        String hostname = "ca." + org.getDomain();

        node.put("url", caNodeDomain.getUrl());
        node.put("caName", hostname);

        JSONObject grpcOptions = new JSONObject();
        grpcOptions.put("ssl-target-name-override", hostname);
        grpcOptions.put("hostnameOverride", hostname);
        grpcOptions.put("allow-insecure", 0);
        grpcOptions.put("trustServerCertificate", true);
        node.put("grpcOptions", grpcOptions);

        JSONObject httpOptions = new JSONObject();
        httpOptions.put("verify", false);
        node.put("httpOptions", httpOptions);

        JSONArray registrar = new JSONArray();

        JSONObject admin = new JSONObject();
        admin.put("enrollId", CommonConstant.ADMIN);
        admin.put("enrollSecret", CommonConstant.ADMIN_PASSWD);
        registrar.add(admin);
        node.put("registrar", registrar);

        //        JSONObject tlsCACerts = new JSONObject();
        //        if (usePem) {
        //            Properties properties = caNodeDomain.getProperties();
        //            String pemBytes = new String((byte[]) properties.get("pemBytes"));
        //            tlsCACerts.put("pem", pemBytes);
        //            //tlsCACerts.put("pem", getCaCertPem(org.getDomain()));
        //        } else {
        //            tlsCACerts.put("path", getCaCertPath(org.getDomain()));
        //        }
        //        node.put("tlsCACerts", tlsCACerts);

        return node;
    }

    private JSONObject buildCertificateAuthorities() {
        JSONObject root = new JSONObject();

        Map<String, Map<String, List<FabricNode>>> channelOrgNodesMap = getChannelOrgNodesMap();
        for (Map.Entry<String, Map<String, List<FabricNode>>> entry : channelOrgNodesMap.entrySet()) {
            String channelId = entry.getKey();
            Map<String, List<FabricNode>> orgNodeMap = entry.getValue();

            for (Map.Entry<String, List<FabricNode>> orgEntry : orgNodeMap.entrySet()) {
                String orgDomain = orgEntry.getKey();
                FabricOrg org = fabricOrgService.getByOrgDomain(orgDomain);

                CANodeDomain caNode = org.selectCANode();
                if (caNode == null) {
                    // 不存在CA的跳过
                    continue;
                }
                JSONObject node = buildCertificateAuthorityNode(org);
                root.put("ca." + org.getDomain(), node);
            }
        }

        return root;
    }

    private JSONObject buildPeers() {
        JSONObject root = new JSONObject();

        Map<String, Map<String, List<FabricNode>>> channelOrgNodesMap = getChannelOrgNodesMap();
        for (Map.Entry<String, Map<String, List<FabricNode>>> entry : channelOrgNodesMap.entrySet()) {
            String channelId = entry.getKey();

            // org: nodeList
            Map<String, List<FabricNode>> orgNodeMap = entry.getValue();

            for (Map.Entry<String, List<FabricNode>> orgEntry : orgNodeMap.entrySet()) {
                String orgDomain = orgEntry.getKey();
                List<FabricNode> orgNodeList = orgEntry.getValue();

                List<FabricNode> peerNodeList = orgNodeList.stream()
                        .filter(n -> n.getNodeType().equals(NodeEnum.NodeType.PEER.getNodeType()))
                        .collect(Collectors.toList());

                for (int i = 0; i < peerNodeList.size(); i++) {
                    FabricNode peerNode = peerNodeList.get(i);

                    String hostname = peerNode.getNodeName();
                    // . 转换
                    // peer0.www.hlzh.com
                    // peer0-www-hlzh-com
                    hostname = hostname.replaceAll("\\.", "-");

                    JSONObject node = new JSONObject();

                    NodeDomain peerNodeDomain = peerNode.createNodeDomain();
                    node.put("url", peerNodeDomain.getUrl());
                    // node.put("eventUrl", "grpcs://" + getUrl(o, "*") + ":" + getPort("*", "event_" + p, 7053));

                    JSONObject grpcOptions = new JSONObject();
                    grpcOptions.put("ssl-target-name-override", hostname);
                    grpcOptions.put("hostnameOverride", hostname);
                    grpcOptions.put("grpc.http2.keepalive_time", 15);
                    grpcOptions.put("request-timeout", 120001);
                    grpcOptions.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 100 * 1024 * 1024);

                    node.put("grpcOptions", grpcOptions);

                    JSONObject tlsCACerts = new JSONObject();
                    if (usePem) {
                        Properties properties = peerNodeDomain.getProperties();
                        if (properties.get("pemBytes") != null) {
                            String pemBytes = new String((byte[]) properties.get("pemBytes"));
                            tlsCACerts.put("pem", pemBytes);
                        } else {
                            tlsCACerts.put("pem", "");
                        }
                        //tlsCACerts.put("pem", getPeerCertPem(org.getDomain(), peer));
                    } else {
                        tlsCACerts.put("path", getPeerCertPath(orgDomain, ""));
                    }
                    node.put("tlsCACerts", tlsCACerts);
                    root.put(peerNode.getNodeName(), node);
                }
            }
        }

        return root;
    }

    private JSONObject buildOrderers() {
        JSONObject node = new JSONObject();

        Map<String, Map<String, List<FabricNode>>> channelOrgNodesMap = getChannelOrgNodesMap();
        for (Map.Entry<String, Map<String, List<FabricNode>>> entry : channelOrgNodesMap.entrySet()) {
            String channelId = entry.getKey();
            Map<String, List<FabricNode>> orgNodeMap = entry.getValue();

            for (Map.Entry<String, List<FabricNode>> orgEntry : orgNodeMap.entrySet()) {
                String orgDomain = orgEntry.getKey();
                List<FabricNode> orgNodeList = orgEntry.getValue();

                List<FabricNode> orderNodeList = orgNodeList.stream()
                        .filter(n -> n.getNodeType().equals(NodeEnum.NodeType.ORDER.getNodeType()))
                        .collect(Collectors.toList());

                for (int i = 0; i < orderNodeList.size(); i++) {
                    JSONObject orgNode = new JSONObject();

                    FabricNode orderNode = orderNodeList.get(i);
                    NodeDomain orderNodeDomain = orderNode.createNodeDomain();
                    orgNode.put("url", orderNodeDomain.getUrl());

                    String hostname = orderNode.getNodeName();
                    // . 转换
                    // peer0.www.hlzh.com
                    // peer0-www-hlzh-com
                    hostname = hostname.replaceAll("\\.", "-");

                    JSONObject grpcOptions = new JSONObject();
                    grpcOptions.put("ssl-target-name-override", hostname);
                    grpcOptions.put("hostnameOverride", hostname);
                    grpcOptions.put("grpc-max-send-message-length", 15);
                    grpcOptions.put("grpc.keepalive_time_ms", 360000);
                    grpcOptions.put("grpc.keepalive_timeout_ms", 60000);
                    grpcOptions.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 100 * 1024 * 1024);
                    orgNode.put("grpcOptions", grpcOptions);

                    JSONObject tlsCACerts = new JSONObject();
                    if (usePem) {
                        Properties properties = orderNodeDomain.getProperties();
                        if (properties.get("pemBytes") != null) {
                            String pemBytes = new String((byte[]) properties.get("pemBytes"));
                            tlsCACerts.put("pem", pemBytes);
                        } else {
                            tlsCACerts.put("pem", "");
                        }
                        //tlsCACerts.put("pem", getOrdererCertPem(org.getDomain()));
                    } else {
                        tlsCACerts.put("path", getOrdererCertPath(orgDomain));
                    }
                    orgNode.put("tlsCACerts", tlsCACerts);

                    node.put(orderNode.getNodeName(), orgNode);
                }
            }

        }
        return node;
    }

    private JSONObject buildOrgNode(String channelId, FabricOrg org) {
        JSONObject ordererOrgNode = new JSONObject();
        ordererOrgNode.put("mspid", CommonConstant.getOrgMspID(org.getOrgName()));

        JSONArray certificateAuthorities = new JSONArray();
        certificateAuthorities.add("ca." + org.getDomain());
        ordererOrgNode.put("certificateAuthorities", certificateAuthorities);

        //        JSONObject adminPrivateKey = new JSONObject();
        //        if (usePem) {
        //            adminPrivateKey.put("pem", getAdminPrivateKeyPem(org.getDomain()));
        //        } else {
        //            adminPrivateKey.put("path", getAdminPrivateKeyPath(org.getDomain()));
        //        }
        //        ordererOrgNode.put("adminPrivateKey", adminPrivateKey);
        //
        //        JSONObject signedCert = new JSONObject();
        //        if (usePem) {
        //            signedCert.put("pem", getAdminCertPem(org.getDomain()));
        //        } else {
        //            signedCert.put("path", getAdminCertPath(org.getDomain()));
        //        }
        //        ordererOrgNode.put("signedCert", signedCert);

        // org : nodeList
        Map<String, List<FabricNode>> orgNodeMap = getChannelOrgNodesMap().get(channelId);
        List<FabricNode> orgNodeList = orgNodeMap.get(org.getDomain());
        List<String> peerNames = getPeers(orgNodeList);
        JSONArray peers = new JSONArray();
        for (int i = 0; i < peerNames.size(); i++) {
            peers.add(i, peerNames.get(i));
        }
        ordererOrgNode.put("peers", peers);

        return ordererOrgNode;
    }

    private String getCaCertPath(String orgDomain) {
        return orgDomain + "/ca/ca." + orgDomain + "-cert.pem";
    }

    private String getCaCertPem(String orgDomain) {
        File file = new File(cryptoConfigFilePath, getCaCertPath(orgDomain));
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return null;
        }
    }

    private String getOrdererCertPem(String orgDomain) {
        File file = new File(cryptoConfigFilePath, getOrdererCertPath(orgDomain));
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getOrdererCertPath(String orgDomain) {
        return "org-orderer/orderers/" + orgDomain + "/msp/tlscacerts/tlsca.org-orderer-cert.pem";
    }

    private String getPeerCertPem(String orgDomain, String peer) {
        File file = new File(cryptoConfigFilePath, getPeerCertPath(orgDomain, peer));
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPeerCertPath(String orgDomain, String peer) {
        return orgDomain + "/peers/" + peer + "/msp/tlscacerts/tlsca." + orgDomain + "-cert.pem";
    }

    private String getAdminCertPem(String orgDomain) {
        File file = new File(cryptoConfigFilePath, getAdminCertPath(orgDomain));
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return null;
        }
    }

    private String getAdminCertPath(String orgDomain) {
        return orgDomain + "/users/Admin@" + orgDomain + "/msp/admincerts/Admin@" + orgDomain + "-cert.pem";
    }

    private String getAdminPrivateKeyPem(String org) {
        File dir = new File(cryptoConfigFilePath, getAdminPrivateKeyPath(org));
        if (!dir.exists()) {
            return null;
        }
        File[] listFiles = dir.listFiles();
        if (listFiles == null || listFiles.length == 0) {
            return null;
        }
        File keyFile = listFiles[0];
        try {
            return new String(Files.readAllBytes(keyFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAdminPrivateKeyPath(String orgDomain) {
        return orgDomain + "/users/Admin@" + orgDomain + "/msp/keystore";
    }

    private JSONObject buildOrganizations() {
        JSONObject node = new JSONObject();

        for (Map.Entry<String, Map<String, List<FabricNode>>> entry : getChannelOrgNodesMap().entrySet()) {
            String channelId = entry.getKey();

            Map<String, List<FabricNode>> orgNodeMap = entry.getValue();
            for (Map.Entry<String, List<FabricNode>> orgEntry : orgNodeMap.entrySet()) {
                String orgDomain = orgEntry.getKey();
                List<FabricNode> orgNodeList = orgEntry.getValue();

                List<String> orgPeers = getPeers(orgNodeList);
                if (orgPeers.isEmpty()) {
                    // 没有peer
                    continue;
                }

                FabricOrg org = fabricOrgService.getByOrgDomain(orgDomain);
                JSONObject child = buildOrgNode(channelId, org);
                node.put(org.getOrgName(), child);
            }
        }

        return node;
    }


    private JSONObject buildChannels() {
        JSONObject objJson = new JSONObject();

        Map<String, Map<String, List<FabricNode>>> channelOrgNodesMap = getChannelOrgNodesMap();
        for (Map.Entry<String, Map<String, List<FabricNode>>> entry : channelOrgNodesMap.entrySet()) {
            String channelId = entry.getKey();

            objJson.put(channelId, buildChannel(channelId));
        }

        return objJson;
    }


    private List<String> getOrderers(List<FabricNode> orgNodeList) {
        List<FabricNode> orderNodeList = orgNodeList.stream()
                .filter(n -> n.getStatus().equals(NodeEnum.NodeStatus.ONLINE.getStatus()) &&
                        n.getNodeType().equals(NodeEnum.NodeType.ORDER.getNodeType()))
                .collect(Collectors.toList());
        return orderNodeList.stream().map(FabricNode::getNodeName).collect(Collectors.toList());
        //return IntStream.range(0, orgNodeMap.size()).mapToObj(i -> "orderer" + i).collect(Collectors.toList());
    }

    private List<String> getPeers(List<FabricNode> orgNodeList) {
        // org : peerNodes
        List<FabricNode> peerNodeList = orgNodeList.stream()
                .filter(n -> n.getStatus().equals(NodeEnum.NodeStatus.ONLINE.getStatus()) &&
                        n.getNodeType().equals(NodeEnum.NodeType.PEER.getNodeType()))
                .collect(Collectors.toList());

        return peerNodeList.stream().map(FabricNode::getNodeName).collect(Collectors.toList());
        //return IntStream.range(0, peers).mapToObj(i -> "peer" + i).collect(Collectors.toList());
    }

    private JSONObject buildChannel(String channelId) {

        JSONObject node = new JSONObject();
        JSONArray ordersNode = new JSONArray();
        JSONObject peersNode = new JSONObject();

        // org : nodeList
        Map<String, List<FabricNode>> orgNodeMap = getChannelOrgNodesMap().get(channelId);
        for (Map.Entry<String, List<FabricNode>> entry : orgNodeMap.entrySet()) {
            String orgDomain = entry.getKey();
            List<FabricNode> orgNodeList = entry.getValue();

            List<String> orderers = getOrderers(orgNodeList);
            for (int i = 0; i < orderers.size(); i++) {
                ordersNode.add(i, orderers.get(i));
            }

            for (String peerName : getPeers(orgNodeList)) {
                JSONObject content = new JSONObject();
                content.put("endorsingPeer", true);
                content.put("chaincodeQuery", true);
                content.put("ledgerQuery", true);
                content.put("eventSource", true);
                peersNode.put(peerName, content);
            }
        }

        node.put("orderers", ordersNode);
        node.put("peers", peersNode);

        // policies
        JSONObject policies = new JSONObject();
        node.put("policies", policies);

        return node;
    }

    private JSONObject buildClient() {
        FabricOrg usedOrg = null;
        for (Map.Entry<String, Map<String, List<FabricNode>>> entry : getChannelOrgNodesMap().entrySet()) {
            String channelId = entry.getKey();
            Map<String, List<FabricNode>> orgNodeMap = entry.getValue();
            for (Map.Entry<String, List<FabricNode>> orgEntry : orgNodeMap.entrySet()) {
                String orgDomain = orgEntry.getKey();
                List<FabricNode> orgNodeList = orgEntry.getValue();

                orgNodeList = orgNodeList.stream()
                        .filter(n -> n.getNodeType().equals(NodeEnum.NodeType.PEER.getNodeType()))
                        .collect(Collectors.toList());

                FabricOrg org = fabricOrgService.getByOrgDomain(orgDomain);

                if (org != null && !orgNodeList.isEmpty()) {
                    usedOrg = org;
                    break;
                }
            }
        }

        Assert.notNull(usedOrg, "没有找到合法的组织信息");

        JSONObject client = new JSONObject();
        JSONObject logging = new JSONObject();
        logging.put("level", "debug");
        client.put("logging", logging);

        JSONObject connection = new JSONObject();
        JSONObject timeout = new JSONObject();
        JSONObject peer = new JSONObject();
        peer.put("endorser", 30000);
        peer.put("eventHub", 30000);
        peer.put("eventReg", 30000);
        timeout.put("peer", peer);
        timeout.put("orderer", 30000);

        connection.put("timeout", timeout);
        client.put("connection", connection);

        // Current org client
        client.put("organization", usedOrg.getOrgName());

        return client;
    }

}
