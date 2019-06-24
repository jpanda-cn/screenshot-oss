package cn.jpanda.screenshot.oss;

import cn.jpanda.screenshot.oss.core.JPandaApplicationRunner;
import cn.jpanda.screenshot.oss.core.controller.ViewContext;
import cn.jpanda.screenshot.oss.core.i18n.I18nConstants;
import cn.jpanda.screenshot.oss.core.i18n.I18nResource;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.view.main.IndexCutView;
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
    public void start(Stage primaryStage) {
        viewContext = new JPandaApplicationRunner().run(primaryStage, getClass());
        // 执行业务逻辑
        // 加载配置全局配置文件
        load();
    }

    private void load() {
        BootstrapPersistence bootstrapPersistence = viewContext.getConfiguration().getPersistence(BootstrapPersistence.class);
        bootstrapPersistence.updateUseCount();
        viewContext.getConfiguration().storePersistence(bootstrapPersistence);
        // 校验是否为第一次使用该系统
        if (bootstrapPersistence.getUseCount() == 1) {
            //  展示初始化密码页面
            showInitPassword();
        } else if (bootstrapPersistence.isUsePassword()) {
            // 校验是否需要展示密码
            showEnterPassword();
        }
        viewContext.getConfiguration().setStarted(true);
        doStart();

    }

    protected void doStart() {
        Stage stage = viewContext.getStage();
        stage.setTitle(viewContext.getConfiguration().getUniqueBean(I18nResource.class).get(I18nConstants.titleIndex));
        stage.setResizable(false);
        viewContext.getStage().getIcons().add(new Image("/logo.png"));
        viewContext.showScene(IndexCutView.class);
    }

    protected void showInitPassword() {
        // 将密码页面放置到舞台中央
        Stage stage = viewContext.newStage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = viewContext.getScene(ConfigPassword.class);
        stage.initStyle(StageStyle.UNDECORATED);
        AnchorPane password = (AnchorPane) scene.getRoot();
        stage.setScene(scene);
        stage.toFront();
        stage.setTitle("配置主控密码");
        password.toFront();
        stage.toFront();
        stage.showAndWait();
    }

    protected void showEnterPassword() {
        // 将密码页面放置到舞台中央
        Stage stage = viewContext.newStage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = viewContext.getScene(EnterPassword.class);
        AnchorPane password = (AnchorPane) scene.getRoot();
        stage.setScene(scene);
        stage.toFront();
        stage.setTitle("输入密码");
        password.toFront();
        stage.toFront();
        stage.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
