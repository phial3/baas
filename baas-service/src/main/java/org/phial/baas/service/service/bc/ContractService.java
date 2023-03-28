package org.phial.baas.service.service.bc;

public interface ContractService {

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
