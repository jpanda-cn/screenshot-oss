package cn.jpanda.screenshot.oss.core.log.sl4j;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.log.Log;
import org.slf4j.Logger;

public class LogBackLog implements Log {
    private Logger logger;

    private LogBackLog(Logger logger) {
        this.logger = logger;
    }

    public static LogBackLog of(Logger logger) {
        return new LogBackLog(logger);
    }

    @Override
    public void trace(String msg) {
        logger.trace(msg);
    }

    @Override
    public void trace(String msg, Object... objects) {
        logger.trace(adapterPlaceholders(msg), objects);
    }

    @Override
    public void trace(String msg, Throwable throwable) {
        logger.trace("{}:{}", adapterPlaceholders(msg), throwable);
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void debug(String msg, Object... objects) {
        logger.debug(adapterPlaceholders(msg), objects);
    }

    @Override
    public void debug(String msg, Throwable throwable) {
        logger.debug("{}:{}", adapterPlaceholders(msg), throwable);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String msg, Object... objects) {
        logger.info(adapterPlaceholders(msg), objects);
    }

    @Override
    public void info(String msg, Throwable throwable) {
        logger.info("{}:{}", adapterPlaceholders(msg), throwable);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String msg, Object... objects) {
        logger.warn(adapterPlaceholders(msg), objects);
    }

    @Override
    public void warn(String msg, Throwable throwable) {
        logger.warn("{}:{}", adapterPlaceholders(msg), throwable);
    }

    @Override
    public void err(String msg) {
        logger.error(msg);
    }

    @Override
    public void err(String msg, Object... objects) {
        logger.error(adapterPlaceholders(msg), objects);
    }

    @Override
    public void err(String msg, Throwable throwable) {
        logger.error("{}:{}", adapterPlaceholders(msg), throwable);
    }

    private String adapterPlaceholders(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return msg;
        }
        return msg.replaceAll("\\{[0-9]}", "{}");
    }
}

