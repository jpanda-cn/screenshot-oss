package cn.jpanda.screenshot.oss.view.tray.toolkits;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.Data;

/**
 * 标志性接口
 */
@Data
public class TrayConfig {
    private SimpleDoubleProperty stroke = new SimpleDoubleProperty(1);
    private SimpleObjectProperty<Color> strokeColor = new SimpleObjectProperty<>(Color.RED);
    private SimpleObjectProperty<Font> font = new SimpleObjectProperty<>(Font.getDefault());
    private SimpleObjectProperty<Object> ext = new SimpleObjectProperty<>();

    public TrayConfig shallowClone() {
        TrayConfig copy = new TrayConfig();
        copy.stroke.set(stroke.get());
        copy.strokeColor.set(strokeColor.get());
        copy.font.set(font.get());
        return copy;
    }

    public <T> T loadExt() {
        return (T) ext.get();
    }
}
