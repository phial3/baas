package org.phial.baas.manager.controller;

import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.Assert;
import org.mayanjun.mybatisx.api.entity.Entity;
import org.mayanjun.mybatisx.dal.util.ClassUtils;
import org.mayanjun.myrest.BaseController;
import org.mayanjun.myrest.RestResponse;
import org.phial.baas.manager.config.init.Privileged;
import org.phial.baas.manager.config.init.Profiler;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.phial.baas.manager.service.ParametersBuilder;
import org.phial.baas.service.domain.entity.NamedEntity;
import org.phial.baas.service.domain.entity.rbac.SysUser;
import org.phial.baas.service.util.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 提供通用的数据管理功能
 *
 * @author mayanjun
 * @since 2019-10-10
 */
public abstract class DataController<T extends Entity> extends BaseController {

    protected static final String RESP_KEY_LIST = "list";
    protected static final String RESP_KEY_TOTAL = "total";
    protected static final String RESP_KEY_ENTITY = "entity";

    protected abstract ConsoleBaseBusiness<T> business();

    private Class<T> beanType = null;

    @Resource
    private ConsoleSessionManager sessionManager;

    @Profiler
    @GetMapping("{id}")
    @Privileged("获取{module}详细数据")
    public Object get(@PathVariable long id) {
        return RestResponse.ok().add(RESP_KEY_ENTITY, business().get(id));
    }

    @Profiler
    @Privileged("删除{module}")
    @PostMapping("delete")
    public Object delete(@RequestBody Long[] ids) {
        Assert.isTrue(ids != null && ids.length > 0, "数据ID错误");
        business().delete(ids);
        return RestResponse.ok();
    }

    @Profiler
    @Privileged("创建{module}")
    @PostMapping
    public Object save(@RequestBody T bean) {
        T ent = business().save(bean);
        return RestResponse.ok().add(RESP_KEY_ENTITY, ent);
    }

    protected Object saveUnique(T bean, String msg) {
        T ent = business().saveUnique(bean, msg);
        return RestResponse.ok().add(RESP_KEY_ENTITY, ent);
    }

    @Profiler
    @Privileged("更新{module}")
    @PostMapping("update")
    public Object update(@RequestBody T bean) {
        business().update(bean);
        return RestResponse.ok();
    }

    /**
     * 名称唯一性查询
     * @param name
     * @return
     */
    @Profiler
    @Privileged("获取{module}")
    @GetMapping("queryOne")
    public Object queryOne(@RequestParam(required = true) String name,
                           @RequestParam(required = true) boolean isAdd,
                           @RequestParam(required = false) Long id) {
        ParametersBuilder builder = ParametersBuilder.custom();
        if(isAdd){
            if (name != null) {
                builder.add("name", StringUtils.trim(name));
            }
        }else{
            if (name != null) {
                builder.add("name", StringUtils.trim(name));
                builder.add("__!=__id",id);
            }
        }
        return RestResponse.ok().add("entity", business().queryOne(builder));
    }

    /**
     * 导出数据
     *
     * @param pb
     * @return
     */
//    protected Object exportData(ParametersBuilder pb) {
//        return RestResponse.ok().add("meta", business().exportData(pb));
//    }

    /**
     * 按ids导出数据
     *
     * @param pb
     * @param ids
     * @return
     */
//    protected Object exportData(ParametersBuilder pb, Long[] ids) {
//        return RestResponse.ok().add("meta", business().exportData(pb, ids));
//    }


    public SysUser getUser() {
        return sessionManager.getCurrentUser().getOriginUser();
    }

    public void write(byte[] value, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(value);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 获取实际参数类型
     *
     * @return
     */
    protected Class<T> entityType() {
        if (this.beanType != null) return beanType;
        beanType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return beanType;
    }

    protected void fillNamedEntityParameters(ParametersBuilder pb, String sname) {
        if (NamedEntity.class.isAssignableFrom(entityType())) {
            String pinyin = Strings.pinyin(sname);
            pb.add("__LIKE__pinyin", pinyin);
        } else {
            pb.add("__LIKE__name", sname);
        }
    }

    protected void getDateTime(String startTime,String endTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTimes = new Date(0L);
        Date endTimes = new Date(0L);
        try {
             startTimes = simpleDateFormat.parse(startTime);
             endTimes = simpleDateFormat.parse(endTime);
            Assert.isTrue(startTimes.getTime() <= endTimes.getTime(),"结束时间不能小于开始时间");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
