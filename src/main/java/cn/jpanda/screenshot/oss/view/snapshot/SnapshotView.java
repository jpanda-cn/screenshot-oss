package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.common.toolkit.Bounds;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.mouse.GlobalMousePoint;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 截图窗口
 */
@Controller
public class SnapshotView implements Initializable {
    private Configuration configuration;
    private GlobalConfigPersistence globalConfigPersistence;
    private ScreenCapture screenCapture;

    public SnapshotView(Configuration configuration) {
        this.configuration = configuration;
    }

    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenCapture = configuration.getUniqueBean(ScreenCapture.class);
        globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        BufferedImage image = getDesktopSnapshot();
        WritableImage writableImage = new WritableImage(image.getWidth(), image.getHeight());
        SwingFXUtils.toFXImage(image, writableImage);
        imageView.setImage(writableImage);
        Bounds dimension = screenCapture.getTargetScreen(0);
        Canvas canvas = new Canvas(dimension.getWidth(), dimension.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setStroke(Color.rgb(50, 161, 255));
        ((AnchorPane) imageView.getParent()).getChildren().add(canvas);
        // 全体黑化，黑暗能量，巴啦啦小魔仙变身~
        graphicsContext.setFill(Color.rgb(0, 0, 0, 0.3));
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 黑化之后，剩余的就交给绘制处理器来完成了
        canvas.addEventHandler(MouseEvent.ANY, new CanvasDrawEventHandler(Color.rgb(0, 0, 0, 0.3), graphicsContext, configuration));

        // 双击完成截图
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                saveAndClose(canvas);
            }
        });

        canvas.addEventFilter(KeyEvent.KEY_RELEASED, (e) -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.SPACE)) {
                // 执行保存操作
                saveAndClose(canvas);
            }
        });
    }

    private BufferedImage getDesktopSnapshot() {
        int screenIndex = 0;
        if (globalConfigPersistence.isScreenshotMouseFollow()) {
            // 启用了鼠标跟随功能
            screenIndex = screenCapture.getScreenIndex(configuration.getUniqueBean(GlobalMousePoint.class).pointSimpleObjectProperty.get().getX());
        } else {
            if (globalConfigPersistence.getScreenIndex() >= screenCapture.screensCount()) {
                // 校验一下显示器的数量问题
                globalConfigPersistence.setScreenIndex(0);
                configuration.storePersistence(globalConfigPersistence);
            }
            screenIndex = globalConfigPersistence.getScreenIndex();
        }

        Bounds graphicsDevice = screenCapture.getTargetScreen(screenIndex);
        return screenCapture.screenshotImage(screenIndex, (int) graphicsDevice.getWidth(), (int) graphicsDevice.getHeight());
    }

    protected void saveAndClose(Canvas canvas) {
        ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
        // 获取截图区域的图片交由图片处理器来完成保存图片的操作
        CanvasProperties canvasProperties = (CanvasProperties) canvas.getScene().getWindow().getProperties().get(CanvasProperties.class);
        if (canvasProperties == null) {
            return;
        }
        screenshotsProcess.done(screenshotsProcess.snapshot(canvas.getScene(), canvasProperties.getCutRectangle()));
        // 关闭
        ((Stage) canvas.getScene().getWindow()).close();
    }
}
