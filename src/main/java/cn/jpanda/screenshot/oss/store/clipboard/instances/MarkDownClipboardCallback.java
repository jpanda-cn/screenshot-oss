package cn.jpanda.screenshot.oss.store.clipboard.instances;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.ClipType;
import cn.jpanda.screenshot.oss.core.i18n.I18nConstants;
import cn.jpanda.screenshot.oss.core.i18n.I18nResource;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.image.BufferedImage;

@ClipType(name = "MARKDOWN")
public class MarkDownClipboardCallback implements ClipboardCallback {
    private Configuration configuration;

    public MarkDownClipboardCallback(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void callback(BufferedImage image, String path) {
        I18nResource i18nResource = configuration.getUniqueBean(I18nResource.class);
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(String.format("![%s](%s)", i18nResource.get(I18nConstants.markdown_tips), path));
        clipboard.setContent(clipboardContent);
    }
}
