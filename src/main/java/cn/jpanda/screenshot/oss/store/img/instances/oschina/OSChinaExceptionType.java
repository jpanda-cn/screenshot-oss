package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.store.ExceptionType;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 10:27
 */
public class OSChinaExceptionType extends ExceptionType {
    public static OSChinaExceptionType TIME_OUT = new OSChinaExceptionType("请求超时", 1);
    public static OSChinaExceptionType UPLOAD_FAILED = new OSChinaExceptionType("服务器错误", 2);

    public OSChinaExceptionType(String description, Integer level) {
        super(description, level);
    }

    public OSChinaExceptionType() {
    }
}
