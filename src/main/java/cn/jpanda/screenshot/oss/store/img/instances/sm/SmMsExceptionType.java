package cn.jpanda.screenshot.oss.store.img.instances.sm;

import cn.jpanda.screenshot.oss.store.ExceptionType;

public class SmMsExceptionType extends ExceptionType {
    public static SmMsExceptionType CANT_UPLOAD=new SmMsExceptionType("上传失败",2);

    public SmMsExceptionType(String description, Integer level) {
        super(description, level);
    }

    public SmMsExceptionType() {
    }
}
