package cn.jpanda.screenshot.oss.core.destroy;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

@Component
public class DestroyBeanManagementAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public DestroyBeanManagementAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        configuration.registryUniqueBean(DestroyBeanHolder.class, new DestroyBeanHolder());
    }
}
