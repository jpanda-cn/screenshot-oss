package cn.jpanda.screenshot.oss.core.exceptions;

/**
 * 运行异常
 */
public class JpandaRuntimeException extends RuntimeException {
    public JpandaRuntimeException() {
    }

    public JpandaRuntimeException(String message) {
        super(message);
    }

    public JpandaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JpandaRuntimeException(Throwable cause) {
        super(cause);
    }

    public JpandaRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
