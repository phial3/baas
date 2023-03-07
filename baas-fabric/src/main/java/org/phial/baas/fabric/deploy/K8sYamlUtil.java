package org.phial.baas.fabric.deploy;


import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ServicePort;
import org.phial.baas.api.constant.CommonFabricConstant;
import org.phial.baas.api.constant.NodeTypeEnum;
import org.phial.baas.api.domain.entity.Node;
import org.phial.baas.api.util.YamlUtil;
import org.phial.baas.fabric.deploy.yaml.K8sConfigMapYaml;
import org.phial.baas.fabric.deploy.yaml.K8sDepolymentYaml;
import org.phial.baas.fabric.deploy.yaml.K8sServiceYaml;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class K8sYamlUtil {

    @Value("${baas.fabric.k8s.namespace}")
    private String namespace;

    @Value("${baas.fabric.image.ca}")
    private String imageCa;

    @Value("${baas.fabric.image.peer}")
    private String imagePeer;

    @Value("${baas.fabric.image.orderer}")
    private String imageOrderer;

    @Value("${baas.fabric.image.tools}")
    private String imageTools;

    public String createK8sConfigMapYaml(List<ConfigMapProperty> files, Node node) {
        K8sConfigMapYaml k8sConfigMapYaml = new K8sConfigMapYaml("v1", "ConfigMap", node.getDnsName(), namespace);

        //configmap里key写别名，deployment里面再写真名
        //        for (ConfigMapProperty file : files) {
        //            k8sConfigMapYaml.addData(file.getAlias(), file.getValue());
        //        }
        //        for (Map.Entry<String, String> entry : files.entrySet()) {
        //            k8sConfigMapYaml.addData(entry.getKey(), entry.getValue());
        //        }

        return YamlUtil.toYaml(k8sConfigMapYaml.getYaml());
    }


    public V1ConfigMap createConfigMap(ConfigMapBatch batch, String dnsName) {
        V1ConfigMap body = new V1ConfigMap();
        body.setApiVersion("v1");
        body.setKind("ConfigMap");
        V1ObjectMeta meta = new V1ObjectMeta();
        meta.setNamespace(namespace);
        meta.setName(dnsName);
        body.setMetadata(meta);


        Map<String, byte[]> binaryData = new HashMap<>();
        Map<String, String> data = new HashMap<>();

        while (batch != null) {
            for (ConfigMapProperty file : batch.getConfigMapProperties()) {


                if (file.getPath().matches(".*_sk") || file.getPath().matches(".*.block")) {
                    //私钥
                    binaryData.put(file.getAlias(batch.getDnsName()), file.getValue());
                } else {
                    //其他
                    data.put(file.getAlias(batch.getDnsName()), new String(file.getValue()));
                }

//                if (batch.isBinaryData()) {
//                    binaryData.put(file.getAlias(), file.getValue());
//                } else {
//                    data.put(file.getAlias(), new String(file.getValue()));
//                }
            }
            batch = batch.getNext();
        }
        body.setData(data);
        body.setBinaryData(binaryData);
        return body;
    }


    public String createK8sDeploymentYaml(Node node, ConfigMapBatch batch) {

        String dnsName = node.getDnsName();

        K8sDepolymentYaml yaml;

        if (node.isCaNode()) {
            yaml = createCADeploymentYaml(node, dnsName, batch);
        } else if (node.getType().equals(NodeTypeEnum.HYPERLEDGER_FABRIC_NODE_ORDER)) {
            yaml = createOrderDeploymentYaml(node, dnsName, batch);
        } else {
            yaml = createPeerDeploymentYaml(node, dnsName, batch);
        }

        yaml.addPort(node.getP2pPort());
        yaml.addPort(node.getRpcPort());
        yaml.addPort(node.getMonitorPort());

        return YamlUtil.toYaml(yaml.getYaml());
    }


    protected K8sDepolymentYaml createOrderDeploymentYaml(Node node, String dnsName, ConfigMapBatch batch) {
        K8sDepolymentYaml yaml = new K8sDepolymentYaml("apps/v1", "Deployment", dnsName, namespace, imageCa);
        yaml.setNodeName(node.getIp());
        yaml.setReplicas(1);

        yaml.addArgs("orderer");

        yaml.addEnv("ORDERER_GENERAL_BOOTSTRAPMETHOD", "none");
        yaml.addEnv("ORDERER_CHANNELPARTICIPATION_ENABLED", "true");
        yaml.addEnv("ORDERER_ADMIN_LISTENADDRESS", "0.0.0.0:" + node.getP2pPort());
//        yaml.addEnv("ORDERER_ADMIN_LISTENADDRESS", "0.0.0.0:9443");
        yaml.addEnv("ORDERER_ADMIN_TLS_ENABLED", "true");
        yaml.addEnv("ORDERER_ADMIN_TLS_PRIVATEKEY", "/etc/hyperledger/orderer/tls/server.key");
        yaml.addEnv("ORDERER_ADMIN_TLS_CERTIFICATE", "/etc/hyperledger/orderer/tls/server.crt");
        yaml.addEnv("ORDERER_ADMIN_TLS_CLIENTAUTHREQUIRED", "true");
        yaml.addEnv("ORDERER_ADMIN_TLS_CLIENTROOTCAS", "/etc/hyperledger/orderer/tls/ca.crt");
        yaml.addEnv("FABRIC_LOGGING_SPEC", "DEBUG");
        yaml.addEnv("ORDERER_GENERAL_LOCALMSPID", batch.getMspId());
        yaml.addEnv("ORDERER_GENERAL_LOGLEVEL", "debug");
        yaml.addEnv("ORDERER_GENERAL_LISTENADDRESS", "0.0.0.0");
        yaml.addEnv("ORDERER_GENERAL_LOCALMSPDIR", "/etc/hyperledger/orderer/msp");
        yaml.addEnv("ORDERER_GENERAL_TLS_ENABLED", "true");
        yaml.addEnv("ORDERER_GENERAL_TLS_PRIVATEKEY", "/etc/hyperledger/orderer/tls/server.key");
        yaml.addEnv("ORDERER_GENERAL_TLS_CERTIFICATE", "/etc/hyperledger/orderer/tls/server.crt");
        yaml.addEnv("ORDERER_GENERAL_TLS_ROOTCAS", "/etc/hyperledger/orderer/tls/ca.crt");
        yaml.addEnv("ORDERER_GENERAL_CLUSTER_CLIENTPRIVATEKEY", "/etc/hyperledger/orderer/tls/server.key");
        yaml.addEnv("ORDERER_GENERAL_CLUSTER_CLIENTCERTIFICATE", "/etc/hyperledger/orderer/tls/server.crt");
        yaml.addEnv("ORDERER_GENERAL_CLUSTER_ROOTCAS", "/etc/hyperledger/orderer/tls/ca.crt");
        yaml.addEnv("ORDERER_GENERAL_TLS_CLIENTAUTHREQUIRED", "false");
        yaml.addEnv("ORDERER_GENERAL_TLS_CLIENTROOTCAS", "/etc/hyperledger/orderer/tls/ca.crt");
        yaml.addEnv("ORDERER_METRICS_PROVIDER", "prometheus");
        yaml.addEnv("ORDERER_OPERATIONS_LISTENADDRESS", "0.0.0.0:" + node.getMonitorPort());
//        yaml.addEnv("ORDERER_OPERATIONS_LISTENADDRESS", "0.0.0.0:8443");

        //log data
//        String mountPath = "/etc/hyperledger/orderer/";
        while (batch != null) {
            for (ConfigMapProperty file : batch.getConfigMapProperties()) {
                yaml.addVolumeMount(batch.getVolumeMark() + file.getPath(), "configmap", file.getAlias(batch.getDnsName()));
//                binaryData.put(file.getAlias(), file.getValue());
            }
            batch = batch.getNext();
        }
//        for (ConfigMapProperty file : files) {
//            yaml.addVolumeMount(mountPath + file.getPath(), "configmap", file.getAlias());
//        }
//        yaml.addVolumeMount("/etc/hyperledger/orderer/msp", "msp");
//        yaml.addVolumeMount("/etc/hyperledger/orderer/tls", "tls");
        yaml.addVolumeMount("/var/hyperledger/production", "production");
        yaml.addVolumeMount("/etc/localtime", "time");


        //往外挂载
//        yaml.addHostPath(filePathPrefix + "crypto-config/ordererOrganizations/baidu.com/orderers/orderer5-baidu-com/msp", "msp");
//        yaml.addHostPath(filePathPrefix + "crypto-config/ordererOrganizations/baidu.com/orderers/orderer5-baidu-com/tls", "tls");
        yaml.addHostPath(CommonFabricConstant.DATA_PATH + CommonFabricConstant.PROFILE + "/" + node.getName(), "production");
        yaml.addHostPath("/usr/share/zoneinfo/Asia/Shanghai", "time");
        yaml.addConfigMap(dnsName, "configmap");

        //资源
        yaml.setResource(node.getRequestsCpu(), node.getRequestsMemory(), node.getLimitsCpu(), node.getLimitsMemory());

        return yaml;
    }


    protected K8sDepolymentYaml createPeerDeploymentYaml(Node node, String dnsName, ConfigMapBatch batch) {
        K8sDepolymentYaml yaml = new K8sDepolymentYaml("apps/v1", "Deployment", dnsName, namespace, imagePeer);
        yaml.setNodeName(node.getIp());
        yaml.setReplicas(1);

        yaml.addArgs("peer");
        yaml.addArgs("node");
        yaml.addArgs("start");

        yaml.addEnv("CORE_PEER_ID", node.getName());
        yaml.addEnv("FABRIC_LOGGING_SPEC", "DEBUG");
        yaml.addEnv("CORE_CHAINCODE_BUILDER", "hyperledger/fabric-ccenv:latest");
        yaml.addEnv("CORE_METRICS_PROVIDER", "prometheus");
        yaml.addEnv("CORE_PEER_TLS_CLIENTAUTHREQUIRED", "false");
        yaml.addEnv("CORE_PEER_TLS_CLIENTROOTCAS_FILES", "/etc/hyperledger/peer/tls/ca.crt");
        yaml.addEnv("CORE_PEER_MSPCONFIGPATH", "/etc/hyperledger/peer/msp");
        yaml.addEnv("CORE_PEER_ADDRESS", "0.0.0.0:" + node.getRpcPort());
        yaml.addEnv("CORE_PEER_TLS_KEY_FILE", "/etc/hyperledger/peer/tls/server.key");
        yaml.addEnv("CORE_PEER_LOCALMSPID", batch.getMspId());
        yaml.addEnv("CORE_OPERATIONS_LISTENADDRESS", "0.0.0.0:" + node.getMonitorPort());
        yaml.addEnv("CORE_CHAINCODE_LOGGING_LEVEL", "DEBUG");
        yaml.addEnv("CORE_PEER_TLS_CLIENTKEY_FILE", "/etc/hyperledger/peer/tls/server.key");
        yaml.addEnv("CORE_LOGGING_PEER", "debug");
        yaml.addEnv("CORE_VM_ENDPOINT", "unix:///host/var/run/docker.sock");
        yaml.addEnv("CORE_PEER_TLS_CERT_FILE", "/etc/hyperledger/peer/tls/server.crt");
        yaml.addEnv("CORE_PEER_TLS_CLIENTCERT_FILE", "/etc/hyperledger/peer/tls/server.crt");
        yaml.addEnv("CORE_PEER_TLS_ENABLED", "true");
        yaml.addEnv("CORE_PEER_TLS_ROOTCERT_FILE", "/etc/hyperledger/peer/tls/ca.crt");

//        String mountPath = "/etc/hyperledger/fabric/";
        while (batch != null) {
            for (ConfigMapProperty file : batch.getConfigMapProperties()) {
                yaml.addVolumeMount(batch.getVolumeMark() + file.getPath(), "configmap", file.getAlias(batch.getDnsName()));
//                binaryData.put(file.getAlias(), file.getValue());
            }
            batch = batch.getNext();
        }


//        for (ConfigMapProperty file : files) {
//            yaml.addVolumeMount(mountPath + file.getPath(), "configmap", file.getAlias());
//        }
        yaml.addVolumeMount("/host/var/run/", "0");
        yaml.addVolumeMount("/var/hyperledger/production", "production");
        yaml.addVolumeMount("/etc/localtime", "time");
//        yaml.addVolumeMount("/etc/hyperledger/fabric/msp", "msp");
//        yaml.addVolumeMount("/etc/hyperledger/msp/users", "users");
//        yaml.addVolumeMount("/etc/hyperledger/fabric/tls", "tls");

        //往外挂载
        yaml.addHostPath("/var/run/", "0");
        yaml.addHostPath(CommonFabricConstant.DATA_PATH + CommonFabricConstant.PROFILE + "/" + node.getName(), "production");
        yaml.addHostPath("/usr/share/zoneinfo/Asia/Shanghai", "time");
        yaml.addConfigMap(dnsName, "configmap");
//        yaml.addHostPath("/DATA/fabric/crypto-config/peerOrganizations/www.hlzh.com/peers/peer1-www-hlzh-com/msp", "msp");
//        yaml.addHostPath("/DATA/fabric/crypto-config/peerOrganizations/www.hlzh.com/users", "users");
//        yaml.addHostPath("/DATA/fabric/crypto-config/peerOrganizations/www.hlzh.com/peers/peer1-www-hlzh-com/tls", "tls");

        //资源
        yaml.setResource(node.getRequestsCpu(), node.getRequestsMemory(), node.getLimitsCpu(), node.getLimitsMemory());

        return yaml;
    }


    public String createK8sCliDeploymentYaml(String chainName, ConfigMapBatch batch, List<Node> joinNodes, List<Node> removeNodes, String dnsName) {
        K8sDepolymentYaml yaml = new K8sDepolymentYaml("apps/v1", "Deployment", dnsName, namespace, imageTools);
        yaml.setReplicas(1);

        yaml.addCommands("/bin/bash");

        //启动命令
        yaml.addArgs("-c");
        //先order后peer
        List<StringBuilder> argList = new LinkedList<>();

        Map<String, Node> joinNodeDns = joinNodes.stream().filter(node -> node.getType() == NodeTypeEnum.HYPERLEDGER_FABRIC_NODE_ORDER).collect(Collectors.toMap(Node::getName, f -> f));
        Map<String, Node> removeNodeDns = removeNodes.stream().filter(node -> node.getType() == NodeTypeEnum.HYPERLEDGER_FABRIC_NODE_ORDER).collect(Collectors.toMap(Node::getName, f -> f));

        while (batch != null) {
            if (joinNodeDns.get(batch.getDnsName()) != null) {
                StringBuilder args = appendCommand(joinNodeDns.get(batch.getDnsName()), batch, yaml, chainName, true);
                argList.add(0, args);
            } else if (removeNodeDns.get(batch.getDnsName()) != null) {
                StringBuilder args = appendCommand(removeNodeDns.get(batch.getDnsName()), batch, yaml, chainName, false);
                argList.add(args);
            } else {
                //创世块
                for (ConfigMapProperty file : batch.getConfigMapProperties()) {
                    yaml.addVolumeMount(batch.getVolumeMark() + file.getPath(), "configmap", file.getAlias(batch.getDnsName()));
                }
            }
            batch = batch.getNext();
        }
//        //增加order命令
//        if (!CollectionUtils.isEmpty(joinNodes)) {
//            for (NodeDomain node : joinNodes) {
//                if (!(node instanceof OrderNodeDomain)) {
//                    continue;
//                }
//                OrderNodeDomain order = (OrderNodeDomain)node;
//                StringBuilder args = appendCommand(order, yaml, chainName, true);
//                argList.add(0, args);
//            }
//        }
//        //移除order命令
//        if (!CollectionUtils.isEmpty(removeNodes)) {
//            for (NodeDomain node : removeNodes) {
//                if (!(node instanceof OrderNodeDomain)) {
//                    continue;
//                }
//                OrderNodeDomain order = (OrderNodeDomain)node;
//                StringBuilder args = appendCommand(order, yaml, chainName, false);
//                argList.add(args);
//            }
//        }
        //命令按顺序加进去
        StringBuilder finalArgs = new StringBuilder();
        for (StringBuilder arg : argList) {
            finalArgs.append(arg);
        }
        finalArgs.append(" while true; do echo hello;sleep 1; done");
        yaml.addArgs(finalArgs.toString());
        yaml.addConfigMap(dnsName, "configmap");
        return YamlUtil.toYaml(yaml.getYaml());
    }

    private StringBuilder appendCommand(Node order, ConfigMapBatch configFiles, K8sDepolymentYaml yaml, String chainName, boolean join) {
        StringBuilder args = new StringBuilder();
        if (join) {
            args.append("osnadmin channel join --channelID ").append(chainName);
            args.append(" --config-block ").append("/etc/hyperledger/genesis/").append(chainName).append(".block");
        } else {
            args.append("osnadmin channel remove --channelID ").append(chainName);
        }
        args.append(" -o ").append(order.getDnsName()).append(":").append(order.getP2pPort());
        for (ConfigMapProperty file : configFiles.getConfigMapProperties()) {
            String podPath = configFiles.getVolumeMark() + file.getPath();
            //args
            if (podPath.matches(".*ca.crt")) {
                args.append(" --ca-file ").append(podPath);
            } else if (file.getPath().matches(".*server.crt")) {
                args.append(" --client-cert ").append(podPath);
            } else if (file.getPath().matches(".*server.key")) {
                args.append(" --client-key ").append(podPath);
            } else {
                continue;
            }
            //volumeMount
            yaml.addVolumeMount(podPath, "configmap", file.getAlias(order.getDnsName()));
        }
        args.append(" > /").append(order.getDnsName()).append("; ");
        return args;
    }


    protected K8sDepolymentYaml createCADeploymentYaml(Node node, String dnsName, ConfigMapBatch batch) {
        K8sDepolymentYaml yaml = new K8sDepolymentYaml("apps/v1", "Deployment", dnsName, namespace, imageCa);
        yaml.setReplicas(2);

        //启动命令
        yaml.addArgs("sh");
        yaml.addArgs("-c");
        yaml.addArgs("fabric-ca-server start -b admin:adminpw -d");


        //mountPath和env
        //FABRIC_CA_SERVER_CA_NAME好像没用
//        yaml.addEnv("FABRIC_CA_SERVER_CA_NAME", "ca-hlzh");
        yaml.addEnv("FABRIC_CA_HOME", "/etc/hyperledger/fabric-ca-server");
        while (batch != null) {
            for (ConfigMapProperty file : batch.getConfigMapProperties()) {
                String mountPath = batch.getVolumeMark() + file.getPath();
                yaml.addVolumeMount(mountPath, "configmap", file.getAlias(node.getDnsName()));

                if (mountPath.matches(".*_sk")) {
                    //私钥
                    yaml.addEnv("FABRIC_CA_SERVER_CA_KEYFILE", mountPath);
                } else if (mountPath.matches(".*.pem")) {
                    //证书
                    yaml.addEnv("FABRIC_CA_SERVER_CA_CERTFILE", mountPath);
                }
            }
            batch = batch.getNext();
        }
        yaml.addVolumeMount("/etc/localtime", "time");


        yaml.addHostPath("/usr/share/zoneinfo/Asia/Shanghai", "time");
        yaml.addConfigMap(dnsName, "configmap");

        // 资源配置
        yaml.setResource(node.getRequestsCpu(), node.getRequestsMemory(), node.getLimitsCpu(), node.getLimitsMemory());

        return yaml;
    }


    public synchronized String createK8sServiceClusterIPYaml(Node node) {
        String dnsName = node.getDnsName();
        K8sServiceYaml k8sServiceYaml = new K8sServiceYaml("v1", "Service", dnsName, namespace, "ClusterIP");
        k8sServiceYaml.setName(dnsName);

        if (node.getType().equals(NodeTypeEnum.HYPERLEDGER_FABRIC_NODE_PEER)) {
            //peer节点
            k8sServiceYaml.addPort("p2pport", node.getP2pK8s(), CommonFabricConstant.DEFAULT_PEER_P2P_PORT, CommonFabricConstant.DEFAULT_PEER_P2P_PORT);
//            k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), NodeFactory.DEFAULT_PEER_RPC_PORT, NodeFactory.DEFAULT_PEER_RPC_PORT);
            k8sServiceYaml.addPort("monitorport", node.getMonitorK8s(), CommonFabricConstant.DEFAULT_PEER_MONITOR_PORT, CommonFabricConstant.DEFAULT_PEER_MONITOR_PORT);
        } else if (node.getType().equals(NodeTypeEnum.HYPERLEDGER_FABRIC_NODE_ORDER)) {
            //order节点
            k8sServiceYaml.addPort("p2pport", node.getP2pK8s(), CommonFabricConstant.DEFAULT_ORDERER_P2P_PORT, CommonFabricConstant.DEFAULT_ORDERER_P2P_PORT);
//            k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), NodeFactory.DEFAULT_ORDER_RPC_PORT, NodeFactory.DEFAULT_ORDER_RPC_PORT);
            k8sServiceYaml.addPort("monitorport", node.getMonitorK8s(), CommonFabricConstant.DEFAULT_ORDERER_MONITOR_PORT, CommonFabricConstant.DEFAULT_ORDERER_MONITOR_PORT);
//        //order节点
            k8sServiceYaml.addPort("rpcport", 0L, CommonFabricConstant.DEFAULT_ORDERER_RPC_PORT, CommonFabricConstant.DEFAULT_ORDERER_RPC_PORT);
        } else if (node.isCaNode()) {
//            //ca服务
            //k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), CommonFabricConstant.DEFAULT_CA_RPC_PORT, CommonFabricConstant.DEFAULT_CA_RPC_PORT);
        }

        if (k8sServiceYaml.checkPortCount() == 0) {
            return null;
        }

        return YamlUtil.toYaml(k8sServiceYaml.getYaml());
    }

    public static List<String> checkServiceNames(Node node) {
        String dnsName = node.getDnsName();
        if (node.isCaNode()) {
            return Collections.singletonList(dnsName + "-nodeport");
        } else {
            return Arrays.asList(dnsName, dnsName + "-nodeport");
        }
    }


    public synchronized String createK8sServiceNodePortYaml(Node node) {
        String dnsName = node.getDnsName();
        K8sServiceYaml k8sServiceYaml = new K8sServiceYaml("v1", "Service", dnsName, namespace, "NodePort");
        k8sServiceYaml.setName(dnsName + "-nodeport");
        if (node.getType().equals(NodeTypeEnum.HYPERLEDGER_FABRIC_NODE_PEER)) {
            //peer节点
            k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), CommonFabricConstant.DEFAULT_PEER_RPC_PORT, CommonFabricConstant.DEFAULT_PEER_RPC_PORT);
        } else if (node.getType().equals(NodeTypeEnum.HYPERLEDGER_FABRIC_NODE_ORDER)) {
            //order节点
            k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), CommonFabricConstant.DEFAULT_ORDERER_RPC_PORT, CommonFabricConstant.DEFAULT_ORDERER_RPC_PORT);
        } else if (node.isCaNode()) {
            //ca服务
            k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), CommonFabricConstant.DEFAULT_CA_RPC_PORT, CommonFabricConstant.DEFAULT_CA_RPC_PORT);
        }

        if (k8sServiceYaml.checkPortCount() == 0) {
            return null;
        }

        return YamlUtil.toYaml(k8sServiceYaml.getYaml());

//
//        String dnsName = NodeFactory.getDnsName(node.getNodeName());
//
//        Map<String, String> labels = new HashMap<>();
//        labels.put("ioKomposeService", dnsName);
//
//        V1Service yamlSvc = new V1Service();
//        yamlSvc.apiVersion("v1");
//        yamlSvc.kind("Service");
//        V1ObjectMeta metadata = new V1ObjectMeta();
//        metadata.name(K8sClient.getNodePortName(dnsName))
//                .namespace(nameSpace)
//                .labels(labels);
//        yamlSvc.metadata(metadata);
//
//
//        V1ServiceSpec spec = new V1ServiceSpec();
//        if (node.isPeerNode()) {
//            spec.addPortsItem(addPort("rpcport", node.getRpcK8s(), NodeFactory.DEFAULT_PEER_RPC_PORT, NodeFactory.DEFAULT_PEER_RPC_PORT));
//        } else if (node.isOrderNode()) {
//            spec.addPortsItem(addPort("rpcport", node.getRpcK8s(), NodeFactory.DEFAULT_ORDER_RPC_PORT, NodeFactory.DEFAULT_ORDER_RPC_PORT));
//        } else if (node.isCANode()) {
//            spec.addPortsItem(addPort("rpcport", node.getRpcK8s(), NodeFactory.DEFAULT_CA_RPC_PORT, NodeFactory.DEFAULT_CA_RPC_PORT));
//        }
//        spec.selector(labels).type("NodePort");
//        yamlSvc.spec(spec);
//
//        return yamlSvc;
    }

    private V1ServicePort addPort(String name, Long nodePort, Long port, Long targetPort) {
        V1ServicePort portItem = new V1ServicePort();
        portItem.name(name);
        portItem.port(port.intValue());
        portItem.targetPort(new IntOrString(targetPort + ""));
        if (nodePort > 0) {
            portItem.nodePort(nodePort.intValue());
        }
        return portItem;
    }
}
