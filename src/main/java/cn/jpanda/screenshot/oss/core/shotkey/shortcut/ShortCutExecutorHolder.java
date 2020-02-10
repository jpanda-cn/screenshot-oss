package cn.jpanda.screenshot.oss.core.shotkey.shortcut;


import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 10:19
 */
@Builder
@Getter
@EqualsAndHashCode
public class ShortCutExecutorHolder implements EventHandler<KeyEvent> {

    private Shortcut shortcut;

    private ShortCutExecutor executor;

    private ShortcutMatch match;


    @Override
    public void handle(KeyEvent event) {
        if (match.isMatch(event, shortcut)) {
            executor.exec(event);
        }
    }
}
