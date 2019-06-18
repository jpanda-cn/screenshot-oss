package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.img.instances.git.GitPersistence;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

@Controller
public class GitFileImageStoreConfig implements Initializable {
    public static final String DEFAULT_REPOSITORY_DIRECTOR_NAME = "screenshot";
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
        boolean update = false;
        GitPersistence gitPersistence = configuration.getPersistence(GitPersistence.class);
        if (StringUtils.isEmpty(gitPersistence.getLocalRepositoryDir())) {
            gitPersistence.setLocalRepositoryDir(Paths.get(configuration.getWorkPath(), DEFAULT_REPOSITORY_DIRECTOR_NAME).toFile().getAbsolutePath());
            update = true;
        }
        if (StringUtils.isEmpty(gitPersistence.getSubDir()))

        localRepositoryDir.editableProperty().setValue(false);
        localRepositoryDir.textProperty().setValue(gitPersistence.getLocalRepositoryDir());
    }
}
