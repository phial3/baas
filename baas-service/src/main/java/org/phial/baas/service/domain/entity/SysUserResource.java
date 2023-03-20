package org.phial.baas.service.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("baas_user_resource")
@EqualsAndHashCode(callSuper = true)
public class SysUserResource extends Entity<Long> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
    private Long id;
    private String userId; //用户ID，主键
    private String resourceId; //资源ID，主键
}
