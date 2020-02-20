package cn.jpanda.screenshot.oss.store.img.instances.qiniu;

import cn.jpanda.screenshot.oss.store.ExceptionType;

public class QiNiuExceptionType extends ExceptionType {
    public static QiNiuExceptionType CANT_SAVE=new QiNiuExceptionType("保存失败",1);
    public static QiNiuExceptionType CANT_UPLOAD=new QiNiuExceptionType("上传失败",2);

    public QiNiuExceptionType(String description, Integer level) {
        super(description, level);
    }

    public QiNiuExceptionType() {
    }
}
