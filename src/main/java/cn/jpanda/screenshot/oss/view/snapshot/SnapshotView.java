package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.clipboard.instances.ImageClipboardCallback;
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

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 截图窗口
 */
@Controller
public class SnapshotView implements Initializable {
    private Configuration configuration;
    private CanvasDrawEventHandler canvasDrawEventHandler;
    private Canvas canvas;

    public SnapshotView(Configuration configuration) {
        this.configuration = configuration;
    }

    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BufferedImage image = getDesktopSnapshot();
        // 一个用于计算，一个用于绘制，比较占内存
        WritableImage writableImage = new WritableImage(image.getWidth(), image.getHeight());
        WritableImage computerImage = new WritableImage(image.getWidth(), image.getHeight());
        SwingFXUtils.toFXImage(image, writableImage);
        SwingFXUtils.toFXImage(getDesktopSnapshot(), computerImage);
        imageView.setImage(writableImage);

        canvas = new Canvas(image.getWidth(), image.getHeight());
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setStroke(Color.rgb(50, 161, 255));
        ((AnchorPane) imageView.getParent()).getChildren().add(canvas);
        // 全体黑化，黑暗能量，巴啦啦小魔仙变身~
        graphicsContext.setFill(Color.rgb(0, 0, 0, 0.3));
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 黑化之后，剩余的就交给绘制处理器来完成了
        canvasDrawEventHandler = new CanvasDrawEventHandler(Color.rgb(0, 0, 0, 0.4), graphicsContext, configuration, writableImage, computerImage);
        canvas.addEventHandler(MouseEvent.ANY, canvasDrawEventHandler);

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
        return configuration.getUniqueBean(ScreenCapture.class).screenshotImage();
    }

    protected void saveAndClose(Canvas canvas) {
        ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
        // 获取截图区域的图片交由图片处理器来完成保存图片的操作
        Stage stage = ((Stage) canvas.getScene().getWindow());
        CanvasProperties canvasProperties = (CanvasProperties) stage.getProperties().get(CanvasProperties.class);
        if (canvasProperties == null) {
            return;
        }
        try {
            // 获取截图
            BufferedImage bufferedImage = screenshotsProcess.snapshot(canvas.getScene(), canvasProperties.getCutRectangle());
            // 不执行图片保存操作
            // 将图片放置剪切板
            ClipboardCallback clipboardCallback = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class).get(ImageClipboardCallback.NAME);
            clipboardCallback.callback(bufferedImage, "");
        } finally {
            ((WaitRemoveElementsHolder) (stage.getProperties().getOrDefault(WaitRemoveElementsHolder.class, new WaitRemoveElementsHolder()))).clear();
            canvas.removeEventHandler(MouseEvent.ANY, canvasDrawEventHandler);
            canvasDrawEventHandler = null;
            this.canvas = null;
            stage.close();
        }

    }
}
