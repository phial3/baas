package org.phial.baas.fabric.deploy;

import com.umetrip.blockchain.fabric.constants.CommonConstant;
import com.umetrip.blockchain.fabric.constants.enums.CryptoEnum;
import com.umetrip.blockchain.fabric.domain.crypto.CryptoFactory;
import com.umetrip.blockchain.fabric.domain.entity.FabricCert;
import com.umetrip.blockchain.fabric.domain.entity.FabricUser;
import com.umetrip.blockchain.fabric.domain.member.UserManagerFactory;
import com.umetrip.blockchain.fabric.domain.member.config.ClientCachePool;
import com.umetrip.blockchain.fabric.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NewCertBatch {

    private final List<CertPath> newFiles = new LinkedList<>();

    private final Map<String, CertKey> adminCerts = new HashMap<>();

    public void addNewFile(String dnsName, String prePath, String subPath) {
        newFiles.add(new CertPath(dnsName, prePath, subPath));
    }

    @Data
    @AllArgsConstructor
    private static class CertPath {
        String dnsName;
        String prePath;
        String subPath;
    }

    @Data
    private static class CertKey {
        String cert;
        String key;
    }

    public void saveNewFile() {
        List<FabricCert> insertList = new LinkedList<>();
        for (CertPath certs : newFiles) {
            listFile(certs.dnsName, new File(certs.prePath + certs.subPath), insertList, certs.prePath);
        }

        if (insertList.isEmpty()) {
            throw new RuntimeException("cert list is empty");
        }

        //写admin证书
        UserManagerFactory userFactory = UserManagerFactory.getInstance();
        for (Map.Entry<String, CertKey> entry : adminCerts.entrySet()) {
            String adminName = CommonConstant.getAdminCaId(entry.getKey());
            FabricUser fabricUser = ClientCachePool.getInstance().selectFabricUser(adminName, CryptoEnum.CryptoUserType.ADMIN);
            if (fabricUser != null) {
                continue;
            }
            CertKey certKey = entry.getValue();
            FabricUser admin = FabricUser.builder()
                    .cert(certKey.getCert())
                    .privateKey(certKey.getKey())
                    .caId(adminName)
                    .orgDomain(CommonConstant.getOrgDomain(entry.getKey()))
                    .build();
            userFactory.createBusinessUser(admin, adminName);
        }

        //写其他证书
        CryptoFactory cryptoFactory = CryptoFactory.getInstance();
        if (insertList.size() > 0) {
            cryptoFactory.saveNewCerts(insertList);
        }
    }

    private List<FabricCert> listFile(String dnsName, File dir, List<FabricCert> fileList, String basePath) {
        if (dir == null || dir.listFiles() == null) {
            return fileList;
        }
        for (File fileItem : dir.listFiles()) {
            if (fileItem.isDirectory()) {
                listFile(dnsName, fileItem, fileList, basePath);
            } else {
                //不读mac文件
                if (".DS_Store".equals(fileItem.getName())) {
                    continue;
                }
                getAdminCert(dnsName, fileItem);
                String content = FileUtil.readFile(fileItem.getPath());
                fileList.add(
                        FabricCert.builder()
                                .content(content)
                                .path(fileItem.getPath().replace(basePath, ""))
                                .dnsName(dnsName)
                                .build()
                );
            }
        }
        return fileList;
    }

    public void getAdminCert(String dnsName, File fileItem) {
        String path = fileItem.getPath();
        if (!path.contains("/users/")) {
            return;
        }
        CertKey certKey = adminCerts.get(dnsName);
        if (certKey == null) {
            certKey = new CertKey();
            adminCerts.put(dnsName, certKey);
        }

        //user目录下面的
        if (path.contains("/signcerts/")) {
            certKey.setCert(FileUtil.readFile(fileItem.getPath()));
        }
        if (path.contains("/keystore/")) {
            certKey.setKey(FileUtil.readFile(fileItem.getPath()));
        }
    }


}
