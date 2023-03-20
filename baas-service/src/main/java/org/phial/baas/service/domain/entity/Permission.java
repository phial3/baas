package org.phial.baas.service.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("baas_permission")
@EqualsAndHashCode(callSuper = true)
public class Permission extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
    private Long id; //  //权限ID，主键
    //private String permission_id;
    private String tenantId; //租户ID
    //private String permission_name; //权限名称
    private String name; //权限名称
    private String description; //权限描述
    private Integer status; //状态（1:启用, 0:禁用等）
}
