package cn.jpanda.screenshot.oss.core.shotkey.shortcut;

import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 * 快键键管理器
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/20 9:56
 */
public class KeyboardShortcutsManager {

    private Log log = LogHolder.getInstance().getLog(getClass());

    /**
     * 注册快捷键
     *
     * @return
     */
    public boolean registryShortCut(EventTarget eventTarget, ShortCutExecutorHolder holder) {
        log.debug(String.format("will registry the short key with %s  and %s ,%s", eventTarget.toString(),holder.getExecutor(), holder.getShortcut().toString()));

        if (eventTarget instanceof Window) {
            if (((Window) eventTarget).getScene() != null) {
                registryShortCut(((Window) eventTarget).getScene(), holder);
            } else {
                ((Window) eventTarget).sceneProperty().addListener((observable, oldValue, newValue) -> registryShortCut(newValue, holder));
            }
            return true;
        }
        if (eventTarget instanceof Scene) {
            ((Scene) eventTarget).addEventHandler(holder.getShortcut().getKeyEvent(), holder);
            return true;
        }
        if (eventTarget instanceof Node) {
            ((Node) eventTarget).addEventHandler(holder.getShortcut().getKeyEvent(), holder);
            return true;
        }
        return false;
    }

    public boolean remove(EventTarget eventTarget, ShortCutExecutorHolder holder) {
        log.debug(String.format("will remove the short key with %s  and %s,%s", eventTarget.toString(), holder.getExecutor(), holder.getShortcut().toString()));

        if (eventTarget instanceof Window) {
            if (((Window) eventTarget).getScene() != null) {
                remove(((Window) eventTarget).getScene(), holder);
            } else {
                ((Window) eventTarget).sceneProperty().addListener((observable, oldValue, newValue) -> remove(newValue, holder));
            }
            return true;
        }
        if (eventTarget instanceof Scene) {
            ((Scene) eventTarget).removeEventHandler(holder.getShortcut().getKeyEvent(), holder);
            return true;
        }
        if (eventTarget instanceof Node) {
            ((Node) eventTarget).removeEventHandler(holder.getShortcut().getKeyEvent(), holder);
            return true;
        }
        return false;
    }
}
