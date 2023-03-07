package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.phial.baas.api.constant.ContractEnum;

import java.io.Serializable;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmartContract extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 唯一id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 中文名称和描述
     */
    private String description;

    /**
     * 版本
     */
    private String version;

    /**
     * 部署的chainId,英文+字母_
     */
    private String chainId;

    /**
     * 合约状态
     */
    private ContractEnum.ContractStatusEnum status;

    /**
     * 运行类型, go,cpp,wasm
     */
    private ContractEnum.RuntimeType runtimeType;
}