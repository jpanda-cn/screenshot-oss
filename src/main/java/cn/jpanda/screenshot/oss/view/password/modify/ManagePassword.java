package cn.jpanda.screenshot.oss.view.password.modify;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ManagePassword implements Initializable {

    private Configuration configuration;

    public ManagePassword(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
