package cn.jpanda.screenshot.oss.view.password.modify;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import cn.jpanda.screenshot.oss.core.persistence.PersistenceBeanCatalogManagement;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * 修改密码
 * <p>
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2019/6/21 13:45
 */
@Controller
public class ModifyPassword implements Initializable {
    private Configuration configuration;

    public ModifyPassword(Configuration configuration) {
        this.configuration = configuration;
    }

    @FXML
    private PasswordField pwd;
    @FXML
    public PasswordField cpwd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + ModifyPassword.class.getCanonicalName(), (Callable<Boolean, ButtonType>) a -> {
            System.out.println(a);
            if (a.equals(ButtonType.APPLY)) {
                return done();
            }
            return true;
        });
    }

    /**
     * 修改密码完成
     * 先加载出所有的配置
     * 设置密码属性
     * 重新存储
     */
    public boolean done() {
        // 校验两次密码是否一致
        String password = pwd.textProperty().get();
        if (StringUtils.isEmpty(password)) {
            PopDialogShower.message("密码不得为空", pwd.getScene().getWindow());

            return false;
        }
        String checkPassword = cpwd.textProperty().get();
        if (StringUtils.isEmpty(checkPassword)) {
            PopDialogShower.message("密码不得为空", pwd.getScene().getWindow());
            return false;
        }
        if (!password.equals(checkPassword)) {
            PopDialogShower.message("两次密码不一致", pwd.getScene().getWindow());
            return false;
        }
        // 加载所有配置
        PersistenceBeanCatalogManagement persistenceBeanCatalogManagement = configuration.getUniqueBean(PersistenceBeanCatalogManagement.class);
        List<Persistence> ps =
                persistenceBeanCatalogManagement.list().stream().filter((p) -> !BootstrapPersistence.class.isAssignableFrom(p)).map((p) -> configuration.getPersistence(p)).collect(Collectors.toList());

        // 修改密码配置
        configuration.setPassword(password);
        BootstrapPersistence bootstrapPersistence = configuration.getPersistence(BootstrapPersistence.class);
        bootstrapPersistence.setUsePassword(true);
        configuration.storePersistence(bootstrapPersistence);
        // 重新存储
        ps.forEach((p) -> {
            configuration.storePersistence(p);
        });
        // 完成
        return true;
    }

}
