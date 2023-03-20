package org.phial.baas.chainmaker.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class K8sDepolymentYaml {

    private JSONObject yaml;

    private JSONObject spec;


    public K8sDepolymentYaml(String apiVersion, String kind, String dnsName, String nameSpace, String image) {
        yaml = new JSONObject();

        yaml.put("apiVersion", apiVersion);
        yaml.put("kind", kind);

        JSONObject metadata = new JSONObject();

        JSONObject annotations = new JSONObject();
        annotations.put("deployment.kubernetes.io/revision", new StringType("1"));
        metadata.put("annotations", annotations);

        JSONObject labels = new JSONObject();
        labels.put("ioKomposeService", dnsName);
        metadata.put("labels", labels);

        metadata.put("generation", 1);
        metadata.put("name", dnsName);
        metadata.put("namespace", nameSpace);

        yaml.put("metadata", metadata);


        JSONObject specBlock = new JSONObject();

        specBlock.put("replicas", 1);

        JSONObject selector = new JSONObject();
        JSONObject matchLabels = new JSONObject();
        matchLabels.put("ioKomposeService", dnsName);
        selector.put("matchLabels", matchLabels);
        specBlock.put("selector", selector);

        JSONObject strategy = new JSONObject();
        strategy.put("type", "Recreate");
        specBlock.put("strategy", strategy);


        JSONObject template = new JSONObject();
        JSONObject template_metadata = new JSONObject();
        JSONObject template_metadata_labels = new JSONObject();
        template_metadata_labels.put("ioKomposeService", dnsName);
        template_metadata.put("labels", template_metadata_labels);

        this.spec = new JSONObject();
        template.put("spec", this.spec);
        template.put("metadata", template_metadata);
        specBlock.put("template", template);

        yaml.put("spec", specBlock);


        JSONObject container = getContainer();
        container.put("image", image);
        container.put("name", dnsName);
    }

    public void setReplicas(int num) {
        JSONObject spec = yaml.getJSONObject("spec");
        spec.put("replicas", num);
    }

    public void setNodeName(String nodeName) {
        this.spec.put("nodeName", nodeName);
    }

    public void addArgs(String arg) {
        JSONObject container = getContainer();

        JSONArray args = container.getJSONArray("args");
        if (args == null){
            args = new JSONArray();
            container.put("args", args);
        }
        args.add(arg);
    }

    public void resetArgs() {
        JSONObject container = getContainer();
        container.remove("args");
    }

    public void addEnv(String name, String value) {
        JSONArray envs = getEnv();

        JSONObject env = new JSONObject();
        env.put("name", name);
        env.put("value", value);
        envs.add(env);
    }

    public void privilegedTrue() {
        JSONObject container = getContainer();
        JSONObject securityContext = new JSONObject();
        securityContext.put("privileged", true);
        container.put("securityContext", securityContext);
    }


    public void addPort(Long port){
        JSONObject container = getContainer();

        JSONArray ports = container.getJSONArray("ports");
        if (ports == null){
            ports = new JSONArray();
            container.put("ports", ports);
        }

        JSONObject containerPort = new JSONObject();
        containerPort.put("containerPort", new NumberType(String.valueOf(port)));
        ports.add(containerPort);
    }


    public void addVolumeMount(String mountPath, String name) {
        JSONObject container = getContainer();

        JSONArray volumeMounts = container.getJSONArray("volumeMounts");
        if (volumeMounts == null){
            volumeMounts = new JSONArray();
            container.put("volumeMounts", volumeMounts);
        }

        JSONObject volumeMount = new JSONObject();
        volumeMount.put("mountPath", mountPath);
        volumeMount.put("name", name);
        volumeMounts.add(volumeMount);
    }

    public void setResource(String requestCPU, String requestMemory, String limitCPU, String limitMemory) {
        JSONObject container = getContainer();

        JSONObject resource = new JSONObject();
        JSONObject limits = new JSONObject();
        limits.put("cpu", limitCPU);
        limits.put("memory", limitMemory);
        resource.put("limits", limits);

        JSONObject requests = new JSONObject();
        requests.put("cpu", requestCPU);
        requests.put("memory", requestMemory);
        resource.put("requests", requests);

        container.put("resources", resource);

    }

    public void addHostPath(String path, String name){

        JSONArray volumes = this.spec.getJSONArray("volumes");
        if (volumes == null){
            volumes = new JSONArray();
            this.spec.put("volumes", volumes);
        }

        JSONObject hostPath = new JSONObject();
        JSONObject InnerPath = new JSONObject();
        InnerPath.put("path", path);
        hostPath.put("hostPath", InnerPath);
        hostPath.put("name", name);
        volumes.add(hostPath);
    }

    public void addConfigMap(String configMapName, String name){

        JSONArray volumes = this.spec.getJSONArray("volumes");
        if (volumes == null){
            volumes = new JSONArray();
            this.spec.put("volumes", volumes);
        }

        JSONObject configMap = new JSONObject();
        JSONObject InnerPath = new JSONObject();
        InnerPath.put("name", configMapName);
        configMap.put("configMap", InnerPath);
        configMap.put("name", name);
        volumes.add(configMap);
    }


    public void addPVCVolumeClaim(String name, String claimName) {
        JSONArray volumes = this.spec.getJSONArray("volumes");
        if (volumes == null){
            volumes = new JSONArray();
            this.spec.put("volumes", volumes);
        }
        JSONObject volumeClaim = new JSONObject();
        JSONObject persistentVolumeClaim = new JSONObject();
        persistentVolumeClaim.put("claimName", claimName);
        volumeClaim.put("persistentVolumeClaim", persistentVolumeClaim);
        volumeClaim.put("name", name);
        volumes.add(volumeClaim);
    }


    public void addEmptyDir(String name) {
        JSONArray volumes = this.spec.getJSONArray("volumes");
        if (volumes == null){
            volumes = new JSONArray();
            this.spec.put("volumes", volumes);
        }

        JSONObject emptyDir = new JSONObject();
        emptyDir.put("name", name);
        emptyDir.put("emptyDir", new JSONObject());
        volumes.add(emptyDir);
    }

    //使用节点存储，路径必须是下面这个规则，主要用于提取输出日志文件，会自动提取到es
    public void addFlexVolume(String name, String nodeName) {
        JSONArray volumes = this.spec.getJSONArray("volumes");
        if (volumes == null){
            volumes = new JSONArray();
            this.spec.put("volumes", volumes);
        }

        JSONObject flexVolume = new JSONObject();
        flexVolume.put("driver", "travelsky/hostpath");
        flexVolume.put("name", name);
        JSONObject options = new JSONObject();
        options.put("driver.root", new StringType("/opt/applog/podlog/app/${kubernetes.io/pod.namespace}/" + nodeName + "/${kubernetes.io/pod.name}"));
        flexVolume.put("options", options);
        volumes.add(flexVolume);
    }





    private JSONArray getEnv() {
        JSONObject container = getContainer();

        JSONArray args = container.getJSONArray("env");
        if (args == null){
            args = new JSONArray();
            container.put("env", args);
        }
        return args;
    }

    private JSONObject getContainer(){
        JSONArray containers = this.spec.getJSONArray("containers");
        JSONObject container;
        if (containers == null){
            containers = new JSONArray();
            container = new JSONObject();
            containers.add(container);
            this.spec.put("containers", containers);
        } else {
            container = containers.getJSONObject(0);
        }
        return container;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NumberType {
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StringType {
        private String value;
    }
}
