package org.phial.baas.manager.config.interceptor;

import org.mayanjun.core.Assert;
import org.mayanjun.myrest.interceptor.AnnotationBasedHandlerInterceptor;
import org.phial.baas.manager.util.SignUtils;
import org.phial.baas.manager.util.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 接口调用验证拦截器
 * @since 2019-10-10
 * @author mayanjun
 */
@Component
public class VerifySignatureInterceptor extends AnnotationBasedHandlerInterceptor {

    @Autowired
    private MobileSessionManager sessionManager;

    @Override
    public int getOrder() {
        return InterceptorOrder.VERIFY_SIGNATURE.ordinal();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return true;
    }

}