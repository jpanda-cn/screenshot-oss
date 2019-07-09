package cn.jpanda.screenshot.oss.store;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class ImageStoreResultWrapper {
    public ImageStoreResultWrapper() {
    }

    public ImageStoreResultWrapper(ImageStoreResult imageStoreResult) {
        this.success = imageStoreResult.getSuccess().get();
        this.image = imageStoreResult.getImage().get();
        this.path = imageStoreResult.getPath().get();
        this.imageStore = imageStoreResult.getImageStore().get();
        this.exception = imageStoreResult.getException().get();
    }

    /**
     * 存储是否成功
     */
    private Boolean success;
    /**
     * 图片本身
     */
    private BufferedImage image;

    /**
     * 图片保存路径
     */
    private String path;

    /**
     * 使用的图片存储方式
     */
    private String imageStore;
    /**
     * 对应的异常数据
     */
    private ExceptionWrapper exception;

    public ImageStoreResult toImageStoreResult() {
        return new ImageStoreResult(new SimpleBooleanProperty(success), new SimpleObjectProperty<>(image), new SimpleStringProperty(path), new SimpleStringProperty(imageStore), new SimpleObjectProperty<>(exception));
    }
}
