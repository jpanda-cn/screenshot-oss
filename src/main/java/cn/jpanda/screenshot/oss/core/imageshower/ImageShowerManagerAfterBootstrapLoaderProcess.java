package cn.jpanda.screenshot.oss.core.imageshower;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

/**
 * 图钉管理器
 */
@Component
public class ImageShowerManagerAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;
    private Log log;

    public ImageShowerManagerAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
        this.log = configuration.getLogFactory().getLog(getClass());
    }

    @Override
    public void after() {
        configuration.registryUniqueBean(ImageShowerManager.class, new ImageShowerManager());
    }
}
