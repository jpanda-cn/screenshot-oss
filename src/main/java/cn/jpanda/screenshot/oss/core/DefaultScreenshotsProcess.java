package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.common.toolkit.LoadingShower;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.SneakyThrows;

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
        return bufferedImage.getSubimage(rectangle.xProperty().intValue(), rectangle.yProperty().intValue(), rectangle.widthProperty().intValue(), rectangle.heightProperty().intValue());
    }

    @Override
    public void done(Window window, BufferedImage image) {
        done(window, image, "png");

    }

    @Override
    public void done(Window window, BufferedImage image, String extendsName) {
        handlerClipboardContent(image, saveImage(window, image, extendsName));
    }

    @SneakyThrows
    public String saveImage(Window window, BufferedImage image, String extendsName) {

        // 获取当前
        Stage loading = LoadingShower.createUploading(window);
        ImageStore imageStore = configuration.getUniqueBean(ImageStoreRegisterManager.class).getImageStore(configuration.getPersistence(GlobalConfigPersistence.class).getImageStore());
        final String[] path = {null};
        new Thread(() -> {
            path[0] = imageStore.store(image, extendsName);
            Platform.runLater(loading::close);
        }).start();

        loading.showAndWait();
        return path[0];
    }

    public String saveImage(WritableImage image) {
        return configuration.getUniqueBean(ImageStoreRegisterManager.class).getImageStore(configuration.getPersistence(GlobalConfigPersistence.class).getImageStore()).store(image);
    }

    public void handlerClipboardContent(BufferedImage image, String path) {
        ClipboardCallback clipboardCallback = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class).get(configuration.getPersistence(GlobalConfigPersistence.class).getClipboardCallback());
        clipboardCallback.callback(image, path);
    }
}
