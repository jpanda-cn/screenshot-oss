package cn.jpanda.screenshot.oss.core.shotkey;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.Snapshot;
import cn.jpanda.screenshot.oss.core.annotations.Component;
import cn.jpanda.screenshot.oss.core.scan.AfterBootstrapLoaderProcess;
import javafx.scene.input.KeyCode;
import lombok.SneakyThrows;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 全局快捷键监听
 */
@Component
public class GlobalKeyListenerAfterBootstrapLoaderProcess implements AfterBootstrapLoaderProcess {
    private Configuration configuration;

    public GlobalKeyListenerAfterBootstrapLoaderProcess(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @SneakyThrows
    public void after() {
        LogManager.getLogManager().reset();
        Logger.getGlobal().setLevel(Level.OFF);
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            private Set<KeyCode> codes = new HashSet<>();

            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
                KeyCode code = NativeCodeKeyCodeConvert.getKeyCode(nativeKeyEvent.getKeyCode());
                codes.add(code);
                // 校验是否满足了全局按键
                if (isHotKey()) {
                    configuration.getUniqueBean(Snapshot.class).cut();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
                codes.remove(NativeCodeKeyCodeConvert.getKeyCode(nativeKeyEvent.getKeyCode()));
            }

            private boolean isHotKey() {
                HotKey2CutPersistence hotKey2CutPersistence = configuration.getPersistence(HotKey2CutPersistence.class);
                int count = 0;
                if (hotKey2CutPersistence.isShift()) {
                    count++;
                    if (!codes.contains(KeyCode.SHIFT)) {

                        return false;
                    }
                }
                if (hotKey2CutPersistence.isAlt()) {
                    count++;
                    if (!codes.contains(KeyCode.ALT)) {
                        return false;
                    }
                }
                if (hotKey2CutPersistence.isCtrl()) {
                    count++;
                    if (!codes.contains(KeyCode.CONTROL)) {
                        return false;
                    }
                }
                if (codes.size() > ++count) {
                    return false;
                }
                return codes.contains(KeyCode.valueOf(hotKey2CutPersistence.getCode()));
            }
        });
    }


}
