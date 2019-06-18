package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

/**
 * 截图助手
 */
@Component
public class ScreenshotProcessSpecialUniqueBeanAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public ScreenshotProcessSpecialUniqueBeanAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        ScreenshotsProcess screenshotsProcess = new DefaultScreenshotsProcess(configuration);
        configuration.registryUniqueBean(ScreenshotsProcess.class, screenshotsProcess);
    }
}
