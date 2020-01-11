package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.AlertUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.img.instances.alioss.AliOssImageStore;
import cn.jpanda.screenshot.oss.store.img.instances.alioss.AliOssPersistence;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + AliOssImageStore.NAME, (Callable<Boolean, ButtonType>) a -> {
            if (a.equals(ButtonType.APPLY)) {
                return save();
            }
            return true;
        });
    }

    public void close() {
        ((Stage) endpoint.getScene().getWindow()).close();
    }

    public boolean save() {
        if (!check()) {
            return false;
        }
        AliOssPersistence aliOssPersistence = configuration.getPersistence(AliOssPersistence.class);
        aliOssPersistence.setEndpoint(endpoint.textProperty().get());
        aliOssPersistence.setBucket(bucket.textProperty().get());
        aliOssPersistence.setAccessKeyId(accessKeyId.textProperty().get());
        aliOssPersistence.setAccessKeySecret(accessKeySecret.textProperty().get());
        aliOssPersistence.setAccessUrl(accessUrl.textProperty().get());
        aliOssPersistence.setAsync(async.isSelected());
        configuration.storePersistence(aliOssPersistence);
//        close();
        return true;
    }

    private boolean check() {
        if (StringUtils.isEmpty(endpoint.textProperty().get())) {
            alert("endpoint为必填项");
            return false;
        }
        if (StringUtils.isEmpty(bucket.textProperty().get())) {
            alert("bucket为必填项");
            return false;
        }
        if (StringUtils.isEmpty(accessKeyId.textProperty().get())) {
            alert("accessKeyId为必填项");
            return false;
        }
        if (StringUtils.isEmpty(accessKeySecret.textProperty().get())) {
            alert("accessKeySecret为必填项");
            return false;
        }

        return true;
    }

    public void alert(String message) {
        PopDialogShower.message(message,endpoint.getScene().getWindow());
    }
}
