package org.phial.baas.fabric.factory;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.phial.baas.fabric.domain.CANodeDomain;
import org.phial.baas.fabric.domain.FabricUser;
import org.phial.baas.service.constant.CommonFabricConstant;
import org.phial.baas.service.constant.CryptoEnum;
import org.phial.baas.service.domain.entity.Node;
import org.phial.baas.service.domain.entity.Organization;
import org.phial.baas.service.domain.entity.SysUser;
import org.phial.baas.service.service.OrganizationService;
import org.phial.baas.service.service.SysUserCertService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringWriter;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public final class CryptoFactory {

    private final Map<String, HFCAClient> hfcaClientCache = new HashMap<>();

    @Resource
    private SysUserCertService sysUserCertService;

    @Resource
    private OrganizationService organizationService;

    public static String printOpensslPemFormatKeyFileContent(Key key, String keyType) throws IOException {
        PemObject pem = new PemObject(keyType, key.getEncoded());
        StringWriter str = new StringWriter();
        PemWriter pemWriter = new PemWriter(str);
        pemWriter.writeObject(pem);
        pemWriter.close();
        str.close();
        return str.toString();
    }

    public FabricUser createRegister(CANodeDomain caNode, CryptoEnum.CryptoUserType userType) {
        Organization organization = caNode.getFabricOrg();
        String orgDomain = organization.getDomain();
        try {
            HFCAClient caClient = getHFCAClient(caNode.getFabricNode());
            Enrollment enrollment = caClient.enroll(CommonFabricConstant.ADMIN, CommonFabricConstant.ADMIN_PASSWD);

            String registerCaId = organization.getRegisterId();

            return new FabricUser(
                    enrollment.getCert(),
                    printOpensslPemFormatKeyFileContent(enrollment.getKey(), "PRIVATE KEY"),
                    registerCaId,
                    CommonFabricConstant.ADMIN_PASSWD,
                    orgDomain,
                    userType,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private HFCAClient getHFCAClient(Node caNode) throws Exception {
        HFCAClient hfcaClient = hfcaClientCache.get(caNode.getOrgDomain());
        if (hfcaClient == null) {
            Properties properties = new Properties();
            hfcaClient = HFCAClient.createNewInstance(caNode.getNodeHttpUrl(), properties);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite(properties);
            hfcaClient.setCryptoSuite(cryptoSuite);
            hfcaClientCache.put(caNode.getOrgDomain(), hfcaClient);
        }
        return hfcaClient;
    }


    public String revokeCrypto(CANodeDomain caNodeDomain, String caId, CryptoEnum.CryptoUserType userType) {
        Organization organization = caNodeDomain.getFabricOrg();
        String orgDomain = organization.getDomain();
        try {
            // admin
            SysUser adminUser = sysUserCertService.getUser(organization.getRegisterId(), CryptoEnum.CryptoUserType.REGISTER);
            Assert.notNull(adminUser, "admin not exist");

            SysUser targetUser = sysUserCertService.getUser(caId, userType);
            Assert.notNull(targetUser, "user not exist:" + caId);

//            if (!StringUtils.isEmpty(targetUser.getCrl())) {
//                return targetUser.getCrl();
//            }

//            HFCAClient caClient = getHFCAClient(caNodeDomain.getFabricNode());
//            String crl = caClient.revoke(fabricUser.parseUserContext(), targetUser.getEnrollment(), "derb", true);
//            com.baidu.blockchain.fabric.domain.member.User user = targetUser.parseUser(userType);
//            user.setCrl(crl);
//            user.update();

            //todo 需要记一下crl，否则无法再次生成crl
            //return crl;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public FabricUser createCrypto(CANodeDomain caNode, String caId, Map<String, Object> attr, CryptoEnum.CryptoUserType userType) {
        Organization organization = caNode.getFabricOrg();
        String orgDomain = organization.getDomain();
        try {
            //示例化client
            HFCAClient caClient = getHFCAClient(caNode.getFabricNode());

            //admin
//            SysUser adminUser = ClientCachePool.getInstance().selectFabricUser(organization.getRegisterId(), CryptoEnum.CryptoUserType.REGISTER);
//            if (adminUser == null) {
//                fabricUser = createRegister(caNode, userType);
//                UserManagerFactory userFactory = UserManagerFactory.getInstance();
//                userFactory.createBusinessUser(fabricUser, "register." + orgDomain);
//            }


//            Enrollment adminEnroll = new X509Enrollment(fabricUser.parsePrivateKey(), fabricUser.getCert());
//            User admin = new GatewayUser(CommonFabricConstant.ADMIN, organization.getMspID(), adminEnroll);

            //开始register
            RegistrationRequest registrationRequest = new RegistrationRequest(caId);
            registrationRequest.setType(userType.getUserType());
            //attr
            if (!CollectionUtils.isEmpty(attr)) {
                for (String key : attr.keySet()) {
                    registrationRequest.addAttribute(new Attribute(key, attr.get(key).toString()));
                }
            }
            //String caSecret = caClient.register(registrationRequest, admin);
            String caSecret = caClient.register(registrationRequest, null);

            //开始enroll
            EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
            if (!CollectionUtils.isEmpty(attr)) {
                for (String key : attr.keySet()) {
                    enrollmentRequest.addAttrReq(key).setOptional(false);
                }
            }
            Enrollment enrollment = caClient.enroll(caId, caSecret, enrollmentRequest);

            return new FabricUser(
                    enrollment.getCert(),
                    printOpensslPemFormatKeyFileContent(enrollment.getKey(), "PRIVATE KEY"),
                    caId,
                    caSecret,
                    orgDomain,
                    userType,
                    attr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
