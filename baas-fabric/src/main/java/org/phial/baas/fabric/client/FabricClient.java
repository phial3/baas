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

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.LifecycleChaincodePackage;
import org.hyperledger.fabric.sdk.LifecycleInstallChaincodeProposalResponse;
import org.hyperledger.fabric.sdk.LifecycleInstallChaincodeRequest;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Collection;


public class FabricClient {

    private HFClient instance;


    /**
     * Constructor
     *
     * @param context
     * @throws CryptoException
     * @throws InvalidArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public FabricClient(User context) throws CryptoException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        // setup the client
        instance = HFClient.createNewInstance();
        instance.setCryptoSuite(cryptoSuite);
        instance.setUserContext(context);
    }


    public FabricClient(HFClient client) {
        this.instance = client;
    }

    /**
     * Return an instance of HFClient.
     *
     * @return
     */
    public HFClient getInstance() {
        return instance;
    }


    /**
     * Deploy chain code.
     *
     * @param chainCodeName
     * @param chaincodePath
     * @param codePath
     * @param version
     * @param peers
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     */
    public Collection<LifecycleInstallChaincodeProposalResponse> installChainCode(String chainCodeName, String chaincodePath, String codePath, String version, Collection<Peer> peers)
            throws InvalidArgumentException, ProposalException, IOException {
        LifecycleInstallChaincodeRequest installRequest = instance.newLifecycleInstallChaincodeRequest();

        LifecycleChaincodePackage chaincodePackage =
                LifecycleChaincodePackage.fromSource(chainCodeName, Paths.get(chaincodePath), TransactionRequest.Type.GO_LANG, null, null);
        installRequest.setLifecycleChaincodePackage(chaincodePackage);
        installRequest.setUserContext(instance.getUserContext());
        installRequest.setProposalWaitTime(0L);
        Collection<LifecycleInstallChaincodeProposalResponse> proposalResponses = instance.sendLifecycleInstallChaincodeRequest(installRequest, peers);
        handleResult(proposalResponses);
        return proposalResponses;
    }

    public void handleResult(Collection<? extends ProposalResponse> proposalResponse) {
        for (ProposalResponse response : proposalResponse) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                // Proposal was successful
                System.out.println("txId:" + response.getTransactionID() + ", msg:" + response.getMessage());
            } else {
                // Proposal failed, handle the error
                System.err.println(response.getMessage());
            }
        }
    }
}
