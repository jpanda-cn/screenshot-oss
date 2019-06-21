package cn.jpanda.screenshot.oss.core.shotkey;

import cn.jpanda.screenshot.oss.core.annotations.Profile;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import javafx.scene.input.KeyCode;
import lombok.Data;

/**
 * 截图快捷键
 */
@Data
@Profile
public class HotKey2CutPersistence implements Persistence {
    private boolean shift = true;
    private boolean alt = true;
    private boolean ctrl = true;
    private String code = KeyCode.J.getName();
}
