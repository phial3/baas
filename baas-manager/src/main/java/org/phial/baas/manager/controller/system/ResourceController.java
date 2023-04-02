package org.phial.baas.manager.controller.system;

import org.mayanjun.myrest.BaseController;
import org.mayanjun.myrest.util.JSON;
import org.phial.baas.service.domain.entity.system.AppType;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("api/resource")
public class ResourceController extends BaseController {

    private static String DICT;

    private static final Class<?> [] ENUMS_TYPES = {
            AppType.class,
    };

    static {

        Map<String, Object> vm = new LinkedHashMap<>();

        for (Class c : ENUMS_TYPES) {
            if (Enum.class.isAssignableFrom(c)) {

                try {
                    Method method = c.getDeclaredMethod("values");
                    Object[] vs = (Object[]) method.invoke(c);

                    Map<String, Object> valueMap = new LinkedHashMap<>();
                    PropertyDescriptor [] pds = BeanUtils.getPropertyDescriptors(c);

                    for (Object o : vs) {
                        Method nameMethod = c.getMethod("name");
                        Map<String, Object> fieldMap = new LinkedHashMap<>();
                        String name = (String) nameMethod.invoke(o);
                        fieldMap.put("name", name);

                        if (pds != null && pds.length > 0) {
                            for (PropertyDescriptor pd : pds) {
                                Method readMethod = pd.getReadMethod();
                                Object value = readMethod.invoke(o);

                                if (!"class".equals(pd.getName()) && !"declaringClass".equals(pd.getName())) {
                                    fieldMap.put(pd.getName(), value);
                                }
                            }
                        }
                        valueMap.put(name, fieldMap);
                    }
                    vm.put(c.getSimpleName(), valueMap);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        DICT = new StringBuffer("SYSTEM_DICT = ").append(JSON.se(vm)).append(";").toString();
    }

    @GetMapping("dict")
    public Object dict() {
        return new JavascriptView(DICT);
    }
}
