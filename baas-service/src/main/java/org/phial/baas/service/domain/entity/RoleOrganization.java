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
@TableName("baas_role_organization")
@EqualsAndHashCode(callSuper = true)
public class RoleOrganization extends Entity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
    private Long id;
    private String roleId; //角色ID主键
    private String orgId; //组织机构ID主键
}
