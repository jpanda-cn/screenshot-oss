package cn.jpanda.screenshot.oss.core.toolkit;

import cn.jpanda.screenshot.oss.core.BootstrapLoader;
import cn.jpanda.screenshot.oss.core.Configuration;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;

public class DefaultBeanInstance<T> implements BeanInstance<T> {
    private Configuration configuration;
    private BootstrapLoader bootstrapLoader;

    public DefaultBeanInstance(Configuration configuration, BootstrapLoader bootstrapLoader) {
        this.configuration = configuration;
        this.bootstrapLoader = bootstrapLoader;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public T instance(Class<? extends T> clazz) {
        Constructor<T> candidateConstructor = null;
        Constructor<T>[] beanRegistryConstructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        // 循环处理所有构造，在这里能够注入的属性只有Configuration对象，以及BootstrapLoader本身

        for (Constructor<T> c : beanRegistryConstructors) {
            if (c.getParameterCount() > 2) {
                // 超过两个以上的构造参数的不用理会
                continue;
            }
            if (c.getParameterCount() == 0) {
                // 默认构造参数,优先级最低，只有在没有其他更合适的构造方法时才会采用
                if (candidateConstructor == null) {
                    candidateConstructor = c;
                }
                continue;
            }
            if (c.getParameterCount() == 1) {
                Class[] cs = c.getParameterTypes();
                if (Configuration.class.isAssignableFrom(cs[0]) || BootstrapLoader.class.isAssignableFrom(cs[0])) {
                    // 只会覆盖0参构造参数，如果有两个构造方法，一个是Configuration，一个是BootstrapLoader，后加载的覆盖先加载的
                    if (candidateConstructor == null || candidateConstructor.getParameterCount() < 2) {
                        candidateConstructor = c;
                    }
                }
                continue;
            }
            // 两个参数
            if (c.getParameterCount() == 2) {
                Class[] cs = c.getParameterTypes();
                if (cs[0].equals(cs[1])) {
                    // ??? 讲道理，这种场景，我不是很想给你注入，为什么会有两个一样的参数呢？
                }
                if (
                        (Configuration.class.isAssignableFrom(cs[0]) || BootstrapLoader.class.isAssignableFrom(cs[0]))
                                && (Configuration.class.isAssignableFrom(cs[1]) || BootstrapLoader.class.isAssignableFrom(cs[1]))
                ) {
                    candidateConstructor = c;
                    break;
                }
                // 匹配
            }

        }
        if (candidateConstructor == null) {
            return null;
        }

        int count = candidateConstructor.getParameterCount();
        Class[] params = candidateConstructor.getParameterTypes();
        Object[] objs = new Object[count];
        for (int i = 0; i < count; i++) {
            if (Configuration.class.isAssignableFrom(params[i])) {
                objs[i] = configuration;
            } else {
                objs[i] = bootstrapLoader;
            }
        }
        return candidateConstructor.newInstance(objs);
    }
}
