package cn.jpanda.screenshot.oss.view.password.init;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 配置密码页面
 */
@Controller
public class ConfigPassword implements Initializable {

    private Configuration configuration;

    private BootstrapPersistence bootstrapPersistence;

    public ConfigPassword(Configuration configuration) {
        this.configuration = configuration;
    }

    @FXML
    private PasswordField passwordField;
    @FXML
    public PasswordField checkPasswordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bootstrapPersistence = configuration.getPersistence(BootstrapPersistence.class);
        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + ConfigPassword.class.getCanonicalName(), (Callable<Boolean, ButtonType>) a -> {
            if (a.equals(ButtonType.APPLY)) {
                return savePassword();
            } else {
                return closeConfigPassword();
            }
        });
    }

    public boolean savePassword() {
        String password = passwordField.textProperty().get();
        if (StringUtils.isEmpty(password)) {
            PopDialogShower.message("密码不得为空",passwordField.getScene().getWindow());
            return false;
        }
        String checkPassword = checkPasswordField.textProperty().get();
        if (StringUtils.isEmpty(checkPassword)) {
            PopDialogShower.message("确认密码不得为空",passwordField.getScene().getWindow());
            return false;
        }
        if (!password.equals(checkPassword)) {
            PopDialogShower.message("两次密码不一致",passwordField.getScene().getWindow());
            return false;
        }
        bootstrapPersistence.setUsePassword(true);
        configuration.storePersistence(bootstrapPersistence);
        configuration.setPassword(password);
        return true;
    }


    /**
     * 取消配置密码
     */
    public boolean closeConfigPassword() {
        ButtonType back = new ButtonType("再想想");
        ButtonType doIt = new ButtonType("不设置");

        PopDialog.create().setHeader("警告").setContent("不设置主控密码，将会导致你的数据直接暴露在电脑中，请谨慎操作")
                .addButtonClass(back, "button-cancel")
                .buttonTypes(back, doIt)
                .callback(b -> {
                    if (b.equals(doIt)) {
                        bootstrapPersistence.setUsePassword(false);
                        configuration.storePersistence(bootstrapPersistence);
                        ((Stage) checkPasswordField.getScene().getWindow()).close();
                    }
                    return true;
                })
                .bindParent(checkPasswordField.getScene().getWindow())
                .showAndWait();

        return false;
    }
}
