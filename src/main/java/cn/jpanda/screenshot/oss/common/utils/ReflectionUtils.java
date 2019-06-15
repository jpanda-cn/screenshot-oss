package cn.jpanda.screenshot.oss.common.utils;

import cn.jpanda.screenshot.oss.core.exceptions.JpandaRuntimeException;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public final class ReflectionUtils {
    public static final List<Class<? extends Annotation>> shouldContinueAnnotationType = Arrays.asList(Documented.class, Retention.class, Target.class);

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

    public static  boolean hasAnnotation(Class c, Class<? extends Annotation> a, boolean findParent) {
        if (!findParent) {
            return hasAnnotation(c, a);
        }
        // 判断是否有注解
        if (hasAnnotation(c, a)) {
            return true;
        }

        // 判断注解上是否有注解
        for (Annotation annotation : c.getDeclaredAnnotations()) {
            if (shouldContinueAnnotationType.contains(annotation.annotationType())) {
                continue;
            }
            if (hasAnnotation(c, annotation.annotationType())) {
                return true;
            }
            if (hasAnnotation(c, annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAnnotation(Class c, Class<? extends Annotation> a) {
        return null != c.getDeclaredAnnotation(a);
    }
}
