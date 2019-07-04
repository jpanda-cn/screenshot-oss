package cn.jpanda.screenshot.oss.core.log.logging;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogFactory;

import java.util.logging.Logger;

public class LoggingFactory implements LogFactory {

    @Override
    public Log getLog(Object o) {
        return getLog(o.getClass());
    }

    @Override
    public Log getLog(Class c) {
        return getLog(c.getCanonicalName());
    }

    @Override
    public Log getLog(String msg) {
        Logger logger=Logger.getLogger(msg);
        logger.setLevel(Logger.getGlobal().getLevel());
        return new LoggingLog(logger);
    }
}
