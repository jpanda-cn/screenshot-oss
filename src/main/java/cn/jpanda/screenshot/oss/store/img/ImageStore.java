package cn.jpanda.screenshot.oss.store.img;

import cn.jpanda.screenshot.oss.store.ImageStoreResultWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.Window;

import java.awt.image.BufferedImage;
import java.util.UUID;

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
    default String store(BufferedImage image) {
        return store(image, "png");
    }

    String store(BufferedImage image, String extensionName);

    boolean retry(ImageStoreResultWrapper imageStoreResultWrapper, Window window);

    /**
     * 执行存储图片的操作
     *
     * @param image 图片
     */
    default String store(WritableImage image) {
        return store(SwingFXUtils.fromFXImage(image, null));
    }

    default String store(WritableImage image, String extensionName) {
        return store(SwingFXUtils.fromFXImage(image, null), extensionName);
    }

    default String getFileSuffix(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    default String fileNameGenerator(String extensionName) {
        return UUID.randomUUID().toString().concat(".").concat(extensionName);
    }

}
