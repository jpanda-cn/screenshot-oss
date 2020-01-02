package cn.jpanda.screenshot.oss.view.tray;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.capture.ScreenCapture;
import cn.jpanda.screenshot.oss.core.controller.ViewContext;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.clipboard.instances.ImageClipboardCallback;
import cn.jpanda.screenshot.oss.view.main.SettingsView;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.subs.ResizeEventHandler;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayColorView;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayFontView;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayPointView;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 截图托盘
 */
@Controller
public class CanvasCutTrayView implements Initializable {

    public Button settings;
    public Button mosaic;
    public Button save;
    public Button rgb;
    public Button drawingPin;
    private Configuration configuration;

    public CanvasCutTrayView(Configuration configuration) {
        this.configuration = configuration;
    }

    @FXML
    public Button drag;
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
        bar.getChildren().addListener((ListChangeListener<Node>) c -> c.getList().forEach((n) -> {
            n.visibleProperty().setValue(true);
        }));
        drag.tooltipProperty().setValue(new Tooltip("拖动"));
        roundness.tooltipProperty().setValue(new Tooltip("圆形:<<按住shift键试试>>"));
        rectangle.tooltipProperty().setValue(new Tooltip("矩形"));
        arrow.tooltipProperty().setValue(new Tooltip("箭头"));
        pen.tooltipProperty().setValue(new Tooltip("画笔"));
        text.tooltipProperty().setValue(new Tooltip("文字"));
        mosaic.tooltipProperty().setValue(new Tooltip("马赛克"));
        rgb.tooltipProperty().setValue(new Tooltip("取色器"));
        drawingPin.tooltipProperty().setValue(new Tooltip("图钉"));
        settings.tooltipProperty().setValue(new Tooltip("设置"));
        save.tooltipProperty().setValue(new Tooltip("上传至云"));
        cancel.tooltipProperty().setValue(new Tooltip("取消"));
        submit.tooltipProperty().setValue(new Tooltip("保存"));
    }

    private void initRectangle() {
        canvasProperties = (CanvasProperties) submit.getScene().getWindow().getProperties().get(CanvasProperties.class);
    }

    // 画圆
    public void doRoundness() {
        // 尝试初始化
        initRectangle();
        configuration.getUniqueBean(DestroyGroupBeanHolder.class).destroy();
        canvasProperties.setCutInnerType(CutInnerType.ROUNDNESS);
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class, false, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, false, true, false).getRoot();
        add2Bar(new HBox(points, colors));
        // 为圆形框内的每个元素添加事件
    }

    public void doRectangle() {
        // 尝试初始化
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.RECTANGLE);
        // 生成左侧大小按钮
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class, false, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, false, true, false).getRoot();
        add2Bar(new HBox(points, colors));
        // 为圆形框内的每个元素添加事件
    }

    public void doArrow() {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.ARROW);
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class, false, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, false, true, false).getRoot();
        add2Bar(new HBox(points, colors));
    }

    public void doPen() {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.PEN);
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class, false, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, false, true, false).getRoot();
        add2Bar(new HBox(points, colors));
    }

    public void doText() {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.TEXT);
        ViewContext v = configuration.getViewContext();
        Pane fonts = (Pane) v.getScene(TrayFontView.class, false, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, false, true, false).getRoot();
        add2Bar(new HBox(fonts, colors));
    }

    // 拖动
    public void doDrag() {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.DRAG);
        add2Bar(new HBox());
    }

    public void doMosaic() {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.MOSAIC);
        add2Bar(new HBox((Pane) configuration.getViewContext().getScene(TrayPointView.class, false, true, false).getRoot()));
    }

    public void doDrawingPin(MouseEvent mouseEvent) {
        final double stroke = 5;
        try {
            DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
            destroyGroupBeanHolder.destroy();
            initRectangle();
            canvasProperties.setCutInnerType(CutInnerType.DRAWING_PIN);
            // 获取截图区域图片
            Scene scene = canvasProperties.getCutPane().getScene();
            Rectangle rectangle = canvasProperties.getCutRectangle();
            ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
            BufferedImage image = screenshotsProcess.snapshot(scene, rectangle);
            WritableImage showImage = new WritableImage(image.getWidth(), image.getHeight());
            showImage = SwingFXUtils.toFXImage(image, showImage);
            Stage stage = configuration.getViewContext().newStage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.NONE);
            stage.initOwner(configuration.getViewContext().getStage());
            // 添加边框
            ImagePattern imagePattern = new ImagePattern(showImage);
            Rectangle rect = new Rectangle(showImage.getWidth() + stroke * 2, showImage.getHeight() + stroke * 2);
            rect.setLayoutX(stroke);
            rect.setLayoutY(stroke);
            rect.setFill(imagePattern);
            rect.strokeWidthProperty().set(stroke);
            rect.strokeProperty().set(Color.rgb(0, 0, 0, 0.3));
            rect.strokeTypeProperty().set(StrokeType.OUTSIDE);
//            rect.mouseTransparentProperty().setValue(true);

            Button button = drawingPin(Color.RED);
            AnchorPane top = new AnchorPane();
//            top.mouseTransparentProperty().setValue(true);
            top.styleProperty().set(" -fx-background-color: rgba(0,0,0,0.5);");
            top.getChildren().addAll(button);
            top.addEventHandler(MouseEvent.ANY, addDrag(top));

            AnchorPane body = new AnchorPane(rect) {

            };
            body.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (oldValue.intValue() == 0) {
                        return;
                    }

                    rect.widthProperty().set(rect.widthProperty().add(newValue.doubleValue() - oldValue.doubleValue()).getValue());
                }
            });




            body.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (oldValue.intValue()==0){
                        return;
                    }
                    System.out.println("=============");
                    System.out.println(newValue);
                    System.out.println(oldValue);
                    System.out.println("=============");
                    System.out.println(rect.heightProperty().get());
                    rect.heightProperty().set(rect.heightProperty().add(newValue.doubleValue() - oldValue.doubleValue()).getValue());
                }
            });

            AnchorPane.setTopAnchor(rect, 0D);
            AnchorPane.setLeftAnchor(rect, 0D);
            AnchorPane.setBottomAnchor(rect, 0D);
            AnchorPane.setRightAnchor(rect, 0D);
            body.styleProperty().set(" -fx-background-color: rgba(0,0,0,0.5);");
            VBox box = new VBox();
            box.styleProperty().set(" -fx-background-color: transparent;");
            box.getChildren().addAll(top, body);

            EventHandler<MouseEvent> resize = new ResizeEventHandler(stage, body, rect, Collections.singletonList(top));
            EventHandler<MouseEvent> drag = addDrag(body);
            rect.addEventHandler(MouseEvent.ANY, stageHandler(rect, resize, drag));
            top.addEventHandler(MouseEvent.ANY, addDrag(top));

            Scene sc = new Scene(box);
            sc.setFill(Color.TRANSPARENT);
            stage.setScene(sc);
            stage.setAlwaysOnTop(true);
            stage.show();
        } finally {
            doCancel();
        }

    }

    public EventHandler<MouseEvent> stageHandler(Rectangle rectangle, EventHandler<MouseEvent> resize, EventHandler<MouseEvent> drag) {
        return new EventHandler<MouseEvent>() {
            private double offset = 10;
            private boolean onEdge = false;


            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
                    // 判断如何展示
                    double mouseX = event.getX();
                    double mouseY = event.getY();
                    double ox = rectangle.xProperty().getValue();
                    double oy = rectangle.yProperty().getValue();


                    boolean onStartX = MathUtils.offset(mouseX, ox, offset);
                    boolean onEndX = MathUtils.offset(mouseX, ox + rectangle.widthProperty().getValue(), offset);

                    boolean onStartY = MathUtils.offset(mouseY, oy, offset);
                    boolean onEndY = MathUtils.offset(mouseY, oy + rectangle.heightProperty().getValue(), offset);

                    boolean onX = onStartX || onEndX;
                    boolean onY = onStartY || onEndY;
                    onEdge = onX || onY;
                }
                // 判断当前是否是在边缘
                if (onEdge) {
                    if (resize != null) {
                        resize.handle(event);
                    }

                } else {
                    if (drag != null) {
                        drag.handle(event);
                    }
                }
            }
        };
    }


    public void doRgb(MouseEvent mouseEvent) {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.RGB);

    }

    // 设置
    public void doSettings() {
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        add2Bar(new HBox());
        Stage cutStage = (Stage) settings.getScene().getWindow();
        cutStage.setAlwaysOnTop(false);
        Stage stage = configuration.getViewContext().newStage();
        stage.initOwner(settings.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(configuration.getViewContext().getScene(SettingsView.class, true, false));
        stage.toFront();
        stage.showAndWait();
        cutStage.setAlwaysOnTop(true);
    }

    public void doCancel() {
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        initRectangle();
        if (canvasProperties == null) {
            return;
        }
        Scene scene = canvasProperties.getCutPane().getScene();
        // 关闭
        ((Stage) scene.getWindow()).close();
    }

    public void doSave() {

        // 执行销毁操作
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();

        initRectangle();
        Scene scene = canvasProperties.getCutPane().getScene();
        Rectangle rectangle = canvasProperties.getCutRectangle();
        Stage stage = ((Stage) scene.getWindow());
        // 提示用户当前采用保存方式
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        String imageStore = globalConfigPersistence.getImageStore();
        String clipboard = globalConfigPersistence.getClipboardCallback();

        // 弹窗提示
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(String.format("本次操作会采用【%s】方式保存图片,并以【%s】形式保存在剪切板内", imageStore, clipboard));
        alert.setContentText("如需变更保存方式或者剪切板内容，请【取消】之后，点击左侧【设置】按钮");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(new ButtonType("取消", ButtonBar.ButtonData.BACK_PREVIOUS), new ButtonType("保存至云", ButtonBar.ButtonData.OK_DONE));
        alert.initOwner(stage);
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.APPLICATION_MODAL);
        // 调整位置，将其放置在截图框的正中间
        ScreenCapture screenCapture = configuration.getUniqueBean(ScreenCapture.class);
        alert.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                //  转换为全局坐标
                double aw = alert.widthProperty().get();
                double offsetW = (rectangle.getWidth() - aw) / 2;
                alert.setX(screenCapture.minx() + rectangle.getX() + offsetW);
                double ah = alert.heightProperty().get();
                double offsetH = (rectangle.getHeight() - ah) / 2;
                alert.setY(screenCapture.miny() + rectangle.getY() + offsetH);
            }
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
                // 获取截图区域的图片交由图片处理器来完成保存图片的操作
                if (canvasProperties == null) {
                    return;
                }
                try {
                    screenshotsProcess.done(screenshotsProcess.snapshot(scene, rectangle));
                } finally {
                    stage.close();
                }
            }
        }

    }

    public void add2Bar(Node... nodes) {
        ObservableList<Node> child = bar.getChildren();
        if (child != null && child.size() > 0) {
            child.forEach((n) -> {
                n.visibleProperty().setValue(false);
            });
            child.clear();

        }
        if (nodes == null || nodes.length == 0) {
            return;
        }
        for (Node n : nodes) {
            if (n == null) {
                continue;
            }
            bar.getChildren().add(n);
        }

    }

    public void doDone() {
        // 获取
        // 执行销毁操作
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();

        ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);

        initRectangle();
        // 获取截图区域的图片交由图片处理器来完成保存图片的操作
        if (canvasProperties == null) {
            return;
        }
        Scene scene = canvasProperties.getCutPane().getScene();
        Rectangle rectangle = canvasProperties.getCutRectangle();
        try {
            // 获取截图
            BufferedImage bufferedImage = screenshotsProcess.snapshot(scene, rectangle);
            // 不执行图片保存操作
            // 将图片放置剪切板
            ClipboardCallback clipboardCallback = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class).get(ImageClipboardCallback.NAME);
            clipboardCallback.callback(bufferedImage, "");
        } finally {
            Stage stage = ((Stage) scene.getWindow());
            stage.close();
        }

    }

    public EventHandler<MouseEvent> addDrag(Node node) {

        return new EventHandler<MouseEvent>() {
            private double xOffset = 0;
            private double yOffset = 0;
            Stage stage;

            @Override
            public void handle(MouseEvent event) {
                node.setCursor(Cursor.MOVE);
                if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                    stage = (Stage) node.getScene().getWindow();
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            }
        };
    }

    public Button drawingPin(Color color) {

        Group svg = new Group(
                createPath("M864.3584 421.2736a46.08 46.08 0 1 1 41.4208-82.3296c26.5216 13.3632 51.6608 29.5424 75.2128 48.5376a81.92 81.92 0 0 1 6.3488 121.3952l-206.4896 206.4896 9.1648 9.1648c150.272 149.1456 213.7088 212.6336 219.0848 219.4944 1.6384 2.2016 1.6384 2.2016-1.024 57.9584l-61.3376 8.3968c-2.4064-1.6896-2.4064-1.6896-4.096-3.1232l-1.7408-1.536-1.1776-1.1776-3.072-3.072-11.9808-11.8784-48.0768-48.0256a343675.648 343675.648 0 0 1-160.768-160.8704l-205.6192 207.2064c-15.616 15.5648-36.352 24.0128-57.856 24.0128-24.3712 0-47.616-10.8032-63.5904-30.208a421.888 421.888 0 0 1-80.5376-373.0432L159.4368 421.0176a245.6576 245.6576 0 0 1-117.7088-39.168 80.896 80.896 0 0 1-12.8-124.928L257.536 28.7744a80.8448 80.8448 0 0 1 125.0304 13.056c22.8864 35.328 36.096 75.6224 38.8608 117.1456l187.6992 148.8384a423.2192 423.2192 0 0 1 107.1616-13.824 46.08 46.08 0 1 1 0 92.16c-34.816 0-69.4784 5.5296-102.8096 16.384l-23.552 7.68-260.9152-206.8992 0.7168-23.1424c0.8192-26.5216-5.12-52.736-17.3056-75.9296L104.3456 311.808a153.8048 153.8048 0 0 0 75.3664 17.4592l23.7056-1.1776 207.2576 261.376-7.68 23.552a329.8816 329.8816 0 0 0 50.176 301.4656l197.5296-199.0656 32.2048-32.4608 32.3584-32.6656 198.656-198.3488a327.424 327.424 0 0 0-49.5616-30.72z", color)
        );

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(20 / bounds.getWidth(), 20 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        Button btn = new Button();
        btn.setGraphic(svg);
        btn.setMaxSize(30, 30);
        btn.setMinSize(30, 30);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.styleProperty().set(" -fx-background-color: rgba(255,255,255,0.5);");
        btn.setLayoutX(0);
        btn.setLayoutY(0);
        return btn;

    }

    private static SVGPath createPath(String d, Color color) {
        SVGPath path = new SVGPath();
        path.setContent(d);
        path.setFill(color);
        return path;
    }

}
