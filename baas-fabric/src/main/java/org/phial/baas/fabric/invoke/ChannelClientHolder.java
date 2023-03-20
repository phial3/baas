package org.phial.baas.fabric.invoke;


import lombok.extern.slf4j.Slf4j;
import org.phial.baas.fabric.client.ChannelClient;
import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.domain.entity.ChainNode;
import org.phial.baas.service.service.ChainNodeService;
import org.phial.baas.service.service.ChainService;
import org.phial.baas.service.service.NodeService;
import org.phial.baas.service.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChannelClientHolder {
    @Resource
    private ChainService chainService;

    @Resource
    private ChainNodeService chainNodeService;

    @Resource
    private NodeService nodeService;

    @Resource
    private OrganizationService organizationService;

    public void initNetworkConfig(final String source) {
        try {
            List<FabricChain> chainList = fabricChainService.listAllByCondition(FabricChain.builder()
                    .isuse(1)
                    .networkStatus("ONLINE")
                    .build()
            );

            for (FabricChain chain : chainList) {

                Map<String, ChannelClient> channelClientMap = NetworkConfigCache.getInstance().getChannelClientMap();
                if (channelClientMap != null && channelClientMap.containsKey(chain.getName())) {
                    continue;
                }

                String channelId = chain.getName();

                List<FabricChainNode> chainNodeList = fabricChainNodeService.getByChainId(channelId);
                List<Long> nodeIds = chainNodeList.stream()
                        .map(FabricChainNode::getNodeId)
                        .collect(Collectors.toList());
                List<FabricNode> nodeList = fabricNodeService.getByIds(nodeIds);
                List<FabricNode> peerNodeList = nodeList.stream()
                        .filter(n -> n.getNodeType().equals(NodeEnum.NodeType.PEER.getNodeType()))
                        .collect(Collectors.toList());
                if (peerNodeList.isEmpty()) {
                    continue;
                }

                FabricOrg peerNodeOrg = fabricOrgService.getByOrgDomain(peerNodeList.get(0).getOrgDomain());
                ChannelClient client = createChannelClient(channelId, peerNodeOrg);
                if (client == null) {
                    logger.error("FabricChannelGatewayHolder createChannelClient error! source:{}, channelId={}, orgDomain={} ", source, channelId, peerNodeOrg.getDomain());
                    continue;
                }
                NetworkConfigCache.getInstance().addChannelClient(channelId, client);
                logger.info("FabricChannelGatewayHolder create ChannelClient source:{}, channelId={}, orgDomain={} ", source, channelId, peerNodeOrg.getDomain());
            }
        } catch (Exception e) {
            logger.error("FabricChannelGatewayHolder initNetworkConfig() source:{}, error:{}", source, e.getMessage(), e);
        }
    }


    public ChannelClient getChannelClient(String channelId) {
        return NetworkConfigCache.getInstance().getChannelClientMap().get(channelId);
    }

    private ChannelClient createChannelClient(String channelId, FabricOrg peerNodeOrg) {
        ChannelClient client = null;
        try {
            String mspId = CommonConstant.getOrgMspID(peerNodeOrg.getOrgName());
            FabricUser adminUser = ClientCachePool.getInstance().selectFabricUser(CommonConstant.getAdminCaId(peerNodeOrg.getDomain()), CryptoEnum.CryptoUserType.CLIENT_BUSINESS_USER);
            if (adminUser == null) {
                logger.warn("channelGatewayHolder createChannelClient adminUser failed! channelId={} org={}", channelId, peerNodeOrg.getOrgName());
                return null;
            }

            client = new ChannelClient(channelId, mspId, adminUser.getCert(), adminUser.getPrivateKey());
        } catch (Exception e) {
            logger.error("FabricChannelGatewayHolder create ChannelClient error! channelId={}, organization={}", channelId, peerNodeOrg, e);
        }
        return client;
    }

    public ChannelClient addChannelClient(String channelId, FabricOrg org) {
        ChannelClient client = createChannelClient(channelId, org);
        if (client != null) {
            NetworkConfigCache.getInstance().addChannelClient(channelId, client);
        }
        return client;
    }

    public ChannelClient removeChannelClient(String channelId) {
        return NetworkConfigCache.getInstance().getChannelClientMap().remove(channelId);
    }
}