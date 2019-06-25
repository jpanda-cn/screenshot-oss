package cn.jpanda.screenshot.oss.service.handlers.snapshot;

import cn.jpanda.screenshot.oss.core.shotkey.ScreenshotsElementConvertor;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SnapshotRegionKeyEventHandler implements EventHandler<KeyEvent> {
    private ScreenshotsElementConvertor screenshotsElementConvertor;

    public SnapshotRegionKeyEventHandler(ScreenshotsElementConvertor screenshotsElementConvertor) {
        this.screenshotsElementConvertor = screenshotsElementConvertor;
    }

    @Override
    public void handle(KeyEvent event) {
        // 处理快捷键
        KeyCode code = event.getCode();
        System.out.println(code.getName());
        if (code.equals(KeyCode.Z)) {
            if (event.isControlDown()) {
                if (event.isShiftDown()) {
                    screenshotsElementConvertor.activateOne();
                } else {
                    // 撤销一步操作
                    screenshotsElementConvertor.destroyOne();
                }
            }
        }
    }
}
