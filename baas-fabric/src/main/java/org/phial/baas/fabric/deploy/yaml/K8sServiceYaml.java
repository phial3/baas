package org.phial.baas.fabric.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class K8sServiceYaml {

    private JSONObject yaml;

    private JSONObject spec;

    public K8sServiceYaml(String apiVersion, String kind, String dnsName, String nameSpace, String type) {
        this.yaml = new JSONObject();
        this.yaml.put("apiVersion", apiVersion);
        this.yaml.put("kind", kind);
        JSONObject metadata = new JSONObject();

        JSONObject labels = new JSONObject();
        labels.put("ioKomposeService", dnsName);

        metadata.put("labels", labels);
//        metadata.put("name", dnsName);
        metadata.put("namespace", nameSpace);

        this.yaml.put("metadata", metadata);

        this.spec = new JSONObject();

        JSONObject selector = new JSONObject();
        selector.put("ioKomposeService", dnsName);
        this.spec.put("selector", selector);
        this.spec.put("type", type);

        this.yaml.put("spec", this.spec);
    }

    public void setName(String name) {
        JSONObject metadata = this.yaml.getJSONObject("metadata");
        metadata.put("name", name);
    }

    public void addPrometheus(Integer prometheusPort) {
        JSONObject metadata = this.yaml.getJSONObject("metadata");
        JSONObject annotations = new JSONObject();
        annotations.put("prometheus.io/port", prometheusPort);
        annotations.put("prometheus.io/scrape", new StringType("true"));
        metadata.put("annotations", annotations);
    }


    public void addPort(String name, Long nodePort, Long port, Long targetPort) {
        JSONArray ports = this.spec.getJSONArray("ports");
        if (ports == null) {
            ports = new JSONArray();
            this.spec.put("ports", ports);
        }

        JSONObject newPort = new JSONObject();
        newPort.put("name", name);
        if (nodePort != 0) {
            newPort.put("nodePort", nodePort);
        }
        newPort.put("port", port);
        newPort.put("targetPort", targetPort);

        ports.add(newPort);
    }

    public int checkPortCount() {
        JSONArray ports = this.spec.getJSONArray("ports");
        if (ports == null) {
            ports = new JSONArray();
            this.spec.put("ports", ports);
        }
        return ports.size();
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
