package cn.jpanda.screenshot.oss.store.clipboard;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.image.BufferedImage;

public class LocalPathClipboardCallback implements ClipboardCallback {

    @Override
    public void callback(BufferedImage image, String path) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(path);
        clipboard.setContent(clipboardContent);
    }
}
