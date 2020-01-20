package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 10:00
 */
public class SimpleShortcutMatch implements ShortcutMatch {

    @Override
    public boolean isMatch(KeyEvent event, Shortcut shortcut) {
        System.out.println(event.getEventType().getName());
        System.out.println(event.getCode().getName());

        if (shortcut.getAlt() && !event.isAltDown()) {
            return false;
        }
        if (shortcut.getCtrl() && !event.isControlDown()) {
            return false;
        }
        if (shortcut.getShift() && !event.isShiftDown()) {
            return false;
        }
        KeyCode keyCode = shortcut.getCodes().stream().findFirst().orElse(null);
        if (keyCode == null) {
            return false;
        }
        return keyCode.equals(event.getCode());
    }
}
