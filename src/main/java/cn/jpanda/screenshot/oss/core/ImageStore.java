package cn.jpanda.screenshot.oss.core;

import java.awt.image.BufferedImage;

public interface ImageStore<T> {
    /**
     * 执行存储图片的操作
     * @param image 图片
     * @return 图片存储完成后的返回结果
     */
    T store(BufferedImage image);
}
