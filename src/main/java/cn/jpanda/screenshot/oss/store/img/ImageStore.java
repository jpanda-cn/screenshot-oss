package cn.jpanda.screenshot.oss.store.img;

import cn.jpanda.screenshot.oss.store.ImageStoreResultWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.Window;

import java.awt.image.BufferedImage;

/**
 * 图片存储实现类
 */
public interface ImageStore {

    /**
     * 前置处理
     */
    default boolean check(Window stage) {
        return true;
    }

    /**
     * 展示对应的配置页面
     */
    default void config(Window stage) {

    }

    /**
     * 执行存储图片的操作
     *
     * @param image 图片
     */
    String store(BufferedImage image);

    boolean retry(ImageStoreResultWrapper imageStoreResultWrapper,Window window);

    /**
     * 执行存储图片的操作
     *
     * @param image 图片
     */
    default String store(WritableImage image) {
        return store(SwingFXUtils.fromFXImage(image, null));
    }
}
