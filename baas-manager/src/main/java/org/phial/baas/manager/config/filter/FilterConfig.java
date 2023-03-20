package org.phial.baas.manager.config.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gyf
 * @date 2022/12/12
 */
@Configuration
public class FilterConfig {

    @Bean("corsFilter")
    public FilterRegistrationBean crosFilteRegistration() {
        FilterRegistrationBean<CrosFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CrosFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
