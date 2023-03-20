package org.phial.baas.service.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.phial.baas.service.constant.ContractEnum;

import java.io.Serializable;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@Accessors(chain = true)
@TableName("baas_contract")
@EqualsAndHashCode(callSuper = true)
public class SmartContract extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 唯一id
     */
    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
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