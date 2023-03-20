package org.phial.baas.fabric.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umetrip.blockchain.fabric.constants.CommonConstant;
import com.umetrip.blockchain.fabric.constants.enums.CryptoEnum;
import com.umetrip.blockchain.fabric.deploy.ConfigMapBatch;
import com.umetrip.blockchain.fabric.deploy.ConfigMapProperty;
import com.umetrip.blockchain.fabric.domain.entity.FabricNode;
import com.umetrip.blockchain.fabric.domain.entity.FabricOrg;
import com.umetrip.blockchain.fabric.domain.node.CANodeDomain;
import com.umetrip.blockchain.fabric.domain.node.OrderNodeDomain;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenesisBlockYaml {

    @Getter
    private final JSONObject yaml = new JSONObject();

    private final JSONObject policies;

    private final JSONObject orderer;

    private JSONArray orderOrganizations;

    private JSONArray applicationOrganizations;

    private final JSONObject application;

    private JSONArray consenters;

    private List<String> addresses;


    public GenesisBlockYaml() {
        JSONObject profiles = new JSONObject();
        JSONObject genesisBlock = new JSONObject();
        this.policies = new JSONObject();
//        this.policies.put("Capabilities", getCapabilities());
        genesisBlock.put("Policies", policies);
        genesisBlock.put("Capabilities", getCapabilities());
        this.orderer = new JSONObject();
        this.orderer.put("Capabilities", getCapabilities());
        genesisBlock.put("Orderer", orderer);
        this.application = new JSONObject();
        this.applicationOrganizations = new JSONArray();
        this.application.put("Capabilities", getCapabilities());
        this.application.put("Organizations", applicationOrganizations);
        genesisBlock.put("Application", application);

        profiles.put("GenesisBlock", genesisBlock);
        this.yaml.put("Profiles", profiles);
    }


    private JSONObject getCapabilities() {
        JSONObject capabilities = new JSONObject();
        capabilities.put("V1_4_2", true);
        return capabilities;
    }

    public void initPolicies() {
        this.policies.put("Writers", newPolicy("ImplicitMeta", "ANY Writers"));
        this.policies.put("Admins", newPolicy("ImplicitMeta", "MAJORITY Admins"));
        this.policies.put("Readers", newPolicy("ImplicitMeta", "ANY Readers"));
    }

    public void initOrder() {
        this.orderer.put("OrdererType", "etcdraft");
        this.orderer.put("BatchTimeout", "200ms");


        JSONObject batchSize = new JSONObject();
        batchSize.put("MaxMessageCount", 10);
        batchSize.put("AbsoluteMaxBytes", "99 MB");
        batchSize.put("PreferredMaxBytes", "512 KB");
        this.orderer.put("BatchSize", batchSize);

        JSONObject etcdRaft = new JSONObject();
        consenters = new JSONArray();
        etcdRaft.put("Consenters", consenters);
        this.orderer.put("EtcdRaft", etcdRaft);

        addresses = new ArrayList<>();
        this.orderer.put("Addresses", addresses);

        this.orderOrganizations = new JSONArray();
        this.orderer.put("Organizations", orderOrganizations);
    }

    public void addOrderOrganization(FabricOrg org, String path) {
        addOrganization(orderer, org);
        addOrganization(application, org);
    }


    private final Set<String> orderOrgExist = new HashSet<>();

    public void addOrder(OrderNodeDomain node, CANodeDomain orgCa) {
        FabricNode fabricNode = node.getFabricNode();
        JSONObject consenter = new JSONObject();
        consenter.put("Host", node.getDnsName());
        consenter.put("Port", fabricNode.getRpcPort());

        ConfigMapBatch configFiles = node.getConfigFiles(CryptoEnum.CryptoUserType.ORDER);
        for (ConfigMapProperty property : configFiles.getConfigMapProperties()) {
            String path = property.getPath();
            if (path.matches(".*server.crt")) {
                consenter.put("ClientTLSCert", node.getNodePath(CryptoEnum.CryptoUserType.ORDER) + property.getPath());
                consenter.put("ServerTLSCert", node.getNodePath(CryptoEnum.CryptoUserType.ORDER) + property.getPath());
                break;
            }
        }

        this.consenters.add(consenter);
        this.addresses.add(node.getDnsName() + ":" + fabricNode.getRpcPort());

        //Organizations结构
        if (!this.orderOrgExist.contains(orgCa.getDnsName())) {
            JSONObject org = new JSONObject();
            org.put("ID", orgCa.getFabricOrg().getMspId());
            org.put("Name", orgCa.getFabricOrg().getOrgName());
            org.put("MSPDir", orgCa.getNodePath("msp"));
            this.orderOrganizations.add(org);
            this.orderOrgExist.add(orgCa.getDnsName());
        }
    }


    private final Set<String> peerOrgExist = new HashSet<>();

    public void addPeerOrganization(CANodeDomain orgCa) {
        if (!this.peerOrgExist.contains(orgCa.getDnsName())) {
            JSONObject org = new JSONObject();
            org.put("ID", orgCa.getFabricOrg().getMspId());
            org.put("Name", orgCa.getFabricOrg().getOrgName());
            org.put("MSPDir", orgCa.getNodePath("msp"));
            this.applicationOrganizations.add(org);
            this.peerOrgExist.add(orgCa.getDnsName());
        }
    }


    private void addOrganization(JSONObject group, FabricOrg org) {
        JSONArray organizations = group.getJSONArray("Organizations");
        if (organizations == null) {
            organizations = new JSONArray();
            group.put("Organizations", organizations);
        }

        JSONObject organization = new JSONObject();
        organization.put("ID", CommonConstant.getOrgMspID(org.getOrgName()));
        organization.put("Name", org.getOrgName());
        organization.put("MSPDir", "");
        organizations.add(organization);
    }

    private JSONObject newPolicy(String type, String rule) {
        JSONObject policy = new JSONObject();
        policy.put("Type", type);
        policy.put("Rule", rule);
        return policy;
    }
}
