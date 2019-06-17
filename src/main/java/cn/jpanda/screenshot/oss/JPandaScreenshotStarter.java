package cn.jpanda.screenshot.oss;

import cn.jpanda.screenshot.oss.newcore.JPandaApplicationRunner;
import cn.jpanda.screenshot.oss.newcore.controller.ViewContext;
import cn.jpanda.screenshot.oss.newcore.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.view.main.CutView;
import cn.jpanda.screenshot.oss.view.password.enter.EnterPassword;
import cn.jpanda.screenshot.oss.view.password.init.ConfigPassword;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JPandaScreenshotStarter extends Application {
    private ViewContext viewContext;

    @Override
    public void start(Stage primaryStage) throws Exception {
        viewContext = new JPandaApplicationRunner().run(primaryStage, getClass());
        // 执行业务逻辑
        // 加载配置全局配置文件
        load();
    }

    private void load() {
        BootstrapPersistence bootstrapPersistence = new BootstrapPersistence();
        bootstrapPersistence.updateUseCount();
        // 校验是否为第一次使用该系统
        if (bootstrapPersistence.getUseCount() == 1) {
            //  展示初始化密码页面
            showInitPassword();
        } else {
            // 校验是否需要展示密码
            showEnterPassword();
        }
        doStart();
    }

    protected void doStart() {
        Stage stage = viewContext.getStage();
        stage.setTitle("一个专属于程序员的截图工具");
        stage.setResizable(false);
        viewContext.getStage().getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("logo.png")));
        viewContext.showScene(CutView.class);
    }

    protected void showInitPassword() {
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