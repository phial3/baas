package com.phial.baas.manager.constant;


/**
 * @author gyf
 * @date 2022/12/12
 */
public interface CommonConstant {

    /**
     * default source
     */
    String DEFAULT_SOURCE = "master";

    /**
     * time format
     */
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String DATE_PATTERN = "yyyy-MM-dd";
    String TIME_PATTERN = "HH:mm:ss";

    /**
     * default constant
     */
    int DEFAULT_PAGE_NO = 1;
    int DEFAULT_PAGE_SIZE = 10000;

    /**
     *
     */
    String ILLEGAL_ACCESS = "系统不支持当前域名的访问！";

    String ADMIN = "admin";
    String ADMIN_PASSWD = "adminpw";

    String CHAINCODE_PATH = "/opt/applog/umeblockchain-fabric/chaincode/";

    String FABRIC_CONFIG_TXLATOR_REST_SERVER = "http://127.0.0.1:7059";

    String FABRIC_EXECUTE_COMMAND_PATH = "/opt/app/mskyprocess/umeblockchain-fabric/src/main/resources/fabric-1.4.2/";

    String GENESIS_BLOCK_PATH = "/tmp/";

    static String getNodeGrpcUrl(long rpcK8sPort) {
        return "grpcs://" + System.getenv("NODE_IP") + ":" + rpcK8sPort;
    }

    static String getNodeHttpUrl(long rpcK8sPort) {
        return "http://" + System.getenv("NODE_IP") + ":" +  rpcK8sPort;
    }

    static String getOrgMspID(String orgName) {
        return orgName + "MSP";
    }

    static String getAdminCaId(String orgDomain) {
        return "admin." + orgDomain.replace("ca-", "").replaceAll("-", ".");
    }

    static String getOrgDomain(String orgDnsName) {
        return orgDnsName.replace("ca-", "").replaceAll("-", ".");
    }

    static String getGenesisBlockPath(String channelName) {
        return GENESIS_BLOCK_PATH + channelName + "/";
    }
}
