package org.phial.baas.chainmaker.domain;


import org.phial.baas.chainmaker.deploy.NodeYamlUtil;
import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.domain.entity.Node;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class CANodeDomain extends NodeDomain {

    @Resource
    private NodeYamlUtil nodeYamlUtil;

    public void createDefaultAdmin() {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        String orgDomain = getChainmakerOrg().getDomain();
//        String fullUserId = Crypto.getFullUserId(DefaultAdminConfig.DEFAULT_ADMIN_PREFIX, orgDomain);
//        Crypto defaultAdmin = cryptoFactory.selectSingleCryptoInfo(orgDomain, fullUserId);
//
//        if (defaultAdmin == null) {
//            cryptoFactory.createCrypto(this, DefaultAdminConfig.DEFAULT_ADMIN_PREFIX, CryptoEnum.CryptoUserType.ADMIN);
//        }
    }

    public String createRevokeCrl(String chainId, Long revokedCertSn) {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        OrganizationServerInfo orgInfo = ClientCachePool.getInstance().getDefaultAdmin(chainId, this.getChainmakerOrg().getDomain());
//        return cryptoFactory.createRevokeCrl(this, revokedCertSn, orgInfo.getCaSn());
        return null;
    }



    public void createNodeCrypto(ChainMakerNodeDomain targetNodeDomain) {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        ChainmakerNode targetNode = targetNodeDomain.getChainmakerNode();
//        CryptoEnum.CryptoUserType userType = CryptoEnum.CryptoUserType.getCryptoUserType(targetNode.getNodeType());
//        String prefixName = getNamePrefix(targetNode.getNodeName());
//
//        //创建节点证书
//        Crypto crypto = cryptoFactory.createCrypto(this, prefixName, userType);
//
//        targetNodeDomain.setCrypto(crypto);
    }

    public Crypto createUserCrypto(String userId, CryptoEnum.CryptoUserType userType) {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        //创建节点证书
//        return cryptoFactory.createCrypto(this, userId, userType);
        return null;
    }



    public void deployNode() {

        Node chainmakerNode = getChainmakerNode();
        //没启动过或ca pod状态不是Running才部署
        if (!checkPodReady()) {
            Map<String, String> configYaml = nodeYamlUtil.createCAConfigYaml(getChainmakerOrg(), chainmakerNode);
            deployed(configYaml);
        }
    }


    public Map<String, String> createTrustRootCertFile(){
        Crypto crypto = getCrypto();
        return crypto.createTrustRootCertFile();
    }

}
