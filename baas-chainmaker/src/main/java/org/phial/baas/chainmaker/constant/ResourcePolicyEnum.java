package org.phial.baas.chainmaker.constant;

import lombok.Getter;

import java.util.List;
import java.util.regex.Pattern;

@Getter
public enum ResourcePolicyEnum {
    /**
     *
     */
    CHAIN_CONFIG_CORE_UPDATE("CHAIN_CONFIG-CORE_UPDATE", "MAJORITY"),
    CHAIN_CONFIG_BLOCK_UPDATE("CHAIN_CONFIG-BLOCK_UPDATE", "MAJORITY"),
    CHAIN_CONFIG_TRUST_ROOT_ADD("CHAIN_CONFIG-TRUST_ROOT_ADD", "MAJORITY"),
    CHAIN_CONFIG_TRUST_ROOT_UPDATE("CHAIN_CONFIG-TRUST_ROOT_UPDATE", "SELF"),
    CHAIN_CONFIG_TRUST_ROOT_DELETE("CHAIN_CONFIG-TRUST_ROOT_DELETE", "MAJORITY"),
    CHAIN_CONFIG_TRUST_MEMBER_ADD("CHAIN_CONFIG-TRUST_MEMBER_ADD", "MAJORITY"),
    CHAIN_CONFIG_TRUST_MEMBER_UPDATE("CHAIN_CONFIG-TRUST_MEMBER_UPDATE", "MAJORITY"),
    CHAIN_CONFIG_TRUST_MEMBER_DELETE("CHAIN_CONFIG-TRUST_MEMBER_DELETE", "MAJORITY"),
    CHAIN_CONFIG_NODE_ADDR_ADD("CHAIN_CONFIG-NODE_ADDR_ADD", "MAJORITY"),
    CHAIN_CONFIG_NODE_ADDR_UPDATE("CHAIN_CONFIG-NODE_ADDR_UPDATE", "MAJORITY"),
    CHAIN_CONFIG_NODE_ADDR_DELETE("CHAIN_CONFIG-NODE_ADDR_DELETE", "MAJORITY"),
    CHAIN_CONFIG_NODE_ORG_ADD("CHAIN_CONFIG-NODE_ORG_ADD", "MAJORITY"),
    CHAIN_CONFIG_NODE_ORG_UPDATE("CHAIN_CONFIG-NODE_ORG_UPDATE", "MAJORITY"),
    CHAIN_CONFIG_NODE_ORG_DELETE("CHAIN_CONFIG-NODE_ORG_DELETE", "MAJORITY"),
    CHAIN_CONFIG_CONSENSUS_EXT_ADD("CHAIN_CONFIG-CONSENSUS_EXT_ADD", "MAJORITY"),
    CHAIN_CONFIG_CONSENSUS_EXT_UPDATE("CHAIN_CONFIG-CONSENSUS_EXT_UPDATE", "MAJORITY"),
    CHAIN_CONFIG_CONSENSUS_EXT_DELETE("CHAIN_CONFIG-CONSENSUS_EXT_DELETE", "MAJORITY"),
    CHAIN_CONFIG_PERMISSION_ADD("CHAIN_CONFIG-PERMISSION_ADD", "MAJORITY"),
    CHAIN_CONFIG_PERMISSION_UPDATE("CHAIN_CONFIG-PERMISSION_UPDATE", "MAJORITY"),
    CHAIN_CONFIG_PERMISSION_DELETE("CHAIN_CONFIG-PERMISSION_DELETE", "MAJORITY"),
    CHAIN_CONFIG_NODE_ID_ADD("CHAIN_CONFIG-NODE_ID_ADD", "MAJORITY"),
    CHAIN_CONFIG_NODE_ID_UPDATE("CHAIN_CONFIG-NODE_ID_UPDATE", "SELF"),
    CHAIN_CONFIG_NODE_ID_DELETE("CHAIN_CONFIG-NODE_ID_DELETE", "MAJORITY"),
    CERT_MANAGE_CERTS_DELETE("CERT_MANAGE-CERTS_DELETE", "ANY"),
    CERT_MANAGE_CERTS_FREEZE("CERT_MANAGE-CERTS_FREEZE", "ANY"),
    CERT_MANAGE_CERTS_UNFREEZE("CERT_MANAGE-CERTS_UNFREEZE", "ANY"),
    CERT_MANAGE_CERTS_REVOKE("CERT_MANAGE-CERTS_REVOKE", "ANY"),
    CERT_MANAGE_CERT_ALIAS_UPDATE("CERT_MANAGE-CERT_ALIAS_UPDATE", "SELF"),
    CERT_MANAGE_CERTS_ALIAS_DELETE("CERT_MANAGE-CERTS_ALIAS_DELETE", "SELF"),
    CONTRACT_MANAGE_INIT_CONTRACT("CONTRACT_MANAGE-INIT_CONTRACT", "MAJORITY"),
    CONTRACT_MANAGE_UPGRADE_CONTRACT("CONTRACT_MANAGE-UPGRADE_CONTRACT", "MAJORITY"),
    CONTRACT_MANAGE_FREEZE_CONTRACT("CONTRACT_MANAGE-FREEZE_CONTRACT", "MAJORITY"),
    CONTRACT_MANAGE_UNFREEZE_CONTRACT("CONTRACT_MANAGE-UNFREEZE_CONTRACT", "MAJORITY"),
    CONTRACT_MANAGE_REVOKE_CONTRACT("CONTRACT_MANAGE-REVOKE_CONTRACT", "MAJORITY"),
    PRIVATE_COMPUTE_SAVE_CA_CERT("PRIVATE_COMPUTE-SAVE_CA_CERT", "MAJORITY"),
    PRIVATE_COMPUTE_SAVE_ENCLAVE_REPORT("PRIVATE_COMPUTE-SAVE_CA_CERT", "MAJORITY");

    private final String resourceName;

    private final String rule;

    ResourcePolicyEnum(String resourceName, String rule) {
        this.resourceName = resourceName;
        this.rule = rule;
    }

    public static ResourcePolicyEnum getResourcePolicy(String resourceName) {
        for (ResourcePolicyEnum item : ResourcePolicyEnum.values()) {
            if (resourceName.equals(item.resourceName)) {
                return item;
            }
        }
        throw new IllegalArgumentException("ResourcePolicyEnum.getResourcePolicy resourceName=" + resourceName + " not found.");
    }


    private static final String pattern = ".*[0-9]+.*";

    public static int getAdminNumber(String rule, List<String> orgList) {

        Pattern r = Pattern.compile(pattern);

        if (r.matcher(rule).matches()) {
            if (rule.contains("/")) {
                String[] point = rule.split("/");

                return (int) Math.ceil(Double.parseDouble(point[0]) * orgList.size() / Integer.parseInt(point[1]));
            } else {
                return Integer.parseInt(rule);
            }
        } else {
            switch (rule) {
                case "ALL":
                    return orgList.size();
                case "SELF":
                case "ANY":
                    return 1;
                case "MAJORITY":
                    return orgList.size() / 2 + 1;
                case "FORBIDDEN":
                    throw new RuntimeException("this resource is FORBIDDEN");
                default:
                    throw new IllegalArgumentException("no support rule:" + rule);
            }
        }
    }
}
