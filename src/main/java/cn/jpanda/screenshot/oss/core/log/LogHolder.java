package cn.jpanda.screenshot.oss.core.log;

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
}
