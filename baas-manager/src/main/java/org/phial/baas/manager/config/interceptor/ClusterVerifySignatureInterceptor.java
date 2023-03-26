package org.phial.baas.manager.config.interceptor;

import org.mayanjun.core.Assert;
import org.mayanjun.core.Status;
import org.mayanjun.myrest.interceptor.AnnotationBasedHandlerInterceptor;
import org.phial.baas.manager.config.app.AppConfig;
import org.phial.baas.manager.util.SignUtils;
import org.phial.baas.manager.util.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Component
public class ClusterVerifySignatureInterceptor extends AnnotationBasedHandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterVerifySignatureInterceptor.class);

    public static final Status SIGN_ERROR       = new Status(3100, "签名参数错误");
    public static final Status TIMESTAMP_ERROR  = new Status(3101, "时间戳参数错误");
    public static final Status RANDOM_ERROR     = new Status(3102, "随机数参数错误");

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private AppConfig config;

    @Override
    public int getOrder() {
        return InterceptorOrder.CLUSTER_VERIFY_INTERCEPTOR.ordinal();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        int sp = request.getServerPort();
        Assert.isTrue(sp == serverPort, "非法请求");
        Assert.notBlank(request.getParameter("timestamp"), TIMESTAMP_ERROR);
        Assert.notBlank(request.getParameter("random"), RANDOM_ERROR);
        String sign = request.getParameter("sign");
        Assert.notBlank(sign, SIGN_ERROR);

        Map<String, String[]> params = SignUtils.getParameters(request);
        String computedSign = SignUtils.computeSignParams(params, config.getClusterAesKey().getKey(), "");
        if (LOG.isDebugEnabled()) {
            LOG.info("Check cluster signature: sign={}, computedSign={}", sign, computedSign);
        }
        Assert.isTrue(sign.equalsIgnoreCase(computedSign),  StatusCode.OPEN_API_PERMISSION_DENIED);
        return true;
    }
}
