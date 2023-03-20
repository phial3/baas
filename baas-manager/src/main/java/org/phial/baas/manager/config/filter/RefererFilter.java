package org.phial.baas.manager.config.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.phial.baas.service.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RefererFilter implements Filter {

    /**
     * 过滤器配置对象
     */
    FilterConfig filterConfig = null;

    /**
     * 是否启用
     */
    private final boolean enable = true;

    /**
     * 忽略的URL
     */
    @Value("${security.csrf.excludes}")
    private String excludes;

    /**
     * 允许的refer，一般配置域名，多个之间逗号隔开
     * eg: "http://test.baidu.com,https://test.baidu.com"
     */
    @Value("${security.csrf.allows}")
    private String allows;


    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        log.error("RefererFilter enable:{}, [{} {}]", enable, req.getMethod(), req.getRequestURL());

        // 不启用或者已忽略的URL不拦截
        if (!enable || isExcludeUrl(req.getServletPath())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String referer = req.getHeader("Referer");
        String matchedReferer = matchAllowedReferer(referer);
        Assert.notNull(matchedReferer, CommonConstant.ILLEGAL_ACCESS);

        String origin = req.getHeader("Origin");
        if (origin == null) {
            req.setAttribute("Origin", referer);
        }

        String serverName = req.getServerName();
        // 判断是否存在外链请求本站
        if (null != referer && !referer.contains(serverName)) {
            log.error("RefererFilter => server:{} -> currentOrigin：{}", serverName, referer);
            servletResponse.setContentType("text/html; charset=utf-8");
            servletResponse.getWriter().write(CommonConstant.ILLEGAL_ACCESS);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * 判断是否为忽略的URL
     *
     * @param url URL路径
     * @return true-忽略，false-过滤
     */
    private boolean isExcludeUrl(String url) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        List<String> urls = Arrays.asList(excludes.split(","));
        return urls.stream().map(
                        pattern -> Pattern.compile("^" + pattern))
                .map(p -> p.matcher(url))
                .anyMatch(Matcher::find);
    }

    private String matchAllowedReferer(String referer) {
        Assert.isTrue(StringUtils.isNotBlank(referer), CommonConstant.ILLEGAL_ACCESS);

        if (allows != null) {
            for (String r : allows.split(",")) {
                if (referer.startsWith(r)) {
                    return r;
                }
            }
        }
        return null;
    }

}
