package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

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
        return bufferedImage.getSubimage(rectangle.xProperty().intValue() + 1, rectangle.yProperty().intValue() + 1, rectangle.widthProperty().intValue() - 2, rectangle.heightProperty().intValue() - 2);
    }

    @Override
    public void done(BufferedImage image) {
        handlerClipboardContent(image, saveImage(image));
        // 处理是否配置了截图预览功能
        preview(image);

    }

    public void preview(BufferedImage image) {
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        if (globalConfigPersistence.isPreview()) {
            // 从剪切板搞定图片
            Stage stage = new Stage();
            stage.getIcons().add(new Image("/logo.png"));
            stage.setTitle("预览图片,如不需要，请到设置中关闭该功能。");
            stage.setScene(new Scene(new AnchorPane(new ImageView(SwingFXUtils.toFXImage(image, null)))));
            stage.showAndWait();
        }
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
