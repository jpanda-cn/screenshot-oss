package cn.jpanda.screenshot.oss.core.log.defaults;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogFactory;

public class DefaultOutLogFactory implements LogFactory {
    private DefaultOutLogConfig config;

    public DefaultOutLogFactory(DefaultOutLogConfig config) {
        this.config = config;
    }

    @Override
    public Log getLog(Object o) {
        return new DefaultOutLog(o.getClass().getCanonicalName(), config);
    }

    @Override
    public Log getLog(Class c) {
        return new DefaultOutLog(c.getCanonicalName(), config);
    }

    @Override
    public Log getLog(String msg) {
        return new DefaultOutLog(msg, config);
    }
}
