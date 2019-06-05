package cn.jpanda.screenshot.oss.view.tray.handlers;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.Data;

/**
 * 标志性接口
 */
@Data
public class TrayConfig {

    private double stroke = 1;
    private Color strokeColor = Color.RED;
    private Font font = Font.getDefault();
}
