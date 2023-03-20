package org.phial.baas.chainmaker.domain;

import org.chainmaker.sdk.User;
import org.phial.baas.service.domain.entity.SysUser;

public class ChainmakerUser extends SysUser {

    private User defaultAdmin;

    private String caCrt;

    private Long caSn;

    private Crypto defaultAdminCrypto;
}
