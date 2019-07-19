package cn.jpanda.screenshot.oss.store.img.instances.alioss;

import cn.jpanda.screenshot.oss.store.ExceptionType;

public class AliOSSExceptionType extends ExceptionType {
    public static AliOSSExceptionType CANT_SAVE=new AliOSSExceptionType("保存失败",1);
    public static AliOSSExceptionType CANT_UPLOAD=new AliOSSExceptionType("上传失败",2);

    public AliOSSExceptionType(String description, Integer level) {
        super(description, level);
    }

    public AliOSSExceptionType() {
    }
}
