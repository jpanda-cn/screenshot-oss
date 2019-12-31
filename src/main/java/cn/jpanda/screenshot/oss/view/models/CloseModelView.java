package cn.jpanda.screenshot.oss.view.models;

import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.shape.ModelDialog;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

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
        min.setUserData("min");
        close.setUserData("close");
        group.getToggles().add(min);
        group.getToggles().add(close);
    }

    @SuppressWarnings("rawtypes")
    public void ok(ActionEvent actionEvent) {
        Parent root = submit.getParent().getParent();
        ModelDialog modelDialog = (ModelDialog) root.getProperties().get(ModelDialog.class);
        //noinspection unchecked
        modelDialog.resultProperty().setValue(group.getSelectedToggle().getUserData());
        modelDialog.close();
        // 时间传递给模态框，调用模态框的close方法。

    }
}
