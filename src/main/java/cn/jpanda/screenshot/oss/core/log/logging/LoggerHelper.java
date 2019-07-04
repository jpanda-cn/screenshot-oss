package cn.jpanda.screenshot.oss.core.log.logging;

import java.util.logging.Logger;

public class LoggerHelper {


    public static Logger getLogger(Class caller) {
        return getLogger(caller.getCanonicalName());
    }

    public static Logger getLogger(String name) {
        return CustomLogManager.demandLogger(name);
    }
}
