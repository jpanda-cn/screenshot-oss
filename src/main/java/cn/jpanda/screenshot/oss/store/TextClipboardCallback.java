package cn.jpanda.screenshot.oss.store;

import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TextClipboardCallback implements ClipboardCallback {

    @Override
    public void callback(BufferedImage image, String path) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        Map<DataFormat, Object> map = new HashMap<>();
        map.put(DataFormat.PLAIN_TEXT, path);
        clipboard.setContent(map);
    }
}
