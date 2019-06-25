package cn.jpanda.screenshot.oss.service.handlers.snapshot;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.shotkey.ScreenshotsElementConvertor;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class SnapshotRegionKeyEventHandler implements EventHandler<KeyEvent> {
    private ScreenshotsElementConvertor screenshotsElementConvertor;
    private Configuration configuration;
    private CanvasProperties canvasProperties;

    public SnapshotRegionKeyEventHandler(ScreenshotsElementConvertor screenshotsElementConvertor, Configuration configuration, CanvasProperties canvasProperties) {
        this.screenshotsElementConvertor = screenshotsElementConvertor;
        this.configuration = configuration;
        this.canvasProperties = canvasProperties;
    }


    @Override
    public void handle(KeyEvent event) {
        // 处理快捷键
        KeyCode code = event.getCode();
        if (code.equals(KeyCode.Z)) {
            if (event.isControlDown() && !event.isShiftDown() && !event.isAltDown() && !event.isMetaDown() && !event.isShortcutDown()) {
                // 撤销一步操作
                screenshotsElementConvertor.destroyOne();
            }
        } else if (code.equals(KeyCode.Y)) {
            if (event.isControlDown() && !event.isShiftDown() && !event.isAltDown() && !event.isMetaDown() && !event.isShortcutDown()) {
                // 恢复一步操作
                screenshotsElementConvertor.activateOne();
            }
        } else if (code.equals(KeyCode.ENTER)) {
            System.out.println(code);
            if (!event.isControlDown() && !event.isShiftDown() && !event.isAltDown() && !event.isMetaDown() && !event.isShortcutDown()) {
                // 完成截图操作
                ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
                // 获取截图区域的图片交由图片处理器来完成保存图片的操作
                Platform.runLater(()->{ // 关闭
                    ((Stage) canvasProperties.getCutPane().getScene().getWindow()).close();});
                screenshotsProcess.done(screenshotsProcess.snapshot(canvasProperties.getCutPane().getScene(), canvasProperties.getCutRectangle()));
            }
        }
    }
}
