package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 10:00
 */
public class SimpleShortcutMatch implements ShortcutMatch {
    private Log log = LogHolder.getInstance().getLog(getClass());
    @Override
    public boolean isMatch(KeyEvent event, Shortcut shortcut) {
        if (event.isConsumed()) {
            log.trace("event :{} is consumed ",event);
            return false;
        }
        if (!shortcut.getAlt().equals(event.isAltDown())) {
            log.trace("alt not down ");
            return false;
        }
        if (!shortcut.getCtrl().equals(event.isControlDown())) {
            log.trace("ctrl not down ");
            return false;
        }
        if (!shortcut.getShift().equals(event.isShiftDown())) {
            log.trace("shift not down ");
            return false;
        }
        KeyCode keyCode = shortcut.getCodes().stream().findFirst().orElse(null);
        if (keyCode == null) {
            log.trace("no key code ");
            return false;
        }

        if (keyCode.equals(event.getCode())) {
            log.debug("event={},key={},alt={},ctrl={},shift={},description={}",event.getEventType().getName(),event.getCode().getName(),event.isAltDown(),event.isControlDown(),event.isShiftDown(),shortcut.getDescription());
            event.consume();
            return true;
        }
        return false;
    }
}
