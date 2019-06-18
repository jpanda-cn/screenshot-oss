package cn.jpanda.screenshot.oss.core.interceptor.value;


import java.lang.reflect.Field;

/**
 * 对数据进行持久化操作时，使用的拦截器
 */
public interface ValueInterceptor {
    /**
     * 获取值的拦截
     */
    String get(Field field, String object);

    /**
     * 设值的拦截
     */
    String set(Field field, String object);
}
