package cn.jpanda.screenshot.oss.store;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Data
public class ExceptionWrapper {
    @Getter
    private String message;
    @Getter
    private String details;


    public ExceptionWrapper(Throwable throwable) {
        Throwable e = throwable;
        while (e != null && e.getCause() != null) {
            e = e.getCause();
        }
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
