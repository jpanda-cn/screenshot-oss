package cn.jpanda.screenshot.oss.store;

import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/18 11:21
 */

public interface ImageStoreConfigBuilder {

    default boolean   tips(Window stage){return true;}
    Parent config();

}
