package cn.jpanda.screenshot.oss.core.shotkey.shortcut;


import javafx.scene.input.KeyEvent;
import lombok.Builder;
import lombok.Getter;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 10:19
 */
@Builder
@Getter
public class ShortCutExecutorHolder {

    private Shortcut shortcut;

    private ShortCutExecutor executor;

    private ShortcutMatch match;

    public void exec(KeyEvent event) {
        if (match.isMatch(event, shortcut)) {
            executor.exec(event);
        }
    }
}
