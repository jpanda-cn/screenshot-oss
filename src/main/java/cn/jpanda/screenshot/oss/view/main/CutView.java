package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.service.handlers.KeyExitStageEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.SnapshotView;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class CutView implements Initializable {
    public Label screenIndex;
    private Configuration configuration;

    public CutView(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        int index = globalConfigPersistence.getScreenIndex();
        if (index >= configuration.getUniqueBean(ScreenCapture.class).GraphicsDeviceCount()) {
            globalConfigPersistence.setScreenIndex(0);
            configuration.storePersistence(globalConfigPersistence);
        }
        screenIndex.textProperty().setValue(String.valueOf(globalConfigPersistence.getScreenIndex()));
    }

    public void doCut() {
        // 执行截图操作
        // 处理ICON
        Stage stage = new Stage();
        stage.initOwner(configuration.getViewContext().getStage());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(configuration.getViewContext().getScene(SnapshotView.class, true, false));

        // 输入ESC退出截屏
        stage.setFullScreenExitHint("输入ESC退出截屏");
        stage.addEventHandler(KeyEvent.KEY_RELEASED, new KeyExitStageEventHandler(KeyCode.ESCAPE, stage));
        stage.setOnCloseRequest(event -> {
            if (event.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST)) {
                configuration.getViewContext().showScene(CutView.class);
            }
        });
        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        stage.showAndWait();
    }

    public void doKeyCut(KeyEvent event) {
        if (event.getCode().equals(KeyCode.SPACE) || event.getCode().equals(KeyCode.ENTER)) {
            doCut();
        }
    }

    public void toSettings() {
        Stage stage = new Stage();
        stage.initOwner(configuration.getViewContext().getStage());
        stage.setX(Math.max(stage.getOwner().xProperty().getValue(), 0));
        stage.setY(Math.max(stage.getOwner().yProperty().getValue(), 0));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(configuration.getViewContext().getScene(MainView.class, true, false));
        stage.showAndWait();
    }

    public void toChoseScreen() {
        ScreenCapture screenCapture = configuration.getUniqueBean(ScreenCapture.class);
        // 获取当前窗口所属的显示器
        Stage defaultStage = configuration.getViewContext().getStage();
        Stage stage = new Stage();
        stage.initOwner(defaultStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = configuration.getViewContext().getScene(ChoseScreenView.class, true, false);
        stage.resizableProperty().setValue(false);
        stage.setScene(scene);
        stage.showAndWait();
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        screenIndex.textProperty().setValue(String.valueOf(globalConfigPersistence.getScreenIndex()));
    }

    public void toKeySettings(KeyEvent event) {
        if (event.getCode().equals(KeyCode.SPACE) || event.getCode().equals(KeyCode.ENTER)) {
            toSettings();
        }
    }
}
