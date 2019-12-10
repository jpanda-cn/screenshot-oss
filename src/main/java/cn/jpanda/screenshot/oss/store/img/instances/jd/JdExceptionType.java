package cn.jpanda.screenshot.oss.store.img.instances.jd;

import cn.jpanda.screenshot.oss.store.ExceptionType;

public class JdExceptionType extends ExceptionType {
    public static JdExceptionType CANT_SAVE=new JdExceptionType("保存失败",1);
    public static JdExceptionType CANT_UPLOAD=new JdExceptionType("上传失败",2);

    public JdExceptionType(String description, Integer level) {
        super(description, level);
    }

    public JdExceptionType() {
    }
}
