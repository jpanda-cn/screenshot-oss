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
@ClipType(name = "图片", type = ClipboardType.NOT_NEED)
public class ImageClipboardCallback implements ClipboardCallback {
    @Override
    public void callback(BufferedImage image, String path) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putImage(SwingFXUtils.toFXImage(image, null));
        clipboard.setContent(clipboardContent);
    }
}
