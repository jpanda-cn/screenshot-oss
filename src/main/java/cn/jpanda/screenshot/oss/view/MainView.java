package cn.jpanda.screenshot.oss.view;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.annotations.FX;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

@FX
public class MainView implements Initializable {
    Configuration configuration = BootStrap.configuration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 加载配置
        Persistence mainViewConfig = configuration.getDataPersistenceStrategy().load(MainViewConfig.class);
        ((MainViewConfig)mainViewConfig).setTest(123);
        configuration.getDataPersistenceStrategy().store(mainViewConfig);
    }
}
