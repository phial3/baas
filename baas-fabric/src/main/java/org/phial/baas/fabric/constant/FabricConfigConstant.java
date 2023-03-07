package org.phial.baas.fabric.constant;

import org.apache.tomcat.util.compat.TLS;
import org.hyperledger.fabric.sdk.Orderer;

import java.security.PrivateKey;

/**
 * env配置参考：
 * https://blog.csdn.net/cx776474961/article/details/124257083
 * <p>
 * (1) peer ENV配置详解:
 * FABRIC_LOGGING_SPEC ：指定日志级别
 * CORE_PEER_ID ： Peer 在网络中的 ID 信息，用于辨识不同的节点
 * CORE_PEER_LISTENADDRESS ：服务监听的本地地址，本地有多个网络接口时可指定仅监听某个接口
 * CORE_PEER_ADDRESS ：对同组织内其他节点的监听连接地址。当服务在NAT设备上运行时，该配置可以指定服务对外宣称的可访问地址。如果是客户端，则作为其连接的 Peer 服务地址
 * CORE_PEER_LOCALMSPID ：Peer 所关联的 MSPID ，一般为所属组织名称，需要与通道配置内名称一致
 * CORE_PEER_MSPCONFIGPATH ：MSP 目录所在的路径，可以为绝对路径，或相对配置目录的路径
 * CORE_PEER_TLS_ENABLED ：是否开启 server 端 TLS 检查
 * CORE_PEER_TLS_CERT_FILE ：server 端使用的 TLS 证书路径
 * CORE_PEER_TLS_KEY_FILE ：server 端使用的 TLS 私钥路径
 * CORE_PEER_TLS_ROOTCERT_FILE ：server 端使用的根CA的证书，签发服务端的 TLS证书
 * CORE_PEER_GOSSIP_USELEADERELECTION ：是否允许节点之间动态进行组织的代表（leader）节点选举，通常情况下推荐开启
 * CORE_PEER_GOSSIP_ORGLEADER ：本节点是否指定为组织的代表节点，与useLeaderElection不能同时指定为true
 * CORE_PEER_GOSSIP_EXTERNALENDPOINT ：节点向组织外节点公开的服务地址，默认为空，代表不被其他组织所感知
 * CORE_VM_ENDPOINT ：docker daemon 的地址
 * CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE ：运行链码容器的网络
 * <p>
 * (2) orderer ENV配置详解:
 * ORDERER_HOME ：orderer 运行的根目录
 * ORDERER_HOST ：orderer 运行的主机
 * ORDERER_GENERAL_LOCALMSPID ： orderer 所关联的 MSPID ，一般为所属组织名称，需要与通道配置内名称一致
 * ORDERER_GENERAL_LISTENPORT ：服务绑定的监听端口
 * ORDERER_GENERAL_LISTENADDRESS ：服务绑定的监听地址，一般需要指定为所服务的特定网络接口的地址或全网（0.0.0.0）
 * ORDERER_GENERAL_BOOTSTRAPMETHOD ：获取引导块的方法，2.x版本中仅支持file或none
 * ORDERER_CHANNELPARTICIPATION_ENABLED ：是否提供参与通道的 API
 * ORDERER_GENERAL_GENESISMETHOD ：当 ORDERER_GENERAL_BOOTSTRAPMETHOD 为 file 时启用，指定创世区块类型
 * ORDERER_GENERAL_GENESISFILE ：指定创世区块位置
 * ORDERER_GENERAL_LOCALMSPDIR ：本地 MSP 文件路径
 * ORDERER_GENERAL_LOGLEVEL ：日志级别
 * ORDERER_GENERAL_TLS_ENABLED ：启用TLS时的相关配置
 * ORDERER_GENERAL_TLS_CERTIFICATE ：Orderer 身份证书
 * ORDERER_GENERAL_TLS_PRIVATEKEY ：Orderer 签名私钥
 * ORDERER_GENERAL_TLS_ROOTCAS ：信任的根证书
 * ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE ：双向TLS认证时，作为客户端证书的文件路径，如果没设置会使用 TLS.Certificate
 * ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY ：双向TLS认证时，作为客户端私钥的文件路径，如果没设置会使用 TLS.PrivateKey
 * ORDERER_GENERAL_CLUSTER_ROOTCAS ：信任的根证书
 * ORDERER_ADMIN_TLS_ENABLED ：是否启用 orderer 的管理服务面板
 * ORDERER_ADMIN_TLS_CERTIFICATE ：管理服务的证书
 * ORDERER_ADMIN_TLS_PRIVATEKEY ：管理服务的私钥
 * ORDERER_ADMIN_TLS_ROOTCAS ：管理服务的可信根证书
 * ORDERER_ADMIN_TLS_CLIENTROOTCAS ：管理服务客户端的可信根证书
 * ORDERER_ADMIN_LISTENADDRESS ：管理服务监听地址
 * ORDERER_METRICS_PROVIDER ：统计服务类型，可以为statsd(推送模式)，prometheus(拉取模式)，disabled
 * ORDERER_OPERATIONS_LISTENADDRESS ：RESTful 管理服务的监听地址
 * ORDERER_DEBUG_BROADCASTTRACEDIR ：广播请求的追踪路径
 * <p>
 * (3)ca ENV配置详解:
 * FABRIC_CA_SERVER_HOME ：指定 fabric-ca-server 运行的根目录
 * FABRIC_CA_SERVER_TLS_ENABLED ：是否启用 TLS
 * FABRIC_CA_SERVER_DEBUG ：是否启用 debug 模式
 * FABRIC_CA_SERVER_CSR_CN ：指定证书主体的 cn 字段
 * FABRIC_CA_SERVER_CSR_HOSTS ：指定证书主体的 hosts 字段
 */
public interface FabricConfigConstant {
    String FABRIC_CFG_PATH = "/etc/hyperledger/fabric";

    //
    // peer 配置
    String FABRIC_LOGGING_SPEC = ""; // 指定日志级别
    String CORE_PEER_ID = ""; //  Peer 在网络中的 ID 信息，用于辨识不同的节点
    String CORE_PEER_LISTENADDRESS = ""; // 服务监听的本地地址，本地有多个网络接口时可指定仅监听某个接口
    String CORE_PEER_ADDRESS = ""; // 对同组织内其他节点的监听连接地址。当服务在NAT设备上运行时，该配置可以指定服务对外宣称的可访问地址。如果是客户端，则作为其连接的 Peer 服务地址
    String CORE_PEER_LOCALMSPID = ""; // Peer 所关联的 MSPID ，一般为所属组织名称，需要与通道配置内名称一致
    String CORE_PEER_MSPCONFIGPATH = ""; // MSP 目录所在的路径，可以为绝对路径，或相对配置目录的路径
    String CORE_PEER_TLS_ENABLED = ""; // 是否开启 server 端 TLS 检查
    String CORE_PEER_TLS_CERT_FILE = ""; // server 端使用的 TLS 证书路径
    String CORE_PEER_TLS_KEY_FILE = ""; // server 端使用的 TLS 私钥路径
    String CORE_PEER_TLS_ROOTCERT_FILE = ""; // server 端使用的根CA的证书，签发服务端的 TLS证书
    String  CORE_PEER_TLS_CLIENTAUTHREQUIRED = "";
    String CORE_PEER_TLS_CLIENTROOTCAS_FILES = "";
    String CORE_PEER_TLS_CLIENTCERT_FILE = "";
    String CORE_PEER_TLS_CLIENTKEY_FILE = "";
    String CORE_PEER_GOSSIP_USELEADERELECTION = ""; // 是否允许节点之间动态进行组织的代表（leader）节点选举，通常情况下推荐开启
    String CORE_PEER_GOSSIP_ORGLEADER = ""; // 本节点是否指定为组织的代表节点，与useLeaderElection不能同时指定为true
    String CORE_PEER_GOSSIP_EXTERNALENDPOINT = ""; // 节点向组织外节点公开的服务地址，默认为空，代表不被其他组织所感知
    String CORE_VM_ENDPOINT = ""; // docker daemon 的地址
    String CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE = ""; // 运行链码容器的网络

    // order 配置
    String ORDERER_HOME = ""; // orderer 运行的根目录
    String ORDERER_HOST = ""; // orderer 运行的主机
    String ORDERER_GENERAL_LOCALMSPID = ""; //  orderer 所关联的 MSPID ，一般为所属组织名称，需要与通道配置内名称一致
    String ORDERER_GENERAL_LISTENPORT = ""; // 服务绑定的监听端口
    String ORDERER_GENERAL_LISTENADDRESS = ""; // 服务绑定的监听地址，一般需要指定为所服务的特定网络接口的地址或全网（0.0.0.0String * ORDERER_GENERAL_BOOTSTRAPMETHOD ：获取引导块的方法，2.x版本中仅支持file或none
    String ORDERER_CHANNELPARTICIPATION_ENABLED = ""; // 是否提供参与通道的 API
    String ORDERER_GENERAL_GENESISMETHOD = ""; // 当 ORDERER_GENERAL_BOOTSTRAPMETHOD 为 file 时启用，指定创世区块类型
    String ORDERER_GENERAL_GENESISFILE = ""; // 指定创世区块位置
    String ORDERER_GENERAL_LOCALMSPDIR = ""; // 本地 MSP 文件路径
    String ORDERER_GENERAL_LOGLEVEL = ""; // 日志级别
    String ORDERER_GENERAL_TLS_ENABLED = ""; // 启用TLS时的相关配置
    String ORDERER_GENERAL_TLS_CERTIFICATE = ""; // Orderer 身份证书
    String ORDERER_GENERAL_TLS_PRIVATEKEY = ""; // Orderer 签名私钥
    String ORDERER_GENERAL_TLS_ROOTCAS = ""; // 信任的根证书
    String ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE = ""; // 双向TLS认证时，作为客户端证书的文件路径，如果没设置会使用 TLS.Certificate
    String ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY = ""; // 双向TLS认证时，作为客户端私钥的文件路径，如果没设置会使用 TLS.PrivateKey
    String ORDERER_GENERAL_CLUSTER_ROOTCAS = ""; // 信任的根证书
    String ORDERER_ADMIN_TLS_ENABLED = ""; // 是否启用 orderer 的管理服务面板
    String ORDERER_ADMIN_TLS_CERTIFICATE = ""; // 管理服务的证书
    String ORDERER_ADMIN_TLS_PRIVATEKEY = ""; // 管理服务的私钥
    String ORDERER_ADMIN_TLS_ROOTCAS = ""; // 管理服务的可信根证书
    String ORDERER_ADMIN_TLS_CLIENTROOTCAS = ""; // 管理服务客户端的可信根证书
    String ORDERER_ADMIN_LISTENADDRESS = ""; // 管理服务监听地址
    String ORDERER_METRICS_PROVIDER = ""; // 统计服务类型，可以为statsd(推送模式)，prometheus(拉取模式)，disabled
    String ORDERER_OPERATIONS_LISTENADDRESS = ""; // RESTful 管理服务的监听地址
    String ORDERER_DEBUG_BROADCASTTRACEDIR = ""; // 广播请求的追踪路径


    // ca 配置
    String FABRIC_CA_SERVER_CA_NAME = "";
    String FABRIC_CA_SERVER_CA_CERTFILE = "";
    String FABRIC_CA_SERVER_CA_KEYFILE = "";
    String FABRIC_CA_SERVER_HOME = ""; // 指定 fabric-ca-server 运行的根目录
    String FABRIC_CA_SERVER_TLS_ENABLED = ""; // 是否启用 TLS
    String FABRIC_CA_SERVER_DEBUG = ""; // 是否启用 debug 模式
    String FABRIC_CA_SERVER_CSR_CN = ""; // 指定证书主体的 cn 字段
    String FABRIC_CA_SERVER_CSR_HOSTS = ""; // 指定证书主体的 hosts 字段
}
