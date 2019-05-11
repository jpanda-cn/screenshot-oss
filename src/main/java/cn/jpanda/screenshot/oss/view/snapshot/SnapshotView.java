package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.annotations.FX;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 截图窗口
 */
@FX
public class SnapshotView implements Initializable {
    private Configuration configuration = BootStrap.configuration;
    private SnapshotProperties snapshotProperties;
    private ScreenCapture screenCapture;

    @FXML
    private ImageView imageView;

    private BufferedImage getDesktopSnapshot() {
        GraphicsDevice graphicsDevice = screenCapture.getTargetGraphicsDevice(snapshotProperties.getScreenIndex());
        Dimension dimension = graphicsDevice.getDefaultConfiguration().getBounds().getSize();
        return screenCapture.screenshotImage(snapshotProperties.getScreenIndex(), (int) dimension.getWidth(), (int) dimension.getHeight());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenCapture = configuration.getScreenCapture();
        snapshotProperties = configuration.getPersistence(SnapshotProperties.class);

        BufferedImage image = getDesktopSnapshot();
        WritableImage writableImage = new WritableImage(image.getWidth(), image.getHeight());
        SwingFXUtils.toFXImage(image, writableImage);
        imageView.setImage(writableImage);
        Dimension dimension = configuration.getScreenCapture().getTargetGraphicsDevice(0).getDefaultConfiguration().getBounds().getSize();
        javafx.scene.canvas.Canvas canvas = new Canvas(dimension.getWidth(), dimension.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setStroke(Color.rgb(50, 161, 255));
        ((AnchorPane) imageView.getParent()).getChildren().add(canvas);
        // 全体黑化
        graphicsContext.setFill(Color.rgb(0, 0, 0, 0.3));
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.addEventHandler(MouseEvent.ANY, new CanvasDrawEventHandler(Color.rgb(0, 0, 0, 0.3), graphicsContext));
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount()==2){
                System.out.println("save");
            }
        });
    }
    /**
     * new EventHandler<MouseEvent>() {
     *             private double startX;
     *             private double startY;
     *             private double oldEndX;
     *             private double oldEndY;
     *
     *             @Override
     *             public void handle(MouseEvent event) {
     *                 if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
     *                     startX = event.getScreenX();
     *                     startY = event.getScreenY();
     *                     oldEndX = startX;
     *                     oldEndY = startY;
     *                     graphicsContext.setFill(Color.rgb(0, 0, 0, 0.3));
     *                     graphicsContext.clearRect(0, 0, canvas.getWidth(),canvas.getHeight());
     *                     graphicsContext.fillRect(0, 0, canvas.getWidth(),canvas.getHeight());
     *                 } else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
     *
     *                     graphicsContext.clearRect(startX, startY, oldEndX+ NGCanvas.STROKE_RECT - startX, oldEndY+ NGCanvas.STROKE_RECT - startY);
     *                     graphicsContext.setFill(Color.rgb(0, 0, 0, 0.3));
     *                     graphicsContext.fillRect(startX, startY, oldEndX+ NGCanvas.STROKE_RECT - startX, oldEndY+ NGCanvas.STROKE_RECT - startY);
     *                     graphicsContext.setFill(Color.rgb(0, 0, 0, 0.0));
     *                     graphicsContext.strokeRect(startX, startY, event.getScreenX()-startX, event.getScreenY()-startY);
     *                     graphicsContext.clearRect(startX, startY, event.getScreenX() - startX, event.getScreenY() - startY);
     *                     graphicsContext.fillRect(startX, startY, event.getScreenX() - startX, event.getScreenY() - startY);
     *                     canvas.toFront();
     *                     oldEndX=event.getScreenX();
     *                     oldEndY=event.getScreenY();
     *                 }
     *             }
     *         }
     */
}
