package cn.jpanda.screenshot.oss.store;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ExceptionWrapper {

    private String message;

    private String details;

    public ExceptionWrapper(Throwable throwable) {
        this.message = throwable.getMessage();
        // 获取异常对象
        // 展示五十行日志
        StackTraceElement[] traceElements = throwable.getStackTrace();
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement traceElement : traceElements) {
            stackTrace.append(traceElement.toString()).append("\r\n");
        }
        this.details = stackTrace.toString();
    }

    public ExceptionWrapper() {
    }
}
