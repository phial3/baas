package org.phial.baas.chainmaker.domain;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.phial.baas.chainmaker.deploy.K8sYamlUtil;
import org.phial.baas.service.client.K8sClient;
import org.phial.baas.service.constant.CommonChainmakerConstant;
import org.phial.baas.service.constant.CommonFabricConstant;
import org.phial.baas.service.domain.entity.Node;
import org.phial.baas.service.domain.entity.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Data
@Component
public class NodeDomain {

    private Node chainmakerNode;

    private Crypto crypto;

    private Organization chainmakerOrg;

    private String dnsName;

    @Resource
    protected K8sYamlUtil k8sYamlUtil;

    private static K8sClient k8sClientInstance;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeDomain that = (NodeDomain) o;
        return Objects.equals(chainmakerNode.getId(), that.chainmakerNode.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(chainmakerNode.getId());
    }


    public static String getNamePrefix(String fullName) {
        return fullName.split("\\.")[0];
    }


    public static K8sClient getK8sClient() {
        if (k8sClientInstance == null) {
            synchronized (K8sClient.class) {
                if (k8sClientInstance == null) {
                    try {
                        InputStream inStream = Thread.currentThread().getClass().getResourceAsStream("config/kubeconfig.yaml");
                        Assert.notNull(inStream, "K8sClient load config error: kubeconfig.yaml file not found!");
                        KubeConfig kubeConfig = KubeConfig.loadKubeConfig(new InputStreamReader(inStream));
                        ApiClient apiClient = Config.fromConfig(kubeConfig);
                        k8sClientInstance = new K8sClient(new CoreV1Api(apiClient), new AppsV1Api(apiClient), apiClient, CommonChainmakerConstant.K8S_NAMESPACE);
                    } catch (Exception e) {
                        log.error("K8sClient instance error:{}", e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }
        }
        return k8sClientInstance;
    }

    public void deployed(Map<String, String> files) {
        K8sClient k8sClient = getK8sClient();

        //k8s ConfigMap yaml
        if (!CollectionUtils.isEmpty(files)) {
            String k8sConfigMapYaml = k8sYamlUtil.createK8sConfigMapYaml(files, chainmakerNode);
            k8sClient.createConfigMap(k8sConfigMapYaml, chainmakerNode.getDnsName());
        }

        //k8s Deployment yaml
        String k8sDeploymentYaml = k8sYamlUtil.createK8sDeploymentYaml(chainmakerNode);
        log.info("{} deployment yaml:\n{}", this.dnsName, k8sDeploymentYaml);
        k8sClient.createDeployment(k8sDeploymentYaml, chainmakerNode.getDnsName(), true);

        //k8s Service yaml
        String k8sServiceYaml = k8sYamlUtil.createK8sServiceYaml(chainmakerNode);
        log.info("{} service yaml:\n{}", this.dnsName, k8sServiceYaml);
        k8sClient.createService(k8sServiceYaml, chainmakerNode.getDnsName());

        //onlineNode();
    }


    public void rebuildDbs(Map<String, String> files, String chainId) {
        K8sClient k8sClient = getK8sClient();

        //k8s ConfigMap yaml到某高度为止
        if (!CollectionUtils.isEmpty(files)) {
            String k8sConfigMapYaml = k8sYamlUtil.createK8sConfigMapYaml(files, chainmakerNode);
            k8sClient.createConfigMap(k8sConfigMapYaml, chainmakerNode.getDnsName());
        }

        //k8s Deployment yaml
        String k8sDeploymentYaml = k8sYamlUtil.createChainmakerRebuildYaml(chainmakerNode, chainId);
        log.info("{} deployment yaml:\n{}", this.dnsName, k8sDeploymentYaml);
        k8sClient.createDeployment(k8sDeploymentYaml, chainmakerNode.getDnsName(), true);

        //k8s Service yaml
        String k8sServiceYaml = k8sYamlUtil.createK8sServiceYaml(chainmakerNode);
        log.info("{} service yaml:\n{}", this.dnsName, k8sServiceYaml);
        k8sClient.createService(k8sServiceYaml, chainmakerNode.getDnsName());
    }


    public void undeployed() {
        K8sClient k8sClient = getK8sClient();

        k8sClient.deleteConfigMap(chainmakerNode.getDnsName());

        k8sClient.deleteDeployment(chainmakerNode.getDnsName(), true);

        k8sClient.deleteService(chainmakerNode.getDnsName());

        //offlineNode();
    }

    public boolean checkPodReady() {
        K8sClient k8sClient = getK8sClient();
        V1Pod pod = k8sClient.selectPod(this.dnsName);
        if (pod != null && pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null){
            log.info("{} pod status:{},", this.dnsName, pod.getStatus().getPhase());
            boolean containerReady = false;
            for (V1ContainerStatus container : pod.getStatus().getContainerStatuses()) {
                log.info("{} pod:{} container status:{} image:{}", this.dnsName, container.getName(), container.getReady(), container.getImage());
                containerReady = container.getReady();
            }
            if (containerReady && "Running".equals(pod.getStatus().getPhase())) {
                return true;
            }
        }
        return false;
    }

    public String checkPodStatus(){
        K8sClient k8sClient = getK8sClient();
        V1Pod pod = k8sClient.selectPod(this.dnsName);
        if (pod != null){
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


}
