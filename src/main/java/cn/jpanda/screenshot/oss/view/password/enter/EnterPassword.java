package cn.jpanda.screenshot.oss.view.password.enter;

import cn.jpanda.screenshot.oss.common.utils.AlertUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class EnterPassword implements Initializable {
    private Configuration configuration = BootStrap.configuration;
    @FXML
    private PasswordField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void enterPassword() {
        String pwd = password.textProperty().get();
        if (StringUtils.isEmpty(pwd)) {
            AlertUtils.alert(Alert.AlertType.WARNING, "密码不得为空");
            return;
        }
        configuration.setPassword(pwd);
        ((Stage) password.getScene().getWindow()).close();
    }

    public void noPassword() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText("不输入主控密码，将会导致你的配置数据无法完成解密操作");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(new ButtonType("取消", ButtonBar.ButtonData.BACK_PREVIOUS), new ButtonType("确认", ButtonBar.ButtonData.OK_DONE));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                // 进入主界面
                ((Stage) password.getScene().getWindow()).close();
            }
        }
    }
}
