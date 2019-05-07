package cn.jpanda.screenshot.oss.view;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.annotations.FX;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

@FX
public class MainView implements Initializable {

    private Log log = LogHolder.getInstance().getLogFactory().getLog(getClass());

    private Configuration configuration = BootStrap.configuration;

    private MainViewConfig mainViewConfig;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 加载配置
        mainViewConfig = configuration.getDataPersistenceStrategy().load(MainViewConfig.class);
    }

}
