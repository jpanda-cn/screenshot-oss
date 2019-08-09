package cn.jpanda.screenshot.oss.core.log.defaults;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.Loglevel;

import java.time.LocalDateTime;

public class DefaultOutLog implements Log {
    // 统一前缀
    private final String prefix;

    private final DefaultOutLogConfig config;

    public DefaultOutLog(String prefix, DefaultOutLogConfig config) {
        this.prefix = prefix;
        this.config = config;
    }

    @Override
    public void trace(String msg) {
        if (config.needOut(Loglevel.TRACE)) {
            out(Loglevel.TRACE, msg);
        }
    }

    @Override
    public void trace(String msg, Object... objects) {
        if (config.needOut(Loglevel.TRACE)) {
            out(Loglevel.TRACE, msg, objects);
        }
    }

    @Override
    public void trace(String msg, Throwable throwable) {
        if (config.needOut(Loglevel.TRACE)) {
            out(Loglevel.TRACE, msg, throwable);
        }
    }

    @Override
    public void debug(String msg) {
        if (config.needOut(Loglevel.DEBUG)) {
            out(Loglevel.DEBUG, msg);
        }
    }

    @Override
    public void debug(String msg, Object... objects) {
        if (config.needOut(Loglevel.DEBUG)) {
            out(Loglevel.DEBUG, msg, objects);
        }
    }

    @Override
    public void debug(String msg, Throwable throwable) {
        if (config.needOut(Loglevel.DEBUG)) {
            out(Loglevel.DEBUG, msg, throwable);
        }
    }

    @Override
    public void info(String msg) {
        if (config.needOut(Loglevel.INFO)) {
            out(Loglevel.INFO, msg);
        }
    }

    @Override
    public void info(String msg, Object... objects) {
        if (config.needOut(Loglevel.INFO)) {
            out(Loglevel.INFO, msg, objects);
        }
    }

    @Override
    public void info(String msg, Throwable throwable) {
        if (config.needOut(Loglevel.INFO)) {
            out(Loglevel.INFO, msg, throwable);
        }
    }

    @Override
    public void warn(String msg) {
        if (config.needOut(Loglevel.WARN)) {
            out(Loglevel.WARN, msg);
        }
    }

    @Override
    public void warn(String msg, Object... objects) {
        if (config.needOut(Loglevel.WARN)) {
            out(Loglevel.WARN, msg, objects);
        }
    }

    @Override
    public void warn(String msg, Throwable throwable) {
        if (config.needOut(Loglevel.WARN)) {
            out(Loglevel.WARN, msg, throwable);
        }
    }

    @Override
    public void err(String msg) {
        if (config.needOut(Loglevel.ERR)) {
            out(Loglevel.ERR, msg);
        }
    }

    @Override
    public void err(String msg, Object... objects) {
        if (config.needOut(Loglevel.ERR)) {
            out(Loglevel.ERR, msg, objects);
        }
    }

    @Override
    public void err(String msg, Throwable throwable) {
        if (config.needOut(Loglevel.ERR)) {
            out(Loglevel.ERR, msg, throwable);
        }
    }

    protected void out(Loglevel loglevel, String msg, Object... objects) {
        msg = msg.replaceAll("\\{}", "%s");
        out(loglevel, String.format(msg, objects));
    }

    protected void out(Loglevel loglevel, String msg, Throwable throwable) {
        out(loglevel, msg);
        Throwable throwable1 = throwable;
        do {
            out(loglevel, throwable1.getMessage());
            throwable1 = throwable1.getCause();
        } while (throwable1 != null);
    }

    protected void out(Loglevel loglevel, String msg) {

        System.out.println(getCurrentTime()+"\t"+loglevel.name() + "\t" +  " [ Thread: " + Thread.currentThread().getName() + " ] " + prefix + " : " + msg);
    }

    protected String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear()
                +"-" +
                now.getMonthValue() +
                "-" +
                now.getDayOfMonth() +
                " " +
                now.getHour() +
                ":" +
                now.getMinute() +
                ":" +
                now.getSecond() +
                "." +
                now.getNano();
    }
}
