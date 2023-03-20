package org.phial.baas.service.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@Accessors(chain = true)
@TableName("baas_chain_node")
@EqualsAndHashCode(callSuper = true)
public class ChainNode extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
    private Long id;

    /**
     * 节点id
     */
    @TableField("node_id")
    @Column(comment = "节点ID")
    private Long nodeId;

    /**
     * 链名
     */
    @TableField("chain_id")
    @Column(comment = "链ID")
    private String chainId;

    /**
     * 组织域名
     */
    @TableField("org_domain")
    @Column(comment = "组织域名")
    private String orgDomain;
}