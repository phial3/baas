package org.phial.baas.manager.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * Calculate the signature for the parameter
 * @since 2020/11/17
 * @author mayanjun
 */
public class SignUtils {

    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String KEY_VALUE_DELIMITER = "&";

    private static final Logger LOG = LoggerFactory.getLogger(SignUtils.class);

    private SignUtils() {
    }

    /**
     * Calculation of the signature
     * @param map
     * @return
     */
    public static String computeSign(Map<String, String> map, String secretKey, String userSecretKey) {
        if (map == null || map.isEmpty()) return null;
        List<String> list = new ArrayList<>();
        map.entrySet().stream().forEach(e -> {
            if(!"sign".equalsIgnoreCase(e.getKey())) {
                list.add(e.getKey() + KEY_VALUE_SEPARATOR + e.getValue());
            }
        });
        return computeSign(list, secretKey, userSecretKey);
    }

    /**
     * Calculation of the signature
     * @param map
     * @return
     */
    public static String computeSignParams(Map<String, String[]> map, String secretKey, String userSecretKey) {
        if (map == null || map.isEmpty()) return null;
        List<String> list = new ArrayList<>();
        map.entrySet().stream().forEach(e -> {
            if(!"sign".equalsIgnoreCase(e.getKey())) {
                String vals[] = e.getValue();
                String key = e.getKey();
                if (vals != null && vals.length > 0) {
                    for (String v : vals) {
                        list.add(key + KEY_VALUE_SEPARATOR + v);
                    }
                }
            }
        });
        return computeSign(list, secretKey, userSecretKey);
    }

    public static String computeSign(String plain) {
        return DigestUtils.sha256Hex(plain);
    }

    /**
     * Calculation of the signature
     * @param list
     * @return
     */
    private static String computeSign(List<String> list, String secretKey, String userSecretKey) {
        Collections.sort(list);
        StringJoiner joiner = new StringJoiner(KEY_VALUE_DELIMITER, secretKey, userSecretKey);
        for (String s : list) {
            joiner.add(s);
        }
        String s = joiner.toString();
        if (LOG.isDebugEnabled()) {
            LOG.info("sign >>>> {}", s);
        }

        String digest = DigestUtils.sha256Hex(s);
        return digest;
    }

    public static Map<String, String[]> getParameters(HttpServletRequest request) {
        Map<String, String[]> params = new HashMap<>(request.getParameterMap());
        if (request instanceof MultipartRequest) {
            LOG.info("========== MultiPartRequest ===========");
            Map<String, MultipartFile> fileMap = ((MultipartRequest) request).getFileMap();
            fileMap.entrySet().stream().forEach(e -> {
                String key = e.getKey();
                MultipartFile file = e.getValue();
                try {
                    String value = DigestUtils.md5Hex(file.getBytes());
                    params.put(key, new String[]{value});
                } catch (IOException ioException) {
                    LOG.error("Compute sign error: fileName=" + key, e);
                }
            });
        }
        return params;
    }

    public static String timestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    private static final char[] LETTERS = "zxcvbnmasdfghjklqwertyuiop1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static String random(int len) {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            stringBuffer.append(LETTERS[random.nextInt(LETTERS.length)]);
        }
        return stringBuffer.toString();
    }

    public static void fillParameters(Map<String, String> parameters) {
        if (parameters != null) {
            parameters.put("timestamp", timestamp());
            parameters.put("random", random(16));
        }
    }


    /**
     * SDK 参数签名
     * @param parameters
     * @param secretKey
     * @return
     */
    public static void computeSign(Map<String, String> parameters, String secretKey) {
        fillParameters(parameters);
        List<String> list = new ArrayList<>();
        parameters.entrySet().stream().forEach(e -> {
            if(!"sign".equalsIgnoreCase(e.getKey())) {
                list.add(e.getKey() + KEY_VALUE_SEPARATOR + e.getValue());
            }
        });
        String sign = computeSign(list, secretKey, "");
        parameters.put("sign", sign);
    }
}
