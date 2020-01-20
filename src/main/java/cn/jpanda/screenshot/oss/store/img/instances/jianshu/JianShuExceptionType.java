package cn.jpanda.screenshot.oss.store.img.instances.jianshu;

import cn.jpanda.screenshot.oss.store.ExceptionType;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 10:27
 */
public class JianShuExceptionType extends ExceptionType {
    public static JianShuExceptionType TOKEN_FAIL = new JianShuExceptionType("获取TOKEN失败", 1);
    public static JianShuExceptionType TIME_OUT = new JianShuExceptionType("请求超时", 2);
    public static JianShuExceptionType UPLOAD_FAILED = new JianShuExceptionType("服务器错误", 3);

    public JianShuExceptionType(String description, Integer level) {
        super(description, level);
    }

    public JianShuExceptionType() {
    }
}
