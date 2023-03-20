package org.phial.baas.service.util;

import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;

public class YamlUtil {

    /**
     * Java 对象转 yaml 字符串
     *
     * @param object 对象
     * @return 字符串
     */
    public static String toYaml(Object object) {
        Yaml yaml = new Yaml();
        StringWriter sw = new StringWriter();
        yaml.dump(object, sw);
        return sw.toString();
    }

    /**
     * 从 yaml 文件读取转到 Java 对象
     *
     * @param yamlStr yaml 字符串
     * @param clazz   目标类.class
     * @param <T>     目标类
     * @return 目标类对象
     */
    public static <T> T toObject(String yamlStr, Class<T> clazz) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(yamlStr, clazz);
    }
}
