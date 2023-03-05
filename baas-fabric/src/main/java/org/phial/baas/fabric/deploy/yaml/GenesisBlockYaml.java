package org.phial.baas.fabric.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.phial.baas.api.constant.CommonConstant;
import org.phial.baas.api.domain.Node;
import org.phial.baas.api.domain.Organization;
import org.phial.baas.fabric.entity.CANodeDomain;
import org.phial.baas.fabric.entity.NodeDomain;
import org.phial.baas.fabric.entity.OrderNodeDomain;

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
        //this.policies.put("Capabilities", getCapabilities());
        genesisBlock.put("Policies", policies);
        //genesisBlock.put("Capabilities", getCapabilities());
        this.orderer = new JSONObject();
        //this.orderer.put("Capabilities", getCapabilities());
        genesisBlock.put("Orderer", orderer);
        this.application = new JSONObject();
        this.applicationOrganizations = new JSONArray();
        //this.application.put("Capabilities", getCapabilities());
        this.application.put("Organizations", applicationOrganizations);
        genesisBlock.put("Application", application);

        profiles.put("GenesisBlock", genesisBlock);
        this.yaml.put("Profiles", profiles);
    }


    private JSONObject getCapabilities() {
        JSONObject capabilities = new JSONObject();
        capabilities.put("V2_5", true);
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

    public void addOrderOrganization(Organization org, String path) {
        addOrganization(orderer, org);
        addOrganization(application, org);
    }


    private final Set<String> orderOrgExist = new HashSet<>();

    public void addOrder(OrderNodeDomain node, CANodeDomain orgCa) {
        Node fabricNode = node.getFabricNode();
        JSONObject consenter = new JSONObject();
        consenter.put("Host", node.getDnsName());
        consenter.put("Port", fabricNode.getRpcPort());

//        ConfigMapBatch configFiles = node.getConfigFiles();
//        for (ConfigMapProperty property : configFiles.getConfigMapProperties()) {
//            String path = property.getPath();
//            if (path.matches(".*server.crt")) {
//                consenter.put("ClientTLSCert", node.getNodePath() + property.getPath());
//                consenter.put("ServerTLSCert", node.getNodePath() + property.getPath());
//                break;
//            }
//        }

        this.consenters.add(consenter);
        this.addresses.add(node.getDnsName() + ":" + fabricNode.getRpcPort());

        //Organizations结构
        if (!this.orderOrgExist.contains(orgCa.getDnsName())) {
            JSONObject org = new JSONObject();
            org.put("ID", orgCa.getFabricOrg().getMspID());
            org.put("Name", orgCa.getFabricOrg().getName());
            org.put("MSPDir", orgCa.getNodePath("msp"));
            this.orderOrganizations.add(org);
            this.orderOrgExist.add(orgCa.getDnsName());
        }
    }


    private final Set<String> peerOrgExist = new HashSet<>();

    public void addPeerOrganization(CANodeDomain orgCa) {
        if (!this.peerOrgExist.contains(orgCa.getDnsName())) {
            JSONObject org = new JSONObject();
            org.put("ID", orgCa.getFabricOrg().getMspID());
            org.put("Name", orgCa.getFabricOrg().getName());
            org.put("MSPDir", orgCa.getNodePath("msp"));
            this.applicationOrganizations.add(org);
            this.peerOrgExist.add(orgCa.getDnsName());
        }
    }


    private void addOrganization(JSONObject group, Organization org) {
        JSONArray organizations = group.getJSONArray("Organizations");
        if (organizations == null) {
            organizations = new JSONArray();
            group.put("Organizations", organizations);
        }

        JSONObject organization = new JSONObject();
        organization.put("ID", org.getMspID());
        organization.put("Name", org.getName());
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
