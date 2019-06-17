package cn.jpanda.screenshot.oss.newcore.scan;

import cn.jpanda.screenshot.oss.newcore.BeanRegistry;
import cn.jpanda.screenshot.oss.newcore.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.Component;
import cn.jpanda.screenshot.oss.newcore.annotations.Exclude;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据持久化策略注册器
 * 需要确保最终只有一个数据持久化策略可以生效
 */
@Component
public class AfterBootstrapLoaderProcessBeanRegistry implements BeanRegistry {

    private Configuration configuration;

    private Set<Class> waitRemoveClass = new HashSet<>();
    private Set<Class> isRemoveClass = new HashSet<>();

    public AfterBootstrapLoaderProcessBeanRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void doRegistry(Class c) {
        if (AfterBootstrapLoaderProcess.class.isAssignableFrom(c)) {
            if (waitRemoveClass.contains(c)) {
                isRemoveClass.add(c);
                waitRemoveClass.remove(c);
                return;
            }
            handleExclude(c);
            // 获取当前类上是否包含指定注解
            AfterBootstrapLoaderProcess afterBootstrapLoaderProcess = configuration.createBeanInstance(AfterBootstrapLoaderProcess.class).instance(c);
            configuration.registryAfterBootstrapLoaderProcesses(afterBootstrapLoaderProcess);
        }
    }

    public void handleExclude(Class c) {
        Exclude exclude = (Exclude) c.getDeclaredAnnotation(Exclude.class);
        if (exclude != null) {
            Class[] excludeClasses = exclude.value();
            List<Class> currentClasses = configuration.getAfterBootstrapLoaderProcesses().stream().map(Object::getClass).collect(Collectors.toList());
            for (Class cls : excludeClasses) {
                if (isRemoveClass.contains(cls)) {
                    continue;
                }
                if (waitRemoveClass.contains(cls)) {
                    continue;
                }
                if (currentClasses.contains(cls)) {
                    currentClasses.remove(cls);
                    configuration.getAfterBootstrapLoaderProcesses().remove(currentClasses.indexOf(cls));
                    isRemoveClass.add(cls);
                }
                waitRemoveClass.add(c);
            }
        }
    }
}
