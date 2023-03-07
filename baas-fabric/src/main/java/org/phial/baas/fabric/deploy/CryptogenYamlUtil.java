package org.phial.baas.fabric.deploy;

import lombok.extern.slf4j.Slf4j;
import org.phial.baas.api.constant.CommonFabricConstant;
import org.phial.baas.api.constant.CryptoEnum;
import org.phial.baas.api.domain.Node;
import org.phial.baas.api.util.FileUtil;
import org.phial.baas.fabric.entity.NodeDomain;

import java.util.List;


@Slf4j
public class CryptogenYamlUtil {

    private static final String cryptogenPath = ""; //CommonConstant.FABRIC_EXECUTE_COMMAND_PATH + "cryptogen";
    private static final String configtxgenPath = ""; // CommonConstant.FABRIC_EXECUTE_COMMAND_PATH + "configtxgen";

//    private static boolean writeCertToLocal(String dnsName, String prePath, String subPath, CryptoEnum.CryptoUserType userType, NewCertBatch newFiles) {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        List<FabricCert> fabricCerts = cryptoFactory.getFabricCertMapper().selectCert(dnsName);
//        if (CollectionUtils.isEmpty(fabricCerts)) {
//            switch (userType) {
//                case CA:
//                    newFiles.addNewFile(dnsName, prePath, subPath + "ca/");
//                    newFiles.addNewFile(dnsName, prePath, subPath + "msp/");
//                    newFiles.addNewFile(dnsName, prePath, subPath + "tlsca/");
//                    newFiles.addNewFile(dnsName, prePath, subPath + "users/");
//                    break;
//                default:
//                    newFiles.addNewFile(dnsName, prePath, subPath);
//            }
//            return false;
//        }
//        for (FabricCert cert : fabricCerts) {
//            FileUtil.writeFile(cert.getContent(), prePath + cert.getPath());
//        }
//        return true;
//    }


//    public static boolean fetchYaml(CryptogenYaml cryptogenYaml, CryptogenBatch cryptogenBatch, NewCertBatch newFiles) {
//        FabricOrg nodeOrg = cryptogenBatch.getFabricOrg();
//        String type = cryptogenBatch.getType().getMemberType();
//
//        String basePath = getTypePath(cryptogenBatch.getType().getUserType());
//
//        //如果org文件夹存在说明需要extend
//        boolean isGenerate = true;
//        //写ca证书，如果库里没有加入newFiles
//        if (writeCertToLocal(nodeOrg.getCaDnsName(), basePath, nodeOrg.getDomain() + "/", CryptoEnum.CryptoUserType.CA, newFiles)) {
//            isGenerate = false;
//        }
//
//
//        //用dnsName查库判断是否存在
//        List<String> needCreateNodes = new ArrayList<>();
//        for (NodeDomain nodeDomain : cryptogenBatch.getNodeDomainList()) {
//            String dnsName = nodeDomain.getDnsName();
//            if (!writeCertToLocal(dnsName, basePath, nodeOrg.getDomain() + "/" + type + "s/" + nodeDomain.getFabricNode().getNodeName() + "/", cryptogenBatch.getType(), newFiles)) {
//                needCreateNodes.add(dnsName);
//            }
//        }
//
//        //全都存在
//        if (CollectionUtils.isEmpty(needCreateNodes)) {
//            return isGenerate;
//        }
//
//        if (CryptoEnum.CryptoUserType.PEER.equals(cryptogenBatch.getType())) {
//            cryptogenYaml.setPeerOrg(nodeOrg.getDomain(), nodeOrg.getLocality(), nodeOrg.getCountry(), nodeOrg.getProvince());
//            cryptogenYaml.addPeer(nodeOrg.getDomain(), needCreateNodes);
//        } else {
//            cryptogenYaml.setOrdererOrg(nodeOrg.getDomain(), nodeOrg.getLocality(), nodeOrg.getCountry(), nodeOrg.getProvince());
//            cryptogenYaml.addOrder(nodeOrg.getDomain(), needCreateNodes);
//        }
//        return isGenerate;
//    }


//    private static boolean certExist(String dnsName, String basePath, List<FabricCert> newCerts) {
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        if (!cryptoFactory.certExist(dnsName)) {
//            newCerts.add(new FabricCert(dnsName, basePath));
//            return false;
//        }
//        return true;
//    }


//    public static void generateCrypto(List<CryptogenBatch> cryptogenBatches) {
//        boolean isGenerate = true;
//        NewCertBatch newCertBatch = new NewCertBatch();
//
//        CryptogenYaml cryptogenYaml = new CryptogenYaml();
//        for (CryptogenBatch cryptogenBatch : cryptogenBatches) {
//            isGenerate = fetchYaml(cryptogenYaml, cryptogenBatch, newCertBatch) && isGenerate;
//        }
//
//        //不为空
//        if (!cryptogenYaml.hasItem()) {
//            logger.warn("generateCrypto cryptogen Yaml have no 'PeerOrgs' and  'OrdererOrgs'");
//            return;
//        }
//
//        //config写临时文件
//        String yaml = YamlUtil.toYaml(cryptogenYaml.getYaml());
//        String yamlName = CommonFabricConstant.UNIX_TMP_DIR + "/config" + System.currentTimeMillis() + "/config.yaml";
//        logger.info("generateCrypto writeFile yamlConfig:{}, yamlName:{}", yaml, yamlName);
//        FileUtil.writeFile(yaml, yamlName);
//
//        //判断generate还是extend
//        String option, param;
//        if (isGenerate) {
//            option = "generate";
//            param = "--output";
//        } else {
//            option = "extend";
//            param = "--input";
//        }
//
//        //生成证书
//        String cryptoPath = getCryptoPath();
//        String command = cryptogenPath + " " + option + " --config=" + yamlName + " " + param + "=" + cryptoPath;
//        logger.info("generateCrypto command:{}", command);
//        FileUtil.execute(command);
//
//        //往mysql写证书
//        //newCertBatch.saveNewFile();
//    }


    public static void generateGenesisBlock(List<NodeDomain> nodes, String channelName) {

        String configtx = GenesisBlockYamlUtil.generateGenesisYaml(nodes);
        //config写临时文件
        String configPath = CommonFabricConstant.UNIX_TMP_DIR + "/configtx" + System.currentTimeMillis();
        String yamlName = configPath + "/configtx.yaml";
        FileUtil.writeFile(configtx, yamlName);

        String command = configtxgenPath + " -profile GenesisBlock -outputBlock " + CommonFabricConstant.getGenesisBlockPath(channelName) + channelName + ".block" + " -configPath=" + configPath + " -channelID " + channelName;
        log.info("generateGenesisBlock command:{}", command);
        FileUtil.execute(command);
    }


    //示例
    //xxx/baidu/crypto-config/
    public static String getCryptoPath() {
        return CommonFabricConstant.DATA_PATH + CommonFabricConstant.PROFILE + "/crypto-config/";
    }

    //示例
    //xxx/baidu/crypto-config/peerOrganizations/
    //xxx/baidu/crypto-config/ordererOrganizations/
    public static String getTypePath(String type) {
        return getCryptoPath() + CryptoEnum.CryptoUserType.getCryptoUserType(type).getMemberType() + "Organizations/";
    }


    //示例
    //xxx/baidu/crypto-config/peerOrganizations/www.hlzh.com/
    public static String getOrgPath(String type, String domain) {
        return getTypePath(type) + domain + "/";
    }


    //示例
    //xxx/baidu/crypto-config/peerOrganizations/www.hlzh.com/peers/peer0-www-hlzh-com/
    //xxx/baidu/crypto-config/ordererOrganizations/www.hlzh.com/orderers/peer0-www-hlzh-com/
    public static String getNodePath(Node node) {
        return getOrgPath(node.getType().getType(), node.getOrgDomain())
                + CryptoEnum.CryptoUserType.getCryptoUserType(node.getType().getType()).getMemberType() + "s/" + node.getDnsName() + "/";
    }
}
