package cn.jpanda.screenshot.oss.core.log;

public interface Log {

    void trace(String msg);
    void trace(String msg,Object ...objects);
    void trace(String msg,Throwable throwable);

    void debug(String msg);
    void debug(String msg,Object ...objects);
    void debug(String msg,Throwable throwable);

    void info(String msg);
    void info(String msg,Object ...objects);
    void info(String msg,Throwable throwable);

    void warn(String msg);
    void warn(String msg,Object ...objects);
    void warn(String msg,Throwable throwable);

    void err(String msg);
    void err(String msg,Object ...objects);
    void err(String msg,Throwable throwable);

}
