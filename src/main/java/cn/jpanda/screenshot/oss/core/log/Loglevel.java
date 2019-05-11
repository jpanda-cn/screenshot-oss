package cn.jpanda.screenshot.oss.core.log;

public enum Loglevel {
    TRACE(0),
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERR(4);


    private int level;

    Loglevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
