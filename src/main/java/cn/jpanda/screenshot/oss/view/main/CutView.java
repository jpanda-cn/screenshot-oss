package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.annotations.View;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.newcore.annotations.Controller;
import cn.jpanda.screenshot.oss.service.handlers.KeyExitStageEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.SnapshotView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
    private Configuration configuration = BootStrap.configuration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
        Stage defaultStage = configuration.getViewContext().getStage();
        Stage stage = new Stage();
        stage.initOwner(defaultStage);
        stage.setY(Math.max(stage.getOwner().yProperty().getValue(), 0));
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = configuration.getViewContext().getScene(ChoseScreenView.class, true, false);
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                stage.setX(defaultStage.xProperty().subtract(scene.widthProperty().subtract(stage.widthProperty()).divide(2)).get());
            }
        });
        stage.setX(defaultStage.xProperty().subtract(scene.widthProperty().divide(2)).get());
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void toKeySettings(KeyEvent event) {
        if (event.getCode().equals(KeyCode.SPACE) || event.getCode().equals(KeyCode.ENTER)) {
            toSettings();
        }
    }
}
