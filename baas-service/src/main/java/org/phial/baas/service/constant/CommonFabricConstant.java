package org.phial.baas.service.constant;

/**
 * @author gyf
 * @date 2022/12/12
 */
public interface CommonFabricConstant extends CommonConstant {

    String PROFILE = "dev";

    String ADMIN = "admin";

    String ADMIN_PASSWD = "adminpw";

    String DATA_PATH = "/opt/app/fabric/";

    String K8S_NAMESPACE = "fabric";

    String FABRIC_CONFIG_TXLATOR_REST_SERVER = "http://127.0.0.1:7059";

    String FABRIC_EXECUTE_COMMAND_PATH = "/opt/app/fabric/cli/";

    String UNIX_TMP_DIR = "/tmp/";

    Integer DEFAULT_K8s_PORT = 0;
    //ca服务端口
    Long DEFAULT_CA_RPC_PORT = 7054L;
    //长安链p2p端口
    Long DEFAULT_PEER_P2P_PORT = 7053L;
    Long DEFAULT_PEER_RPC_PORT = 7051L;
    Long DEFAULT_PEER_MONITOR_PORT = 9443L;
    //长安链rpc端口
    Long DEFAULT_ORDERER_P2P_PORT = 9443L;
    Long DEFAULT_ORDERER_RPC_PORT = 7050L;
    Long DEFAULT_ORDERER_MONITOR_PORT = 8443L;

    static String getCaNodeAdmin(String caDnsName) {
        return "admin." + caDnsName.replace("ca-", "").replaceAll("-", ".");
    }

    static String getGenesisBlockPath(String channelName) {
        return UNIX_TMP_DIR + channelName + "/";
    }
}
