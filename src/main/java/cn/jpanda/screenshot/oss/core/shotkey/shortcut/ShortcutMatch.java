package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import javafx.scene.input.KeyEvent;

/**
 * 快键键匹配器
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 10:12
 */
public interface ShortcutMatch {
    boolean isMatch(KeyEvent event, Shortcut shortcut);
}
