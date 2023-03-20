package org.phial.baas.chainmaker.client;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.chainmaker.pb.accesscontrol.PolicyOuterClass;
import org.chainmaker.pb.common.Request;
import org.chainmaker.pb.common.ResultOuterClass;
import org.chainmaker.pb.config.ChainConfigOuterClass;
import org.chainmaker.sdk.ChainClient;
import org.chainmaker.sdk.ChainClientException;
import org.chainmaker.sdk.ChainManager;
import org.chainmaker.sdk.User;
import org.chainmaker.sdk.config.*;
import org.chainmaker.sdk.crypto.ChainMakerCryptoSuiteException;
import org.chainmaker.sdk.utils.SdkUtils;
import org.chainmaker.sdk.utils.UtilsException;
import org.phial.baas.chainmaker.constant.ResourcePolicyEnum;
import org.phial.baas.chainmaker.domain.Crypto;
import org.phial.baas.chainmaker.domain.Policy;
import org.phial.baas.service.constant.CommonChainmakerConstant;
import org.phial.baas.service.domain.entity.Node;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
public class SdkClient {

    @Getter
    private final ChainClient chainClient;

    public SdkClient(ChainClient chainClient) {
        this.chainClient = chainClient;
    }

    public synchronized static ChainClient createChainClient(String chainId, Crypto userCrypto, List<Node> nodes) throws Exception {

//        Map<String, OrganizationServerInfo> stringOrganizationServerInfoMap = ClientCachePool.getInstance().getDefaultAdmins(chainId);
        List<byte[]> cas = new ArrayList<>();
//        for (OrganizationServerInfo value : stringOrganizationServerInfoMap.values()) {
//            cas.add(value.getCaCrt().getBytes());
//        }

        byte[][] tlsCaCerts = cas.toArray(new byte[cas.size()][]);
        //Collections.singletonList(caCrt.getBytes()).toArray(new byte[0][0]);

        SdkConfig sdkConfig = new SdkConfig();
        ChainClientConfig chainClientConfig = new ChainClientConfig();
        sdkConfig.setChainClient(chainClientConfig);

        //
        chainClientConfig.setOrgId(userCrypto.getOrgDomain());
        chainClientConfig.setChainId(chainId);

        // cert
        byte[] cert = userCrypto.getCert().getBytes();
        byte[] privateKey = userCrypto.getPrivateKey().getBytes();
        chainClientConfig.setUserKeyBytes(privateKey);
        chainClientConfig.setUserCrtBytes(cert);
        chainClientConfig.setUserSignKeyBytes(privateKey);
        chainClientConfig.setUserSignCrtBytes(cert);


        List<NodeConfig> nodeConfigs = new LinkedList<>();
        for (Node node : nodes) {
            NodeConfig nodeConfig = new NodeConfig();
            nodeConfig.setTrustRootBytes(tlsCaCerts);

            nodeConfig.setTlsHostName(node.getName());
            nodeConfig.setEnableTls(true);
            nodeConfig.setNodeAddr(node.getNodeHttpUrl());
            nodeConfig.setConnCnt(CommonChainmakerConstant.CONNECT_COUNT);
            nodeConfigs.add(nodeConfig);
        }
        // nodes
        chainClientConfig.setNodes(nodeConfigs.toArray(new NodeConfig[0]));

        // rpc_client
        RpcClientConfig rpcClientConfig = new RpcClientConfig();
        rpcClientConfig.setMaxReceiveMessageSize(CommonChainmakerConstant.MAX_MESSAGE_SIZE);
        chainClientConfig.setRpcClient(rpcClientConfig);


        // 同步交易结果模式下，每次轮训交易结果时的等待时间，单位：ms 删除此项或设为<=0则使用默认值 500
        chainClientConfig.setRetryLimit(CommonChainmakerConstant.CHAIN_CLIENT_RETRY_LIMIT);
        chainClientConfig.setRetryInterval(CommonChainmakerConstant.CHAIN_CLIENT_RETRY_INTERVAL_MILLISECONDS);

        // pkcs11
        Pkcs11Config pkcs11Config = new Pkcs11Config();
        pkcs11Config.setEnabled(false);
        chainClientConfig.setPkcs11(pkcs11Config);

        // 交易结果是否订阅获取
        chainClientConfig.setEnableTxResultDispatcher(true);

        // connPool
        ConnPoolConfig connPoolConfig = new ConnPoolConfig();
        connPoolConfig.setBlockWhenExhausted(true);
        connPoolConfig.setMaxWaitMillis(3000);
        connPoolConfig.setMaxTotal(3);
        connPoolConfig.setMinIdle(1);
        connPoolConfig.setMaxIdle(3);
        connPoolConfig.setMinEvictableIdleTime(350000);
        connPoolConfig.setTimeBetweenEvictionRuns(10000);
        chainClientConfig.setConnPool(connPoolConfig);

        ChainManager chainManager = ChainManager.getInstance();
        return chainManager.createChainClient(sdkConfig);
    }


    protected Request.EndorsementEntry[] getEndorsementEntries(ChainClient chainClient, Request.Payload payload, String resourceName) throws ChainClientException, ChainMakerCryptoSuiteException, UtilsException {
        Policy resourcePolicy = getResourcePolicy(resourceName);

        if (resourcePolicy == null) {
            return null;
        }

        //获取默认管理员
        User[] defaultAdminList = getDefaultAdminList(chainClient.getChainId(), resourcePolicy);

        //给payload背书
        return SdkUtils.getEndorsers(payload, defaultAdminList);
    }


    /**
     * 获取合约资源权限
     */
    private Policy getResourcePolicy(String resourceName) throws ChainClientException, ChainMakerCryptoSuiteException {

        ChainConfigOuterClass.ChainConfig currentChainConfig = getChainConfig();

        List<String> availableOrgList = new ArrayList<>();
        for (int i = 0; i < currentChainConfig.getTrustMembersCount(); i++) {
            ChainConfigOuterClass.TrustRootConfig trustRoots = currentChainConfig.getTrustRoots(i);
            availableOrgList.add(trustRoots.getOrgId());
        }
        //先看链上配置
        for (ChainConfigOuterClass.ResourcePolicy resourcePolicy : currentChainConfig.getResourcePoliciesList()) {
            if (resourceName.equals(resourcePolicy.getResourceName())) {
                PolicyOuterClass.Policy policy = resourcePolicy.getPolicy();
                return new Policy(policy.getRule(), policy.getOrgListList(), policy.getRoleListList(), availableOrgList);
            }
        }

        //链上没有，查默认的
        ResourcePolicyEnum resourcePolicy = ResourcePolicyEnum.getResourcePolicy(resourceName);
        if (resourcePolicy != null) {
            return new Policy(resourcePolicy.getRule(), null, Collections.singletonList("admin"), new ArrayList<>(availableOrgList));
        }

        //不需要签名
        return null;
    }

    /**
     * 获取满足背书数量要求的默认管理员
     *
     * @param chainId
     * @return
     */
    private User[] getDefaultAdminList(String chainId, Policy policy) {

        List<String> orgIds = null;
        if (!CollectionUtils.isEmpty(policy.getOrgList())) {
            orgIds = policy.getOrgList();
        } else {
            //查链上有几个组织
            //TODO:
//            orgIds = SDKClient.selectOrgDomainsInChain(chainId);
//            if (CollectionUtils.isEmpty(orgIds)) {
//                throw new RuntimeException("chain has no organization chianId:" + chainId);
//            }
        }

        //本次需要几个组织的管理员签名
        int adminNumber = ResourcePolicyEnum.getAdminNumber(policy.getRule(), orgIds);

        //TODO:
        //List<User> adminCert = SDKClient.selectOrgUsersInChain(chainId, orgIds.subList(0, adminNumber));
        // return adminCert.toArray(new User[0]);

        return null;
    }

    protected synchronized ChainConfigOuterClass.ChainConfig getChainConfig() throws ChainClientException, ChainMakerCryptoSuiteException {
        return chainClient.getChainConfig(CommonChainmakerConstant.RPC_CALL_TIMEOUT);
    }

    protected Map<String, byte[]> getParams(Map<String, Object> inputParams) {
        Map<String, byte[]> params = new HashMap<>();
        if (inputParams == null) {
            return params;
        }

        // ObjectMapper objectMapper = new ObjectMapper();
        // JSONObject objectMap = objectMapper.convertValue(inputParams, JSONObject.class);
        for (Map.Entry<String, Object> entry : (inputParams.entrySet())) {
            Object value = entry.getValue();
            if (value != null) {
                if (value.getClass().equals(String.class)) {
                    params.put(entry.getKey(), ((String) value).getBytes());
                } else if (value.getClass().equals(byte[].class)) {
                    params.put(entry.getKey(), (byte[]) value);
                } else {
                    params.put(entry.getKey(), JSON.toJSONString(value).getBytes());
                }
            }
        }
        return params;
    }


    protected void handleResult(ResultOuterClass.TxResponse txResponse) {

        if (ResultOuterClass.TxStatusCode.SUCCESS.equals(txResponse.getCode())) {

        }
        if (0 != txResponse.getContractResult().getCode()) {
            String contractResult = txResponse.getContractResult().getResult().toStringUtf8();

        }
    }

    protected void checkResultSuccess(ResultOuterClass.TxResponse txResponse) {
        switch (txResponse.getCode()) {
            case SUCCESS:
                return;
            case INVALID_CONTRACT_PARAMETER_CONTRACT_NAME:
                //合约不存在
                throw new RuntimeException("合约不存在");
            case CONTRACT_FAIL:
                //主动返回错误
                throw new RuntimeException(txResponse.getContractResult().getResult().toStringUtf8());
        }
        log.info("unknown resultType :{}, result:{}, message:{}", txResponse.getCode(), txResponse.getContractResult().getResult().toStringUtf8(), txResponse.getContractResult().getMessage());
    }
}
