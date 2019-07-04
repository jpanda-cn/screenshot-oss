package cn.jpanda.screenshot.oss.core.shotkey;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.util.logging.Level;

public class DaemonGlobalScreen extends GlobalScreen {
    static {
        // 禁用日志
        log.setLevel(Level.OFF);
    }

    public static void registerNativeHook() throws NativeHookException {

        if (hookThread == null || !hookThread.isAlive()) {
            hookThread = new GlobalScreen.NativeHookThread();
            hookThread.setDaemon(true);
            synchronized (hookThread) {
                hookThread.start();

                try {
                    hookThread.wait();
                } catch (InterruptedException var3) {
                    throw new NativeHookException(var3);
                }

                NativeHookException var1 = hookThread.getException();
                if (var1 != null) {
                    throw var1;
                }
            }
        }
    }
}
