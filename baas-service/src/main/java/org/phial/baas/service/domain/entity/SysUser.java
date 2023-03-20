package org.phial.baas.service.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.constant.SysUserStatusEnum;

import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("baas_user")
@EqualsAndHashCode(callSuper = true)
public class SysUser extends Entity<Long> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @Column(comment = "主键id",isAutoIncrement = true)
    protected Long id;

    //protected String user_id; //用户ID，主键
    protected String tenantId; //租户ID
    protected String username;//用户名
    protected String password; //密码
    protected String email; //电子邮箱
    protected String phone; //手机号
    protected String address; //联系地址

    // 用户类型: 企业; 个人
    protected CryptoEnum.CryptoUserType userType;

    /**
     * 身份ID
     */
    protected String enrollmentId;

    /**
     * 状态
     */
    protected SysUserStatusEnum status;

    /**
     * 失效时间
     */
    protected Date expirationDate;
}
