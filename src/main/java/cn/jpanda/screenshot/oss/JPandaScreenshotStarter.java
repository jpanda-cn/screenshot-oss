package cn.jpanda.screenshot.oss;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.JPandaApplicationRunner;
import cn.jpanda.screenshot.oss.core.controller.ViewContext;
import cn.jpanda.screenshot.oss.core.i18n.I18nConstants;
import cn.jpanda.screenshot.oss.core.i18n.I18nResource;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.view.main.IndexCutView;
import cn.jpanda.screenshot.oss.view.password.init.ConfigPassword;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JPandaScreenshotStarter extends Application {
    private ViewContext viewContext;
    private Log log;

    @Override
    public void start(Stage primaryStage) {
        viewContext = new JPandaApplicationRunner().run(primaryStage, getClass());
        log = viewContext.getConfiguration().getLogFactory().getLog(getClass());
        // 执行业务逻辑
        // 加载配置全局配置文件
        load();
    }

    private void load() {
        log.debug("load bootstrap config...");
        BootstrapPersistence bootstrapPersistence = viewContext.getConfiguration().getPersistence(BootstrapPersistence.class);
        bootstrapPersistence.updateUseCount();
        viewContext.getConfiguration().storePersistence(bootstrapPersistence);

        // 校验是否为第一次使用该系统
        if (bootstrapPersistence.getUseCount() == 1) {
            log.debug("For the first time, initialize the password");
            //  展示初始化密码页面
            showInitPassword();
        } else if (bootstrapPersistence.isUsePassword()) {
            log.debug("show password");
            // 校验是否需要展示密码
            showEnterPassword();
        }
        viewContext.getConfiguration().setStarted(true);
        doStart();

    }

    protected void doStart() {
        log.debug("load index cut page");
        Stage stage = viewContext.getStage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(viewContext.getConfiguration().getUniqueBean(I18nResource.class).get(I18nConstants.titleIndex));
        stage.setResizable(false);
        viewContext.getStage().getIcons().add(new Image("/images/icon-red.png"));
        viewContext.showScene(IndexCutView.class);
    }

    protected void showInitPassword() {
        Scene scene = viewContext.getScene(ConfigPassword.class);

        HBox header = new HBox();
        Label main = new Label("初始化密码");
        main.setStyle(" -fx-underline: true;-fx-font-weight: bold;");
        header.getChildren().addAll(main);
        Callable<Boolean, ButtonType> callable = viewContext.getConfiguration().getUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + ConfigPassword.class.getCanonicalName());
        ButtonType skip = new ButtonType("跳过", ButtonBar.ButtonData.CANCEL_CLOSE);
        Stage stage = viewContext.newStage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(new AnchorPane(PopDialog.create()
                .setHeader(header)
                .setContent(scene.getRoot())
                .callback(callable)
                .addButtonClass(skip, "button-light")
                .buttonTypes(skip, ButtonType.APPLY)
                .getDialogPane())));
        stage.showAndWait();
    }

    protected void showEnterPassword() {
        ButtonType skip = new ButtonType("跳过", ButtonBar.ButtonData.CANCEL_CLOSE);

        HBox body = new HBox();
        body.setSpacing(10);
        Label pwdLabel = new Label("输入密码");
        PasswordField pwdField = new PasswordField();

        body.getChildren().addAll(pwdLabel, pwdField);

        HBox header = new HBox();
        Label main = new Label("输入密码");
        main.setStyle(" -fx-underline: true;-fx-font-weight: bold;");
        header.getChildren().addAll(main);
        Stage stage = viewContext.newStage();
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setScene(new Scene(
                        new AnchorPane(
                                PopDialog.create()
                                        .setHeader(header)
                                        .setContent(body)
                                        .callback(buttonType -> {
                                            if (skip.equals(buttonType)) {
                                                // 跳过密码
                                                /* 是否不输入密码 */
                                                ButtonType back = new ButtonType("再想想");
                                                ButtonType doIt = new ButtonType("我确定");
                                                if (doIt.equals(PopDialog.create().setHeader("警告").setContent("不输入主控密码，将会导致你的配置数据无法完成解密操作，从而导致部分功能无法使用")
                                                        .addButtonClass(back, "button-cancel")
                                                        .buttonTypes(back, doIt)
                                                        .showAndWait().orElse(back))) {
                                                    ((Stage) (pwdField.getScene().getWindow())).close();
                                                    return true;
                                                }
                                                return false;

                                            }

                                            /* 已输入密码 */
                                            String pwd = pwdField.textProperty().get();
                                            if (StringUtils.isEmpty(pwd)) {
                                                PopDialogShower.message("密码不得为空",pwdField.getScene().getWindow());
                                                return false;
                                            }
                                            viewContext.getConfiguration().setPassword(pwd);
                                            ((Stage) (pwdField.getScene().getWindow())).close();
                                            return true;
                                        })
                                        .addButtonClass(skip, "button-light")
                                        .buttonTypes(skip, ButtonType.APPLY)
                                        .getDialogPane()
                        )
                )
        );

        stage.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
