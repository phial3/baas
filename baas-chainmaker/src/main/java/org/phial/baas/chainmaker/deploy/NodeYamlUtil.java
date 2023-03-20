package org.phial.baas.chainmaker.deploy;

import org.phial.baas.chainmaker.deploy.yaml.CAConfigYaml;
import org.phial.baas.chainmaker.deploy.yaml.ChainmakerConfigYaml;
import org.phial.baas.chainmaker.deploy.yaml.ChainmakerGenesisYaml;
import org.phial.baas.chainmaker.deploy.yaml.ChainmakerLogYaml;
import org.phial.baas.chainmaker.domain.ChainMakerNodeDomain;
import org.phial.baas.service.constant.CommonChainmakerConstant;
import org.phial.baas.service.domain.entity.Node;
import org.phial.baas.service.domain.entity.Organization;
import org.phial.baas.service.util.YamlUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NodeYamlUtil {

    @Resource
    private String dbType;
    @Resource
    private String dbUserName;
    @Resource
    private String dbPassword;
    @Resource
    private String dbIpHost;
    @Resource
    private String dbPort;
    @Resource
    private String dbName;


    public Map<String, String> generateChainYaml(String chainId, Map<String, List<String>> orgDomainNodeIds) {

        Map<String, String> result = new HashMap<>();

        // auth_type: permissionedWithCert,permissionedWithKey,public
        // consensus_type: 0-SOLO, 1-TBFT, 3-MAXBFT, 4-RAFT, 5-DPOS, 6-ABFT
        ChainmakerGenesisYaml yaml = new ChainmakerGenesisYaml("permissionedWithCert", chainId, "SM3", "v2.3.1", 0, 1, false);

        yaml.setBlock(100, 10, 200, 600, true);

        yaml.setCore(10, 10);

        yaml.setVm();

        //加入这条链的组织
        for (Map.Entry<String, List<String>> entry : orgDomainNodeIds.entrySet()) {

            String orgDomain = entry.getKey();
            List<String> nodeIds = entry.getValue();

            yaml.addConsensusNode(orgDomain, nodeIds);

            yaml.addTrustRoot(orgDomain, Collections.singletonList("../config/certs/ca/" + orgDomain + "-ca.crt"));
        }

        result.put(chainId + ".yml", YamlUtil.toYaml(yaml.getYaml()));

        return result;
    }

    public List<String> selectGenerateInitialOrgs(String yaml) {
        List<String> result = new LinkedList<>();

        Map<String, Object> load = YamlUtil.toObject(yaml, Map.class);
        ArrayList<Object> nodes = (ArrayList<Object>) ((Map<String, Object>) load.get("consensus")).get("nodes");
        for (Object node : nodes) {
            String orgId = (String) ((Map<String, Object>) node).get("org_id");
            result.add(orgId);
        }
        return result;
    }


    public Map<String, String> generateChainmakerConfigYaml(Node preparedNode,
                                                            List<ChainMakerNodeDomain> otherNodes,
                                                            List<String> chainIds,
                                                            String nodeCertFileName,
                                                            String nodeKeyFileName,
                                                            long rebuildHeight) {

        Map<String, String> result = new HashMap<>();
        String orgDomain = preparedNode.getOrgDomain();

        ChainmakerConfigYaml chainmakerConfig = new ChainmakerConfigYaml("permissionedWithCert");
        chainmakerConfig.setCryptoEngine("tencentsm");
        chainmakerConfig.setLog("../config/log.yml");

        chainIds = chainIds.stream().distinct().collect(Collectors.toList());
        for (String chainId : chainIds) {
            chainmakerConfig.addBlockChain(chainId, "../config/chainconfig/" + chainId + ".yml");
        }

        chainmakerConfig.setNode(orgDomain, 1000,
                "../config/certs/node/" + nodeCertFileName,
                "../config/certs/node/" + nodeKeyFileName);

        chainmakerConfig.setNet("/ip4/0.0.0.0/tcp/" + CommonChainmakerConstant.DEFAULT_CHAINMAKER_P2P_PORT,
                "LibP2P", true, "../config/certs/node/" + nodeCertFileName,
                "../config/certs/node/" + nodeKeyFileName, 100, 100);

        for (ChainMakerNodeDomain nodeCrypto : otherNodes) {
            if (nodeCrypto.getCrypto() == null) {
                //nodeCrypto.selectNodeCrypto();
            }
            Node node = nodeCrypto.getChainmakerNode();
            chainmakerConfig.addSeed("/dns/" + node.getDnsName() + "/tcp/" +
                    CommonChainmakerConstant.DEFAULT_CHAINMAKER_P2P_PORT + "/p2p/" + nodeCrypto.getCrypto().getNodeId());

            // int port = 0;
            //chainmakerConfig.addSeed("/ip4/172.24.86.83/tcp/" + port + "/p2p/" + nodeCrypto.getCrypto().getNodeId());
        }

        //TxPool
        chainmakerConfig.setTxpool("single", 100, 100000);

        //Rpc
        chainmakerConfig.setRpc(60, CommonChainmakerConstant.DEFAULT_CHAINMAKER_RPC_PORT, "grpc",
                false, -1, -1, 0,
                100, 100,
                "twoway", "../config/certs/node/" + nodeCertFileName,
                "../config/certs/node/" + nodeKeyFileName);

        //Storage
        boolean disableBlockFileDb = false;
        String dbPath = preparedNode.getName() + "/" + dbType + "_" + disableBlockFileDb;
        chainmakerConfig.setStorage("../data/" + dbPath + "/ledgerData", 30000,
                true, disableBlockFileDb, rebuildHeight);
        chainmakerConfig.setBlockdbConfig(dbType, "../data/" + dbPath + "/block", "blockdb_config");
        chainmakerConfig.setBlockdbConfig(dbType, "../data/" + dbPath + "/state", "statedb_config");
        chainmakerConfig.setBlockdbConfig(dbType, "../data/" + dbPath + "/history", "historydb_config");
        chainmakerConfig.setBlockdbConfig(dbType, "../data/" + dbPath + "/result", "resultdb_config");

        //Consensus
        chainmakerConfig.setConsensus(true, 10, 1);

        //Monitor
        chainmakerConfig.setMonitor(true, CommonChainmakerConstant.DEFAULT_CHAINMAKER_MONITOR_PORT);

        //vm
        chainmakerConfig.setVM(true,
                "../data/" + dbPath + "/dockervm_mount",
                preparedNode.getDnsName(),
                "../data/" + dbPath + "/vm_mount",
                preparedNode.getDnsName(),
                100, 100, 10);

        //TxFilter
        chainmakerConfig.setTxFilter("../data/tx_filter");

        String data = YamlUtil.toYaml(chainmakerConfig.getYaml());
        result.put("chainmaker.yml", data);

        return result;

    }


    public Map<String, String> generateChainmakerLogYaml(ChainMakerNodeDomain preparedNode) {

        Map<String, String> result = new HashMap<>();

        ChainmakerLogYaml yaml = new ChainmakerLogYaml();

        String nodeName = preparedNode.getChainmakerNode().getName();

        yaml.setSystem("INFO", "../log/" + nodeName + "/log/system.log", 365, 1,
                "INFO", "INFO", "INFO", "INFO", "INFO");

        yaml.setBrief("INFO", "../log/" + nodeName + "/log/brief.log", 365, 1, false, false);

        yaml.setEvent("INFO", "../log/" + nodeName + "/log/event.log", 365, 1, false, false);

        String data = YamlUtil.toYaml(yaml);

        result.put("log.yml", data);

        return result;
    }


    public Map<String, String> createCAConfigYaml(Organization organization, Node caNode) {

        Map<String, String> result = new HashMap<>();

        //证书信息和ca证书一致

        CAConfigYaml caConfigYaml = new CAConfigYaml();
        caConfigYaml.setLogConfig("INFO", "../log/" + caNode.getName() + "/log/ca.log", 1, 7, 5);

        caConfigYaml.setDBConfig(dbUserName, dbPassword, dbIpHost, dbPort, dbName);
        caConfigYaml.setBaseConfig(CommonChainmakerConstant.DEFAULT_CA_PORT, "single_root", "99", "SM3", "SM2",
                "false", Collections.singletonList(organization.getDomain()), "false", "true");
        caConfigYaml.addRootConfigCert("sign",
                "../crypto-config/" + caNode.getName() + "/crypto-config/rootCA/sign/root-sign.crt",
                "../crypto-config/" + caNode.getName() + "/crypto-config/rootCA/sign/root-sign.key");

        caConfigYaml.setRootConfigCsr("root." + organization.getDomain(), organization.getDomain(), "root", organization.getCountry(),
                organization.getLocality(), organization.getProvince());

        caConfigYaml.addIntermediateConfig("ca." + organization.getDomain(), organization.getDomain(), "ca", organization.getCountry(),
                organization.getLocality(), organization.getProvince(), organization.getDomain());

        caConfigYaml.addAccessControlConfig("admin", "admin-" + organization.getDomain(), organization.getDomain());

        caConfigYaml.setPkcs11();

        String data = YamlUtil.toYaml(caConfigYaml);

        result.put("config.yaml", data);

        return result;
    }
}
