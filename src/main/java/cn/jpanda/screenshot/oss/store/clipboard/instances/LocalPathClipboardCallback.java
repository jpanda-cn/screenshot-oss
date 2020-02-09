package cn.jpanda.screenshot.oss.store.clipboard.instances;

import cn.jpanda.screenshot.oss.core.annotations.ClipType;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.image.BufferedImage;

@ClipType(name = "地址",icon ="/images/stores/icons/url.png")
public class LocalPathClipboardCallback implements ClipboardCallback {

    @Override
    public void callback(BufferedImage image, String path) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(path);
        clipboard.setContent(clipboardContent);
    }
}
