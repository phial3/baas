package org.phial.baas.fabric.deploy;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class ConfigMapBatch implements Cloneable{

    private ConfigMapBatch next;

    private String basePath;

    private String volumeMark;

    private String mspId;

    private String dnsName;

    private List<ConfigMapProperty> configMapProperties;


    private ConfigMapBatch(String dnsName, String mspId) {
        this.mspId = mspId;
        this.dnsName = dnsName;
    }

    public static ConfigMapBatch newBatch(String dnsName, String mspId) {
        return new ConfigMapBatch(dnsName, mspId);
    }

    public ConfigMapBatch addConfigMapBatch(String basePath, String volumeMark) {
        ConfigMapBatch target;
        if (this.basePath == null && this.volumeMark == null) {
            target = this;
        } else {
            target = new ConfigMapBatch(this.dnsName, this.mspId);
            this.last().next = target;
        }
        target.basePath = basePath;
        target.volumeMark = volumeMark;
        target.configMapProperties = ConfigMapProperty.createConfigMapProperties(basePath);
        return this;
    }

    public ConfigMapBatch addNext(ConfigMapBatch configMapBatch) {
        this.last().next = configMapBatch;
        return this;
    }

    public ConfigMapBatch addExtraFile(String volumeMark, String subPath, byte[] value) {
        ConfigMapBatch batch = this;
        while (batch != null) {
            if (batch.getVolumeMark().equals(volumeMark)) {
                break;
            }
            batch = batch.getNext();
        }
        //新的volumeMark
        if (batch == null) {
            //新增一个batch
            batch = this.addConfigMapBatch("", volumeMark);
        }
        batch.addConfigMapProperties(subPath, value);
        return this;
    }

    private void addConfigMapProperties(String subPath, byte[] date) {
        if (configMapProperties == null) {
            configMapProperties = new LinkedList<>();
        }
        configMapProperties.add(new ConfigMapProperty(subPath, date));
    }

    private ConfigMapBatch last() {
        ConfigMapBatch last = this;
        while (last.next != null) {
            last = last.next;
        }
        return last;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
