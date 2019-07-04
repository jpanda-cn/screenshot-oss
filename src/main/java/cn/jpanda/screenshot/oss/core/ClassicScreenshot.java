package cn.jpanda.screenshot.oss.core;

import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.service.handlers.KeyExitStageEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.snapshot.SnapshotView;
import cn.jpanda.screenshot.oss.view.snapshot.WaitRemoveElementsHolder;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 经典样式的截图实现
 */
public class ClassicScreenshot implements Snapshot {
    private Log log;
    private Configuration configuration;

    public ClassicScreenshot(Configuration configuration) {
        this.configuration = configuration;
        this.log = configuration.getLogFactory().getLog(getClass());
    }

    @Override
    public synchronized void cut() {
        if (configuration.getCutting().get() != null) {
            Platform.runLater(() -> {
                configuration.getCutting().get().toFront();
            });
            log.info("is cutting ...");
            return;
        }
        // 判断是否已经初始化完成，决定是否可以启用
        if (!configuration.isStarted()) {
            log.info("The application has not started yet.");
            return;
        }
        Platform.runLater(() -> {
            beforeCut();
            // 调用截图操作
            // 执行截图操作
            // 处理ICON
            Stage stage = configuration.getViewContext().newStage();
            afterNewStage(stage);
            configuration.getCutting().set(stage);
            stage.initOwner(configuration.getViewContext().getStage());
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = configuration.getViewContext().getScene(SnapshotView.class, true, false);
            stage.setScene(scene);

            // 添加屏幕跟随，截哪个屏幕就在哪个屏幕上展示
            ScreenCapture screenCapture = configuration.getUniqueBean(ScreenCapture.class);
            stage.setX(screenCapture.minx());
            stage.setY(screenCapture.miny());
            EventHandler<KeyEvent> keyEventEventHandler = new KeyExitStageEventHandler(KeyCode.ESCAPE, stage);
            stage.addEventHandler(KeyEvent.KEY_RELEASED, keyEventEventHandler);
            stage.toFront();
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
            stage.removeEventHandler(KeyEvent.KEY_RELEASED, keyEventEventHandler);
            stage.close();
            afterCut(stage);
            configuration.getCutting().set(null);
        });

    }

    protected void beforeCut() {
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        if (globalConfigPersistence.isHideIndexScreen()) {
            Stage stage = configuration.getViewContext().getStage();
            stage.opacityProperty().set(0);
        }
    }

    protected void afterNewStage(Stage stage) {
        stage.getProperties().putIfAbsent(WaitRemoveElementsHolder.class, new WaitRemoveElementsHolder());
    }

    protected void afterCut(Stage stage) {
        // 移除需要移除的数据
        // 注意顺序
        CanvasProperties canvasProperties = ((CanvasProperties) stage.getProperties().get(CanvasProperties.class));

        if (canvasProperties != null) {
            canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).destroy();
            canvasProperties.getScreenshotsElementConvertor().clear();
        }

        ((WaitRemoveElementsHolder) (stage.getProperties().get(WaitRemoveElementsHolder.class))).clear();
        stage.getProperties().clear();
        Stage defaultStage = configuration.getViewContext().getStage();
        if (configuration.getPersistence(GlobalConfigPersistence.class).isHideIndexScreen()) {
            defaultStage.opacityProperty().set(1);
        }

    }
}
