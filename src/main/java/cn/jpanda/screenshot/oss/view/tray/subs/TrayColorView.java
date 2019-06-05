package cn.jpanda.screenshot.oss.view.tray.subs;

import cn.jpanda.screenshot.oss.core.annotations.View;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

@View
public class TrayColorView implements Initializable {
    public Button red;
    public Button yellow;
    public Button blue;
    public Button green;
    public Button black;
    public Button white;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void red(){
        changeColor(Color.RED);
    }
    public void yellow(){
        changeColor(Color.YELLOW);
    }
    public void blue(){
        changeColor(Color.BLUE);
    }
    public void green(){
        changeColor(Color.GREEN);
    }
    public void black(){
        changeColor(Color.BLACK);
    }
    public void white(){
        changeColor(Color.WHITE);
    }

    private void changeColor(Color color) {
        CanvasProperties canvasProperties = (CanvasProperties) red.getScene().getWindow().getProperties().get(CanvasProperties.class);
        canvasProperties.getCurrentConfig().setStrokeColor(color);
    }
}
