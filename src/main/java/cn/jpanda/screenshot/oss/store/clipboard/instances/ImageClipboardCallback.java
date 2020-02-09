package cn.jpanda.screenshot.oss.store.clipboard.instances;

import cn.jpanda.screenshot.oss.common.enums.ClipboardType;
import cn.jpanda.screenshot.oss.core.annotations.ClipType;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.image.BufferedImage;

/**
 * 将图片放到剪切板
 */
@ClipType(name = ImageClipboardCallback.NAME, type = ClipboardType.NOT_NEED,icon ="/images/stores/icons/image.png")
public class ImageClipboardCallback implements ClipboardCallback {
    public static final String NAME = "图片";

    @Override
    public void callback(BufferedImage image, String path) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putImage(SwingFXUtils.toFXImage(image, null));
        clipboard.setContent(clipboardContent);
    }
}
