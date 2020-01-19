package cn.jpanda.screenshot.oss.store;

import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/18 11:49
 */
public class NoImageStoreConfigBuilder implements ImageStoreConfigBuilder {

    @Override
    public Parent config() {
        return new Pane();
    }
}
