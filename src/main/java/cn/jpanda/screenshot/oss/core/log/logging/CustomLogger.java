package cn.jpanda.screenshot.oss.core.log.logging;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CustomLogger extends Logger {

    protected CustomLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }


    @Override
    public void log(Level level, String msg) {
        if (!isLoggable(level)) {
            return;
        }
        LogRecord lr = new CustomLogRecord(level, msg);
        lr.setLoggerName(getName());
        log(lr);
    }

    @Override
    public void log(Level level, Supplier<String> msgSupplier) {
        if (!isLoggable(level)) {
            return;
        }
        LogRecord lr = new CustomLogRecord(level, msgSupplier.get());
        lr.setLoggerName(getName());
        log(lr);
    }

    @Override
    public void log(Level level, String msg, Object param1) {
        if (!isLoggable(level)) {
            return;
        }
        LogRecord lr = new CustomLogRecord(level, msg);
        lr.setLoggerName(getName());
        Object params[] = {param1};
        lr.setParameters(params);
        log(lr);
    }

    @Override
    public void log(Level level, String msg, Object[] params) {
        if (!isLoggable(level)) {
            return;
        }
        LogRecord lr = new CustomLogRecord(level, msg);
        lr.setLoggerName(getName());
        lr.setParameters(params);
        log(lr);
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
        if (!isLoggable(level)) {
            return;
        }
        LogRecord lr = new CustomLogRecord(level, msg);
        lr.setLoggerName(getName());
        lr.setThrown(thrown);
        log(lr);
    }

    @Override
    public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
        if (!isLoggable(level)) {
            return;
        }
        LogRecord lr = new LogRecord(level, msgSupplier.get());
        lr.setLoggerName(getName());
        lr.setThrown(thrown);
        log(lr);
    }
}
