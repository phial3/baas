package org.phial.baas.chainmaker.deploy.yaml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.phial.baas.service.constant.CommonChainmakerConstant;

@Data
public class ChainmakerConfigYaml {

    private JSONObject yaml;

    public ChainmakerConfigYaml(String auth_type) {
        this.yaml = new JSONObject();
        this.yaml.put("auth_type", auth_type);

        setPprof(false, 1234);
    }

    public void setLog(String config_file) {
        JSONObject log = new JSONObject();
        log.put("config_file", config_file);
        this.yaml.put("log", log);
    }

    public void setCryptoEngine(String engine) {
        this.yaml.put("crypto_engine", engine);
    }

    public void setConsensus(Boolean async_wal_save, Integer snap_count, Integer ticker) {
        JSONObject consensus = new JSONObject();
        JSONObject raft = new JSONObject();
        raft.put("async_wal_save", async_wal_save);
        raft.put("snap_count", snap_count);
        raft.put("ticker", ticker);
        consensus.put("raft", raft);
        this.yaml.put("consensus", consensus);
    }

    public void addBlockChain(String chainId, String genesis) {
        JSONArray blockchain = this.yaml.getJSONArray("blockchain");
        if (blockchain == null) {
            blockchain = new JSONArray();
            this.yaml.put("blockchain", blockchain);
        }

        JSONObject oneChain = new JSONObject();
        oneChain.put("chainId", chainId);
        oneChain.put("genesis", genesis);

        blockchain.add(oneChain);
    }


    public void setMonitor(boolean enabled, Integer port) {
        JSONObject monitor = new JSONObject();
        monitor.put("enabled", enabled);
        monitor.put("port", port);
        this.yaml.put("monitor", monitor);
    }

    public void setVM(boolean enabled,
                      String dockerGoVmMoundPath, String dockerVmHost,
                      String goVmMountPath, String goVmHost,
                      Integer maxSendMsgSize, Integer maxRecvMsgSize, Integer maxConnection) {
        JSONObject vm = new JSONObject();

        JSONObject dockerGo = setVmDockerGo(enabled, dockerGoVmMoundPath, "../log/dockervm_log", dockerVmHost, CommonChainmakerConstant.DEFAULT_VM_ENGINE_PORT, maxSendMsgSize, maxRecvMsgSize, maxConnection);
        vm.put("docker_go", dockerGo);

//        JSONObject go = setVmGo(enabled, goVmMountPath, "../log/vm_log", goVmHost, NodeFactory.DEFAULT_DOCKER_GO_VM_PORT, maxSendMsgSize, maxRecvMsgSize, maxConnection);
//        vm.put("go", go);

        this.yaml.put("vm", vm);
    }

    // 2.2.1 升级 2.3.0 中，chainmaker.yml将原来vm下内容移到vm下docker_go部分
    private JSONObject setVmDockerGo(boolean enabled, String mountPath, String logPath, String host, Integer port, Integer maxSendMsgSize, Integer maxRecvMsgSize, Integer maxConnection) {
        JSONObject dg = new JSONObject();
        dg.put("enable_dockervm", enabled);
        if (enabled) {
            dg.put("uds_open", false);
            dg.put("docker_vm_host", host);
            dg.put("docker_vm_port", port);
            dg.put("max_send_msg_size", maxSendMsgSize);
            dg.put("max_recv_msg_size", maxRecvMsgSize);
            dg.put("max_connection", maxConnection);
            dg.put("dockervm_mount_path", mountPath);
            dg.put("dockervm_log_path", logPath);
            dg.put("log_level", "DEBUG");
            dg.put("log_in_console", false);
        }
        return dg;
    }

    // 2.3.0 中，chainmaker.yml将新增vm下go部分
    private JSONObject setVmGo(boolean enabled, String mountPath, String logPath, String host, Integer port, Integer maxSendMsgSize, Integer maxRecvMsgSize, Integer maxConnection) {
        JSONObject go = new JSONObject();
        go.put("enable", enabled);
        if (enabled) {
            go.put("data_mount_path", mountPath);
            go.put("log_mount_path", logPath);
            go.put("protocol", "tcp");
            go.put("log_in_console", false);
            go.put("log_level", "DEBUG");
            go.put("max_send_msg_size", maxSendMsgSize);
            go.put("max_recv_msg_size", maxRecvMsgSize);
            go.put("dial_timeout", 10);  // server的最大连接超时时间, 默认10s
            go.put("max_concurrency", maxConnection);

            JSONObject runtimeServer = new JSONObject();
            runtimeServer.put("port", CommonChainmakerConstant.DEFAULT_GO_VM_RUNTIME_SERVER_PORT);
            go.put("runtime_server", runtimeServer);

            JSONObject contractEngine = new JSONObject();
            contractEngine.put("host", host);
            contractEngine.put("port", port);
            contractEngine.put("max_connection", 5);
            go.put("contract_engine", contractEngine);
        }
        return go;
    }

    //过滤器
    public void setTxFilter(String path) {
        JSONObject txFilter = new JSONObject();
        txFilter.put("type", 3);
        JSONObject sharding = new JSONObject();
        sharding.put("length", 5);
        sharding.put("timeout", 3);
        JSONObject snapshot = new JSONObject();
        snapshot.put("type", 0);
        JSONObject blockHeight = new JSONObject();
        blockHeight.put("interval", 10);
        snapshot.put("block_height", blockHeight);
        JSONObject timed = new JSONObject();
        timed.put("interval", 10);
        snapshot.put("timed", timed);
        snapshot.put("path", path);
        sharding.put("snapshot", snapshot);

        JSONObject birdsNest = new JSONObject();
        birdsNest.put("length", 10);
        JSONObject rules = new JSONObject();
        rules.put("absolute_expire_time", 172800);
        birdsNest.put("rules", rules);
        JSONObject cuckoo = new JSONObject();
        cuckoo.put("tags_per_bucket", 2);
        cuckoo.put("bits_per_item", 11);
        cuckoo.put("max_num_keys", 2000000);
        cuckoo.put("table_type", 0);
        birdsNest.put("cuckoo", cuckoo);
        sharding.put("birds_nest", birdsNest);

        txFilter.put("sharding", sharding);
        this.yaml.put("tx_filter", txFilter);
    }

    public void setNet(String listen_addr, String provider, Boolean enabled, String cert_file, String priv_key_file, Integer maxSendSize, Integer maxRecvSize) {
        JSONObject net = new JSONObject();
        net.put("listen_addr", listen_addr);
        net.put("provider", provider);
        net.put("max_send_msg_size", maxSendSize);
        net.put("max_recv_msg_size", maxRecvSize);

        JSONObject tls = new JSONObject();
        tls.put("enabled", enabled);
        tls.put("cert_file", cert_file);
        tls.put("priv_key_file", priv_key_file);

        net.put("tls", tls);
        this.yaml.put("net", net);
    }

    public void addSeed(String seed) {
        JSONObject net = this.yaml.getJSONObject("net");
        JSONArray seeds = net.getJSONArray("seeds");
        if (seeds == null) {
            seeds = new JSONArray();
            net.put("seeds", seeds);
        }

        seeds.add(seed);
    }


    public void setNode(String org_id, Integer cert_cache_size, String cert_file, String priv_key_file) {
        JSONObject node = new JSONObject();
        node.put("org_id", org_id);
        node.put("cert_cache_size", cert_cache_size);
        node.put("cert_file", cert_file);
        node.put("priv_key_file", priv_key_file);

        //硬件加密
        JSONObject pkcs11 = new JSONObject();
        pkcs11.put("enabled", false);
        node.put("pkcs11", pkcs11);

        //开启快速同步
        JSONObject fastSync = new JSONObject();
        fastSync.put("enabled", true);
        fastSync.put("min_full_blocks", 10);
        node.put("fast_sync", fastSync);

        this.yaml.put("node", node);
    }

    public void setPprof(Boolean enabled, Integer port) {
        JSONObject pprof = new JSONObject();
        pprof.put("enabled", enabled);
        pprof.put("port", port);
        this.yaml.put("pprof", pprof);
    }

    public void setRpc(Integer check_chain_conf_trust_roots_change_interval, Integer port, String provider,
                       Boolean ratelimit_enabled, Integer ratelimit_token_bucket_size, Integer ratelimit_token_per_second, Integer ratelimit_type,
                       Integer subscriber_ratelimit_token_bucket_size, Integer subscriber_ratelimit_token_per_second,
                       String tls_mode, String tls_cert_file, String tls_priv_key_file) {
        JSONObject rpc = new JSONObject();
        rpc.put("check_chain_conf_trust_roots_change_interval", check_chain_conf_trust_roots_change_interval);
        rpc.put("port", port);
        rpc.put("provider", provider);

        JSONObject ratelimit = new JSONObject();
        ratelimit.put("enabled", ratelimit_enabled);
        ratelimit.put("token_bucket_size", ratelimit_token_bucket_size);
        ratelimit.put("token_per_second", ratelimit_token_per_second);
        ratelimit.put("type", ratelimit_type);
        rpc.put("ratelimit", ratelimit);

        JSONObject subscriber = new JSONObject();
        JSONObject subscriber_ratelimit = new JSONObject();
        subscriber_ratelimit.put("token_bucket_size", subscriber_ratelimit_token_bucket_size);
        subscriber_ratelimit.put("token_per_second", subscriber_ratelimit_token_per_second);
        subscriber.put("ratelimit", subscriber_ratelimit);
        rpc.put("subscriber", subscriber);

        JSONObject tls = new JSONObject();
        tls.put("mode", tls_mode);
        tls.put("cert_file", tls_cert_file);
        tls.put("priv_key_file", tls_priv_key_file);
        rpc.put("tls", tls);

        this.yaml.put("rpc", rpc);
    }


    public void setTxpool(String pool_type, Integer max_config_txpool_size, Integer max_txpool_size) {
        JSONObject txpool = new JSONObject();
        txpool.put("pool_type", pool_type);
        txpool.put("max_config_txpool_size", max_config_txpool_size);
        txpool.put("max_txpool_size", max_txpool_size);
        this.yaml.put("txpool", txpool);
    }

    public void setStorage(String store_path, Integer unarchive_block_height, Boolean disable_contract_eventdb, Boolean disable_block_file_db, long rebuildHeight) {
        JSONObject storage = new JSONObject();
        storage.put("store_path", store_path);
        storage.put("unarchive_block_height", unarchive_block_height);
        storage.put("disable_contract_eventdb", disable_contract_eventdb);
        storage.put("disable_block_file_db", disable_block_file_db);
        storage.put("logdb_segment_async", false);

        if (rebuildHeight > 0) {
            storage.put("rebuild_block_height", rebuildHeight);
        }

//        storage.put("encryptor", "SM4");
//        storage.put("encrypt_key", "123456");

        this.yaml.put("storage", storage);
    }

    public void setBlockdbConfig(String provider, String store_path, String configName) {
        JSONObject storage = this.yaml.getJSONObject("storage");

        JSONObject dbConfig = new JSONObject();
        dbConfig.put("provider", provider);
        JSONObject config = new JSONObject();
        config.put("store_path", store_path);
        dbConfig.put(provider + "_config", config);

        storage.put(configName, dbConfig);
    }


}
