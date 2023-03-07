package org.phial.baas.api.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.phial.baas.api.constant.ChainStatusEnum;
import org.phial.baas.api.constant.ChainTypeEnum;

import java.io.Serializable;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Chain extends Entity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一id
     */
    private Long id;

    /**
     * 中文名称
     */
    private String name;

    /**
     * 中文名称
     */
    private String chainId;

    /**
     * 网络状态
     */
    private ChainStatusEnum status;

    /**
     * 联盟类型
     */
    private ChainTypeEnum type;

    /**
     * 块高度
     */
    private Long blockHeight;

    /**
     * 交易数
     */
    private Long txCount;

    /**
     * 创世块yaml
     */
    private byte[] genesisBlock;

}