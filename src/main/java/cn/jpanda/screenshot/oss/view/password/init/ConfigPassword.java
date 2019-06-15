package cn.jpanda.screenshot.oss.view.password.init;

import cn.jpanda.screenshot.oss.common.utils.AlertUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 配置密码页面
 */
public class ConfigPassword implements Initializable {

    private Configuration configuration = BootStrap.configuration;

    @FXML
    private PasswordField passwordField;
    @FXML
    public PasswordField checkPasswordField;
    @FXML
    public Button cancel;

    @FXML
    public Button submit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.textProperty().setValue("跳过");
    }

    public void savePassword() {
        String password = passwordField.textProperty().get();
        if (StringUtils.isEmpty(password)) {
            AlertUtils.alert(Alert.AlertType.ERROR, "密码不得为空");
            return;
        }
        String checkPassword = checkPasswordField.textProperty().get();
        if (StringUtils.isEmpty(checkPassword)) {
            AlertUtils.alert(Alert.AlertType.ERROR, "密码不得为空");
            return;
        }
        if (!password.equals(checkPassword)) {
            AlertUtils.alert(Alert.AlertType.ERROR, "两次密码不一致");
            return;
        }
        configuration.setUsePassword(true);
        configuration.setPassword(password);
        closeConfigPasswordAndReturnMainView();
    }

    public void closeConfigPasswordAndReturnMainView() {
        ((Stage) cancel.getScene().getWindow()).close();
    }

    /**
     * 取消配置密码
     */
    public void closeConfigPassword() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText("不设置主控密码，将会导致你的数据直接暴露在电脑中，请谨慎操作");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(new ButtonType("取消", ButtonBar.ButtonData.BACK_PREVIOUS), new ButtonType("确认", ButtonBar.ButtonData.OK_DONE));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                configuration.setUsePassword(false);
                closeConfigPasswordAndReturnMainView();
            }
        }
    }
}
