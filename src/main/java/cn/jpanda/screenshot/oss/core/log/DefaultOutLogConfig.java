package cn.jpanda.screenshot.oss.core.log;

public class DefaultOutLogConfig {

    private Loglevel loglevel;

    public DefaultOutLogConfig(Loglevel loglevel) {
        this.loglevel = loglevel;
    }

    public boolean needOut(Loglevel loglevel) {
        return loglevel.getLevel() >= this.loglevel.getLevel();
    }
}
