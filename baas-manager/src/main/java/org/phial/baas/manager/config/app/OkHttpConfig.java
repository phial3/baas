package org.phial.baas.manager.config.app;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.phial.baas.service.util.OkHttpUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Resource
    private OkHttpConfiguration okHttpConfiguration;

    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectionPool(pool())
                .connectTimeout(okHttpConfiguration.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)
                .readTimeout(okHttpConfiguration.getReadTimeoutMs(), TimeUnit.MILLISECONDS)
                .writeTimeout(okHttpConfiguration.getWriteTimeoutMs(),TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.setOkHttpClient(client);
        return client;
    }

    public ConnectionPool pool() {
        return new ConnectionPool(okHttpConfiguration.getMaxIdle(), okHttpConfiguration.getKeepAliveDurationSec(), TimeUnit.SECONDS);
    }

    @Component
    @ConfigurationProperties(prefix = "okhttp")
    static class OkHttpConfiguration {
        private Long connectTimeoutMs;
        private Long readTimeoutMs;
        private Long writeTimeoutMs;
        private Integer maxIdle;
        private Long keepAliveDurationSec;

        public Long getConnectTimeoutMs() {
            return connectTimeoutMs;
        }

        public void setConnectTimeoutMs(Long connectTimeoutMs) {
            this.connectTimeoutMs = connectTimeoutMs;
        }

        public Long getReadTimeoutMs() {
            return readTimeoutMs;
        }

        public void setReadTimeoutMs(Long readTimeoutMs) {
            this.readTimeoutMs = readTimeoutMs;
        }

        public Long getWriteTimeoutMs() {
            return writeTimeoutMs;
        }

        public void setWriteTimeoutMs(Long writeTimeoutMs) {
            this.writeTimeoutMs = writeTimeoutMs;
        }

        public Integer getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(Integer maxIdle) {
            this.maxIdle = maxIdle;
        }

        public Long getKeepAliveDurationSec() {
            return keepAliveDurationSec;
        }

        public void setKeepAliveDurationSec(Long keepAliveDurationSec) {
            this.keepAliveDurationSec = keepAliveDurationSec;
        }
    }

}
