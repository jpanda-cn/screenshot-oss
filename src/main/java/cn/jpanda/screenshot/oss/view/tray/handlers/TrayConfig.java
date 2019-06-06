package cn.jpanda.screenshot.oss.view.tray.handlers;

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
}
