package cn.jpanda.screenshot.oss.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 字符串工具类
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2018/11/16 16:55
 */
public final class StringUtils {

    private static final Map<Class, Class> BASIC_TYPE_MAP = new HashMap<>(8);

    static {
        BASIC_TYPE_MAP.put(byte.class, Byte.class);
        BASIC_TYPE_MAP.put(short.class, Short.class);
        BASIC_TYPE_MAP.put(int.class, Integer.class);
        BASIC_TYPE_MAP.put(long.class, Long.class);
        BASIC_TYPE_MAP.put(float.class, Float.class);
        BASIC_TYPE_MAP.put(double.class, Double.class);
        BASIC_TYPE_MAP.put(char.class, Character.class);
        BASIC_TYPE_MAP.put(boolean.class, Boolean.class);
    }

    public static Byte toByte(String source) {
        return Byte.valueOf(source);
    }

    public static Short toShort(String source) {
        return Short.valueOf(source);
    }


    public static Integer toInteger(String source) {
        return Integer.valueOf(source);
    }

    public static Long toLang(String source) {
        return Long.valueOf(source);
    }

    public static Float toFloat(String source) {
        return Float.valueOf(source);
    }

    public static Double toDouble(String source) {
        return Double.valueOf(source);
    }

    public static Character toCharacter(String source) {
        if (null == source || source.length() != 1) {
            throw new IllegalArgumentException("source can not be cast to character");
        }
        return source.charAt(0);
    }

    public static Boolean toBoolean(String source) {

        if (null == source || source.isEmpty()) {
            return false;
        }

        return "1".equalsIgnoreCase(source) || "true".equalsIgnoreCase(source);
    }

    public static String toString(Object source) {

        if (null == source) {
            return null;
        }
        if (BASIC_TYPE_MAP.containsKey(source.getClass()) || BASIC_TYPE_MAP.containsValue(source.getClass())) {
            return String.valueOf(source);
        }
        return source instanceof String
                ? (String) source
                : source.toString();
    }

    public static boolean isEmpty(String source) {
        return null == source || source.isEmpty();
    }

    public static boolean isNotEmpty(String source) {
        return !isEmpty(source);
    }

    /**
     * 将字符串转换为指定的基本数据类型
     *
     * @param source      字符串
     * @param targetClass 目标基本类型
     * @param <T>         泛型
     * @return 转换后的数据
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast2BasicType(String source, Class<T> targetClass) {


        if (!BASIC_TYPE_MAP.containsKey(targetClass) && !BASIC_TYPE_MAP.containsValue(targetClass)) {
            throw new IllegalArgumentException("targetClass not Basic Type");
        }

        if (isEmpty(source)) {
            throw new IllegalArgumentException("The source can not be null");
        }
        if (targetClass.equals(byte.class) || targetClass.equals(Byte.class)) {
            return (T) toByte(source);
        }

        if (targetClass.equals(short.class) || targetClass.equals(Short.class)) {
            return (T) toShort(source);
        }

        if (targetClass.equals(int.class) || targetClass.equals(Integer.class)) {
            return (T) toInteger(source);
        }

        if (targetClass.equals(long.class) || targetClass.equals(Long.class)) {
            return (T) toLang(source);
        }

        if (targetClass.equals(float.class) || targetClass.equals(Float.class)) {
            return (T) toFloat(source);
        }

        if (targetClass.equals(double.class) || targetClass.equals(Double.class)) {
            return (T) toDouble(source);
        }

        if (targetClass.equals(char.class) || targetClass.equals(Character.class)) {
            return (T) toCharacter(source);
        }

        if (targetClass.equals(boolean.class) || targetClass.equals(Boolean.class)) {
            return (T) toBoolean(source);
        }

        return (T) source;
    }

    /**
     * 获取字符串，如果指定的字符串为空，则返回 replaceStr ,可能 为{@code null}.
     * @param defaultStr 默认字符串
     * @param replaceStr 替换字符串
     * @return 返回指定的字符串或替换后的字符串
     */
    public static String getOrDefault(String defaultStr, String replaceStr) {
        if (isNotEmpty(defaultStr)) {
            return defaultStr;
        }
        return replaceStr;
    }
}
