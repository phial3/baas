package org.phial.baas.manager.config.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.phial.baas.manager.config.source.DynamicDataSourceContextHolder;
import org.springframework.http.ResponseEntity;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;


/**
 * Created by admin on 2019/11/12.
 */
@Slf4j
public class CrosFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.setHeader("Access-Control-Expose-Headers", "*");
        resp.setHeader("Access-Control-Allow-Headers", "*");
        resp.setHeader("Access-Control-Allow-Methods", "*");
        resp.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equals(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String source = req.getHeader("source");
        log.info("EnvFilter source:{}, [{} {}], params:{}", source, req.getMethod(), req.getRequestURL(), JSONUtil.toJsonStr(req.getParameterMap()));

        if (StringUtils.isBlank(source)) {
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/json; charset=utf-8");
            PrintWriter writer = resp.getWriter();
            ResponseEntity<String> res = ResponseEntity.of(Optional.of("source not found error"));
            log.error("source:{} not found error!", source);
            writer.write(JSON.toJSONString(res));
            return;
        }

        // set source
        DynamicDataSourceContextHolder.setCurrentSource(source);

        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }
}
