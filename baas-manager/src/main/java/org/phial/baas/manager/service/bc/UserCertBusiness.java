package org.phial.baas.manager.service.bc;

import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.phial.baas.service.service.bc.UserCertService;
import org.springframework.stereotype.Component;

/**
 * @author admin
 */
@Component
public class UserCertBusiness extends ConsoleBaseBusiness<SysUser> implements UserCertService {


    @Override
    public SysUser getUser(String uniqueId, CryptoEnum.CryptoUserType userType) {
        return null;
    }
}
