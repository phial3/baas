package org.phial.baas.manager.config.app;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 系统配置。接受YAML文件中的配置
 * @author mayanjun
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "app-config")
public class AppConfig implements InitializingBean {

    /**
     * 设备最小心跳周期：秒
     */
    public static final Integer MIN_HEARTBEAT_PERIOD = 10;

    /**
     * 默认的API时间戳校验容忍度
     */
    private static final int DEFAULT_API_TIMESTAMP_TOLERANCE = 30;

    private String buildVersion;

    private String profile;

    /**
     * 当前系统使用的域名
     */
    private String domain;
    private String mobileDomain;

    /**
     * 后台登录的TOKEN名称
     */
    private String tokenCookieName = "token";
    private String mobileTokenCookieName = "token";

    private AESKey consoleAesKey;

    private AESKey mobileAesKey;

    private AESKey clusterAesKey;

    /**
     * 系统上传文件的根目录
     */
    private String uploadDir = "/data/upload";

    private String systemName = "后台管理系统";

    private String redisHost = "127.0.0.1";
    private int redisPort = 6379;
    private boolean redisEnabled = false;

    private String nodeName;
    private String[] clusterNodes;
    private boolean clusterEnabled;
    private List<String> clusterNodesList = new ArrayList<>();

    /**
     * 是否验证API时间戳的时效性，如果开启则调用接口时传入的时间戳参数与服务器时间差不能大于 apiTimestampTolerance 秒
     */
    private boolean verifyApiTimestamp = true;

    private int apiTimestampTolerance = DEFAULT_API_TIMESTAMP_TOLERANCE;

    private Boolean autoShot = true;

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    /**
     * 获取 clusterEnabled
     *
     * @return clusterEnabled
     */
    public boolean isClusterEnabled() {
        return clusterEnabled;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.isBlank(nodeName)) {
            throw new IllegalArgumentException("Node name is not specified");
        }

        // check value
        Assert.notBlank(domain, "请配置系统域名");
        Assert.isTrue(consoleAesKey != null && consoleAesKey.verify(), "请配置后台登录AES秘钥");
        Assert.isTrue(mobileAesKey != null && mobileAesKey.verify(), "请配置移动端登录AES秘钥");
        Assert.isTrue(clusterAesKey != null && clusterAesKey.verify(), "请配置集群接口AES秘钥");

        if (clusterNodes != null && clusterNodes.length > 0) {
            clusterEnabled = true;
        } else {
            clusterNodes = new String[]{"127.0.0.1"};
        }

        clusterNodesList = Arrays.asList(clusterNodes);
        log.info("Cluster server is enabled: hosts={}", StringUtils.join(clusterNodes, ","));

        // 修正参数
        if (this.verifyApiTimestamp) {
            if (apiTimestampTolerance <= 0) {
                apiTimestampTolerance = DEFAULT_API_TIMESTAMP_TOLERANCE; // 强制修正为5秒
            }
        }
    }

}
