package org.phial.baas.chainmaker.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class ChainmakerGenesisYaml {

    private JSONObject yaml;

    public ChainmakerGenesisYaml(String auth_type, String chain_id, String hash, String version, Integer sequence,
                                 Integer consensus_type, boolean enable_sql_support) {
        this.yaml = new JSONObject();
        this.yaml.put("auth_type", auth_type);
        this.yaml.put("chain_id", chain_id);
        this.yaml.put("sequence", sequence);
        this.yaml.put("version", version);

        JSONObject crypto = new JSONObject();
        crypto.put("hash", hash);
        this.yaml.put("crypto", crypto);

        JSONObject contract = new JSONObject();
        contract.put("enable_sql_support", enable_sql_support);
        this.yaml.put("contract", contract);

        JSONObject consensus = new JSONObject();
        consensus.put("type", consensus_type);
        this.yaml.put("consensus", consensus);
    }

    public void addConsensusNode(String orgId, List<String> nodes) {
        JSONObject consensus = this.yaml.getJSONObject("consensus");

        JSONArray orgNodes = consensus.getJSONArray("nodes");
        if (orgNodes == null) {
            orgNodes = new JSONArray();
            consensus.put("nodes", orgNodes);
        }

        JSONObject orgNode = new JSONObject();
        orgNode.put("org_id", orgId);
        orgNode.put("node_id", nodes);

        orgNodes.add(orgNode);
    }


    public void setBlock(Integer block_interval, Integer block_size, Integer block_tx_capacity,
                         Integer tx_timeout, Boolean tx_timestamp_verify) {
        JSONObject block = new JSONObject();
        block.put("block_interval", block_interval);
        block.put("block_size", block_size);
        block.put("block_tx_capacity", block_tx_capacity);
        block.put("tx_timeout", tx_timeout);
        block.put("tx_timestamp_verify", tx_timestamp_verify);

        this.yaml.put("block", block);
    }

    public void setVm() {
        JSONObject vm = new JSONObject();
        vm.put("addr_type", 0);
        vm.put("support_list", Arrays.asList("wasmer", "gasm", "evm", "dockergo"));
        this.yaml.put("vm", vm);
    }


    public void addTrustRoot(String orgId, List<String> nodes) {
        JSONArray trust_roots = this.yaml.getJSONArray("trust_roots");
        if (trust_roots == null) {
            trust_roots = new JSONArray();
            this.yaml.put("trust_roots", trust_roots);
        }

        JSONObject nodeRoot = new JSONObject();
        nodeRoot.put("org_id", orgId);
        nodeRoot.put("root", nodes);

        trust_roots.add(nodeRoot);
    }

    public void setCore(Integer tx_scheduler_timeout, Integer tx_scheduler_validate_timeout) {
        JSONObject core = new JSONObject();
        core.put("tx_scheduler_timeout", tx_scheduler_timeout);
        core.put("tx_scheduler_validate_timeout", tx_scheduler_validate_timeout);
        core.put("enable_sender_group", false);
        core.put("enable_conflicts_bit_window", true);

        this.yaml.put("core", core);
    }
}
