package cn.jpanda.screenshot.oss.core.log.sl4j;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogFactory;
import org.slf4j.LoggerFactory;

public class LogBackSl4jLogFactory implements LogFactory {
    @Override
    public Log getLog(Object o) {
        return LogBackLog.of(LoggerFactory.getLogger(o.getClass()));
    }

    @Override
    public Log getLog(Class c) {
        return LogBackLog.of(LoggerFactory.getLogger(c));
    }

    @Override
    public Log getLog(String msg) {
        return LogBackLog.of(LoggerFactory.getLogger(msg));
    }
}
