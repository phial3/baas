package org.phial.baas.manager.service;

import org.mayanjun.core.ServiceException;
import org.mayanjun.myrest.util.JSON;

import java.util.HashMap;
import java.util.Map;

public class Extras {

    private Map<String, Object> data = new HashMap<>();

    private Extras() {
    }

    public Extras(Map<String, Object> data) {
        this.data = data;
    }

    public static Extras of(String json) {
        return new Extras(JSON.de(json, Map.class));
    }

    public static Extras of(String name, Object value) {
        Extras extras = new Extras();
        extras.data.put(name, value);
        return extras;
    }

    public Extras put(String name, Object value) {
        data.put(name, value);
        return this;
    }

    public Object remove(String name) {
        return data.remove(name);
    }

    public Object get(String name) {
        return data.get(name);
    }

    public String toJSONString() {
        return toJSONString(0);
    }

    public String toJSONString(int len) {
        if (data == null) return "{}";
        String json = JSON.se(data);
        if (len <= 0) return json;

        if (json.length() > len) {
            throw new ServiceException("无法存储过多的附件信息");
        }

        return json;
    }

    public Map<String, Object> getData() {
        return data;
    }


}
