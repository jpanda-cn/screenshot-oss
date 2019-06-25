package cn.jpanda.screenshot.oss.core.shotkey;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

@Component
public class ScreenshotsElementConvertorAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public ScreenshotsElementConvertorAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        configuration.registryUniqueBean(ScreenshotsElementsHolder.class,new ScreenshotsElementsHolder());
        configuration.registryUniqueBean(ScreenshotsElementConvertor.class, new DefaultScreenshotsElementConvertor(configuration));
    }
}
