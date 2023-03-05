package org.phial.baas.fabric.entity;

import org.phial.baas.api.constant.CommonConstant;
import org.phial.baas.api.constant.CommonFabricConstant;
import org.phial.baas.api.constant.CryptoEnum;
import org.phial.baas.fabric.deploy.ConfigMapBatch;
import org.phial.baas.fabric.deploy.CryptogenYamlUtil;
import org.phial.baas.fabric.deploy.NodeYamlUtil;
import org.phial.baas.fabric.factory.BaasFabricApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CANodeDomain extends NodeDomain {
    private final Map<String, ConfigMapBatch> configMapBatch = new HashMap<>();

    public void createRegister() {
        String registerCaId = getFabricOrg().getRegisterId();

//        FabricUser register = ClientCachePool.getInstance().selectFabricUser(registerCaId, CryptoEnum.CryptoUserType.ADMIN);
//        if (register != null) {
//            return;
//        }
//
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        register = cryptoFactory.createRegister(this);
//
//        UserManagerFactory userFactory = UserManagerFactory.getInstance();
//        userFactory.createBusinessUser(register, registerCaId);

    }

//    public String createRevokeCrl(String caId, CryptoEnum.CryptoUserType userType) {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        return cryptoFactory.revokeCrypto(this, caId, userType);
//    }



//    public void createNodeCrypto(ChainMakerNodeDomain targetNodeDomain) {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        ChainmakerNode targetNode = targetNodeDomain.getChainmakerNode();
//        CryptoEnum.CryptoUserType userType = CryptoEnum.CryptoUserType.getCryptoUserType(targetNode.getNodeType());
//        String prefixName = getNamePrefix(targetNode.getNodeName());
//
//        //创建节点证书
//        Crypto crypto = cryptoFactory.createCrypto(this, prefixName, userType);
//
//        targetNodeDomain.setCrypto(crypto);
//    }

//    public FabricUser createUserCrypto(String userId, Map<String, Object> attr, CryptoEnum.CryptoUserType userType) {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        //创建节点证书
//        return cryptoFactory.createCrypto(this, userId, attr, userType);
//    }

//    public String revokeUserCrypto() {
//
//    }

    @Override
    public String getVolumeMountPath() {
        return "/etc/hyperledger/fabric-ca-server/";
    }

    @Override
    public String getNodePath() {
        String nodePath = CryptogenYamlUtil.getTypePath(getFabricNode().getType().getType());
        if (!localSaved) {
            //writeFileToLocal(nodePath);
            localSaved = true;
        }
        return nodePath + getFabricOrg().getDomain() + "/ca/";
    }

    public String getNodePath(String dir) {
        String nodePath = CryptogenYamlUtil.getTypePath(getFabricNode().getType().getType());
        if (!localSaved) {
            //writeFileToLocal(nodePath);
            localSaved = true;
        }
        return nodePath + getFabricOrg().getDomain() + "/" + dir + "/";
    }

    @Override
    protected void getExtraConfigFiles(ConfigMapBatch configMapBatch) {

        NodeYamlUtil nodeYamlUtil = BaasFabricApplicationContext.getBean(NodeYamlUtil.class);
        Map<String, String> dataMap = new HashMap<>(nodeYamlUtil.generateCaConfigYaml(this));

        for (Map.Entry<String, String> entry: dataMap.entrySet()) {
            configMapBatch.addExtraFile("/etc/hyperledger/fabric-ca-server/", entry.getKey(), entry.getValue().getBytes());
        }
    }

    @Override
    public Properties getProperties() {
        throw new RuntimeException("CANodeDomain 没有Properties");
    }


    @Override
    public String getUrl() {
        return getFabricNode().getNodeHttpUrl();
    }

    public void deployNode() {
        //没启动过才部署
//        ConfigMapBatch configFiles = getConfigFiles();
//        if (!checkPodReady()) {
//            deployed(configFiles);
//        }
    }

    public String getMSPDirPath() {
        String nodePath = CryptogenYamlUtil.getNodePath(getFabricNode());
        if (!localSaved) {
            //writeFileToLocal(nodePath);
            localSaved = true;
        }
        return nodePath + getFabricOrg().getDomain() + "/msp/";
    }



    public ConfigMapBatch getConfigFiles(String dir) {
        if (configMapBatch.get(dir) != null) {
            return configMapBatch.get(dir);
        }
        ConfigMapBatch configMapBatch = ConfigMapBatch.newBatch(getDnsName(), getFabricOrg().getMspID())
                .addConfigMapBatch(getNodePath(dir), getVolumeMountPath());
        getExtraConfigFiles(configMapBatch);
        this.configMapBatch.put(dir, configMapBatch);
        return configMapBatch;
    }

}
