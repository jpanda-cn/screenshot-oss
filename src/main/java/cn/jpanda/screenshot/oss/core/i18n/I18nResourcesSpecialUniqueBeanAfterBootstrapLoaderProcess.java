package cn.jpanda.screenshot.oss.core.i18n;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

@Component
public class I18nResourcesSpecialUniqueBeanAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public I18nResourcesSpecialUniqueBeanAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        configuration.registryUniqueBean(I18nResource.class, new I18nResource(configuration));
    }
}
