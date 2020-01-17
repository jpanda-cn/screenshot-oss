package cn.jpanda.screenshot.oss.view.image;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.img.instances.oschina.OSChainPersistence;
import cn.jpanda.screenshot.oss.store.img.instances.oschina.OSChinaImageStore;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/17 10:41
 */
@Controller
public class OSChinaFileImageStoreConfig implements Initializable {

    public TextField blogId;

    private Configuration configuration;

    private OSChainPersistence persistence;

    public OSChinaFileImageStoreConfig(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        persistence = configuration.getPersistence(OSChainPersistence.class);

        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + OSChinaImageStore.NAME, (Callable<Boolean, ButtonType>) a -> {
            if (a.equals(ButtonType.APPLY)) {
                return save();
            }
            return true;
        });
    }

    private Boolean save() {
        String id = blogId.getText();
        if (StringUtils.isEmpty(id)) {
            PopDialogShower.message("请输入文章ID", blogId.getScene().getWindow());
            return false;
        }
        persistence.setBlogId(id);
        configuration.storePersistence(persistence);
        return true;
    }
}
