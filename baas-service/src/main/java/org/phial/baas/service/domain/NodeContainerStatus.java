package org.phial.baas.service.domain;

import io.kubernetes.client.openapi.models.V1Pod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NodeContainerStatus {

    String podName;

    String podStatus;

    List<ContainerStatus> containerList;

    V1Pod pod;

    public NodeContainerStatus(String podName, String podStatus, V1Pod pod) {
        this.podName = podName;
        this.podStatus = podStatus;
        this.containerList = new ArrayList<>();
        this.pod = pod;
    }

    @Data
    @AllArgsConstructor
    public static class ContainerStatus {

        String containerName;

        boolean containerReady;
    }

    public void addContainer(String containerName, boolean ready) {
        this.containerList.add(new ContainerStatus(containerName, ready));
    }

    public boolean podIsReady() {
        boolean containerReady = false;
        for (ContainerStatus container : this.containerList) {
            containerReady = container.isContainerReady();
        }
        return "Running".equals(podStatus) && containerReady;
    }
}
