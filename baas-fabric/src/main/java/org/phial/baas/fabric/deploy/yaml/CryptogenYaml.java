package org.phial.baas.fabric.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class CryptogenYaml {
    private JSONObject yaml;


    public CryptogenYaml() {
        this.yaml = new JSONObject();
    }

    public void setOrdererOrg(String domain, String locality, String country, String province) {
        JSONArray ordererOrgs = yaml.getJSONArray("OrdererOrgs");
        if (ordererOrgs == null) {
            ordererOrgs = new JSONArray();
            yaml.put("OrdererOrgs", ordererOrgs);
        }
        JSONObject org = createOrg(domain, locality, country, province);

        ordererOrgs.add(org);
    }


    public void addOrder(String orgDomain, List<String> needCreateNodes) {
        JSONObject ordererOrg = selectDomain(yaml.getJSONArray("OrdererOrgs"), orgDomain);
        addSpec(ordererOrg, "orderer", needCreateNodes);
    }



    public void setPeerOrg(String domain, String locality, String country, String province) {
        JSONArray peerOrgs = yaml.getJSONArray("PeerOrgs");
        if (peerOrgs == null) {
            peerOrgs = new JSONArray();
            yaml.put("PeerOrgs", peerOrgs);
        }
        JSONObject org = createOrg(domain, locality, country, province);

        JSONObject users = new JSONObject();
        users.put("Count", 0);
        org.put("Users", users);

        peerOrgs.add(org);
    }

    public void addPeer(String orgDomain, List<String> needCreateNodes) {
        JSONObject peerOrg = selectDomain(yaml.getJSONArray("PeerOrgs"), orgDomain);
        addSpec(peerOrg, "peer", needCreateNodes);

        JSONObject template = new JSONObject();
        template.put("Count", needCreateNodes.size());
        peerOrg.put("Template", template);
    }

    public boolean hasItem() {
        return yaml.containsKey("PeerOrgs") || yaml.containsKey("OrdererOrgs");
    }

    private JSONObject createOrg(String domain, String locality, String country, String province) {
        JSONObject org = new JSONObject();
        org.put("Domain", domain);

        JSONObject ca = new JSONObject();
        ca.put("Locality", locality);
        ca.put("Country", country);
        ca.put("Province", province);
        org.put("CA", ca);

        org.put("Name", domain);
        org.put("EnableNodeOUs", true);
        return org;
    }

    private void addSpec(JSONObject org, String type, List<String> needCreateNodes) {
        JSONArray specs = new JSONArray();
        for (String nodeDnsName : needCreateNodes) {
            JSONObject spec = new JSONObject();
            //todo 先写一样的
            spec.put("Hostname", nodeDnsName);
            spec.put("CommonName", nodeDnsName);
            specs.add(spec);
        }
        org.put("Specs", specs);
    }

    private JSONObject selectDomain(JSONArray orgs, String domain) {
        for (Object org : orgs) {
            if (((JSONObject)org).getString("Domain").equals(domain)) {
                return (JSONObject)org;
            }
        }
        return null;
    }
}
