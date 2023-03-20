package org.phial.baas.service.domain.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import org.phial.baas.service.constant.ChainStatusEnum;
import org.phial.baas.service.constant.ChainTypeEnum;

import java.io.Serializable;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Data
@TableName("baas_chain")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Chain extends Entity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一id
     */
    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
    private Long id;

    /**
     * 中文名称
     */
    @TableField("name")
    @Column(comment = "链名称")
    private String name;

    /**
     * 中文名称
     */
    @Unique
    @TableField("chain_id")
    @Column(comment = "链的唯一标识ID")
    private String chainId;

    /**
     * 网络状态
     */
    @TableField("status")
    @Column(comment = "联盟状态")
    private ChainStatusEnum status;

    /**
     * 联盟类型
     */
    @TableField("type")
    @Column(comment = "联盟链类型")
    private ChainTypeEnum type;

    /**
     * 块高度
     */
    @TableField("block_height")
    @Column(comment = "链当前块高度")
    private Long blockHeight;

    /**
     * 交易数
     */
    @TableField("tx_count")
    @Column(comment = "链上的总交易数")
    private Long txCount;

    /**
     * 创世块yaml
     */
    @TableField(value = "genesis_block", jdbcType = JdbcType.BLOB)
    @Column(comment = "创世快文件")
    private byte[] genesisBlock;

}