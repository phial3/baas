package org.phial.baas.service.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.StringJoiner;

/**
 * @author mayanjun
 * @since 2018/9/8
 */
@SuppressWarnings("ALL")
public class Strings {

    private static final Logger LOG = LoggerFactory.getLogger(Strings.class);

    private Strings() {
    }

    private static final char[] LETTERS = "zxcvbnmasdfghjklqwertyuiop1234567890".toCharArray();

    private static final String TIME_UNIT_CHINESE[] = {"天","小时","分钟","秒"};
    private static final String TIME_UNIT_COLON[] = {":",":",":",":"};

    private static final HanyuPinyinOutputFormat HANYUPINYINFORMAT = new HanyuPinyinOutputFormat();

    static {
        HANYUPINYINFORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
        HANYUPINYINFORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public static String pinyin(String src) {
        return pinyin(src, null);
    }

    public static String pinyin(String src, String sepreator) {

        if (StringUtils.isBlank(src)) return null;

        if (sepreator == null) sepreator = ",";

        StringJoiner joiner = new StringJoiner(sepreator);
        StringBuilder sb = new StringBuilder();

        char cs[] = src.toCharArray();
        for (char c : cs) {
            try {
                String pinyin[] = PinyinHelper.toHanyuPinyinStringArray(c, HANYUPINYINFORMAT);
                if (pinyin == null) {
                    sb.append(c);
                } else {
                    if (sb.length() > 0) {
                        joiner.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    joiner.add(pinyin[0]);
                }

            } catch (Exception e) {
                LOG.info("Convert pinyin error: " + c, e);
            }
        }

        if (sb.length() > 0) {
            joiner.add(sb.toString());
        }

        return joiner.toString();
    }

    public static final Double toDouble(String num) {
        Double val = null;
        try {
            val = Double.parseDouble(num);
        } catch (NumberFormatException e) {
        }
        return val;
    }

    public static String secretKey(int len) {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            stringBuffer.append(
                    LETTERS[random.nextInt(LETTERS.length)]
            );
        }
        return stringBuffer.toString();
    }

    private static long ONE_K = 1024;
    private static long ONE_M = 1024 * ONE_K;
    private static long ONE_G = 1024 * ONE_M;

    /**
     * 返回人易于阅读的字节数
     * @return
     */
    public static String humanBytes(long bytes) {
        if (bytes <= 0) return "0";
        long rem = bytes;
        long gs, ms, ks, bs;

        StringBuffer sb = new StringBuffer();

        gs = rem / ONE_G;
        if (gs > 0) {
            rem %= ONE_G;
            sb.append(gs + "GB ");
        }

        ms = rem / ONE_M;
        if (ms > 0) {
            rem %= ONE_M;
            sb.append(ms + "MB ");
        }

        ks = rem / ONE_K;
        if (ks > 0) {
            rem %= ONE_K;
            sb.append(ks + "KB ");
        }

        bs = rem;

        if (bs > 0) {
            sb.append(bs + "B");
        }

        return sb.toString();
    }

    private static long ONE_DAY = 24 * 3600 * 1000;
    private static long ONE_HOUR = 3600 * 1000;
    private static long ONE_MINUTE = 60 * 1000;
    private static long ONE_SECOND = 1000;

    public static String formatUptime(long uptime, boolean unit) {

        long rem = uptime;
        long days,hours,minutes,seconds;

        days = rem / ONE_DAY;
        if(days > 0) rem %= ONE_DAY;

        hours = rem / ONE_HOUR;
        if(hours > 0) rem %= ONE_HOUR;

        minutes = rem / ONE_MINUTE;
        if(minutes > 0) rem %= ONE_MINUTE;

        seconds = rem / ONE_SECOND;
        if(seconds > 0) rem %= ONE_SECOND;

        String [] units = TIME_UNIT_COLON;
        if (unit) units = TIME_UNIT_CHINESE;

        return days + units[0] + hours + units[1] + (minutes < 10 ? "0" + minutes : minutes) + units[2]
                + (seconds < 10 ? "0" + seconds : seconds) + units[3];/* + "." + rem;*/
    }


    public static String toClassName(String src, String sep) {
        String ss[] = src.split(sep);
        StringBuffer sb = new StringBuffer();
        for (String s : ss) {
            char[] cs = s.toCharArray();
            char cu = Character.toUpperCase(cs[0]);
            sb.append(cu);
            sb.append(cs, 1, cs.length - 1);
        }
        return sb.toString();
    }

    public static boolean isEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            return email.matches("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
        }
        return false;
    }


    public static String escape(String src) {
        if (src == null) return "*";
        char cs[] = src.toCharArray();
        if (cs.length == 0) return "*";
        if (cs.length == 1) return "*";
        int harf = cs.length / 2;
        int start = (cs.length / 4);
        int end = start + harf;
        for (int i = start; i <end; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     * @param ds
     * @return
     */
    public static boolean isDate(String ds) {
        if (ds == null) return false;
        return ds.matches("(19|20)\\d{2}\\-([1-9]|(0[1-9])|(1[0-2]))\\-((0[1-9])|([1-2][0-9])|(3[0-1]))");
    }

    public static boolean isDatetime(String ds) {
        if (ds == null) return false;
        return ds.matches("(19|20)\\d{2}\\-([1-9]|(0[1-9])|(1[0-2]))\\-((0[1-9])|([1-2][0-9])|(3[0-1])) ([0-9]|([0-1][0-9])|2[0-3]):([0-9]|[0-5][0-9]):([0-9]|[0-5][0-9])");
    }

    public static boolean isBoolean(String v) {
        return "true".equals(v) || "false".equals(v);
    }

    public static boolean isIdList(String v) {
        if (StringUtils.isNotBlank(v)) {
            String ss[] = v.split(",");
            for (String s : ss) {
                if (!StringUtils.isNumeric(s)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String trimUri(String uri) {

        if (uri == null) {
            return null;
        }

        char cs[] = uri.toCharArray();
        int startIndex = 0;
        int endIndex = cs.length - 1;

        for (int i = 0; i < cs.length; i++) {
            if (cs[i] != '/') {
                startIndex = i;
                break;
            }
        }

        for (int i = cs.length - 1; i >= 0; i--) {
            if (cs[i] != '/') {
                endIndex = i;
                break;
            }
        }

        if (startIndex > 0) {
            return new String(cs, startIndex, endIndex - startIndex + 1);
        }

        return uri;
    }

    public static boolean isArray(Object o) {
        return o != null && o.getClass().isArray();
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
        }
    }


    public static Object[] asObjectArray(Object array) {
        checkArgument(isArray(array), "Given object %s is not an array", new Object[]{array});
        int length = Array.getLength(array);
        Object[] objectArray = new Object[length];

        for(int i = 0; i < length; ++i) {
            objectArray[i] = Array.get(array, i);
        }

        return objectArray;
    }

    public static String convert(Object object) {
        if (object == null) return "";

        if (object instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object);
        }

        if (isArray(object)) {
            Object os[] = asObjectArray(object);
            return StringUtils.join(os, ",");
        }

        if (object instanceof Boolean) {
            return Boolean.TRUE.equals(object) ? "是" : "否";
        }

        if (object instanceof Number) {
            if (((Number) object).intValue() == 0) {
                return "";
            }
        }

        if (object instanceof Enum) {
            try {
                String value = BeanUtilsBean2.getInstance().getProperty(object, "displayName");
                if (StringUtils.isNotBlank(value)) {
                    return value;
                }
            } catch (Exception e) {
            }
            return ((Enum<?>) object).name();
        }

        return object.toString();
    }

    public static boolean validateNumber(String str) {
        if(StringUtils.isBlank(str)) {
            return false;
        }
        return str.matches("[+-]?[0-9]+(\\.[0-9]+)?");
    }


    public static boolean validateAlphanumericHyphen(String str) {
        if(StringUtils.isBlank(str)) {
            return false;
        }
        return str.matches("^[A-Za-z0-9\\-]+$");
    }

}
