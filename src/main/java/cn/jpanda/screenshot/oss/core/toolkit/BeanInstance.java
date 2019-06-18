package cn.jpanda.screenshot.oss.core.toolkit;

/**
 * Bean实例化接口
 *
 * @param <T> 需要实例化的类型
 */
public interface BeanInstance<T> {
    // 实例化操作
    T instance(Class<? extends T> c);
}
