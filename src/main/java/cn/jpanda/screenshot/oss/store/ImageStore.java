package cn.jpanda.screenshot.oss.store;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

/**
 * 图片存储实现类
 */
public interface ImageStore {
    /**
     * 执行存储图片的操作
     *
     * @param image 图片
     */
    void store(BufferedImage image);

    /**
     * 执行存储图片的操作
     *
     * @param image 图片
     */
    default void store(WritableImage image) {
        store(SwingFXUtils.fromFXImage(image, null));
    }
}
