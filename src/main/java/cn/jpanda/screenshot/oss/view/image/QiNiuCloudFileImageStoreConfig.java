package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.img.instances.qiniu.QiNiuOssCloudStore;
import cn.jpanda.screenshot.oss.store.img.instances.qiniu.QiNiuOssPersistence;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 七牛云配置项
 *
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2019/12/10 11:35
 */
@Controller
public class QiNiuCloudFileImageStoreConfig implements Initializable {
    public TextField endpoint;
    public TextField bucket;
    public TextField accessKeyId;
    public PasswordField accessKeySecret;
    public CheckBox async;
    public TextField accessUrl;
    private Configuration configuration;

    public QiNiuCloudFileImageStoreConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    private void init() {
        QiNiuOssPersistence qiNiuOssPersistence = configuration.getPersistence(QiNiuOssPersistence.class);

        if (StringUtils.isNotEmpty(qiNiuOssPersistence.getEndpoint())) {
            endpoint.textProperty().setValue(qiNiuOssPersistence.getEndpoint());
        }
        if (StringUtils.isNotEmpty(qiNiuOssPersistence.getBucket())) {
            bucket.textProperty().setValue(qiNiuOssPersistence.getBucket());
        }
        if (StringUtils.isNotEmpty(qiNiuOssPersistence.getAccessKeyId())) {
            accessKeyId.textProperty().setValue(qiNiuOssPersistence.getAccessKeyId());
        }
        if (StringUtils.isNotEmpty(qiNiuOssPersistence.getAccessKeySecret())) {
            accessKeySecret.textProperty().setValue(qiNiuOssPersistence.getAccessKeySecret());
        }
        if (StringUtils.isNotEmpty(qiNiuOssPersistence.getAccessUrl())) {
            accessUrl.textProperty().setValue(qiNiuOssPersistence.getAccessUrl());
        }

        async.selectedProperty().set(qiNiuOssPersistence.isAsync());

        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + QiNiuOssCloudStore.NAME, (Callable<Boolean, ButtonType>) a -> {
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
        QiNiuOssPersistence qiNiuOssPersistence = configuration.getPersistence(QiNiuOssPersistence.class);
        qiNiuOssPersistence.setEndpoint(endpoint.textProperty().get());
        qiNiuOssPersistence.setBucket(bucket.textProperty().get());
        qiNiuOssPersistence.setAccessKeyId(accessKeyId.textProperty().get());
        qiNiuOssPersistence.setAccessKeySecret(accessKeySecret.textProperty().get());
        String access = accessUrl.textProperty().get();
        if (access.endsWith("\\")) {
            access = access.substring(0, access.length() - 1);
        }
        qiNiuOssPersistence.setAccessUrl(access);
        qiNiuOssPersistence.setAsync(async.isSelected());
        configuration.storePersistence(qiNiuOssPersistence);
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
        PopDialogShower.message(message, endpoint.getScene().getWindow());
    }
}
