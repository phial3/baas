package org.phial.baas.chainmaker.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class K8sServiceYaml {

    private JSONObject yaml;

    private JSONObject spec;

    public K8sServiceYaml(String apiVersion, String kind, String dnsName, String nameSpace) {
        this.yaml = new JSONObject();
        this.yaml.put("apiVersion", apiVersion);
        this.yaml.put("kind", kind);
        JSONObject metadata = new JSONObject();

        JSONObject labels = new JSONObject();
        labels.put("ioKomposeService", dnsName);

        metadata.put("labels", labels);
        metadata.put("name", dnsName);
        metadata.put("namespace", nameSpace);

        this.yaml.put("metadata", metadata);

        this.spec = new JSONObject();

        JSONObject selector = new JSONObject();
        selector.put("ioKomposeService", dnsName);
        this.spec.put("selector", selector);
        this.spec.put("type", "NodePort");

        this.yaml.put("spec", this.spec);
    }

    public void addPrometheus(Integer prometheusPort) {
        JSONObject metadata = this.yaml.getJSONObject("metadata");
        JSONObject annotations = new JSONObject();
        annotations.put("prometheus.io/port", prometheusPort);
        annotations.put("prometheus.io/scrape", new StringType("true"));
        metadata.put("annotations", annotations);
    }


    public void addPort(String name, Long nodePort, Integer port, Integer targetPort) {
        JSONArray ports = this.spec.getJSONArray("ports");
        if (ports == null) {
            ports = new JSONArray();
            this.spec.put("ports", ports);
        }

        JSONObject newPort = new JSONObject();
        newPort.put("name", name);
        newPort.put("nodePort", new NumberType(String.valueOf(nodePort)));
        newPort.put("port", port);
        newPort.put("targetPort", targetPort);

        ports.add(newPort);
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
