package cn.jpanda.screenshot.oss.service.handlers.snapshot;

import cn.jpanda.screenshot.oss.common.toolkit.DragRectangleEventHandler;
import cn.jpanda.screenshot.oss.common.toolkit.RectangleAddTag2ResizeBinding;
import cn.jpanda.screenshot.oss.common.toolkit.ShapeCovertHelper;
import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.log.Log;
import cn.jpanda.screenshot.oss.core.mouse.GlobalMousePoint;
import cn.jpanda.screenshot.oss.core.shotkey.DefaultGroupScreenshotsElements;
import cn.jpanda.screenshot.oss.core.shotkey.ScreenshotsElementConvertor;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.clipboard.instances.ImageClipboardCallback;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SnapshotRegionKeyEventHandler implements EventHandler<KeyEvent> {
    private Log log;
    private ScreenshotsElementConvertor screenshotsElementConvertor;
    private Configuration configuration;
    private CanvasProperties canvasProperties;

    public SnapshotRegionKeyEventHandler(ScreenshotsElementConvertor screenshotsElementConvertor, Configuration configuration, CanvasProperties canvasProperties) {
        this.screenshotsElementConvertor = screenshotsElementConvertor;
        this.configuration = configuration;
        this.canvasProperties = canvasProperties;
        this.log = configuration.getLogFactory().getLog(getClass());
    }


    @Override
    public void handle(KeyEvent event) {
        // 处理快捷键
        if (event.isShortcutDown()) {
            shortKey(event);
        } else {
            simpleKey(event);
        }

    }

    private boolean onlyControlDown(KeyEvent event) {
        return event.isControlDown() && !event.isShiftDown() && !event.isAltDown() && !event.isMetaDown();
    }

    private boolean onlyAltDown(KeyEvent event) {
        return !event.isControlDown() && !event.isShiftDown() && event.isAltDown() && !event.isMetaDown();
    }

    private boolean onlyShiftDown(KeyEvent event) {
        return !event.isControlDown() && event.isShiftDown() && !event.isAltDown() && !event.isMetaDown();
    }

    private boolean onlyMetaDown(KeyEvent event) {
        return !event.isControlDown() && !event.isShiftDown() && !event.isAltDown() && event.isMetaDown();
    }

    private void shortKey(KeyEvent event) {
        KeyCode code = event.getCode();
        if (onlyControlDown(event)) {
            // 只有control按下
            switch (code) {
                case Z: {
                    // 撤销一步操作
                    screenshotsElementConvertor.destroyOne();
                    break;
                }
                case Y: {
                    // 恢复一步操作
                    screenshotsElementConvertor.activateOne();
                    break;
                }
                case V: {
                    paste();
                }

            }
        }
    }

    private void simpleKey(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case ENTER: {
                if (canvasProperties == null) {
                    return;
                }
                Scene scene = canvasProperties.getCutPane().getScene();
                Rectangle rectangle = canvasProperties.getCutRectangle();
                try {
                    ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
                    // 获取截图
                    BufferedImage bufferedImage = screenshotsProcess.snapshot(scene, rectangle);
                    // 不执行图片保存操作
                    // 将图片放置剪切板
                    ClipboardCallback clipboardCallback = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class).get(ImageClipboardCallback.NAME);
                    clipboardCallback.callback(bufferedImage, "");
                } finally {
                    Stage stage = ((Stage) scene.getWindow());
                    // 关闭
                    Platform.runLater(stage::close);
                }
                break;
            }
        }
    }

    private void paste() {
        log.info("will paste ...");
        Rectangle rectangle = canvasProperties.getCutRectangle();
        GlobalMousePoint globalMousePoint = configuration.getUniqueBean(GlobalMousePoint.class);
        Point point = globalMousePoint.pointSimpleObjectProperty.get();
        // 处理坐标，将坐标转换为矩形
        ScreenCapture screenCapture = configuration.getUniqueBean(ScreenCapture.class);
        point = new Point(point.x - screenCapture.minx(), point.y - screenCapture.miny());
        log.info("The mouse point:{0}", point);
        if (rectangle.contains(point.getX(), point.getY())) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            Image image = clipboard.getImage();
            Group group = new Group();
            if (null != image) {
                // 往鼠标左上角粘贴图片
                ImageView imageView = new ImageView(image);
                imageView.setMouseTransparent(true);
                imageView.xProperty().set(point.x);
                imageView.yProperty().set(point.y);
                double maxW = rectangle.xProperty().add(rectangle.widthProperty()).subtract(point.x).get();
                double maxH = rectangle.yProperty().add(rectangle.heightProperty()).subtract(point.y).get();
                double finalW = MathUtils.min(image.getWidth(), maxW);
                double finalH = MathUtils.min(image.getHeight(), maxH);
                // 获取最小比例
                double ratio = MathUtils.min(finalW / image.widthProperty().get(), finalH / image.heightProperty().get());
                imageView.fitWidthProperty().set(image.widthProperty().get() * ratio);
                imageView.fitHeightProperty().set(image.heightProperty().get() * ratio);
                group.getChildren().add(imageView);

                // 获取当前矩形
                Rectangle currentRectangle = ShapeCovertHelper.toRectangle(group);
                imageView.xProperty().bind(currentRectangle.xProperty());
                imageView.yProperty().bind(currentRectangle.yProperty());
                currentRectangle.widthProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() == 0) {
                        newValue = 1;
                    }
                    imageView.fitWidthProperty().set(newValue.doubleValue());
                });
                currentRectangle.heightProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() == 0) {
                        newValue = 1;
                    }
                    imageView.fitHeightProperty().set(newValue.doubleValue());
                });
                // 绑定拖动矩形和矩形的关系
                group.getChildren().add(currentRectangle);
                currentRectangle.visibleProperty().setValue(true);
                currentRectangle.setStroke(javafx.scene.paint.Color.BLUE);
                currentRectangle.setFill(Color.TRANSPARENT);
                rectangle.toBack();
                // 添加拖动事件
                // 添加变更大小事件
                RectangleAddTag2ResizeBinding rectangleAddTag2ResizeBinding = new RectangleAddTag2ResizeBinding(currentRectangle, rectangle).bind();

                canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
                    // 鼠标按下时，清理之前生成的矩形组的事件
                    group.setMouseTransparent(true);
                    currentRectangle.visibleProperty().setValue(false);
                    if (rectangleAddTag2ResizeBinding != null) {
                        rectangleAddTag2ResizeBinding.unbind();
                    }
                });

            } else {
                // 粘贴文字
                String str = clipboard.getString();
                if (StringUtils.isEmpty(str)) {
                    return;
                }
                Label label = new Label(str);
                label.wrapTextProperty().set(true);
                // ok 包装一层用来移动文字的框框
                Rectangle dragRec = ShapeCovertHelper.toRectangle(group);
                dragRec.setStroke(Color.RED);
                dragRec.setFill(Color.TRANSPARENT);
                dragRec.xProperty().set(point.x);
                dragRec.yProperty().set(point.y);
                dragRec.xProperty().addListener((observable, oldValue, newValue) -> dragRec.maxWidth(rectangle.xProperty().add(rectangle.widthProperty()).subtract(dragRec.xProperty()).get()));
                dragRec.yProperty().addListener((observable, oldValue, newValue) -> dragRec.maxHeight(rectangle.yProperty().add(rectangle.heightProperty()).subtract(dragRec.heightProperty()).get()));
                // 绑定关系
                label.layoutXProperty().bind(dragRec.xProperty().add(3));
                label.layoutYProperty().bind(dragRec.yProperty().add(3));
                label.maxWidthProperty().bind(rectangle.xProperty().add(rectangle.widthProperty()).subtract(dragRec.xProperty()).subtract(6));
                label.maxHeightProperty().bind(rectangle.yProperty().add(rectangle.heightProperty()).subtract(dragRec.heightProperty()).subtract(6));

                dragRec.widthProperty().bind(label.widthProperty().add(6));
                dragRec.heightProperty().bind(label.heightProperty().add(6));
                group.getChildren().addAll(label, dragRec);
                dragRec.addEventFilter(MouseEvent.ANY, new DragRectangleEventHandler(dragRec, rectangle));
                canvasProperties.getConfiguration().getUniqueBean(DestroyGroupBeanHolder.class).set(() -> {
                    // 鼠标按下时，清理之前生成的矩形组的事件
                    group.setMouseTransparent(true);
                    dragRec.visibleProperty().setValue(false);
                });
            }
            canvasProperties.getCutPane().getChildren().add(group);
            canvasProperties.getScreenshotsElementsHolder().putEffectiveElement(new DefaultGroupScreenshotsElements(group, canvasProperties));
        }
    }
}
