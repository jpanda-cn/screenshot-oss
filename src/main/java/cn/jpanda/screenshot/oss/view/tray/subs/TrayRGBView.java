package cn.jpanda.screenshot.oss.view.tray.subs;

import cn.jpanda.screenshot.oss.core.annotations.Controller;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2019/12/30 16:15
 */
@Controller
public class TrayRGBView implements Initializable {
    /**
     * 位置
     */
    public Text pos;
    /**
     * RGBA
     */
    public Text rgba;
    /**
     * 十六进制颜色
     */
    public Text hex;

    /**
     * 图片
     */
    public ImageView image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
