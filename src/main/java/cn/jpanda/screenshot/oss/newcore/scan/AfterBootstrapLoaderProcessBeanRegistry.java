package cn.jpanda.screenshot.oss.newcore.scan;

import cn.jpanda.screenshot.oss.core.scan.BeanRegistry;
import cn.jpanda.screenshot.oss.newcore.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.Component;

/**
 * 数据持久化策略注册器
 * 需要确保最终只有一个数据持久化策略可以生效
 */
@Component
public class AfterBootstrapLoaderProcessBeanRegistry implements BeanRegistry {

    private Configuration configuration;

    public AfterBootstrapLoaderProcessBeanRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void doRegistry(Class c) {
        if (AfterBootstrapLoaderProcess.class.isAssignableFrom(c)) {
            AfterBootstrapLoaderProcess afterBootstrapLoaderProcess = configuration.createBeanInstance(AfterBootstrapLoaderProcess.class).instance(c);
            configuration.registryAfterBootstrapLoaderProcesses(afterBootstrapLoaderProcess);
        }
    }
}
