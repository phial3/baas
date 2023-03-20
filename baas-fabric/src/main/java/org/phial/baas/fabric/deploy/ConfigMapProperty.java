package org.phial.baas.fabric.deploy;

import com.umetrip.blockchain.fabric.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigMapProperty {
    //容器内/etc/hyperledger/fabric/tls/ca.crt
    //需要tls/ca.crt
    private String path;

    private byte[] value;

    public String getAlias(String dnsName) {
        return path.replaceAll("[^\\da-zA-Z]","") + dnsName;
    }

    public static List<ConfigMapProperty> createConfigMapProperties(String basePath) {
        List<ConfigMapProperty> fileList = new ArrayList<>();
        return listFile(new File(basePath), fileList, basePath);
    }

    private static List<ConfigMapProperty> listFile(File dir, List<ConfigMapProperty> fileList, String basePath){
        if (dir.isFile()) {
            fileList.add(new ConfigMapProperty(dir.getPath().replace(basePath, ""), FileUtil.readFileToByte(dir.getPath())));
            return fileList;
        }
        if (dir.listFiles() == null || dir.listFiles().length == 0) {
            return fileList;
        }
        for (File fileItem : Objects.requireNonNull(dir.listFiles())){
            if (fileItem.isDirectory()){
                listFile(fileItem, fileList, basePath);
            }else {
                //不读mac文件
                if (".DS_Store".equals(fileItem.getName())) {
                    continue;
                }
                fileList.add(new ConfigMapProperty(fileItem.getPath().replace(basePath, ""), FileUtil.readFileToByte(fileItem.getPath())));
            }
        }
        return fileList;
    }
}
