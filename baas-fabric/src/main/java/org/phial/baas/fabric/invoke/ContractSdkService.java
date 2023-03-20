package org.phial.baas.fabric.invoke;

import com.umetrip.blockchain.fabric.client.ChannelClient;
import com.umetrip.blockchain.fabric.client.FabricClient;
import com.umetrip.blockchain.fabric.config.apollo.ApolloConfig;
import com.umetrip.blockchain.fabric.config.init.FabricChannelGatewayHolder;
import com.umetrip.blockchain.fabric.config.source.DynamicDataSourceContextHolder;
import com.umetrip.blockchain.fabric.domain.entity.FabricContract;
import com.umetrip.blockchain.fabric.service.manager.FabricContractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.phial.baas.service.service.SmartContractService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
@Service
public class ContractSdkService {

    @Resource
    private SmartContractService smartContractService;

    @Resource
    private FabricChannelGatewayHolder fabricChannelGatewayHolder;

    public byte[] submit(String channelId, String chainCodeId, String methodName, String... parameters) {
        try {
            ChannelClient channelClient = fabricChannelGatewayHolder.getChannelClient(channelId);
            FabricClient client = channelClient.getClient();
            Assert.notNull(channelClient, "channelId=[" + channelId + "] channelClient not exists!");

            FabricContract chainCode = fabricContractService.getByChainCodeId(channelId, chainCodeId);
            Assert.notNull(chainCode, "chainCodeId=" + chainCodeId + " not exists!");

            // 该方法简洁按，但是不会返回明确的错误信息
            //Contract contract = channelClient.getContract(chainCodeId);
            //return contract.createTransaction(methodName).submit(parameters);

            Collection<ProposalResponse> proposalResponses = client.sendTransactionProposal(channelClient, chainCodeId, chainCodeId, chainCode.getVersion(), methodName, parameters);
            BlockEvent.TransactionEvent txEvent = client.sendTransaction(channelClient, proposalResponses);
            return proposalResponses.iterator().next().getChaincodeActionResponsePayload();
        } catch (Exception e) {
            log.error("ContractSdkService submit error:{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public byte[] query(String channelId, String chainCodeId, String methodName, String... parameters) {
        try {
            ChannelClient channelClient = fabricChannelGatewayHolder.getChannelClient(channelId);
            Assert.notNull(channelClient, "channelId=[" + channelId + "] channelClient not exists!");

            Contract contract = channelClient.getContract(chainCodeId);
            return contract.createTransaction(methodName).evaluate(parameters);
        } catch (ContractException e) {
            log.error("ContractSdkService query error:{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public byte[] execute(String channelId, String chainCodeId, String methodName, String... parameters) {
        String source = DynamicDataSourceContextHolder.getCurrentSource();
        if (isQueryMethod(methodName, source)) {
            return query(channelId, chainCodeId, methodName, parameters);
        } else {
            return submit(channelId, chainCodeId, methodName, parameters);
        }
    }


    private boolean isQueryMethod(String methodName, String source) {
        if (StringUtils.isBlank(methodName)) {
            return false;
        }

        String invokeContractMethods = ApolloConfig.getInvokeContractMethods();
        String[] submitMethods = invokeContractMethods.split(",");
        String queryContractMethods = ApolloConfig.getQueryContractMethods();
        String[] queryMethods = queryContractMethods.split(",");
        if (Arrays.asList(queryMethods).contains(methodName)) {
            // is query
            return true;
        } else if (Arrays.asList(submitMethods).contains(methodName)) {
            // is submit
            return false;
        } else {
            throw new IllegalArgumentException("methodName" + methodName + " unknown type!");
        }
    }
}
