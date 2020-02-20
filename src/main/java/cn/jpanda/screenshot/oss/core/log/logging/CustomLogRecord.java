package cn.jpanda.screenshot.oss.core.log.logging;

import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class CustomLogRecord extends LogRecord {
    private transient boolean needToInferCaller;

    public CustomLogRecord(Level level, String msg) {
        super(level, msg);
        needToInferCaller = true;
    }

    @Override
    public String getSourceClassName() {
        if (needToInferCaller) {
            inferCaller();
        }
        return super.getSourceClassName();
    }

    @Override
    public void setSourceClassName(String sourceClassName) {
        setSourceMethodName(sourceClassName);
        needToInferCaller = false;
    }

    public void inferCaller() {
        needToInferCaller = false;
        JavaLangAccess access = SharedSecrets.getJavaLangAccess();
        Throwable throwable = new Throwable();
        int depth = access.getStackTraceDepth(throwable);

        boolean lookingForLogger = true;
        for (int ix = 0; ix < depth; ix++) {
            // Calling getStackTraceElement directly prevents the VM
            // from paying the cost of building the entire stack frame.
            StackTraceElement frame =
                    access.getStackTraceElement(throwable, ix);
            String cname = frame.getClassName();
            boolean isLoggerImpl = isLoggerImplFrame(cname) || isJpandaLoggerImplFrame(cname);
            if (lookingForLogger) {
                // Skip all frames until we have found the first logger frame.
                if (isLoggerImpl) {
                    lookingForLogger = false;
                }
            } else {
                if (!isLoggerImpl) {
                    // skip reflection call
                    if (!cname.startsWith("java.lang.reflect.") && !cname.startsWith("sun.reflect.")) {
                        // We've found the relevant frame.
                        setSourceClassName(cname);
                        setSourceMethodName(frame.getMethodName());
                        return;
                    }
                }
            }
        }
        // We haven't found a suitable frame, so just punt.  This is
        // OK as we are only committed to making a "best effort" here.
    }

    private boolean isLoggerImplFrame(String cname) {
        // the log record could be created for a platform logger
        return (cname.equals("java.util.logging.Logger") ||
                cname.startsWith("java.util.logging.LoggingProxyImpl") ||
                cname.startsWith("sun.util.logging."));
    }

    private boolean isJpandaLoggerImplFrame(String cname) {
        return (cname.startsWith("cn.jpanda.screenshot.oss.core.log"));
    }
}
