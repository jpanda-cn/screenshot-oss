package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.AlertUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.img.instances.git.GitImageStore;
import cn.jpanda.screenshot.oss.store.img.instances.jd.JdOssCloudStore;
import cn.jpanda.screenshot.oss.store.img.instances.jd.JdOssPersistence;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + JdOssCloudStore.NAME, (Callable<Boolean, ButtonType>) a -> {
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
        JdOssPersistence JdOssPersistence = configuration.getPersistence(JdOssPersistence.class);
        JdOssPersistence.setEndpoint(endpoint.textProperty().get());
        JdOssPersistence.setBucket(bucket.textProperty().get());
        JdOssPersistence.setAccessKeyId(accessKeyId.textProperty().get());
        JdOssPersistence.setAccessKeySecret(accessKeySecret.textProperty().get());
        String access = accessUrl.textProperty().get();
        if (access.endsWith("\\")) {
            access = access.substring(0, access.length() - 1);
        }
        JdOssPersistence.setAccessUrl(access);
        JdOssPersistence.setAsync(async.isSelected());
        configuration.storePersistence(JdOssPersistence);
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
