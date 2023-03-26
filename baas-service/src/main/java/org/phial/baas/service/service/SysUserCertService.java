package org.phial.baas.service.service;

import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.domain.entity.rbac.SysUser;

public interface SysUserCertService {

    SysUser getUser(String uniqueId, CryptoEnum.CryptoUserType userType);
}
