package org.phial.baas.chainmaker.deploy.yaml;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class K8sConfigMapYaml {

    private JSONObject yaml;


    public K8sConfigMapYaml(String apiVersion, String kind, String name, String nameSpace) {
        this.yaml = new JSONObject();
        this.yaml.put("apiVersion", apiVersion);
        this.yaml.put("kind", kind);

        JSONObject metadata = new JSONObject();
        metadata.put("name", name);
        metadata.put("namespace", nameSpace);

        this.yaml.put("metadata", metadata);
    }

    public void addData(String fileName, String fileData){

        JSONObject data = this.yaml.getJSONObject("data");
        if (data == null){
            data = new JSONObject();
            this.yaml.put("data", data);
        }
        data.put(fileName, new ConfigMapDataType(fileData));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfigMapDataType {
        private String value;
    }
}
