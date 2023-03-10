package org.phial.baas.api.service;

public interface SmartContractService {

    /**
     * 部署
     */
    void create();

    /**
     * 升级
     */
    void upgrade();

    /**
     * 执行合约
     */
    void invokeContract();

    /**
     * 查询
     */
    void queryContract();
}
