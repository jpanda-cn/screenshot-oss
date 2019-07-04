package cn.jpanda.screenshot.oss.core.log.logging;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class CustomLogManager {
    // The global LogManager object
    private static final LogManager logManager = LogManager.getLogManager();

    @SneakyThrows
    public static Logger demandLogger(String name) {
        Logger result = logManager.getLogger(name);
        if (result == null) {
            // only allocate the new logger once
            Constructor<Logger> loggerConstructor = Logger.class.getDeclaredConstructor(String.class, String.class, Class.class, LogManager.class, boolean.class);
            loggerConstructor.setAccessible(true);
            Logger newLogger = new CustomLogger(name, null);
            do {
                if (logManager.addLogger(newLogger)) {
                    return newLogger;
                }
                result = logManager.getLogger(name);
            } while (result == null);
        }
        return result;
    }
}
