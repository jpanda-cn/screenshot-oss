package cn.jpanda.screenshot.oss;


import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.context.ViewContext;
import cn.jpanda.screenshot.oss.view.main.MainView;
import cn.jpanda.screenshot.oss.view.password.enter.EnterPassword;
import cn.jpanda.screenshot.oss.view.password.init.ConfigPassword;
import cn.jpanda.screenshot.oss.view.snapshot.KeyExitStageEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.SnapshotView;
import cn.jpanda.screenshot.oss.view.tray.CanvasCutTrayView;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.awt.*;

public class JpandaBootstrap extends BootStrap {
    @Override
    protected boolean doBootStrap() {
        configuration.updateUserCount();
        if (configuration.getUserCount() < 2) {
            // 执行初始化密码加载
            showInitPassword();
        }
        if (configuration.usePassword()) {
            // 展示输入密码页面
            showEnterPassword();
        }
        return true;
    }

    @Override
    protected void doStart() {
        // 处理ICON
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(configuration.getViewContext().getScene(SnapshotView.class, true, false));

        // 输入ESC退出截屏
        stage.setFullScreenExitHint("输入ESC退出截屏");
        stage.addEventHandler(KeyEvent.KEY_RELEASED, new KeyExitStageEventHandler(KeyCode.ESCAPE, stage));
        stage.setOnCloseRequest(event -> {
            if (event.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST)) {
                configuration.getViewContext().showScene(MainView.class);
            }
        });
        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        stage.showAndWait();
        configuration.getViewContext().getStage().getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("logo.png")));
        configuration.getViewContext().showScene(MainView.class);

    }

    protected void showInitPassword() {
        ViewContext viewContext = configuration.getViewContext();
        // 将密码页面放置到舞台中央
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = viewContext.getScene(ConfigPassword.class);
        AnchorPane password = (AnchorPane) scene.getRoot();
        stage.setScene(scene);
        stage.toFront();
        stage.setTitle("配置主控密码");
        password.toFront();
        stage.showAndWait();
    }

    protected void showEnterPassword() {
        ViewContext viewContext = configuration.getViewContext();
        // 将密码页面放置到舞台中央
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = viewContext.getScene(EnterPassword.class);
        AnchorPane password = (AnchorPane) scene.getRoot();
        stage.setScene(scene);
        stage.toFront();
        stage.setTitle("输入密码");
        password.toFront();
        stage.showAndWait();
    }

}
