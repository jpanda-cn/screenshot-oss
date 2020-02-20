package cn.jpanda.screenshot.oss.store.img.instances.imgurl;

import cn.jpanda.screenshot.oss.store.ExceptionType;

public class ImgUrlExceptionType extends ExceptionType {
    public static ImgUrlExceptionType CANT_UPLOAD=new ImgUrlExceptionType("上传失败",2);

    public ImgUrlExceptionType(String description, Integer level) {
        super(description, level);
    }

    public ImgUrlExceptionType() {
    }
}
