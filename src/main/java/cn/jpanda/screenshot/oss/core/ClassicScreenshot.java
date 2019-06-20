package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.mouse.GlobalMousePoint;
import cn.jpanda.screenshot.oss.service.handlers.KeyExitStageEventHandler;
import cn.jpanda.screenshot.oss.view.main.IndexCutView;
import cn.jpanda.screenshot.oss.view.snapshot.SnapshotView;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ClassicScreenshot implements Snapshot {
    private Log log;
    private Configuration configuration;
    private Stage stage;

    public ClassicScreenshot(Configuration configuration) {
        this.configuration = configuration;
        this.log = configuration.getLogFactory().getLog(getClass());
    }

    @Override
    public synchronized void cut() {
        if (configuration.isCutting()) {
            Platform.runLater(() -> {
                stage.toFront();
            });
            log.debug("is cutting ...");
            return;
        }
        // 判断是否已经初始化完成，决定是否可以启用
        if (!configuration.isStarted()) {
            log.debug("The application has not started yet.");
            return;
        }
        configuration.setCutting(true);
        Platform.runLater(() -> {
            // 调用截图操作
            // 执行截图操作
            // 处理ICON
            stage = new Stage();
            stage.initOwner(configuration.getViewContext().getStage());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(configuration.getViewContext().getScene(SnapshotView.class, true, false));

            // 添加屏幕跟随，截哪个屏幕就在哪个屏幕上展示
            ScreenCapture screenCapture = configuration.getUniqueBean(ScreenCapture.class);
            int index = screenCapture.getGraphicsDeviceIndex(configuration.getUniqueBean(GlobalMousePoint.class).pointSimpleObjectProperty.get().getX());
            stage.setX(screenCapture.getTargetGraphicsDeviceX(index));

            // 输入ESC退出截屏
            stage.setFullScreenExitHint("输入ESC退出截屏");
            stage.addEventHandler(KeyEvent.KEY_RELEASED, new KeyExitStageEventHandler(KeyCode.ESCAPE, stage, configuration));
            stage.setOnCloseRequest(event -> {
                if (event.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST)) {
                    configuration.setCutting(false);
                    configuration.getViewContext().showScene(IndexCutView.class);
                }
            });
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        });
    }

}
