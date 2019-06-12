package cn.jpanda.screenshot.oss.view.tray.subs;

import cn.jpanda.screenshot.oss.core.annotations.View;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.handlers.TrayConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

@View
public class TrayFontView implements Initializable {
    @FXML
    private ChoiceBox<String> fontFamily;
    @FXML
    private ChoiceBox<Integer> size;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initFontFamily();
        initFontSize();
    }

    private void initFontFamily() {
        fontFamily.getItems().addAll(Font.getFamilies());
        fontFamily.getSelectionModel().select(Font.getDefault().getFamily());
        fontFamily.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeFamily(newValue));
    }

    private void initFontSize() {
        List<Integer> l = new ArrayList<>(73 - 10);
        IntStream.range(10, 73).forEach(l::add);
        size.getItems().addAll(l);
        size.getSelectionModel().select((int) Font.getDefault().getSize());
        size.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeSize(newValue));
    }

    @FXML
    private void changeFamily(String f) {
        CanvasProperties canvasProperties = (CanvasProperties) fontFamily.getScene().getWindow().getProperties().get(CanvasProperties.class);
        TrayConfig config = canvasProperties.getCurrentConfig();
        Font font = config.getFont().get();
        config.getFont().set(Font.font(f, font.getSize()));
    }

    @FXML
    private void changeSize(Integer s) {
        CanvasProperties canvasProperties = (CanvasProperties) size.getScene().getWindow().getProperties().get(CanvasProperties.class);
        TrayConfig config = canvasProperties.getCurrentConfig();
        Font font = config.getFont().get();
        config.getFont().set(Font.font(font.getFamily(), s));
    }
}
