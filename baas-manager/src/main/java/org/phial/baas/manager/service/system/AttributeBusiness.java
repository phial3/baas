package org.phial.baas.manager.service.system;


import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.mayanjun.myrest.session.SessionUser;
import org.phial.baas.manager.config.cache.CacheClient;
import org.phial.baas.manager.config.cache.CacheKey;
import org.phial.baas.manager.config.interceptor.ConsoleSessionManager;
import org.phial.baas.service.domain.entity.sys.Attribute;
import org.phial.baas.service.domain.entity.sys.AttributeItem;
import org.phial.baas.service.domain.entity.sys.Settings;
import org.phial.baas.manager.service.ConsoleBaseBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 属性管理
 *
 * @author mayanjun
 * @since 2019-07-06
 */
@Component
public class AttributeBusiness extends ConsoleBaseBusiness<Attribute> {

    private static final Logger LOG = LoggerFactory.getLogger(AttributeBusiness.class);


    private static final String GROUP_SETTINGS = "settings";

    @Autowired
    private ConsoleSessionManager sessionManager;

    @Autowired
    private CacheClient cacheClient;

    public Attribute getAttribute(String group, String name, String user) {
        Query<Attribute> query = QueryBuilder.custom(Attribute.class)
                .andEquivalent("group", group)
                .andEquivalent("name", name)
                .andEquivalent("user", user)
                .forUpdate()
                .build();
        return service.queryOne(query);
    }

    /**
     * 获取用户所有属性
     *
     * @param user
     * @return
     */
    public Map<String, String> allSettings(String user) {

        String users[] = new String[]{""};

        if (StringUtils.isNotBlank(user)) {
            users = new String[]{"", user};
        }

        Query<Attribute> query = QueryBuilder.custom(Attribute.class)
                .andEquivalent("group", GROUP_SETTINGS)
                .andIn("user", users)
                .build();
        List<Attribute> list = service.query(query);
        Map<String, Attribute> data = new HashMap<>();
        list.forEach(e -> {
            String name = e.getName();
            Attribute a = data.get(name);
            if (a == null) {
                data.put(name, e);
            } else {
                if (user.equals(e.getName())) {
                    data.put(name, e);
                }
            }
        });
        Map<String, String> dat = new HashMap<>();
        data.entrySet().stream().forEach(e -> dat.put(e.getKey(), e.getValue().getValue()));
        return dat;
    }

    public void updateSettings(Settings settings) {
        try {
            saveOrUpdateSettings(settings, null, true);
        } finally {
            clearCache();
        }
    }

    public void restoreFactorySettings() {
        try {
            initSystemSettings(true);
        } finally {
            clearCache();
        }
    }

    public Settings initSystemSettings(boolean update) {
        Settings settings = new Settings();
        saveOrUpdateSettings(settings, "", update);
        return settings;
    }

    /**
     * 保存或更新设置
     *
     * @param settings
     * @param user
     * @param update
     */
    private void saveOrUpdateSettings(Settings settings, String user, boolean update) {
        PropertyUtilsBean utilsBean = BeanUtilsBean2.getInstance().getPropertyUtils();
        final PropertyDescriptor[] origDescriptors = utilsBean.getPropertyDescriptors(settings);
        for (PropertyDescriptor descriptor : origDescriptors) {
            final String name = descriptor.getName();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }
            try {
                Object value = utilsBean.getSimpleProperty(settings, name);
                Method m = descriptor.getReadMethod();

                String u = "";
                String g = GROUP_SETTINGS;

                if (update && (
                        name.equals("alarmSuppressInterval")
                                || name.equals("alarmEvidenceDuration")
                                || name.equals("cameraShotInterval"))
                ) {
                    setCache(name, value + "");
                }

                AttributeItem ai = m.getAnnotation(AttributeItem.class);
                if (ai != null) {
                    u = ai.user();
                    g = ai.group();
                    if (StringUtils.isBlank(g)) g = GROUP_SETTINGS;
                }

                if (user != null) {
                    u = user;
                } else {
                    if ("*".equals(u)) {
                        SessionUser su = sessionManager.getCurrentUser();
                        u = su.getUsername();
                    }
                }

                if (value == null) value = "";
                saveOrUpdate(u, g, name, String.valueOf(value), "System init", update);

            } catch (final Exception e) {
                LOG.warn("Can not copy settings value from property:'" + name + "'", e);
            }
        }
    }

    private void saveOrUpdate(String user, String group, String name, String value, String desc, boolean update) {
        Attribute attribute = new Attribute();
        attribute.setGroup(group);
        attribute.setName(name);
        attribute.setValue(value);
        attribute.setDescription(desc);
        attribute.setUser(user);
        saveOrUpdate(attribute, update);
    }

    private void saveOrUpdate(Attribute attribute, boolean update) {
        transaction().execute(transactionStatus -> {
            Attribute a = getAttribute(attribute.getGroup(), attribute.getName(), attribute.getUser());
            if (a == null) {
                service.save(attribute);
            } else {
                if (update) {
                    attribute.setId(a.getId());
                    service.update(attribute);
                }
            }
            return 1;
        });
    }

    /**
     * 获取系统设置
     *
     * @param name
     * @return
     */
    public String getSettings(String name) {
        String val = getFromCache(name);

        if (val != null) return val;

        Query<Attribute> query = QueryBuilder.custom(Attribute.class)
                .andEquivalent("group", GROUP_SETTINGS)
                .andEquivalent("name", name)
                .andEquivalent("user", "")
                .forUpdate()
                .build();
        Attribute attribute = service.queryOne(query);
        if (attribute == null) return null;

        String value = attribute.getValue();
        setCache(name, value);
        return value;
    }

    /**
     * 获取用户设置
     *
     * @param name
     * @return
     */
    public String getUserSettings(String name, String defaultValue) {

        SessionUser user = sessionManager.getCurrentUser();
        String key = name + "-" + user.getUsername();

        String val = getFromCache(name);
        if (val != null) return val;

        Query<Attribute> query = QueryBuilder.custom(Attribute.class)
                .andEquivalent("group", GROUP_SETTINGS)
                .andEquivalent("name", name)
                .andEquivalent("user", user.getUsername())
                .forUpdate()
                .build();

        Attribute attribute = service.queryOne(query);
        if (attribute == null) {
            Attribute a = new Attribute();
            a.setName(name);
            a.setUser(user.getUsername());
            a.setGroup(GROUP_SETTINGS);
            a.setValue(defaultValue);
            a.setDescription("Set by default value");
            saveOrUpdate(a, false);
            return defaultValue;
        }

        String value = attribute.getValue();
        setCache(key, value);
        return value;
    }

    public boolean getBooleanSettings(String name) {
        String value = getSettings(name);
        return "true".equalsIgnoreCase(value);
    }

    public int getIntSettings(String name, int defaultValue) {
        String value = getSettings(name);
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public void setCache(String name, String value) {
        cacheClient.putString(CacheKey.ATTRIBUTE, GROUP_SETTINGS, name, value);
    }

    public String getFromCache(String name) {
        return cacheClient.getStringFromMap(CacheKey.ATTRIBUTE, GROUP_SETTINGS, name);
    }

    private void clearCache() {
        cacheClient.clearMap(CacheKey.ATTRIBUTE, GROUP_SETTINGS);
    }

}
