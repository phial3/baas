package org.phial.baas.fabric.invoke;

import com.ibm.cloud.cloudant.v1.model.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.phial.baas.fabric.client.ChannelClient;
import org.phial.baas.service.constant.CommonConstant;
import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.service.ChainNodeService;
import org.phial.baas.service.service.ChainService;
import org.phial.baas.service.service.NodeService;
import org.phial.baas.service.service.OrganizationService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ChannelService {

    //    orderer
    public static final String ORDERER_PEMFILE = "crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt";
    public static final String ORDERER_CERT = "crypto-config/ordererOrganizations/example.com/users/Admin@example.com/tls/client.crt";
    public static final String ORDERER_KEY = "crypto-config/ordererOrganizations/example.com/users/Admin@example.com/tls/client.key";
    public static final String ORDER_NAME = "orderer.example.com";

    //    peer0.org1
    public static final String PEER1_PEMFILE = "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt";
    public static final String PEER1_CERT = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/client.crt";
    public static final String PEER1_KEY = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/client.key";
    public static final String PEER1_NAME = "peer0.org1.example.com";

    //    peer0.org2
    public static final String PEER2_PEMFILE = "crypto-config/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/server.crt";
    public static final String PEER2_CERT = "crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/tls/client.crt";
    public static final String PEER2_KEY = "crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/tls/client.key";
    public static final String PEER2_NAME = "peer0.org2.example.com";

    public static final String channelConfigFilePath = "/tmp/channel-artifacts/";

    private static final String FABRIC_EXECUTOR_PATH = "/Users/admin/Workspace/golang/src/github.com/fabric/scripts/hyperledger-fabric-darwin-amd64-2.4.7/bin";
    public static final String configtxCommandPath = FABRIC_EXECUTOR_PATH + "/cryptogen";

    private static final String CHANNEL_CONFIG_PROFILE = "ThreeOrgChannel";

    @Resource
    private ChainService chainService;

    @Resource
    private NodeService nodeService;

    @Resource
    private ChainNodeService chainNodeService;

    @Resource
    private OrganizationService organizationService;

    @Resource
    private ChannelClientHolder channelClientHolder;

    public void createChannel() {
        try {
            // 创建org1的用户管理员对象的创建
            UserContext org1Admin = new UserContext();
            // Admin@org1 秘钥地址
            String pkPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore";
            File pkFile1 = new File(pkPath);
            File[] pkFiles1 = pkFile1.listFiles();
            // Admin@org1  证书地址
            String certPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts";
            File certFile1 = new File(certPath);
            File[] certFiles1 = certFile1.listFiles();

            // 进行证书和秘钥的登记
            Enrollment enrollment = getEnrollment(pkPath, pkFiles1[0].getName(), certPath, certFiles1[0].getName());
            org1Admin.setEnrollment(enrollment);
            org1Admin.setName("admin");
            org1Admin.setMspId("Org1MSP");
            org1Admin.setAffiliation("org1");
            org1Admin.setAccount(null);
            org1Admin.setRoles(null);

            // org2的用户管理对象的创建
            UserContext org2Admin = new UserContext();
            String pkPath2 = "crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/keystore";
            File pkFile2 = new File(pkPath2);
            File[] pkFiles2 = pkFile2.listFiles();
            String certPath2 = "crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/signcerts";
            File certFile2 = new File(certPath2);
            File[] certFiles2 = certFile2.listFiles();

            Enrollment enrollment1 = getEnrollment(pkPath2, pkFiles2[0].getName(), certPath2, certFiles2[0].getName());
            org2Admin.setEnrollment(enrollment1);
            org2Admin.setName("user2");
            org2Admin.setMspId("Org2MSP");
            org2Admin.setAffiliation("org2");

            FabricClient fabricClient = new FabricClient(org1Admin);

            // properties配置的信息导入
            Properties orderPro = loadTLSFile(ORDERER_PEMFILE, ORDERER_CERT, ORDERER_KEY, ORDER_NAME, "OrdererMSP");

            // 通过HFClient对象新建一个orderer对象,
            Orderer orderer = fabricClient.getInstance().newOrderer("orderer.example.com", "grpcs://orderer.example.com:7050", orderPro);

            // 创建通道配置对象,参数为创建的.tx通道文件
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File("channel1.tx"));
            byte[] channelConfigurationSignatures = fabricClient.getInstance().getChannelConfigurationSignature(channelConfiguration, org1Admin);

            // 新建一个channel
            Channel channel = fabricClient.getInstance().newChannel("channel1", orderer, channelConfiguration, channelConfigurationSignatures);
            for (Peer peer : channel.getPeers()) {
                System.out.println(peer.getName());
            }

            // 加载配置
            Properties peer1Pro = loadTLSFile(PEER1_PEMFILE, PEER1_CERT, PEER1_KEY, PEER1_NAME, "Org1MSP");
            //新建peer1
            Peer peer0_Org1 = fabricClient.getInstance().newPeer(PEER1_NAME, "grpcs://192.168.127.130:7051", peer1Pro);

            // 新建peer2
            Properties peer2Pro = loadTLSFile(PEER2_PEMFILE, PEER2_CERT, PEER2_KEY, PEER2_NAME, "Org2MSP");
            Peer peer0_Org2 = fabricClient.getInstance().newPeer(PEER2_NAME, "grpcs://192.168.127.130:9051", peer2Pro);

            // 将peer加入到channel中
            channel.joinPeer(peer0_Org1, Channel.PeerOptions.createPeerOptions().setPeerRoles(EnumSet.of(Peer.PeerRole.ENDORSING_PEER, Peer.PeerRole.LEDGER_QUERY, Peer.PeerRole.CHAINCODE_QUERY, Peer.PeerRole.EVENT_SOURCE)));
            channel.addOrderer(orderer);
            // 进行判断,是否加入成功
            Assert.isTrue(channel.getPeers(EnumSet.of(Peer.PeerRole.EVENT_SOURCE)).size() > 0, "join channel error");
            Assert.isTrue(channel.getPeers(Peer.PeerRole.NO_EVENT_SOURCE).isEmpty(), "");

            // 进行初始化通道
            channel.initialize();
            log.info("Channel created " + channel.getName());

            // 切换为org2的用户进行上述操作,添加peer
            fabricClient.getInstance().setUserContext(org2Admin);
            channel = fabricClient.getInstance().getChannel("channel1");
            channel.joinPeer(peer0_Org2, Channel.PeerOptions.createPeerOptions().setPeerRoles(EnumSet.of(Peer.PeerRole.ENDORSING_PEER, Peer.PeerRole.LEDGER_QUERY, Peer.PeerRole.CHAINCODE_QUERY, Peer.PeerRole.EVENT_SOURCE)));
            Iterator<Peer> iterator = channel.getPeers().iterator();
            Assert.isTrue(channel.getPeers(EnumSet.of(Peer.PeerRole.EVENT_SOURCE)).size() > 0, "");
            Assert.isTrue(channel.getPeers(Peer.PeerRole.NO_EVENT_SOURCE).size() > 0, "");

            channel.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将配置项导入到配置pro中
    private static Properties loadTLSFile(String servicePath, String certPath, String keyPath, String hostName, String mspId) throws IOException {
        Properties properties = new Properties();
        // 其实只需要一个TLS根证书就可以了，比如TLS相关的秘钥等都是可选的
        properties.put("pemBytes", Files.readAllBytes(Paths.get(servicePath)));
        properties.setProperty("clientCertFile", certPath);
        properties.setProperty("clientKeyFile", keyPath);
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");
        properties.setProperty("trustServerCertificate", "true");
        properties.setProperty("hostnameOverride", hostName);
        properties.setProperty("ssl-target-name-override", hostName);
        if (hostName.contains("peer")) {
            properties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
            properties.setProperty("org.hyperledger.fabric.sdk.peer.organization_mspid", mspId);
        }
        if (hostName.contains("orderer")) {
            properties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[]{5L, TimeUnit.MINUTES});
            properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{8L, TimeUnit.SECONDS});
            properties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[]{true});
            properties.setProperty("org.hyperledger.fabric.sdk.orderer.organization_mspid", mspId);
        }
        return properties;
    }

    // 证书和秘钥的登记过程
    public static CAEnrollment getEnrollment(String keyFolderPath, String keyFileName, String certFolderPath, String certFileName)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey key = null;
        String certificate = null;
        InputStream isKey = null;
        BufferedReader brKey = null;
        try {
            isKey = Files.newInputStream(Paths.get(keyFolderPath + File.separator + keyFileName));
            brKey = new BufferedReader(new InputStreamReader(isKey));
            StringBuilder keyBuilder = new StringBuilder();
            for (String line = brKey.readLine(); line != null; line = brKey.readLine()) {
                if (!line.contains("PRIVATE")) {
                    keyBuilder.append(line);
                }
            }
            certificate = new String(Files.readAllBytes(Paths.get(certFolderPath, certFileName)));
            byte[] encoded = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");
            key = kf.generatePrivate(keySpec);
        } finally {
            isKey.close();
            brKey.close();
        }
        return new CAEnrollment(key, certificate);
    }


    /////////////////////////////////////////
    /////////////////////////////////////////
    public Channel createChannel(final String newChannelId) throws Exception {
        List<FabricChainNode> chainNodes = fabricChainNodeService.getByChainId(newChannelId);
        Assert.notEmpty(chainNodes, "没有可用的节点");
        List<Long> NodeIds = chainNodes.stream().map(FabricChainNode::getNodeId).collect(Collectors.toList());

        Map<String, ChannelClient> channelClientMap = NetworkConfigCache.getInstance().getChannelClientMap();
        Assert.notNull(channelClientMap, "createChannel channelClient should not be null");

        ChannelClient channelClient = null;
        for (Map.Entry<String, ChannelClient> entry : channelClientMap.entrySet()) {
            channelClient = entry.getValue();
            break;
        }
        Assert.notNull(channelClient, "channelClient should not be null");
        log.info("createChannel client channelId={}", channelClient.getChannelId());
        HFClient client = channelClient.getClient().getInstance();

        List<FabricNode> orgNodeList = fabricNodeService.getByIds(NodeIds);
        List<FabricNode> orderNodeList = orgNodeList.stream()
                .filter(o -> o.getNodeType().equals(NodeEnum.NodeType.ORDER.getNodeType()))
                .collect(Collectors.toList());
        List<FabricNode> peerNodeList = orgNodeList.stream()
                .filter(o -> o.getNodeType().equals(NodeEnum.NodeType.PEER.getNodeType()))
                .collect(Collectors.toList());
        Assert.notEmpty(orderNodeList, "没有找到order节点");

        FabricNode orderNode = orderNodeList.get(0);

        FabricOrg organization = fabricOrgService.getByOrgDomain(orderNode.getOrgDomain());
        //UserContext userContext = ChannelUtil.getAdminUserContext(organization);
        FabricUser fabricUser = ClientCachePool.getInstance().selectFabricUser("admin." + organization.getDomain(), CryptoEnum.CryptoUserType.ADMIN);


        // TODO:
        Orderer orderer = client.newOrderer(orderNode.getNodeName(), CommonConstant.getNodeGrpcUrl(orderNode.getRpcK8s()));
        ChannelConfiguration channelConfiguration = createChannelConfiguration(newChannelId);
        Channel newChannel = client.newChannel(
                newChannelId,
                orderer,
                channelConfiguration,
                client.getChannelConfigurationSignature(channelConfiguration, fabricUser.parseUserContext()));

        joinChannel(client, peerNodeList, newChannel);

        return newChannel;
    }

    private Channel joinChannel(HFClient client, List<FabricNode> peerNodeList, Channel channel) throws Exception {
        for (FabricNode peerNode : peerNodeList) {
            Peer peer = client.newPeer(peerNode.getNodeName(), CommonConstant.getNodeGrpcUrl(peerNode.getRpcK8s()));
            //Default is all roles.
            return channel.joinPeer(peer, Channel.PeerOptions.createPeerOptions());
        }

        channel.initialize();

        return channel;
    }

    public Channel getChannel(String orgName, String channelId) throws Exception {
        ChannelClient channelClient = fabricChannelGatewayHolder.getChannelClient(channelId);
        Assert.notNull(channelClient, "channelId=[" + channelId + "] channelClient 不存在！");

        HFClient client = channelClient.getClient().getInstance();
        // TODO:
        Peer peer = client.newPeer("peerName", "peerUrl");

        if (client.queryChannels(peer).contains(channelId)) {
            Channel channel = client.getChannel(channelId);
            if (channel == null) {
                channel = client.newChannel(channelId);
            }
            channel.addPeer(peer);
            channel.initialize();
            return channel;
        } else {
            throw new IllegalAccessException("Organization [" + orgName + "] does not have access to property [" + channelId + "]");
        }
    }

    private ChannelConfiguration createChannelConfiguration(String channelId) throws Exception {
        try {
            String channelConfigurationOutputPath = channelConfigFilePath + File.separator + channelId + ".tx";

            String command = configtxCommandPath + " -profile " + CHANNEL_CONFIG_PROFILE +
                    " -outputCreateChannelTx " + channelConfigurationOutputPath +
                    " -channelID " + channelId;

            log.info("createChannelConfiguration channelId={}, command={}", channelId, command);
            executeCommand(command);

            return new ChannelConfiguration(new File(channelConfigurationOutputPath));
        } catch (IOException | InvalidArgumentException e) {
            e.printStackTrace();
            throw e;
        }
    }


    public Channel joinPeer(String channelId) throws Exception {
        ChannelClient channelClient = fabricChannelGatewayHolder.getChannelClient(channelId);
        Assert.notNull(channelClient, "channelId=[" + channelId + "] channelClient 不存在！");

        Channel channel = channelClient.getChannel();
        List<FabricChainNode> chainNodes = fabricChainNodeService.getByChainId(channelId);
        List<Long> nodeIds = chainNodes.stream().map(FabricChainNode::getNodeId).collect(Collectors.toList());
        List<FabricNode> nodeList = fabricNodeService.getByIds(nodeIds);
        Assert.notEmpty(nodeList, "没有找到节点,channelId="+channelId);

        List<FabricNode> peerNodeList = nodeList.stream()
                .filter(n -> n.getNodeType().equals(NodeEnum.NodeType.PEER.getNodeType()))
                .collect(Collectors.toList());

        return joinChannel(channelClient.getClient().getInstance(), peerNodeList, channel);
    }


    private String executeCommand(String command) {
        Runtime rt = Runtime.getRuntime();
        StringBuilder response = new StringBuilder();
        try {
            Process process = rt.exec(new String[]{"bash", "-c", command});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            int exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
