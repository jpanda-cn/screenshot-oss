package cn.jpanda.screenshot.oss;


import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.newcore.controller.ViewContext;
import cn.jpanda.screenshot.oss.view.main.CutView;
import cn.jpanda.screenshot.oss.view.password.enter.EnterPassword;
import cn.jpanda.screenshot.oss.view.password.init.ConfigPassword;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        Stage stage = configuration.getViewContext().getStage();
        stage.setTitle("一个专属于程序员的截图工具");
        stage.setResizable(false);
        configuration.getViewContext().getStage().getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("logo.png")));
        configuration.getViewContext().showScene(CutView.class);

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
