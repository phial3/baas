package org.phial.baas.fabric.entity;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.phial.baas.api.constant.CommonFabricConstant;
import org.phial.baas.api.domain.Node;
import org.phial.baas.api.domain.Organization;
import org.phial.baas.api.client.K8sClient;
import org.phial.baas.fabric.deploy.ConfigMapBatch;
import org.phial.baas.fabric.deploy.K8sYamlUtil;
import org.phial.baas.fabric.factory.BaasFabricApplicationContext;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Data
public abstract class NodeDomain {

    private Node fabricNode;

    private Organization fabricOrg;

    private String dnsName;

    private static K8sClient k8sClientInstance;

    public static K8sClient getK8sClient() {
        if (k8sClientInstance == null) {
            synchronized (K8sClient.class) {
                if (k8sClientInstance == null) {
                    try {
                        InputStream inStream = Thread.currentThread().getClass().getResourceAsStream("config/kubeconfig.yaml");
                        Assert.notNull(inStream, "K8sClient load config error: kubeconfig.yaml file not found!");
                        KubeConfig kubeConfig = KubeConfig.loadKubeConfig(new InputStreamReader(inStream));
                        ApiClient apiClient = Config.fromConfig(kubeConfig);
                        k8sClientInstance = new K8sClient(new CoreV1Api(apiClient), new AppsV1Api(apiClient), apiClient, CommonFabricConstant.K8S_NAMESPACE);
                    } catch (Exception e) {
                        log.error("K8sClient instance error:{}", e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }
        }
        return k8sClientInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeDomain that = (NodeDomain) o;
        return Objects.equals(fabricNode.getId(), that.fabricNode.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fabricNode.getId());
    }


    public void deployed(ConfigMapBatch configMapBatch) {
        K8sYamlUtil k8sYamlUtil = BaasFabricApplicationContext.getBean(K8sYamlUtil.class);
        createConfigMap(configMapBatch, k8sYamlUtil);
        createService(k8sYamlUtil);
        createDeployment(configMapBatch, k8sYamlUtil);
        //onlineNode();
    }

    public void restart(ConfigMapBatch configMapBatch) {
        K8sYamlUtil k8sYamlUtil = BaasFabricApplicationContext.getBean(K8sYamlUtil.class);
        createService(k8sYamlUtil);
        createDeployment(configMapBatch, k8sYamlUtil);
        //onlineNode();
    }

    public void offline() {
        K8sClient k8sClient = getK8sClient();
        // 节点下线不能删除ConfigMap,不然导致节点不能重启
        k8sClient.deleteDeployment(getDnsName(), true);
        //clusterIP和nodePort
        List<String> serviceNames = K8sYamlUtil.checkServiceNames(getFabricNode());
        for (String serviceName : serviceNames) {
            k8sClient.deleteService(serviceName);
        }

    }

    private void createConfigMap(ConfigMapBatch configMapBatch, K8sYamlUtil yamlUtil) {
        K8sClient k8sClient = getK8sClient();
        V1ConfigMap configMap = yamlUtil.createConfigMap(configMapBatch, this.dnsName);
        k8sClient.createConfigMap(configMap, this.dnsName);
    }

    private void createDeployment(ConfigMapBatch configMapBatch, K8sYamlUtil yamlUtil) {
        K8sClient k8sClient = getK8sClient();
        String k8sDeploymentYaml = yamlUtil.createK8sDeploymentYaml(this.fabricNode, configMapBatch);
        k8sClient.createDeployment(k8sDeploymentYaml, this.dnsName, true);
        log.info("{} deployment yaml:\n{}", this.dnsName, k8sDeploymentYaml);
    }

    private void createService(K8sYamlUtil yamlUtil) {
        K8sClient k8sClient = getK8sClient();
        String clusterIPYaml = yamlUtil.createK8sServiceClusterIPYaml(this.fabricNode);
        k8sClient.createService(clusterIPYaml, this.dnsName);

        String nodePortYaml = yamlUtil.createK8sServiceNodePortYaml(this.fabricNode);
        k8sClient.createService(nodePortYaml, this.dnsName);
    }


    public void undeploy() {
        K8sClient k8sClient = getK8sClient();
        k8sClient.deleteConfigMap(getDnsName());

        k8sClient.deleteDeployment(getDnsName(), true);
        //clusterIP和nodePort
        List<String> serviceNames = K8sYamlUtil.checkServiceNames(getFabricNode());
        for (String serviceName : serviceNames) {
            k8sClient.deleteService(serviceName);
        }

        //offlineNode();
    }

    public boolean checkPodReady() {
        K8sClient k8sClient = getK8sClient();
        V1Pod pod = k8sClient.selectPod(this.dnsName);
        if (pod != null && pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null) {
            log.info("{} pod status:{},", this.dnsName, pod.getStatus().getPhase());
            boolean containerReady = false;
            for (V1ContainerStatus container : pod.getStatus().getContainerStatuses()) {
                log.info("{} pod:{} container status:{} image:{}", this.dnsName, container.getName(), container.getReady(), container.getImage());
                if (container.getReady()) {
                    containerReady = true;
                    break;
                }
            }
            if (containerReady && "Running".equals(pod.getStatus().getPhase())) {
                return true;
            }
        }
        return false;
    }

    public String checkPodStatus() {
        K8sClient k8sClient = getK8sClient();
        V1Pod pod = k8sClient.selectPod(this.dnsName);
        if (pod != null) {
            log.info("{} pod status:{},", this.dnsName, pod.getStatus().getPhase());
            boolean containerReady = false;
            for (V1ContainerStatus container : pod.getStatus().getContainerStatuses()) {
                log.info("{} pod:{} container status:{} image:{}", this.dnsName, container.getName(), container.getReady(), container.getImage());
                containerReady = container.getReady();
            }
            return pod.getStatus().getPhase() + "-" + containerReady;
        }
        return "INIT";
    }

    public void joinChain(String chainName) {
        //NodeDomain node = getFabricNode();
        //增加链-节点对应关系
        //NodeFactory.getInstance().createChainNode(chainName, node.getId(), node.getOrgDomain());
    }


    public void exitChain(String chainName) {
        //NodeDomain chainmakerNode = getFabricNode();
        //去掉链-节点对应关系
        //NodeFactory.getInstance().deleteChainNode(chainName, chainmakerNode.getId());
    }

    //容器内挂载路径
    abstract public String getVolumeMountPath();

    protected boolean localSaved = false;

    //本地文件路径，会额外把库里数据写到指定位置
    abstract public String getNodePath();

    //把库里数据写到指定位置
//    protected void writeFileToLocal(String nodePath) {
//        File baseDir = new File(nodePath);
//        if (!baseDir.exists()) {
//            baseDir.mkdirs();
//        }
//        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
//        List<FabricCert> fabricCerts = cryptoFactory.getFabricCertMapper().selectCert(getDnsName());
//        for (FabricCert cert : fabricCerts) {
//            FileUtil.writeFile(cert.getContent(), nodePath + cert.getPath());
//        }
//    }

    //加入额外挂载文件，不写到本地，目前只有ca的config yaml
    abstract protected void getExtraConfigFiles(ConfigMapBatch configMapBatch);

    //原FabricPropertiesFactory里order节点和peer节点的grpc连接Properties
    abstract public Properties getProperties();

    //peer和order：grpc地址，ca：http地址
    abstract public String getUrl();

    private ConfigMapBatch configMapBatch;

    //从指定位置读文件，保证路径下有文件
    public ConfigMapBatch getConfigFiles() {
        if (this.configMapBatch != null) {
            return this.configMapBatch;
        }
        ConfigMapBatch configMapBatch = ConfigMapBatch.newBatch(getDnsName(), getFabricOrg().getMspID())
                .addConfigMapBatch(getNodePath(), getVolumeMountPath());
        getExtraConfigFiles(configMapBatch);
        this.configMapBatch = configMapBatch;
        return configMapBatch;
    }
}
