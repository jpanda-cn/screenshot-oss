package cn.jpanda.screenshot.oss.core.mouse;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;
import lombok.SneakyThrows;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Component
public class GlobalMouseMoveListenerAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public GlobalMouseMoveListenerAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @SneakyThrows
    public void after() {
        GlobalMousePoint globalMousePoint = new GlobalMousePoint();
        configuration.registryUniqueBean(GlobalMousePoint.class, globalMousePoint);
        // 监听鼠标位置
        LogManager.getLogManager().reset();
        Logger.getGlobal().setLevel(Level.OFF);
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeMouseMotionListener(new GlobalMousePointNativeMouseMotionListener(configuration));
    }

    private static class GlobalMousePointNativeMouseMotionListener implements NativeMouseMotionListener {
        private Configuration configuration;
        private GlobalMousePoint globalMousePoint;

        public GlobalMousePointNativeMouseMotionListener(Configuration configuration) {
            this.configuration = configuration;
            this.globalMousePoint = new GlobalMousePoint();
            configuration.registryUniqueBean(GlobalMousePoint.class, globalMousePoint);
        }

        @Override
        public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
            // 动态变更鼠标位置
            globalMousePoint.pointSimpleObjectProperty.set(nativeMouseEvent.getPoint());
        }

        @Override
        public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

        }
    }
}
