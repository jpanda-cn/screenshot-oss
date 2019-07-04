package cn.jpanda.screenshot.oss.core.log.logging;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.Loglevel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingLog implements Log {
    private Logger logger;

    public LoggingLog(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void trace(String msg) {
        logger.log(Level.FINEST, msg);
    }

    @Override
    public void trace(String msg, Object... objects) {
        logger.log(Level.FINEST, msg, objects);
    }

    @Override
    public void trace(String msg, Throwable throwable) {
        logger.log(Level.FINEST, msg, throwable);
    }

    @Override
    public void debug(String msg) {
        logger.log(Level.FINE, msg);
    }

    @Override
    public void debug(String msg, Object... objects) {
        logger.log(Level.FINE, msg, objects);
    }

    @Override
    public void debug(String msg, Throwable throwable) {
        logger.log(Level.FINE, msg, throwable);
    }

    @Override
    public void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    @Override
    public void info(String msg, Object... objects) {
        logger.log(Level.INFO, msg, objects);
    }

    @Override
    public void info(String msg, Throwable throwable) {
        logger.log(Level.INFO, msg, throwable);
    }

    @Override
    public void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    @Override
    public void warn(String msg, Object... objects) {
        logger.log(Level.WARNING, msg, objects);
    }

    @Override
    public void warn(String msg, Throwable throwable) {
        logger.log(Level.WARNING, msg, throwable);
    }

    @Override
    public void err(String msg) {
        logger.log(Level.SEVERE, msg);
    }

    @Override
    public void err(String msg, Object... objects) {
        logger.log(Level.SEVERE, msg, objects);
    }

    @Override
    public void err(String msg, Throwable throwable) {
        logger.log(Level.SEVERE, msg, throwable);
    }


    private Loglevel toLogLevel(Level level) {
        if (level == null) {
            // 返回默认
            level = Logger.getGlobal().getLevel();
        }
        if (level.equals(Level.FINEST)) {
            return Loglevel.TRACE;
        } else if (level.equals(Level.FINE)) {
            return Loglevel.DEBUG;
        } else if (level.equals(Level.INFO)) {
            return Loglevel.INFO;
        } else if (level.equals(Level.WARNING)) {
            return Loglevel.WARN;
        } else if (level.equals(Level.SEVERE)) {
            return Loglevel.ERR;
        }
        return Loglevel.DEBUG;
    }
}
