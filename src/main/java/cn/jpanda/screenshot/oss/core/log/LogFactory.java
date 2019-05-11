package cn.jpanda.screenshot.oss.core.log;

public interface LogFactory {

    Log getLog(Object o);

    Log getLog(Class c);

    Log getLog(String msg);
}
