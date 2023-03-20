package org.phial.baas.service.constant;

/**
 * @author gyf
 * @date 2022/12/12
 */
public interface CommonChainmakerConstant extends CommonConstant {

    String DATA_PATH = "/opt/app/chainmaker/";

    String PROFILE = "dev";

    String K8S_NAMESPACE = "chainmaker";


    int MAX_MESSAGE_SIZE = 100;

    int CONNECT_COUNT = 30;

    long RPC_CALL_TIMEOUT = 10000;

    long SYNC_RESULT_TIMEOUT = 10000;

    int CHAIN_CLIENT_RETRY_LIMIT = 10000;

    int CHAIN_CLIENT_RETRY_INTERVAL_MILLISECONDS = 800;

    //k8s起始端口
    Integer DEFAULT_K8s_PORT = 0;
    //ca服务端口
     Integer DEFAULT_CA_PORT = 8090;
    //长安链p2p端口
     Integer DEFAULT_CHAINMAKER_P2P_PORT = 11301;
    //长安链rpc端口
     Integer DEFAULT_CHAINMAKER_RPC_PORT = 12301;
    //长安链Prometheus端口
     Integer DEFAULT_CHAINMAKER_MONITOR_PORT = 14321;
    //docker-go-vm端口
     Integer DEFAULT_VM_ENGINE_PORT = 22351;
    // 2.3.0版本升级 go vm端口
     Integer DEFAULT_GO_VM_RUNTIME_SERVER_PORT = 32351;

    String DEFAULT_ADMIN_PREFIX = "admin0";
}
