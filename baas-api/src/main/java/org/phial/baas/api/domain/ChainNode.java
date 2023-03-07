package org.phial.baas.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChainNode extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 节点id
     */
    private Long nodeId;

    /**
     * 链名
     */
    private String chainId;

    /**
     * 组织域名
     */
    private String orgDomain;
}