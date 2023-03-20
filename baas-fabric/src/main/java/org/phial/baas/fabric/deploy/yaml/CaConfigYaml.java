package org.phial.baas.fabric.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umetrip.blockchain.fabric.constants.CommonConstant;
import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

@Data
public class CaConfigYaml {

    private JSONObject yaml;

    public CaConfigYaml(String version, boolean debug, Long port) {
        this.yaml = new JSONObject();
        this.yaml.put("version", version);
        this.yaml.put("port", port);


        JSONObject cors = new JSONObject();
        cors.put("enabled", false);
        cors.put("origins", Collections.singletonList("*"));
        this.yaml.put("cors", cors);

        this.yaml.put("debug", debug);

        this.yaml.put("crlsizelimit", 512000);

        JSONObject tls = new JSONObject();
        tls.put("enabled", false);
        this.yaml.put("tls", tls);

        JSONObject crl = new JSONObject();
        crl.put("expiry", "24h");
        this.yaml.put("crl", crl);

        JSONObject registry = new JSONObject();
        registry.put("maxenrollments", -1);

        JSONArray identities = new JSONArray();
        JSONObject identity = new JSONObject();
        identity.put("name", CommonConstant.ADMIN);
        identity.put("pass", CommonConstant.ADMIN_PASSWD);
        identity.put("type", "client");
        identity.put("affiliation", "");
        JSONObject attrs = new JSONObject();
        attrs.put("hf.Registrar.Roles", "*");
        attrs.put("hf.Registrar.DelegateRoles", "*");
        attrs.put("hf.Revoker", true);
        attrs.put("hf.IntermediateCA", true);
        attrs.put("hf.GenCRL", true);
        attrs.put("hf.Registrar.Attributes", "*");
        attrs.put("hf.AffiliationMgr", true);
        identity.put("attrs", attrs);
        identities.add(identity);
        registry.put("identities", identities);
        this.yaml.put("registry", registry);

        JSONObject ldap = new JSONObject();
        ldap.put("enabled", false);
        this.yaml.put("ldap", ldap);


        JSONObject affiliations = new JSONObject();
        affiliations.put("org1", Arrays.asList("department1", "department2"));
        affiliations.put("org2", Arrays.asList("department1"));
        this.yaml.put("affiliations", affiliations);


        JSONObject idemix = new JSONObject();
        idemix.put("rhpoolsize", 1000);
        idemix.put("nonceexpiration", "15s");
        idemix.put("noncesweepinterval", "15m");
        this.yaml.put("idemix", idemix);


        JSONObject cfg = new JSONObject();
        JSONObject cfgidentities = new JSONObject();
        cfgidentities.put("passwordattempts", 10);
        cfg.put("identities", cfgidentities);
        this.yaml.put("cfg", cfg);


        JSONObject metrics = new JSONObject();
        metrics.put("provider", "disabled");
        this.yaml.put("metrics", metrics);
    }





    public void setBccsp(String crypto, String hash, int security) {
        JSONObject bccsp = new JSONObject();
        bccsp.put("default", crypto);

        JSONObject lib = new JSONObject();
        lib.put("hash", hash);
        lib.put("security", security);
        JSONObject filekeystore = new JSONObject();
        filekeystore.put("keystore", "msp/keystore");
        lib.put("filekeystore", filekeystore);
        bccsp.put(crypto.toLowerCase(Locale.ROOT), lib);

        this.yaml.put("bccsp", bccsp);
    }

    public void setOperations(Long port) {
        JSONObject operations = new JSONObject();
        operations.put("listenAddress", "127.0.0.1:" + port);
        JSONObject tls = new JSONObject();
        tls.put("enabled", false);
        operations.put("tls", tls);

        this.yaml.put("operations", operations);
    }


    public void setSigning(String expiry) {
        JSONObject signing = new JSONObject();

        JSONObject defaultSigning = new JSONObject();
        defaultSigning.put("usage", Arrays.asList("digital signature"));
        defaultSigning.put("expiry", expiry);
        signing.put("default", defaultSigning);

        JSONObject profiles = new JSONObject();

        JSONObject ca = new JSONObject();
        ca.put("usage", Arrays.asList("cert sign", "crl sign"));
        ca.put("expiry", expiry);
        JSONObject caconstraint = new JSONObject();
        caconstraint.put("isca", true);
        caconstraint.put("maxpathlen", 0);
        ca.put("caconstraint", caconstraint);
        profiles.put("ca", ca);

        JSONObject tls = new JSONObject();
        tls.put("usage", Arrays.asList("signing", "key encipherment", "server auth", "client auth", "key agreement"));
        tls.put("expiry", expiry);
        profiles.put("tls", tls);

        signing.put("profiles", profiles);
        this.yaml.put("signing", signing);
    }

    //生成root ca用的，目前拿可执行文件生成root ca用不到
    public void setCsr(String cn, String c, String st, String o, String ou, String host, String expiry) {
        JSONObject csr = new JSONObject();
        csr.put("cn", cn);
        JSONObject keyrequest = new JSONObject();
        keyrequest.put("algo", "ecdsa");
        keyrequest.put("size", 256);
        csr.put("keyrequest", keyrequest);

        JSONObject name = new JSONObject();
        name.put("C", c);
        name.put("ST", st);
        name.put("O", o);
        name.put("OU", ou);
        csr.put("names", Arrays.asList(name));

        csr.put("hosts", Arrays.asList(host, "localhost"));

        JSONObject ca = new JSONObject();
        ca.put("expiry", expiry);
        ca.put("pathlength", 1);
        csr.put("ca", ca);

        this.yaml.put("csr", csr);
    }


    public void setDB(String dataSource) {
        JSONObject db = new JSONObject();
        db.put("type", "mysql");
        db.put("datasource", dataSource);
        JSONObject tls = new JSONObject();
        tls.put("enabled", false);
        db.put("tls", tls);
        this.yaml.put("db", db);
    }
}
