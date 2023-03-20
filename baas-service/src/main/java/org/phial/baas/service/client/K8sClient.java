package org.phial.baas.service.client;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1Endpoints;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.phial.baas.service.constant.CommonFabricConstant;
import org.phial.baas.service.domain.NodeContainerStatus;
import org.springframework.util.Assert;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class K8sClient {

    private final String namespace;

    private final CoreV1Api coreV1Api;

    private final AppsV1Api appV1Api;

    private final ApiClient apiClient;

    private static final Map<String, K8sClient> instanceMap = new HashMap<>();

    public K8sClient(CoreV1Api coreV1Api, AppsV1Api appV1Api, ApiClient apiClient, String namespace) {
        this.coreV1Api = coreV1Api;
        this.appV1Api = appV1Api;
        this.apiClient = apiClient;
        this.namespace = namespace;
    }

    public synchronized static K8sClient getInstance(String namespace) {
        K8sClient client = instanceMap.get(namespace);
        if (client == null) {
            try {
                InputStream inStream = Thread.currentThread().getClass().getResourceAsStream("config/kubeconfig.yaml");
                Assert.notNull(inStream, "K8sClient load config error: kubeconfig.yaml file not found!");
                KubeConfig kubeConfig = KubeConfig.loadKubeConfig(new InputStreamReader(inStream));
                ApiClient apiClient = Config.fromConfig(kubeConfig);
                client = new K8sClient(new CoreV1Api(apiClient), new AppsV1Api(apiClient), apiClient, CommonFabricConstant.K8S_NAMESPACE);
            } catch (Exception e) {
                log.error("K8sClient instance error:{}", e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return client;
    }

    public V1Pod selectPod(String podName) {
        V1PodList v1PodList = getPodList();
        for (V1Pod pod : v1PodList.getItems()) {
            if (podName.equals(pod.getSpec().getContainers().get(0).getName())) {
                return pod;
            }
        }
        return null;
    }

    public V1PodList getPodList() {
        try {
            return coreV1Api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.info("selectPod err:{}: {}", e.getMessage(), e.getResponseBody());
            throw new RuntimeException("selectPod err:" + e.getMessage(), e.getCause());
        }
    }

    public Map<String, NodeContainerStatus> selectPodsStatus(List<String> podNames) {
        Map<String, NodeContainerStatus> podStatus = new HashMap<>();
        V1PodList v1PodList = getPodList();
        for (V1Pod pod : v1PodList.getItems()) {
            String podName = pod.getSpec().getContainers().get(0).getName();
            if (podNames.contains(podName) && pod.getStatus().getContainerStatuses() != null) {
                NodeContainerStatus nodeStatus = new NodeContainerStatus(podName, pod.getStatus().getPhase(), pod);
                for (V1ContainerStatus container : pod.getStatus().getContainerStatuses()) {
                    nodeStatus.addContainer(container.getName(), container.getReady());
                }
                podStatus.put(podName, nodeStatus);
            }
        }
        return podStatus;
    }


    public String selectPodLog(NodeContainerStatus podStatus) {
        if (podStatus == null || podStatus.getPod() == null) {
            return null;
        }
        V1Pod pod = podStatus.getPod();
        String podName = pod.getSpec().getContainers().get(0).getName();
        String podId = pod.getMetadata().getName();
        try {
            String content = coreV1Api.readNamespacedPodLog(podId, namespace, podName, null, null, null, null, null, null, null, null);
            log.info("{} pod logs: {}", podName, log);
            return content;
        } catch (ApiException e) {
            log.info("{} selectPodLog err:{}: {}", podName, e.getMessage(), e.getResponseBody());
        }
        return null;
    }


    public void createEndPoint(String yaml, String nodeName) {
        Reader reader = new StringReader(yaml);
        try {
            V1Endpoints yamlEp = (V1Endpoints) Yaml.load(reader);
            reader.close();
            V1Endpoints namespacedEndpoints = coreV1Api.replaceNamespacedEndpoints(nodeName, namespace, yamlEp, null, null, null, null);
            log.info("createEndPoint success, name:{}", nodeName);
        } catch (ApiException e) {
            log.info("createEndPoint err:{}: {}", e.getMessage(), e.getResponseBody());
            throw new RuntimeException("createEndPoint api err:" + e.getMessage(), e.getCause());
        } catch (IOException e) {
            log.info("createEndPoint Reader close err:{}", e.getMessage());
        }
    }


    public void createService(String yaml, String podName) {
        if (StringUtils.isBlank(yaml)) {
            return;
        }
        Reader reader = new StringReader(yaml);
        try {
            V1Service yamlSvc = (V1Service) Yaml.load(reader);
            reader.close();

            createService(yamlSvc, podName);
        } catch (IOException e) {
            log.info("createService Reader close err:{}", e.getMessage());
        }
    }


    public void createService(V1Service yamlSvc, String podName) {
        try {
            String name = yamlSvc.getMetadata().getName();
            if (checkServiceExist(name)) {
                deleteService(name);
            }

            V1Service service = coreV1Api.createNamespacedService(namespace, yamlSvc, null, null, null, null);
            log.info("createService success, name:{}", podName);
        } catch (ApiException e) {
            log.info("createService err:{}: {}", e.getMessage(), e.getResponseBody());
            throw new RuntimeException("createService api err:" + e.getMessage(), e.getCause());
        }
    }

    public void deleteService(String nodeName) {
        try {
            V1Service v1Service = coreV1Api.deleteNamespacedService(nodeName, namespace, null, null,
                    null, null, null, null);
            log.info("deleteService success, name:{}", nodeName);
        } catch (ApiException e) {
            log.info("deleteService err:{}: {}", e.getMessage(), e.getResponseBody());
//            throw new RuntimeException("deleteService err:" + e.getMessage());
        }
    }


    public void createDeployment(String yaml, String dnsName, boolean waitReady) {
        try {
            Reader reader = new StringReader(yaml);
            V1Deployment body = Yaml.loadAs(reader, V1Deployment.class);
            reader.close();

            V1Pod oldPod = selectPod(dnsName);
            if (checkDeploymentExist(dnsName)) {
                deleteDeployment(dnsName, false);
            }
            appV1Api.createNamespacedDeployment(namespace, body, null, null, null, null);

            if (waitReady) {
                while (true) {
                    V1Pod pod = selectPod(dnsName);
                    if (pod != null && pod.getMetadata() != null && pod.getStatus() != null && "Running".equals(pod.getStatus().getPhase())) {
                        if (oldPod == null) {
                            break;
                        }
                        if (!oldPod.getMetadata().getUid().equals(pod.getMetadata().getUid())) {
                            break;
                        }
                    }
                }
            }
            log.info("createDeployment success, name:{}", dnsName);
        } catch (ApiException e) {
            log.info("createDeployment err:{}: {}", e.getMessage(), e.getResponseBody());
            throw new RuntimeException("createDeployment err:" + e.getMessage());
        } catch (IOException e) {
            log.info("createDeployment Reader close err:{}", e.getMessage(), e);
        }
    }

    public void deleteConfigMap(String dnsName) {
        try {
            coreV1Api.deleteNamespacedConfigMap(dnsName, namespace, null, null, null, null, null, null);
            log.info("deleteNamespacedConfigMap success, name:{}", dnsName);
        } catch (ApiException e) {
            log.info("deleteConfigMap err:{}: {}", e.getMessage(), e.getResponseBody());
//            throw new RuntimeException("deleteConfigMap err:" + e.getMessage());
        }
    }


    public void createConfigMap(V1ConfigMap configMap, String dnsName) {
        try {
            if (checkConfigMapExist(dnsName)) {
                deleteConfigMap(dnsName);
            }
            coreV1Api.createNamespacedConfigMap(namespace, configMap, null, null, null, null);
            log.info("createNamespacedConfigMap success, name:{}", dnsName);
        } catch (ApiException e) {
            log.info("createConfigMap err:{}: {}", e.getMessage(), e.getResponseBody());
            throw new RuntimeException("createConfigMap err:" + e.getMessage());
        }
    }

    public void createConfigMap(String yaml, String dnsName) {
        Reader reader = new StringReader(yaml);
        try {
            V1ConfigMap body = (V1ConfigMap) Yaml.load(reader);
            reader.close();

            createConfigMap(body, dnsName);
        } catch (IOException e) {
            log.info("createConfigMap Reader close err:{}", e.getMessage(), e);
        }
    }


    public void deleteDeployment(String dnsName, boolean wait) {
        try {
            V1Status v1Status = appV1Api.deleteNamespacedDeployment(dnsName, namespace, null, null,
                    null, null, null, null);
            if (!wait) {
                return;
            }
            boolean exist = true;
            while (exist) {
                V1Pod pod = selectPod(dnsName);
                if (pod == null) {
                    exist = false;
                }
            }
            log.info("deleteDeployment success, name:{}", dnsName);
        } catch (ApiException e) {
            log.info("deleteDeployment err:{}", e.getMessage(), e);
//            throw new RuntimeException("deleteDeployment err:" + e.getMessage(), e.getCause());
        }
    }

    public boolean checkConfigMapExist(String dnsName) {

        try {
            V1ConfigMapList v1ConfigMapList = coreV1Api.listNamespacedConfigMap(namespace, null, null, null, null,
                    null, null, null, null, null, null);
            for (V1ConfigMap item : v1ConfigMapList.getItems()) {
                log.info("checkConfigMapExist, name:{}", item.getMetadata().getName());
                if (dnsName.equals(item.getMetadata().getName())) {
                    return true;
                }
            }
        } catch (ApiException e) {
            log.info("checkConfigMapExist err:{}: {}", e.getMessage(), e.getResponseBody());
            throw new RuntimeException("checkConfigMapExist err:" + e.getMessage());
        }
        return false;
    }

    public boolean checkDeploymentExist(String dnsName) {

        try {
            V1DeploymentList v1DeploymentList = appV1Api.listNamespacedDeployment(namespace, null, null, null, null,
                    null, null, null, null, null, null);
            for (V1Deployment item : v1DeploymentList.getItems()) {
                log.info("checkDeploymentExist, name:{}", item.getMetadata().getName());
                if (dnsName.equals(item.getMetadata().getName())) {
                    return true;
                }
            }
        } catch (ApiException e) {
            log.info("checkDeploymentExist err:{}: {}", e.getMessage(), e.getResponseBody());
            throw new RuntimeException("checkDeploymentExist err:" + e.getMessage());
        }
        return false;
    }


    public boolean checkServiceExist(String dnsName) {

        try {
            V1ServiceList v1ServiceList = coreV1Api.listNamespacedService(namespace, null, null, null, null,
                    null, null, null, null, null, null);
            for (V1Service item : v1ServiceList.getItems()) {
                log.info("checkServiceExist, name:{}", item.getMetadata().getName());
                if (dnsName.equals(item.getMetadata().getName())) {
                    return true;
                }
            }
        } catch (ApiException e) {
            log.info("checkServiceExist err:{}: {}", e.getMessage(), e.getResponseBody());
            throw new RuntimeException("checkServiceExist err:" + e.getMessage());
        }
        return false;
    }

    public static String getNodePortName(String dnsName) {
        return dnsName + "-nodeport";
    }


}
