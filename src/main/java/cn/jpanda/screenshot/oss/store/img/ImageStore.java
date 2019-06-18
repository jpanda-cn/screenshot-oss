package cn.jpanda.screenshot.oss.store.img;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

/**
 * 图片存储实现类
 */
public interface ImageStore {

    /**
     * 前置处理
     */
    default boolean check() {
        return true;
    }

    /**
     * 展示对应的配置页面
     */
    default void config() {

    }

    /**
     * 执行存储图片的操作
     *
     * @param image 图片
     */
    String store(BufferedImage image);

    /**
     * 执行存储图片的操作
     *
     * @param image 图片
     */
    default String store(WritableImage image) {
        return store(SwingFXUtils.fromFXImage(image, null));
    }
}
