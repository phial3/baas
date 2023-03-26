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

import cn.hutool.core.util.NumberUtil;
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
import org.hyperledger.fabric.protos.peer.TransactionPackage;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.UpgradeProposalRequest;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.BaseException;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.phial.baas.fabric.factory.BaasFabricApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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
    private HFClient client;

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
        this.client = gateway.getClient();
    }

    // TODO:
    private Gateway getGateway(String mspId, String adminCert, String adminKey) {
        //NetworkConfigConnection fabricNetworkConnection = BaasFabricApplicationContext.getBean(NetworkConfigConnection.class);
        //JSONObject connectionJson = fabricNetworkConnection.buildConnectionJson();
        //return getGateway(connectionJson.toJSONString(), mspId, adminCert, adminKey);
        return null;
    }

    public HFClient buildClient(User context) throws InvalidArgumentException, CryptoException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        // setup the client
        client = HFClient.createNewInstance();
        client.setCryptoSuite(cryptoSuite);
        client.setUserContext(context);
        return client;
    }

    public Contract getContract(String chainCodeId) {
        return network.getContract(chainCodeId);
    }

    /**
     * Query a transaction by txId.
     *
     * @param txId
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public TransactionInfo queryByTxId(String txId) throws ProposalException, InvalidArgumentException {
        return channel.queryTransactionByID(channel.getPeers(), txId, client.getUserContext());
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
            log.error("buildGateway exception:{}", e.getMessage(), e);
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
    public String queryContract(String chainCodeId, String methodName, String... parameters)
            throws InterruptedException, TimeoutException, ContractException {
        Contract contract = getContract(chainCodeId);
        byte[] result = contract.createTransaction(methodName).evaluate(parameters);
        if (result == null || result.length == 0) {
            return StringUtils.EMPTY;
        }
        return new String(result, StandardCharsets.UTF_8);
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
    public Collection<ProposalResponse> queryByChainCode(String chaincodeId, String chainCodePath, String version, String functionName, String[] args)
            throws InvalidArgumentException, ProposalException {

        QueryByChaincodeRequest request = client.newQueryProposalRequest();
        ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                .setName(chaincodeId)
                .setPath(chainCodePath)
                .setVersion(version)
                .build();
        request.setChaincodeID(chaincodeID);
        request.setFcn(functionName);
        request.setArgs(args);

        Collection<ProposalResponse> proposalResponses = this.channel.queryByChaincode(request);
        handleProposalResponse(proposalResponses);
        return proposalResponses;
    }


    /**
     * 链码安装
     *
     * @param chainCodePath
     * @param chainCodeName
     * @param version
     * @param codeSourceLocation
     * @return
     */
    public boolean installChainCode(String chainCodePath, String chainCodeName, String version, String codeSourceLocation) {

        boolean status = false;
        Channel channel = this.channel;
        try {
            ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                    .setName(chainCodeName)
                    .setPath(chainCodePath)
                    .setVersion(version)
                    .build();

            InstallProposalRequest request = this.client.newInstallProposalRequest();
            request.setChaincodeID(chaincodeID);
            request.setChaincodeVersion(version);
            request.setChaincodeName(chainCodeName);
            request.setUserContext(this.client.getUserContext());
            request.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
            request.setChaincodeSourceLocation(new File(codeSourceLocation));


            Collection<ProposalResponse> responses = null;
            responses = this.client.sendInstallProposal(request, channel.getPeers());
            handleProposalResponse(responses);
            status = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    /**
     * 链码初始化
     *
     * @param chainCodePath
     * @param chainCodeName
     * @param version
     * @param func
     * @param args
     * @return
     */
    public boolean instantiateChaincode(String chainCodePath, String chainCodeName, String version, String func, String[] args) {

        Channel channel = this.channel;
        boolean status = false;
        try {
            String endrosementPath = "/Users/admin/Workspace/java/umetrip.com/umeblockchain/umeblockchain-fabric/src/main/resources/network/endorsement/";
            String pathToEndorsmentPolicy = endrosementPath + this.channelId + "/" + chainCodeName + "_endorsement_policy.yaml";

            ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                    .setName(chainCodeName)
                    .setPath(chainCodePath)
                    .setVersion(version)
                    .build();

            InstantiateProposalRequest instantiateProposalRequest = this.client.newInstantiationProposalRequest();
            instantiateProposalRequest.setChaincodeID(chaincodeID);
            instantiateProposalRequest.setFcn(func);
            instantiateProposalRequest.setArgs(args);

            Map<String, byte[]> tmp = new HashMap<>();
            tmp.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(StandardCharsets.UTF_8));
            tmp.put("method", "InstantiateProposalRequest".getBytes(StandardCharsets.UTF_8));
            instantiateProposalRequest.setTransientMap(tmp);

            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = ChaincodeEndorsementPolicy.fromYamlFile(Paths.get(pathToEndorsmentPolicy));
            instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

            Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());
            handleProposalResponse(responses);

            CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(responses);
            BlockEvent.TransactionEvent txEvent = future.get(30, TimeUnit.SECONDS);
            handleFutureTransactionEvent(txEvent);
            status = txEvent.isValid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 执行交易
     *
     * @param chainCodePath
     * @param chainCodeName
     * @param version
     * @param function
     * @param args
     * @return
     */
    public boolean invokeChaincode(String chainCodePath, String chainCodeName, String version, String function, String[] args) {
        Channel channel = this.channel;
        Collection<ProposalResponse> invokePropResp = null;
        boolean txResultValid = false;
        try {
            ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                    .setName(chainCodeName)
                    .setPath(chainCodePath)
                    .setVersion(version)
                    .build();

            TransactionProposalRequest request = this.client.newTransactionProposalRequest();
            request.setChaincodeID(chaincodeID);
            request.setFcn(function);
            request.setArgs(args);
            request.setProposalWaitTime(10000000);
            request.setUserContext(this.client.getUserContext());

            invokePropResp = channel.sendTransactionProposal(request, channel.getPeers());
            handleProposalResponse(invokePropResp);

            Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(invokePropResp);
            if (proposalConsistencySets.size() != 1) {
                throw new ProposalException("Expected only one set of consistent move proposal responses but got " + proposalConsistencySets.size());
            }

            System.out.println("Sending transaction to orderer ...");
            CompletableFuture<BlockEvent.TransactionEvent> txFuture = channel.sendTransaction(invokePropResp, channel.getOrderers(), this.client.getUserContext());
            BlockEvent.TransactionEvent txEvent = txFuture.get(30, TimeUnit.SECONDS);
            handleFutureTransactionEvent(txEvent);
            txResultValid = txEvent.isValid();
        } catch (Exception e) {
            if (e instanceof ExecutionException) {
                ExecutionException ex = (ExecutionException) e;
                // msg:
                // org.hyperledger.fabric.sdk.exception.TransactionEventException: Received invalid transaction event. Transaction ID b59d1dbf0609cf028a6283ecbe7bf19eb65a84b5d8734f335dc1a9d9b8608ef3 status 10
                String msg = ex.getMessage();
                int i = msg.lastIndexOf(" status ");
                String code = msg.substring(i + 8);
                if (NumberUtil.isNumber(code)) {
                    Integer num = Integer.parseInt(code);
                    TransactionPackage.TxValidationCode validationCode = TransactionPackage.TxValidationCode.forNumber(num);
                    msg += ", validation:" + validationCode.name();
                }
                System.out.println(msg);
                e.printStackTrace();
            } else if (e instanceof BaseException) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return txResultValid;
    }


    public boolean upgradeChainCode(String chainCodePath, String chainCodeName, String version, String function, String[] args) {
        Channel channel = this.channel;

        String endrosementPath = "/Users/admin/Workspace/java/umetrip.com/umeblockchain/umeblockchain-fabric/src/main/resources/network/endorsement/";
        String pathToEndorsmentPolicy = endrosementPath + this.channelId + "/" + chainCodeName + "_endorsement_policy.yaml";

        Collection<ProposalResponse> responses;
        boolean txResultValid = false;
        try {
            UpgradeProposalRequest upgradeProposalRequest = this.client.newUpgradeProposalRequest();
            upgradeProposalRequest.setProposalWaitTime(600000);  //default 180000

            ChaincodeID chaincodeID = ChaincodeID.newBuilder()
                    .setName(chainCodeName)
                    .setPath(chainCodePath)
                    .setVersion(version)
                    .build();
            upgradeProposalRequest.setChaincodeID(chaincodeID);
            upgradeProposalRequest.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
            upgradeProposalRequest.setFcn(function);
            upgradeProposalRequest.setArgs(args);

            Map<String, byte[]> tmap = new HashMap<>();
            tmap.put("HyperLedgerFabric", "UpgradeProposalRequest:JavaSDK".getBytes(StandardCharsets.UTF_8));
            tmap.put("method", "UpgradeProposalRequest".getBytes(StandardCharsets.UTF_8));
            upgradeProposalRequest.setTransientMap(tmap);

            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = ChaincodeEndorsementPolicy.fromYamlFile(Paths.get(pathToEndorsmentPolicy));
            upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

            responses = channel.sendUpgradeProposal(upgradeProposalRequest);
            CompletableFuture<BlockEvent.TransactionEvent> future = channel.sendTransaction(responses);
            BlockEvent.TransactionEvent transactionEvent = future.get();
            handleFutureTransactionEvent(transactionEvent);
            txResultValid = transactionEvent.isValid();
        } catch (Exception e) {
            if (e instanceof ExecutionException) {
                ExecutionException ex = (ExecutionException) e;
                // msg:
                // org.hyperledger.fabric.sdk.exception.TransactionEventException: Received invalid transaction event. Transaction ID b59d1dbf0609cf028a6283ecbe7bf19eb65a84b5d8734f335dc1a9d9b8608ef3 status 10
                String msg = ex.getMessage();
                int i = msg.lastIndexOf(" status ");
                String code = msg.substring(i + 8);
                if (NumberUtil.isNumber(code)) {
                    Integer num = Integer.parseInt(code);
                    TransactionPackage.TxValidationCode validationCode = TransactionPackage.TxValidationCode.forNumber(num);
                    msg += ", validation:" + validationCode.name();
                }
                System.out.println(msg);
                e.printStackTrace();
            } else if (e instanceof BaseException) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return txResultValid;
    }

    private void handleProposalResponse(Collection<? extends ProposalResponse> proposalResponse) throws ProposalException {
        Collection<ProposalResponse> success = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        for (ProposalResponse response : proposalResponse) {
            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                System.out.printf("Successful transaction proposal response Txid: %s from peer %s\n", response.getTransactionID(), response.getPeer().getName());
                success.add(response);
            } else {
                failed.add(response);
            }
        }

        System.out.printf("handleProposalResponse Received %d transaction proposal responses. Successful verified: %d, Failed: %d\n", proposalResponse.size(), success.size(), failed.size());

        if (failed.size() > 0) {
            ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
            throw new ProposalException(firstTransactionProposalResponse.getMessage());
        }

        System.out.printf("handleProposalResponse Successfully received transaction proposal responses size:%d\n", success.size());
    }

    private void handleFutureTransactionEvent(BlockEvent.TransactionEvent txEvent) {
        boolean isValid = txEvent.isValid();
        long blockNumber = txEvent.getBlockEvent().getBlockNumber();
        String channelId = txEvent.getChannelId();
        String txId = txEvent.getTransactionID();
        Peer peer = txEvent.getPeer();
        int transactionCount = txEvent.getBlockEvent().getTransactionCount();
        System.out.printf("handleFutureTransactionEvent verify transaction Successful! isValid:%s, channelId:%s, blockNumber:%d, peer:{%s, %s}, txId:%s, transactionCount:%d\n",
                isValid, channelId, blockNumber, peer.getName(), peer.getUrl(), txId, transactionCount);

    }
}
