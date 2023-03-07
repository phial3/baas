package org.phial.baas.api.service;

import org.phial.baas.api.constant.CryptoEnum;
import org.phial.baas.api.domain.entity.SysUser;

public interface SysUserCertService {

    SysUser getUser(String uniqueId, CryptoEnum.CryptoUserType userType);
}
