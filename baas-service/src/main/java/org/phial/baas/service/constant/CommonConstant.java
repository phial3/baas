package org.phial.baas.service.constant;


import org.apache.commons.lang3.StringUtils;

/**
 * @author gyf
 * @date 2022/12/12
 */
public interface CommonConstant {

    /**
     * time format
     */
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String DATE_PATTERN = "yyyy-MM-dd";
    String TIME_PATTERN = "HH:mm:ss";

    /**
     * default constant
     */
    int DEFAULT_PAGE_NO = 1;
    int DEFAULT_PAGE_SIZE = 10000;

    /**
     *
     */
    String ILLEGAL_ACCESS = "系统不支持当前域名的访问！";

    static String getK8sNodeIp() {
        return StringUtils.isBlank(System.getenv("HOST_NODE_IP")) ? "127.0.0.1" : System.getenv("HOST_NODE_IP");
    }

    static String getNodeGrpcUrl(long rpcK8sPort) {
        return "grpcs://" + getK8sNodeIp() + ":" + rpcK8sPort;
    }

    static String getNodeHttpUrl(long rpcK8sPort) {
        return "http://" + getK8sNodeIp() + ":" + rpcK8sPort;
    }
}
