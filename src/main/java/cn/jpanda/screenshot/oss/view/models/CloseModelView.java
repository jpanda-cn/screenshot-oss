package cn.jpanda.screenshot.oss.view.models;

import cn.jpanda.screenshot.oss.core.annotations.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 关闭模态框
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2019/12/28 9:44
 */
@Controller
public class CloseModelView implements Initializable {

    public RadioButton min;
    public RadioButton close;
    public Button submit;
    ToggleGroup group = new ToggleGroup();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        min.setUserData(true);
        close.setUserData(false);
        group.getToggles().add(min);
        group.getToggles().add(close);
    }

    @SuppressWarnings("rawtypes")
    public void ok(ActionEvent actionEvent) {
        Tooltip tooltip = ((Tooltip) submit.getScene().getWindow());
        Stage w = (Stage) tooltip.getOwnerWindow();

        // 时间传递给模态框，调用模态框的close方法。
        if (group.getSelectedToggle().getUserData().equals(true)) {
            w.setIconified(true);
            cancel();
        } else {
            Platform.exit();
        }
    }

    public void cancel() {
        Tooltip tooltip = ((Tooltip) submit.getScene().getWindow());
        tooltip.hide();
    }
}
