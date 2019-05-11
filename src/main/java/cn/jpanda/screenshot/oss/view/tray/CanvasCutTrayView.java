package cn.jpanda.screenshot.oss.view.tray;

import cn.jpanda.screenshot.oss.core.annotations.FX;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 截图托盘
 */
@FX
public class CanvasCutTrayView implements Initializable {
    @FXML
    public Button roundness;
    @FXML
    public Button rectangle;
    @FXML
    public Button arrow;
    @FXML
    public Button pen;
    @FXML
    public Button text;
    @FXML
    public Button cancel;
    @FXML
    public Button submit;
    @FXML
    public AnchorPane bar;

    private volatile CanvasProperties canvasProperties;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void initRectangle() {
        canvasProperties = (CanvasProperties) submit.getScene().getWindow().getProperties().get(CanvasProperties.class);
    }

    public void doRoundness(MouseEvent event) {
        Rectangle rectangle = canvasProperties.getCutRectangle();
        rectangle.setCursor(Cursor.CROSSHAIR);
        // 尝试初始化
        initRectangle();
            Canvas canvas = new Canvas(rectangle.getWidth(), rectangle.getHeight());
            canvas.layoutXProperty().bindBidirectional(rectangle.xProperty());
            canvas.layoutYProperty().bindBidirectional(rectangle.yProperty());
            canvas.widthProperty().bindBidirectional(rectangle.widthProperty());
            canvas.heightProperty().bindBidirectional(rectangle.heightProperty());
            rectangle.toBack();
            canvas.toFront();
            canvas.setCursor(Cursor.CROSSHAIR);
            canvas.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                private double startX;
                private double startY;
                private double lastX;
                private double lastY;

                @Override
                public void handle(MouseEvent event) {
                    if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                        startX = event.getScreenX();
                        startY = event.getScreenY();
                        lastX = startX;
                        lastY = startY;
                    } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                        GraphicsContext
                                g = canvas.getGraphicsContext2D();
                        g.clearRect(startX, startY, lastX, lastY);
                        g.setFill(Color.RED);
                        g.fillRect(startX, startY, event.getScreenX() - startY, event.getScreenY() - startY);
                    }
                }
            });
        }
    }
