package cn.jpanda.screenshot.oss.store.img.instances.oschina;

import cn.jpanda.screenshot.oss.common.toolkit.ImageShower;
import cn.jpanda.screenshot.oss.core.log.LogHolder;
import cn.jpanda.screenshot.oss.core.log.Loglevel;
import cn.jpanda.screenshot.oss.core.log.defaults.DefaultOutLogConfig;
import cn.jpanda.screenshot.oss.core.log.defaults.DefaultOutLogFactory;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class IndependentTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        LogHolder.getInstance().initLogFactory(new DefaultOutLogFactory(new DefaultOutLogConfig(Loglevel.DEBUG)));
        ImageShower.hidenTaskBar().show(new Image("https://note.youdao.com/ynoteshare1/images/dl-logo.png"));
        ImageShower.hidenTaskBar().show(new Image("https://note.youdao.com/ynoteshare1/images/dl-logo.png"));
        ImageShower.hidenTaskBar().show(new Image("https://note.youdao.com/ynoteshare1/images/dl-logo.png"));
    }
}
