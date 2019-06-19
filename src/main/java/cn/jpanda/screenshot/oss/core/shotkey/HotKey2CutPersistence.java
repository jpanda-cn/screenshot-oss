package cn.jpanda.screenshot.oss.core.shotkey;

import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import javafx.scene.input.KeyCode;
import lombok.Data;

/**
 * 截图快捷键
 */
@Data
public class HotKey2CutPersistence implements Persistence {
    private boolean shift = false;
    private boolean alt = false;
    private boolean ctrl = false;
    private String code = KeyCode.J.getName();
}
