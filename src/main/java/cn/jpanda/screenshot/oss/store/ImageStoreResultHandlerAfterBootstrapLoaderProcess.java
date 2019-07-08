package cn.jpanda.screenshot.oss.store;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

@Component
public class ImageStoreResultHandlerAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public ImageStoreResultHandlerAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        configuration.registryUniqueBean(ImageStoreResultHandler.class, new ImageStoreResultHandler(configuration));
    }
}
