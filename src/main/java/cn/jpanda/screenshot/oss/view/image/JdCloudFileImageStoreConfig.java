package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.utils.AlertUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.img.instances.jd.JdOssPersistence;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 京东云配置项
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2019/12/10 11:35
 */
@Controller
public class JdCloudFileImageStoreConfig implements Initializable {
    public TextField endpoint;
    public TextField bucket;
    public TextField accessKeyId;
    public PasswordField accessKeySecret;
    public CheckBox async;
    public TextField accessUrl;
    private Configuration configuration;

    public JdCloudFileImageStoreConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    private void init() {
        JdOssPersistence JdOssPersistence = configuration.getPersistence(JdOssPersistence.class);

        if (StringUtils.isNotEmpty(JdOssPersistence.getEndpoint())) {
            endpoint.textProperty().setValue(JdOssPersistence.getEndpoint());
        }
        if (StringUtils.isNotEmpty(JdOssPersistence.getBucket())) {
            bucket.textProperty().setValue(JdOssPersistence.getBucket());
        }
        if (StringUtils.isNotEmpty(JdOssPersistence.getAccessKeyId())) {
            accessKeyId.textProperty().setValue(JdOssPersistence.getAccessKeyId());
        }
        if (StringUtils.isNotEmpty(JdOssPersistence.getAccessKeySecret())) {
            accessKeySecret.textProperty().setValue(JdOssPersistence.getAccessKeySecret());
        }
        if (StringUtils.isNotEmpty(JdOssPersistence.getAccessUrl())) {
            accessUrl.textProperty().setValue(JdOssPersistence.getAccessUrl());
        }

        async.selectedProperty().set(JdOssPersistence.isAsync());
    }

    public void close() {
        ((Stage) endpoint.getScene().getWindow()).close();
    }

    public void save() {
        if (!check()) {
            return;
        }
        JdOssPersistence JdOssPersistence = configuration.getPersistence(JdOssPersistence.class);
        JdOssPersistence.setEndpoint(endpoint.textProperty().get());
        JdOssPersistence.setBucket(bucket.textProperty().get());
        JdOssPersistence.setAccessKeyId(accessKeyId.textProperty().get());
        JdOssPersistence.setAccessKeySecret(accessKeySecret.textProperty().get());
        String access=accessUrl.textProperty().get();
        if (access.endsWith("\\")){
            access=access.substring(0,access.length()-1);
        }
        JdOssPersistence.setAccessUrl(access);
        JdOssPersistence.setAsync(async.isSelected());
        configuration.storePersistence(JdOssPersistence);
        close();
    }

    private boolean check() {
        if (StringUtils.isEmpty(endpoint.textProperty().get())) {
            alert("endpoint");
            return false;
        }
        if (StringUtils.isEmpty(bucket.textProperty().get())) {
            alert("bucket");
            return false;
        }
        if (StringUtils.isEmpty(accessKeyId.textProperty().get())) {
            alert("accessKeyId");
            return false;
        }
        if (StringUtils.isEmpty(accessKeySecret.textProperty().get())) {
            alert("accessKeySecret");
            return false;
        }

        return true;
    }

    public void alert(String name) {
        AlertUtils.alert(Alert.AlertType.ERROR, String.format("%s不得为空", name));
    }
}
