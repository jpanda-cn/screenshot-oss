package cn.jpanda.screenshot.oss.store.img.instances.uomg;

import cn.jpanda.screenshot.oss.store.ExceptionType;

public class UmogExceptionType extends ExceptionType {
    public static UmogExceptionType CANT_UPLOAD=new UmogExceptionType("上传失败",2);

    public UmogExceptionType(String description, Integer level) {
        super(description, level);
    }

    public UmogExceptionType() {
    }
}
