package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;

import java.awt.image.BufferedImage;

/**
 * 默认的截图进程
 */
public class DefaultScreenshotsProcess implements ScreenshotsProcess {

    private Configuration configuration;

    public DefaultScreenshotsProcess(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public BufferedImage snapshot(Scene scene, Rectangle rectangle) {
        WritableImage wImage = scene.snapshot(null);
        // 将图片转为BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(wImage, null);
        // 同步红色边框
        return bufferedImage.getSubimage(rectangle.xProperty().intValue() + 1, rectangle.yProperty().intValue() + 1, rectangle.widthProperty().intValue() - 2, rectangle.heightProperty().intValue() - 2);
    }

    @Override
    public void done(BufferedImage image) {
        handlerClipboardContent(image, saveImage(image));

    }

    public String saveImage(BufferedImage image) {
        // 获取当前
        ImageStore imageStore = configuration.getUniqueBean(ImageStoreRegisterManager.class).getImageStore(configuration.getPersistence(GlobalConfigPersistence.class).getImageStore());
        return imageStore.store(image);
    }

    public String saveImage(WritableImage image) {
        return configuration.getUniqueBean(ImageStoreRegisterManager.class).getImageStore(configuration.getPersistence(GlobalConfigPersistence.class).getImageStore()).store(image);
    }

    public void handlerClipboardContent(BufferedImage image, String path) {
        ClipboardCallback clipboardCallback = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class).get(configuration.getPersistence(GlobalConfigPersistence.class).getClipboardCallback());
        clipboardCallback.callback(image, path);
    }
}
