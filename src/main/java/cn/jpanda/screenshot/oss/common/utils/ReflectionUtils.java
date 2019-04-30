package cn.jpanda.screenshot.oss.common.utils;

import cn.jpanda.screenshot.oss.core.exceptions.JpandaRuntimeException;

import java.lang.reflect.Field;

public final class ReflectionUtils {

    public static <T> T newInstance(Class<? extends T> type) {
        try {
            return type.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new JpandaRuntimeException(e);
        }
    }

    public static void setValue(Field field, Object object, Object value) {
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new JpandaRuntimeException(e);
        }
    }

    public static Object readValue(Field field, Object object) {
        makeAccessible(field);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new JpandaRuntimeException(e);
        }
    }

    public static void makeAccessible(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
