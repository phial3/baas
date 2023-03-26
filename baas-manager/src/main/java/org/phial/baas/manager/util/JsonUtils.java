package org.phial.baas.manager.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static String se(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException var2) {
            LOG.error("Serialize object error", var2);
            return null;
        }
    }

    public static <T> T de(String json, Class<T> cls) {
        try {
            return StringUtils.isBlank(json) ? null : MAPPER.readValue(json, cls);
        } catch (IOException var3) {
            LOG.error("Deserialize object error", var3);
            return null;
        }
    }
    public static <T> T de(String json, TypeReference<T> cls) {
        try {
            return StringUtils.isBlank(json) ? null : MAPPER.readValue(json, cls);
        } catch (IOException var3) {
            LOG.error("Deserialize object error", var3);
            return null;
        }
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss.SSS"));
    }

}
