package cn.jpanda.screenshot.oss.store;

import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

/**
 * 图片处理完成回调剪切板
 */
public interface ClipboardCallback {

    void callback(BufferedImage image, String path);
}
