package cn.jpanda.screenshot.oss.core.persistence.interceptor;


import java.lang.reflect.Field;

public interface Interceptor {
    String interceptor(Field field, String object);
}
