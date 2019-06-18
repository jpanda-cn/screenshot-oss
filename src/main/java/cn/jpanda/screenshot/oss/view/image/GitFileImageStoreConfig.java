package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class GitFileImageStoreConfig implements Initializable {

    public TextField localRepositoryDir;
    public TextField subDir;
    public TextField remoteRepositoryUrl;
    public TextField branch;
    public TextField username;
    public CheckBox async;
    public PasswordField password;
    private Configuration configuration;

    public GitFileImageStoreConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
