package cn.jpanda.screenshot.oss.store;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Builder;
import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * 图片存储结果
 */
@Data
@Builder
public class ImageStoreResult {
    /**
     * 存储是否成功
     */
    private SimpleBooleanProperty success = new SimpleBooleanProperty(false);
    /**
     * 图片本身
     */
    private SimpleObjectProperty<BufferedImage> image = new SimpleObjectProperty<>();

    /**
     * 图片保存路径
     */
    private SimpleStringProperty path;

    /**
     * 使用的图片存储方式
     */
    private SimpleStringProperty imageStore;
    /**
     * 对应的异常数据
     */
    private SimpleObjectProperty<ExceptionWrapper> exception;
}
