package cn.jpanda.screenshot.oss.newcore;

import cn.jpanda.screenshot.oss.newcore.annotations.Component;
import cn.jpanda.screenshot.oss.newcore.capture.DefaultScreenCapture;
import cn.jpanda.screenshot.oss.newcore.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.newcore.scan.AfterBootstrapLoaderProcess;

/**
 * 特殊且唯一的Bean注册器
 */
@Component
public class SpecialUniqueBeanAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public SpecialUniqueBeanAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        // 注册一个截图类
        ScreenCapture screenCapture = new DefaultScreenCapture();
        configuration.registryUniqueBean(screenCapture.getClass(), screenCapture);

    }
}
