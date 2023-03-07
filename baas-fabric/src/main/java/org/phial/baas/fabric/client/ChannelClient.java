/****************************************************** 
 *  Copyright 2018 IBM Corporation 
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */

package org.phial.baas.fabric.client;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.UpgradeProposalRequest;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.phial.baas.fabric.deploy.NetworkConfigConnection;
import org.phial.baas.fabric.factory.BaasFabricApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Wrapper class for a channel client.
 */
@Slf4j
@Data
public class ChannelClient {

    private String channelId;
    private Gateway gateway;
    private Channel channel;
    private Identity identity;
    private Network network;
    private FabricClient client;

    private static ConcurrentHashMap<String, Gateway> gatewayMap = new ConcurrentHashMap<>();

    /**
     * Constructor
     */
    public ChannelClient(String channelId, String mspId, String cert, String key) {
        this.channelId = channelId;
        this.gateway = getGateway(mspId, cert, key);
        this.identity = gateway.getIdentity();
        this.network = gateway.getNetwork(channelId);
        this.channel = network.getChannel();
        this.client = new FabricClient(gateway.getClient());
    }

    private Gateway getGateway(String mspId, String adminCert, String adminKey) {
        NetworkConfigConnection fabricNetworkConnection = BaasFabricApplicationContext.getBean(NetworkConfigConnection.class);
        JSONObject connectionJson = fabricNetworkConnection.buildConnectionJson();
        return getGateway(connectionJson.toJSONString(), mspId, adminCert, adminKey);
    }

    public Contract getContract(String chainCodeId) {
        return network.getContract(chainCodeId);
    }

    /**
     * Query by chaincode.
     *
     * @param chaincodeId
     * @param functionName
     * @param args
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     */
    public Collection<ProposalResponse> queryByChainCode(String chaincodeId, String functionName, String[] args)
            throws InvalidArgumentException, ProposalException {
        QueryByChaincodeRequest request = client.getInstance().newQueryProposalRequest();
        ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeId).build();
        request.setChaincodeID(ccid);
        request.setFcn(functionName);
        if (args != null) {
            request.setArgs(args);
        }
        return channel.queryByChaincode(request);
    }

    /**
     * Send transaction proposal.
     *
     * @param request
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public Collection<ProposalResponse> sendTransactionProposal(TransactionProposalRequest request)
            throws ProposalException, InvalidArgumentException, ExecutionException, InterruptedException {

        Collection<ProposalResponse> response = channel.sendTransactionProposal(request, channel.getPeers());
        for (ProposalResponse pres : response) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());

        }

        CompletableFuture<TransactionEvent> future = channel.sendTransaction(response);
        TransactionEvent result = future.get();


        return response;
    }

    /**
     * Instantiate chaincode.
     *
     * @param chaincodeName
     * @param version
     * @param chaincodePath
     * @param language
     * @param functionName
     * @param functionArgs
     * @param policyPath
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     * @throws ChaincodeEndorsementPolicyParseException
     * @throws IOException
     */
    public Collection<ProposalResponse> instantiateChainCode(String chaincodeName, String version, String chaincodePath,
                                                             String language, String functionName, String[] functionArgs, String policyPath)
            throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException {
//        Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
//                "Instantiate proposal request " + chaincodeName + " on channel " + channel.getName()
//                        + " with Fabric client " + client.getInstance().getUserContext().getMspId() + " "
//                        + client.getInstance().getUserContext().getName());
        InstantiateProposalRequest instantiateProposalRequest = client.getInstance()
                .newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(600000);  //default 180000

        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chaincodeName)
                .setVersion(version)
                .setPath(chaincodePath);

        ChaincodeID ccid = chaincodeIDBuilder.build();
        Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO,
                "Instantiating Chaincode ID " + chaincodeName + " on channel " + channel.getName());

        instantiateProposalRequest.setChaincodeID(ccid);
        if (language.equals(Type.GO_LANG.toString())) {
            instantiateProposalRequest.setChaincodeLanguage(Type.GO_LANG);
        } else {
            instantiateProposalRequest.setChaincodeLanguage(Type.JAVA);
        }

        instantiateProposalRequest.setFcn(functionName);
        instantiateProposalRequest.setArgs(functionArgs);
        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);

        if (policyPath != null) {
            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
            chaincodeEndorsementPolicy.fromYamlFile(new File(policyPath));
            instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }

        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest);
        CompletableFuture<TransactionEvent> cf = channel.sendTransaction(responses);
        Logger.getLogger(ChannelClient.class.getName()).log(Level.INFO, "Chaincode " + chaincodeName + " on channel " + channel.getName() + " instantiation " + cf);

        return responses;
    }

    /**
     * upgradeChainCode chaincode.
     *
     * @param chaincodeName
     * @param version
     * @param chaincodePath
     * @param language
     * @param functionName
     * @param functionArgs
     * @param policyPath
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     * @throws ChaincodeEndorsementPolicyParseException
     * @throws IOException
     */
    public Collection<ProposalResponse> upgradeChainCode(String chaincodeName, String version, String chaincodePath,
                                                         String language, String functionName, String[] functionArgs, String policyPath)
            throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException {

        UpgradeProposalRequest upgradeProposalRequest = client.getInstance().newUpgradeProposalRequest();
        upgradeProposalRequest.setProposalWaitTime(600000);  //default 180000
        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder()
                .setName(chaincodeName)
                .setVersion(version)
                .setPath(chaincodePath);
        ChaincodeID ccid = chaincodeIDBuilder.build();

        upgradeProposalRequest.setChaincodeID(ccid);
        if (language.equals(Type.GO_LANG.toString())) {
            upgradeProposalRequest.setChaincodeLanguage(Type.GO_LANG);
        } else {
            upgradeProposalRequest.setChaincodeLanguage(Type.JAVA);
        }
        upgradeProposalRequest.setFcn(functionName);
        upgradeProposalRequest.setArgs(functionArgs);
        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "UpgradeProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "UpgradeProposalRequest".getBytes(UTF_8));
        upgradeProposalRequest.setTransientMap(tm);

        if (policyPath != null) {
            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
            chaincodeEndorsementPolicy.fromYamlFile(new File(policyPath));
            upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }

        Collection<ProposalResponse> responses = channel.sendUpgradeProposal(upgradeProposalRequest);
        CompletableFuture<TransactionEvent> cf = channel.sendTransaction(responses);

        return responses;
    }


    /**
     * Query a transaction by id.
     *
     * @param txId
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public TransactionInfo queryByTxId(String txId) throws ProposalException, InvalidArgumentException {
        return channel.queryTransactionByID(channel.getPeers(), txId, client.getInstance().getUserContext());
    }


    /**
     * 根据mspId, cert,key构建gateway（mspId, cert,key）
     *
     * @param connectionJsonString
     * @param mspId
     * @param cert
     * @param key
     * @return
     */
    public static Gateway getGateway(String connectionJsonString, String mspId, String cert, String key) {
        try {
            X509Certificate x509Certificate = Identities.readX509Certificate(cert);
            PrivateKey privateKey = Identities.readPrivateKey(key);
            X509Identity x509Identity = Identities.newX509Identity(mspId, x509Certificate, privateKey);
            BigInteger serialNumber = x509Certificate.getSerialNumber();
            String mapKey = mspId + serialNumber;
            Gateway gatewayInstance = gatewayMap.get(mapKey);
            // 如果map里没有，根据新connectionJson生成新的gateway。
            if (gatewayInstance == null) {
                Gateway gateway = buildGateway(connectionJsonString, x509Identity);
                gatewayMap.put(mapKey, gateway);
                return gateway;
            }
            return gatewayInstance;
        } catch (CertificateException | IOException | InvalidKeyException e) {
            log.error("buildGateway exception:", e);
            return null;
        }
    }

    private static Gateway buildGateway(String connectionJson, X509Identity identity) throws IOException {
        InputStream in = new ByteArrayInputStream(connectionJson.getBytes());
        Gateway.Builder builder = Gateway.createBuilder().identity(identity).networkConfig(in);
        return builder.connect();
    }


    /**
     * 传入方法名字和请求参数，执行合约中的内容方法
     * Submit transactions that store state to the ledger.
     *
     * @param methodName 方法名
     * @param parameters
     * @return
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws ContractException
     */
    private String invokeContract(String chainCodeId, String methodName, String... parameters)
            throws InterruptedException, TimeoutException, ContractException {
        Contract contract = getContract(chainCodeId);
        byte[] result = contract.createTransaction(methodName).submit(parameters);
        if (result == null || result.length == 0) {
            return StringUtils.EMPTY;
        }
        return new String(result, StandardCharsets.UTF_8);
    }


    /**
     * Evaluate a transaction function and return its results. The transaction function will be evaluated on the
     * endorsing peers but the responses will not be sent to the ordering service and hence will not be committed to the
     * ledger. This is used for querying the world state.
     *
     * @param methodName
     * @param parameters
     * @return
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws ContractException
     */
    public String queryContract(String chainCodeId, String methodName, String... parameters) {
        try {
            Contract contract = getContract(chainCodeId);
            byte[] result = contract.createTransaction(methodName).evaluate(parameters);
            if (result == null || result.length == 0) {
                return StringUtils.EMPTY;
            }
            return new String(result, StandardCharsets.UTF_8);
        } catch (ContractException e) {
            log.error("queryContract error:", e);
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }
}
