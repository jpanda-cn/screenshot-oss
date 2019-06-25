package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.common.toolkit.Bounds;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.mouse.GlobalMousePoint;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
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
            stage.setScene(configuration.getViewContext().getScene(SnapshotView.class, true, false));

            GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
            // 添加屏幕跟随，截哪个屏幕就在哪个屏幕上展示
            ScreenCapture screenCapture = configuration.getUniqueBean(ScreenCapture.class);
            int index;
            if (globalConfigPersistence.isScreenshotMouseFollow()) {
                index = screenCapture.getScreenIndex(configuration.getUniqueBean(GlobalMousePoint.class).pointSimpleObjectProperty.get().getX());

            } else {
                index = globalConfigPersistence.getScreenIndex();
                // 校验索引
                if (index >= screenCapture.screensCount()) {
                    // 校验一下显示器的数量问题
                    globalConfigPersistence.setScreenIndex(0);
                    configuration.storePersistence(globalConfigPersistence);
                }
                index = globalConfigPersistence.getScreenIndex();
            }
            Bounds bounds = screenCapture.getTargetScreen(index);
            stage.setX(bounds.getX());
            stage.setY(bounds.getY());
            // 输入ESC退出截屏
            stage.setFullScreenExitHint("输入ESC退出截屏");

            stage.addEventHandler(KeyEvent.KEY_RELEASED, new KeyExitStageEventHandler(KeyCode.ESCAPE, stage, configuration));
            stage.setOnCloseRequest(event -> {
                if (event.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST)) {
                    configuration.getViewContext().showScene(IndexCutView.class);
                }
            });
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        });

    }

}
