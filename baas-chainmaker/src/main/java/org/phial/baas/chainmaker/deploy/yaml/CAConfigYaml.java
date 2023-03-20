package org.phial.baas.chainmaker.deploy.yaml;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CAConfigYaml {

    private LogConfig log_config;

    private DBConfig db_config;

    private BaseConfig base_config;

    private RootConfig root_config;

    private List<IntermediateConfig> intermediate_config;

    private List<AccessControlConfig> access_control_config;

    private JSONObject pkcs11_config;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LogConfig{
        /**
         * The log level
         */
        private String level;

        /**
         * The path to the log file
         */
        private String filename;

        /**
         * The maximum size of the log file before cutting (MB)
         */
        private Integer max_size;

        /**
         * The maximum number of days to retain old log files
         */
        private Integer max_age;

        /**
         * Maximum number of old log files to keep
         */
        private Integer max_backups;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DBConfig{
        private String user;

        private String password;

        private String ip;

        private String port;

        private String dbname;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BaseConfig{

        private String server_port;

        private String ca_type;

        private String expire_year;

        private String hash_type;

        private String key_type;

        private String can_issue_ca;

        private String[] provide_service_for;

        private String key_encrypt;

        private String access_control;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RootConfig{

        private List<Cert> cert;

        private Map<String, String> csr;


        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Cert{
            private String cert_type;

            private String cert_path;

            private String private_key_path;
        }


        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Csr{

            private String CN;

            private String O;

            private String OU;

            private String country;

            private String locality;

            private String province;
        }


        public void addCert(String cert_type, String cert_path, String private_key_path){

            if (this.cert == null){
                this.cert = new ArrayList<>();
            }

            Cert csrCert = new Cert(cert_type, cert_path, private_key_path);
            this.cert.add(csrCert);

        }

        public void setCsr(String CN, String O, String OU, String country, String locality,String province){
            this.csr = new HashMap<>();
            csr.put("CN", CN);
            csr.put("O", O);
            csr.put("OU", OU);
            csr.put("country", country);
            csr.put("locality", locality);
            csr.put("province", province);
        }

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IntermediateConfig {

        private Map<String, String> csr;

        private String private_key_pwd;


        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Csr{

            private String CN;

            private String O;

            private String OU;

            private String country;

            private String locality;

            private String province;
        }

        IntermediateConfig(String CN, String O, String OU, String country, String locality, String province, String private_key_pwd){
            this.csr = new HashMap<>();
            csr.put("CN", CN);
            csr.put("O", O);
            csr.put("OU", OU);
            csr.put("country", country);
            csr.put("locality", locality);
            csr.put("province", province);


            this.private_key_pwd = private_key_pwd;
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccessControlConfig {

        private String app_role;

        private String app_id;

        private String app_key;

    }




    public void setLogConfig(String level, String filename, Integer maxSize, Integer maxAge, Integer maxBackups){
        this.log_config = new LogConfig(level, filename, maxSize, maxAge, maxBackups);
    }

    public void setPkcs11() {
        this.pkcs11_config = new JSONObject();
        this.pkcs11_config.put("enabled", false);
    }


    public void setDBConfig(String user, String password, String ip, String port, String dbname){
        this.db_config = new DBConfig(user, password, ip, port, dbname);
    }

    public void setBaseConfig(Integer serverPort, String caType, String expireYear, String hashType, String keyType,
                              String canIssueCa, List<String> provideServiceFor, String keyEncrypt, String accessControl){

        this.base_config = new BaseConfig(serverPort + "", caType, expireYear, hashType, keyType, canIssueCa, provideServiceFor.toArray(new String[0]), keyEncrypt, accessControl);
    }


    public void addRootConfigCert(String cert_type, String cert_path, String private_key_path){

        if (this.root_config == null) {
            this.root_config = new RootConfig();
        }

        root_config.addCert(cert_type, cert_path, private_key_path);
    }




    public void setRootConfigCsr(String CN, String O, String OU, String country, String locality,String province){
        if (this.root_config == null) {
            this.root_config = new RootConfig();
        }

        root_config.setCsr(CN, O, OU, country, locality, province);
    }


    public void addAccessControlConfig(String app_role, String app_id, String app_key){
        if (this.access_control_config == null){
            this.access_control_config = new ArrayList<>();
        }

        AccessControlConfig accessControlConfig = new AccessControlConfig(app_role, app_id, app_key);

        this.access_control_config.add(accessControlConfig);
    }


    public void addIntermediateConfig(String CN, String O, String OU, String country, String locality, String province, String private_key_pwd){
        if (this.intermediate_config == null){
            this.intermediate_config = new ArrayList<>();
        }

        IntermediateConfig intermediateConfig = new IntermediateConfig(CN, O, OU, country, locality, province, private_key_pwd);
        this.intermediate_config.add(intermediateConfig);
    }



}
