package org.phial.baas.manager.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mayanjun.util.BouncyCastleCrypto;
import org.mayanjun.util.Crypto;
import org.mayanjun.util.SecretKeyStore;
import org.phial.baas.manager.config.init.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author mayanjun
 * @vendor mayanjun.org
 */
public class CommonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

    private static final Crypto CRYPTO = new BouncyCastleCrypto();

    /**
     * AES 加密的初始向量长度
     */
    private static final int AES_IV_LENGTH = 16;

    private CommonUtils() {
    }

    /**
     * 获取权限系统中对访问方法的别名
     *
     * @param instanceClass 调用方法所在的类
     * @param method        方法
     * @return
     */
    public static String getReferenceMethodName(Class<?> instanceClass, Method method) {
        return instanceClass.getCanonicalName() + "::" + method.getName();
    }

    public static String getReferenceMethodName(Dependency dependency) {
        return dependency.type().getCanonicalName() + "::" + dependency.method();
    }

    /**
     * 字符串脱敏
     *
     * @param src     源字符串
     * @param percent 隐藏百分比
     * @return 脱敏字符串
     */
    public static String insensitiveString(String src, float percent) {
        if (src == null) return "";
        percent = Math.abs(percent);
        if (percent >= 1) return src;
        char cs[] = src.toCharArray();
        int cslen = cs.length;
        int range = (int) (cslen * percent);
        if (range == 0) range = 1;
        int start = (cslen - range) / 2;
        int end = start + range - 1;
        if (end >= cslen) end = cslen - 1;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cslen; i++) {
            if (i >= start && i <= end) {
                sb.append('*');
            } else {
                sb.append(cs[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Return A-Encrypted String
     *
     * @param plain
     * @param appSecretKey
     * @param token
     * @return
     */
    public static String encryptedStringA(String plain, String appSecretKey, String token) {
        try {
            // 字段加密
            SecretKeyStore secretKeyStore = new SecretKeyStore(appSecretKey, token.substring(token.length() - AES_IV_LENGTH));
            return "A-ENCRYPTED:" + CRYPTO.encrypt(plain, secretKeyStore);
        } catch (Exception e) {
            LOG.error("Create EncryptedString error: plain=" + plain, e);
        }
        return null;
    }

    /**
     * Return B-Encrypted String
     *
     * @param plain
     * @param userSecretKey
     * @param token
     * @return
     */
    public static String encryptedStringB(String plain, String userSecretKey, String token) {
        try {
            // 字段加密
            SecretKeyStore secretKeyStore = new SecretKeyStore(userSecretKey, token.substring(token.length() - AES_IV_LENGTH));
            return "B-ENCRYPTED:" + CRYPTO.encrypt(plain, secretKeyStore);
        } catch (Exception e) {
            LOG.error("Create EncryptedString error: plain=" + plain, e);
        }
        return null;
    }

    public static boolean isRFID(String rfid) {
        if (StringUtils.isNotBlank(rfid)) {
            return rfid.matches("[0-9a-zA-Z]{10,64}");
        }
        return false;
    }

    public static boolean isEmpty(Object[] arr) {
        return arr != null && arr.length > 0;
    }

    public static void computeIfNotEmpty(Object[] arr, Consumer consumer) {
        if (arr != null && arr.length > 0) {
            for (Object o : arr) {
                consumer.accept(o);
            }
        }
    }

    /**
     * 判断一个数字在某个区间
     *
     * @param number
     * @param begin
     * @param end
     * @param oc     0=[], 1=(], 2=[), 3=()
     * @param <T>
     * @return
     */
    public static <T extends Number> boolean inRange(T number, T begin, T end, int oc) {
        if (number == null) return false;

        if (begin != null && end != null) {
            if (number instanceof Comparable) {
                Comparable c0 = (Comparable) number;
                Comparable c1 = (Comparable) begin;
                Comparable c2 = (Comparable) end;
                switch (oc) {
                    case 1:
                        return c0.compareTo(c1) > 0 && c0.compareTo(c2) <= 0;
                    case 2:
                        return c0.compareTo(c1) >= 0 && c0.compareTo(c2) < 0;
                    case 3:
                        return c0.compareTo(c1) > 0 && c0.compareTo(c2) < 0;
                    default:
                        return c0.compareTo(c1) >= 0 && c0.compareTo(c2) <= 0;
                }
            }
        }
        return false;
    }

    /**
     * @param n1
     * @param n2
     * @return -1 if n1 less than n2, 0 if n1 equals to n2, 1 if n1 great that n2
     */
    public static int compare(Number n1, Number n2) {
        BigDecimal b1 = NumberUtils.createBigDecimal("" + n1);
        BigDecimal b2 = NumberUtils.createBigDecimal("" + n2);
        return b1.compareTo(b2);
    }

    public static String[] merge(String a1[], String a2[]) {
        Set<String> set = new HashSet<>();

        if (a1 != null) {
            set.addAll(Arrays.asList(a1));
        }

        if (a2 != null) {
            set.addAll(Arrays.asList(a2));
        }

        if (set.isEmpty()) {
            return null;
        }

        String arr[] = new String[set.size()];
        return set.toArray(arr);
    }

    public static Map<String, Object> merge(Map<String, Object>... maps) {
        if (maps != null && maps.length > 0) {
            Map<String, Object> returnMap = null;

            for (Map<String, Object> map : maps) {
                if (returnMap == null) {
                    returnMap = map;
                    continue;
                }
                if (map != null) {
                    returnMap.putAll(map);
                }
            }

            return returnMap;
        }
        return null;
    }


    private final static ThreadLocal<SimpleDateFormat> FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));

    private final static ThreadLocal<SimpleDateFormat> FORMAT_THREAD_LOCAL_YYYYMMDDHHMMSS = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static String format(Date date) {
        if (date == null) return "-";
        return FORMAT_THREAD_LOCAL.get().format(date);
    }

    public static String formatYYYYMMDDHHMMSS(Date date) {
        if (date == null) return "-";
        return FORMAT_THREAD_LOCAL_YYYYMMDDHHMMSS.get().format(date);
    }
}