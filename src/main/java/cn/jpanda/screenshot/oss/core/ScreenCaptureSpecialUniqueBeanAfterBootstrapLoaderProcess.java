package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.capture.JavafxScreenCapture;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;

/**
 * 底层截图实现类——特殊且唯一的Bean注册器
 */
@Component
public class ScreenCaptureSpecialUniqueBeanAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public ScreenCaptureSpecialUniqueBeanAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void after() {
        // 注册一个截图类
        ScreenCapture screenCapture = new JavafxScreenCapture();
        configuration.registryUniqueBean(screenCapture.getClass(), screenCapture);

    }
}
