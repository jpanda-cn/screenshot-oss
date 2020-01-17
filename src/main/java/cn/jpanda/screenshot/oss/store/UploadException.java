package cn.jpanda.screenshot.oss.store;

import lombok.Getter;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 10:30
 */
public class UploadException extends RuntimeException {
    @Getter
    private ExceptionType exceptionType;

    public UploadException(ExceptionType exceptionType) {
        super(exceptionType.getDescription());
        this.exceptionType = exceptionType;
    }
}
