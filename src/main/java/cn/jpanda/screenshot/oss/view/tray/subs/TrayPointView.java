package cn.jpanda.screenshot.oss.view.tray.subs;

import cn.jpanda.screenshot.oss.newcore.annotations.Controller;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class TrayPointView implements Initializable {
    /**
     * 小点点
     */
    public Button smallPoint;
    /**
     * 中点点
     */
    public Button midpoint;
    /**
     * 大点点
     */
    public Button bigpoint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void small() {
        changeStokeSize(1);
    }

    public void mid() {
        changeStokeSize(2);
    }

    public void big() {
        changeStokeSize(3);
    }

    private void changeStokeSize(double size) {
        CanvasProperties canvasProperties = (CanvasProperties) smallPoint.getScene().getWindow().getProperties().get(CanvasProperties.class);
        canvasProperties.getCurrentConfig().getStroke().set(size);
    }
}
