package org.phial.baas.service.service.bc;

import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.domain.entity.rbac.SysUser;

public interface UserCertService {

    SysUser getUser(String uniqueId, CryptoEnum.CryptoUserType userType);
}
