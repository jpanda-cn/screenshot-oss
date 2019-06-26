package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.service.handlers.KeyExitStageEventHandler;
import cn.jpanda.screenshot.oss.view.main.IndexCutView;
import cn.jpanda.screenshot.oss.view.snapshot.SnapshotView;
import javafx.application.Platform;
import javafx.scene.Scene;
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
        if (configuration.getCutting().get()) {
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
        Platform.runLater(() -> {
            // 调用截图操作
            // 执行截图操作
            // 处理ICON
            stage = configuration.getViewContext().newStage();
            configuration.getCutting().bind(stage.showingProperty());
            stage.initOwner(configuration.getViewContext().getStage());
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = configuration.getViewContext().getScene(SnapshotView.class, true, false);
            stage.setScene(scene);

            // 添加屏幕跟随，截哪个屏幕就在哪个屏幕上展示
            ScreenCapture screenCapture = configuration.getUniqueBean(ScreenCapture.class);
            stage.setX(screenCapture.minx());
            stage.setY(screenCapture.miny());

            stage.addEventHandler(KeyEvent.KEY_RELEASED, new KeyExitStageEventHandler(KeyCode.ESCAPE, stage, configuration));
            stage.setOnCloseRequest(event -> {
                if (event.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST)) {
                    configuration.getViewContext().showScene(IndexCutView.class);
                }
            });

            stage.toFront();
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        });

    }

}
