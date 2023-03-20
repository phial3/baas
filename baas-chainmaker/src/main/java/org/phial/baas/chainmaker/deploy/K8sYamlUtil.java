package org.phial.baas.chainmaker.deploy;


import org.phial.baas.chainmaker.deploy.yaml.K8sConfigMapYaml;
import org.phial.baas.chainmaker.deploy.yaml.K8sDepolymentYaml;
import org.phial.baas.chainmaker.deploy.yaml.K8sServiceYaml;
import org.phial.baas.service.constant.CommonChainmakerConstant;
import org.phial.baas.service.constant.NodeTypeEnum;
import org.phial.baas.service.domain.entity.Node;
import org.phial.baas.service.util.YamlUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class K8sYamlUtil {
    @Value("${baas.chainmaker.k8s.namespace}")
    private String namespace;

    @Value("${baas.chainmaker.image.ca}")
    private String imageCa;

    @Value("${baas.chainmaker.image.consensus}")
    private String imageConsensus;

    @Value("${baas.chainmaker.image.common}")
    private String imageCommon;

    @Value("${baas.chainmaker.image.vm}")
    private String imageVmEngine;

    public String createK8sConfigMapYaml(Map<String, String> files, Node node) {

        String dnsName = node.getDnsName();

        K8sConfigMapYaml k8sConfigMapYaml = new K8sConfigMapYaml("v1", "ConfigMap", dnsName, namespace);

        for (Map.Entry<String, String> entry : files.entrySet()) {
            k8sConfigMapYaml.addData(entry.getKey(), entry.getValue());
        }

        return YamlUtil.toYaml(k8sConfigMapYaml.getYaml());
    }


    public String createK8sDeploymentYaml(Node node) {

        String dnsName = node.getDnsName();

        K8sDepolymentYaml yaml;

        if (node.isCaNode()) {
            yaml = createCADeploymentYaml(node, dnsName);
        } else if (node.getType().equals(NodeTypeEnum.CHAIN_MAKER_NODE_VM_ENGINE)) {
            yaml = createDockerGoVmDeploymentYaml(node, dnsName);
        } else {
            yaml = createChainmakerDeploymentYaml(node, dnsName);
        }

        if (node.getP2pPort() != null) {
            yaml.addPort(node.getP2pPort());
        }
        if (node.getRpcPort() != null) {
            yaml.addPort(node.getRpcPort());
        }

        return YamlUtil.toYaml(yaml.getYaml());
    }


    public String createChainmakerRebuildYaml(Node node, String chainName) {
        String dnsName = node.getDnsName();

        K8sDepolymentYaml yaml = createChainmakerDeploymentYaml(node, dnsName);
        //启动命令
        yaml.resetArgs();
        yaml.addArgs("./chainmaker");
        yaml.addArgs("rebuild-dbs");
        yaml.addArgs("-c");
        yaml.addArgs("../config/chainmaker.yml");
//        yaml.addArgs("ci-solo");
        yaml.addArgs("--chain-id=" + chainName);
//        yaml.addArgs("--need-verify=false");
        return YamlUtil.toYaml(yaml.getYaml());
    }


    protected K8sDepolymentYaml createDockerGoVmDeploymentYaml(Node node, String dnsName) {
        K8sDepolymentYaml yaml = new K8sDepolymentYaml("apps/v1", "Deployment", dnsName, namespace, imageVmEngine);
        yaml.setNodeName(node.getIp());

        yaml.privilegedTrue();

        //log data
        yaml.addVolumeMount("/log", "log");

        yaml.addEnv("ENV_LOG_IN_CONSOLE", "false");
        yaml.addEnv("ENV_LOG_LEVEL", "DEBUG");
        yaml.addEnv("ENV_USER_NUM", "200");
        yaml.addEnv("ENV_MAX_CONCURRENCY", "20");
        yaml.addEnv("ENV_TX_TIME_LIMIT", "36000");
        yaml.addEnv("ENV_TX_EXECUTION_TIMEOUT_MILLISECONDS", "36000000");
        yaml.addEnv("ENV_VM_SERVICE_PORT", CommonChainmakerConstant.DEFAULT_VM_ENGINE_PORT + "");
        yaml.addEnv("ENV_MAX_SEND_MSG_SIZE", "100");
        yaml.addEnv("ENV_MAX_RECV_MSG_SIZE", "100");

        //往外挂载
        yaml.addHostPath(CommonChainmakerConstant.DATA_PATH + CommonChainmakerConstant.PROFILE + "/" + node.getName() + "/log", "log");

        //资源
        yaml.setResource(node.getRequestsCpu(), node.getRequestsMemory(), node.getLimitsCpu(), node.getLimitsMemory());
        return yaml;
    }

    protected K8sDepolymentYaml createChainmakerDeploymentYaml(Node node, String dnsName) {
        K8sDepolymentYaml yaml = new K8sDepolymentYaml("apps/v1", "Deployment", dnsName, namespace, imageConsensus);
        yaml.setNodeName(node.getIp());

        //启动命令
        yaml.addArgs("./chainmaker");
        yaml.addArgs("start");
        yaml.addArgs("-c");
        yaml.addArgs("../config/chainmaker.yml");

        //log data
        yaml.addVolumeMount("/chainmaker-go/log", "log");
        yaml.addVolumeMount("/chainmaker-go/data", "data");

        //log.yaml 和 chainmaker.yaml
        yaml.addVolumeMount("/chainmaker-go/config", "configmap-volume");

        //创世块yaml
        yaml.addVolumeMount("/chainmaker-go/config/chainconfig", "configmap-volume");

        //根证书
        yaml.addVolumeMount("/chainmaker-go/config/certs/ca", "configmap-volume");

        //节点证书私钥
        yaml.addVolumeMount("/chainmaker-go/config/certs/node", "configmap-volume");

        //往外挂载
        yaml.addHostPath(CommonChainmakerConstant.DATA_PATH + CommonChainmakerConstant.PROFILE + "/", "log");
        yaml.addHostPath(CommonChainmakerConstant.DATA_PATH + CommonChainmakerConstant.PROFILE + "/", "data");

        //configmap
        yaml.addConfigMap(dnsName, "configmap-volume");

        // 从Apollo获取资源配置
        yaml.setResource(node.getRequestsCpu(), node.getRequestsMemory(), node.getLimitsCpu(), node.getLimitsMemory());

        yaml.addPort(Long.valueOf(CommonChainmakerConstant.DEFAULT_CHAINMAKER_MONITOR_PORT));
        return yaml;
    }

    protected K8sDepolymentYaml createCADeploymentYaml(Node node, String dnsName) {
        K8sDepolymentYaml yaml = new K8sDepolymentYaml("apps/v1", "Deployment", dnsName, namespace, imageCa);
        yaml.setReplicas(2);

        //启动命令
        yaml.addArgs("./chainmaker-ca");
        yaml.addArgs("-config");
        yaml.addArgs("./conf/config.yaml");

        yaml.addVolumeMount("/crypto-config", "root-ca");
        yaml.addVolumeMount("/log", "log");
        yaml.addVolumeMount("/chainmaker-ca/conf", "configmap-volume");

        yaml.addHostPath(CommonChainmakerConstant.DATA_PATH + CommonChainmakerConstant.PROFILE + "/", "root-ca");
        yaml.addHostPath(CommonChainmakerConstant.DATA_PATH + CommonChainmakerConstant.PROFILE + "/", "log");
        yaml.addConfigMap(dnsName, "configmap-volume");

        // 从Apollo获取资源配置
        yaml.setResource(node.getRequestsCpu(), node.getRequestsMemory(), node.getLimitsCpu(), node.getLimitsMemory());

        return yaml;
    }


    public String createK8sServiceYaml(Node node) {
        String dnsName = node.getDnsName();
        K8sServiceYaml k8sServiceYaml = new K8sServiceYaml("v1", "Service", dnsName, namespace);

        if (node.isCaNode()) {
            //ca服务
            k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), CommonChainmakerConstant.DEFAULT_CA_PORT, CommonChainmakerConstant.DEFAULT_CA_PORT);
        } else if (node.getType().equals(NodeTypeEnum.CHAIN_MAKER_NODE_CONSENSUS)) {
            //共识节点
            k8sServiceYaml.addPrometheus(CommonChainmakerConstant.DEFAULT_CHAINMAKER_MONITOR_PORT);
            k8sServiceYaml.addPort("p2pport", node.getP2pK8s(), CommonChainmakerConstant.DEFAULT_CHAINMAKER_P2P_PORT, CommonChainmakerConstant.DEFAULT_CHAINMAKER_P2P_PORT);
            k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), CommonChainmakerConstant.DEFAULT_CHAINMAKER_RPC_PORT, CommonChainmakerConstant.DEFAULT_CHAINMAKER_RPC_PORT);
            k8sServiceYaml.addPort("monitor", node.getMonitorK8s(), CommonChainmakerConstant.DEFAULT_CHAINMAKER_MONITOR_PORT, CommonChainmakerConstant.DEFAULT_CHAINMAKER_MONITOR_PORT);
        } else if (node.getType().equals(NodeTypeEnum.CHAIN_MAKER_NODE_VM_ENGINE)) {
            //docker-go-vm
            k8sServiceYaml.addPort("rpcport", node.getRpcK8s(), CommonChainmakerConstant.DEFAULT_VM_ENGINE_PORT, CommonChainmakerConstant.DEFAULT_VM_ENGINE_PORT);
        }

        return YamlUtil.toYaml(k8sServiceYaml.getYaml());
    }
}
