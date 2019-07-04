package cn.jpanda.screenshot.oss.core.log.logging;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogFactory;
import lombok.SneakyThrows;

import java.util.logging.Logger;

public class LoggingFactory implements LogFactory {

    @Override
    public Log getLog(Object o) {
        return getLog(o.getClass());
    }

    @Override
    @SneakyThrows
    public Log getLog(Class c) {
        Logger logger = LoggerHelper.getLogger(c);
        return new LoggingLog(logger);
    }

    @Override
    public Log getLog(String msg) {
        Logger logger = Logger.getLogger(msg);
        return new LoggingLog(logger);
    }
}
