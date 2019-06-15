package cn.jpanda.screenshot.oss.core.log;

/**
 * 日志工具持有者
 */
public class LogHolder {
    private static LogHolder ourInstance = new LogHolder();

    public static LogHolder getInstance() {
        return ourInstance;
    }

    private LogFactory logFactory;

    private LogHolder() {
    }

    public LogFactory getLogFactory() {
        return logFactory;
    }

    public void initLogFactory(LogFactory logFactory) {
        this.logFactory = logFactory;
    }

    public Log getLog(Object o) {
        return getLogFactory().getLog(o);
    }

    public Log getLog(Class c) {
        return getLogFactory().getLog(c);
    }

    public Log getLog(String msg) {
        return getLogFactory().getLog(msg);
    }
}
