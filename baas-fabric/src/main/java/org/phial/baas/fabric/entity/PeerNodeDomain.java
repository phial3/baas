package org.phial.baas.fabric.entity;


import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.phial.baas.fabric.deploy.ConfigMapBatch;
import org.phial.baas.fabric.deploy.ConfigMapProperty;
import org.phial.baas.fabric.deploy.CryptogenYamlUtil;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class PeerNodeDomain extends NodeDomain {

    @Override
    public String getVolumeMountPath() {
        return "/etc/hyperledger/peer/";
    }

    @Override
    public String getNodePath() {
        String nodePath = CryptogenYamlUtil.getTypePath(getFabricNode().getType().getType());
        if (!localSaved) {
            //writeFileToLocal(nodePath);
            localSaved = true;
        }
        return CryptogenYamlUtil.getNodePath(getFabricNode());
    }

    @Override
    public void getExtraConfigFiles(ConfigMapBatch configMapBatch) {

    }

    @Override
    public Properties getProperties() {
        ConfigMapBatch configMapBatch = ConfigMapBatch.newBatch(getDnsName(), getFabricOrg().getMspID())
                .addConfigMapBatch(getNodePath(), getVolumeMountPath());
        List<ConfigMapProperty> configMapProperties = configMapBatch.getConfigMapProperties();


        Properties properties = new Properties();
        properties.setProperty("sslProvider", "openSSL");
        properties.setProperty("negotiationType", "TLS");
        properties.setProperty("hostnameOverride", getDnsName());  //for development only
        properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[]{60L, TimeUnit.SECONDS});

        //找到3个tls文件后退出循环
        boolean tlsCrt = false, tlsKey = false, tlsCa = false;
        for (ConfigMapProperty file : configMapProperties) {
            if (tlsCrt && tlsKey && tlsCa) {
                break;
            }
            String path = file.getPath();
            if (path.matches(".*server.key")) {
                properties.put("clientKeyBytes", file.getValue());
                tlsKey = true;
            } else if (path.matches(".*server.crt")) {
                properties.put("clientCertBytes", file.getValue());
                tlsCrt = true;
            } else if (path.matches(".*ca.crt")) {
                properties.put("pemBytes", file.getValue());
                tlsCa = true;
            }
        }

        return properties;
    }

    @Override
    public String getUrl() {
        return getFabricNode().getNodeGrpcUrl();
    }


    public Peer newPeer(HFClient fabClient) throws InvalidArgumentException {
        return fabClient.newPeer(getDnsName(), getUrl(), getProperties());
    }
}
