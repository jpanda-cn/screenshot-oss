package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.utils.AlertUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.img.instances.alioss.AliOssPersistence;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class AliOssFileImageStoreConfig implements Initializable {
    public TextField endpoint;
    public TextField bucket;
    public TextField accessKeyId;
    public PasswordField accessKeySecret;
    public CheckBox async;
    public TextField accessUrl;
    private Configuration configuration;

    public AliOssFileImageStoreConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    private void init() {
        AliOssPersistence aliOssPersistence = configuration.getPersistence(AliOssPersistence.class);

        if (StringUtils.isNotEmpty(aliOssPersistence.getEndpoint())) {
            endpoint.textProperty().setValue(aliOssPersistence.getEndpoint());
        }
        if (StringUtils.isNotEmpty(aliOssPersistence.getBucket())) {
            bucket.textProperty().setValue(aliOssPersistence.getBucket());
        }
        if (StringUtils.isNotEmpty(aliOssPersistence.getAccessKeyId())) {
            accessKeyId.textProperty().setValue(aliOssPersistence.getAccessKeyId());
        }
        if (StringUtils.isNotEmpty(aliOssPersistence.getAccessKeySecret())) {
            accessKeySecret.textProperty().setValue(aliOssPersistence.getAccessKeySecret());
        }
        if (StringUtils.isNotEmpty(aliOssPersistence.getAccessUrl())) {
            accessUrl.textProperty().setValue(aliOssPersistence.getAccessUrl());
        }

        async.selectedProperty().set(aliOssPersistence.isAsync());
    }

    public void close() {
        endpoint.getScene().getWindow().hide();
    }

    public void save() {
        if (!check()) {
            return;
        }
        AliOssPersistence aliOssPersistence = configuration.getPersistence(AliOssPersistence.class);
        aliOssPersistence.setEndpoint(endpoint.textProperty().get());
        aliOssPersistence.setBucket(bucket.textProperty().get());
        aliOssPersistence.setAccessKeyId(accessKeyId.textProperty().get());
        aliOssPersistence.setAccessKeySecret(accessKeySecret.textProperty().get());
        aliOssPersistence.setAccessUrl(accessUrl.textProperty().get());
        aliOssPersistence.setAsync(async.isSelected());
        configuration.storePersistence(aliOssPersistence);
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
