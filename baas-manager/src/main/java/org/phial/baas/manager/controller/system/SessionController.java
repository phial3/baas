package org.phial.baas.manager.controller.system;

import org.mayanjun.core.Assert;
import org.mayanjun.core.ServiceException;
import org.mayanjun.myrest.BaseController;
import org.mayanjun.myrest.RestResponse;
import org.mayanjun.myrest.session.SessionUser;
import org.phial.baas.manager.config.app.AppConfig;
import org.phial.baas.manager.config.init.Profiler;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.manager.config.interceptor.Login;
import org.phial.baas.manager.service.system.UserBusiness;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @since 2019-07-10
 * @author mayanjun
 */
@RestController
@RequestMapping("api/session")
public class SessionController extends BaseController {

    private AppConfig config;
    private ConsoleSessionManager sessionManager;
    private UserBusiness userBusiness;

    public SessionController(AppConfig config, ConsoleSessionManager sessionManager, UserBusiness userBusiness) {
        this.config = config;
        this.sessionManager = sessionManager;
        this.userBusiness = userBusiness;
    }

    @Profiler(serializeArguments = false)
    @RequestMapping(method = RequestMethod.POST)
    public Object signin(@RequestBody SysUser user, HttpServletResponse response) {
        try {
            SessionUser<SysUser> suser = sessionManager.signIn(user.getUsername(), user.getPassword(), response);
            Assert.isTrue(suser.getOriginUser().getEnabled(),"该账号未启用");
            return RestResponse.ok(suser.getOriginUser());
        } catch (ServiceException e) {
            if(e.getMessage().contains("该账号未启用")){
                throw new ServiceException(e.getStatus(), "该账号未启用");
            }else{
                throw new ServiceException(e.getStatus(), "用户名或者密码错误");
            }
        }
    }

    @Profiler
    @Login
    @RequestMapping(method = RequestMethod.GET, value = "profile")
    public Object profile() {
        return RestResponse.ok(userBusiness.userProfile());
    }


    @Profiler
    @Login
    @RequestMapping(method = RequestMethod.GET, value = "signout")
    public Object signout(HttpServletRequest request, HttpServletResponse response) {
        sessionManager.signOut(request, response);
        return RestResponse.ok().add("domain", config.getDomain());
    }

}
