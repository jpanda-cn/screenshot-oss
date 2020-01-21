package cn.jpanda.screenshot.oss.view.tray;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.EventHelper;
import cn.jpanda.screenshot.oss.common.toolkit.ImageShower;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.controller.ViewContext;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.clipboard.instances.ImageClipboardCallback;
import cn.jpanda.screenshot.oss.view.main.SettingsView;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayColorView;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayFontView;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayPointView;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.awt.image.BufferedImage;
import java.net.URL;
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

        drawingPin.setOnKeyPressed(e -> {
            if (e.isControlDown() || e.isShiftDown() || e.isAltDown()) {
                return;
            }
            if (e.getCode().equals(KeyCode.ENTER)) {
                // 获取截图区域图片
                showImage("");
                e.consume();
            }
        });
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
        add2Bar(new HBox(configuration.getViewContext().getScene(TrayPointView.class, false, true, false).getRoot()));
    }

    public void doDrawingPin(MouseEvent mouseEvent) {
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        initRectangle();
        canvasProperties.setCutInnerType(CutInnerType.DRAWING_PIN);

        Dialog<ButtonType> inputDialog = new Dialog<ButtonType>() {
            {
                setResultConverter((b) -> b);
            }
        };
        inputDialog.initOwner(canvasProperties.getCutPane().getScene().getWindow());
        inputDialog.initStyle(StageStyle.UNDECORATED);
        // 处理展示位置
        Rectangle rectangle = canvasProperties.getCutRectangle();
        Bounds bounds = rectangle.getScene().getRoot().getLayoutBounds();
        inputDialog.setX(rectangle.xProperty().add(rectangle.widthProperty().subtract(inputDialog.widthProperty()).divide(2)).get());
        inputDialog.widthProperty().addListener((observable, oldValue, newValue) -> {
            // 计算基准位置
            double x = rectangle.xProperty().add(rectangle.widthProperty().subtract(inputDialog.widthProperty()).divide(2)).get();
            // 重置展示位置
            x = Math.max(x, bounds.getMinX());
            x = Math.min(x, bounds.getMaxX());
            inputDialog.setX(x);
        });
        inputDialog.heightProperty().addListener((observable, oldValue, newValue) -> {
            double y = rectangle.yProperty().add(rectangle.heightProperty().subtract(inputDialog.heightProperty()).divide(2)).get();
            // 计算基准位置
            y = Math.max(y, bounds.getMinY());
            // 重置展示位置
            y = Math.min(y, bounds.getMaxY());
            inputDialog.setY(y);
        });

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 0, 10, 0));
        Label text = new Label("请输入便签描述（可为空）");
        text.minHeight(50);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(text);

        TextArea body = new TextArea();

        DialogPane dialogPane = inputDialog.getDialogPane();
        hBox.setStyle("-fx-background-color: #e6e6e6;");
        EventHelper.addDrag(hBox);
        dialogPane.setHeader(hBox);
        dialogPane.setContent(body);
        ButtonType toDesktop = new ButtonType("固定到桌面", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, toDesktop);
        if (inputDialog.showAndWait().orElse(ButtonType.CANCEL).equals(toDesktop)) {
            showImage(body.getText());
        }
    }

    private void showImage(String text) {
        Scene scene = canvasProperties.getCutPane().getScene();
        Rectangle rectangle = canvasProperties.getCutRectangle();
        ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
        BufferedImage image = screenshotsProcess.snapshot(scene, rectangle);
        WritableImage showImage = new WritableImage(image.getWidth(), image.getHeight());
        showImage = SwingFXUtils.toFXImage(image, showImage);
        ImageShower imageShower = ImageShower.of(configuration.getViewContext().getStage()).setTopTitle(text);
        imageShower.setX(rectangle.getX());
        imageShower.setY(rectangle.getY());
        imageShower.show(showImage);
        doCancel();
    }


    public void doRgb(MouseEvent mouseEvent) {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.RGB);
        add2Bar(new HBox());
    }

    // 设置
    public void doSettings() {
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        add2Bar(new HBox());
        initRectangle();
        showConfig();
    }

    public void showConfig() {
        // 截图配置窗口
        Scene setting = configuration.getViewContext().getScene(SettingsView.class, true, false);
        Parent root = setting.getRoot();
        root.setStyle("-fx-background-color: #FFFFFF");
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPadding(new Insets(0, 20, 0, 20));
        anchorPane.getChildren().add(root);
        PopDialog
                .create()
                .setHeader("设置")
                .setContent(anchorPane)
                .buttonTypes(ButtonType.CLOSE)
                .bindParent(settings.getScene().getWindow())
                .centerOnNode(canvasProperties.getCutRectangle())
                .showAndWait();
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

        // 弹窗提示，并允许调转到配置窗口

        ButtonType change = new ButtonType("修改", ButtonBar.ButtonData.NEXT_FORWARD);
        ButtonType upload = new ButtonType("上传", ButtonBar.ButtonData.APPLY);

        VBox body = new VBox();
        body.setAlignment(Pos.CENTER_LEFT);
        body.setSpacing(5);
        Label storeWay = new Label(String.format("存储方式:【%s】", imageStore));
        Label clipboardContent = new Label(String.format("剪切板内容：【%s】", clipboard));
        SimpleStringProperty imageProperty = configuration.getUniquePropertiesHolder(GlobalConfigPersistence.class.getCanonicalName() + "-" + "image-save", new SimpleStringProperty());
        SimpleStringProperty cliProperty = configuration.getUniquePropertiesHolder(GlobalConfigPersistence.class.getCanonicalName() + "-" + "clipboard-save", new SimpleStringProperty());

        storeWay.textProperty().bind(Bindings.createStringBinding(() -> String.format("存储方式:【%s】", imageProperty.get()), imageProperty));
        clipboardContent.textProperty().bind(Bindings.createStringBinding(() -> String.format("剪切板内容：【%s】", cliProperty.get()), cliProperty));

        body.getChildren().addAll(storeWay, clipboardContent);

        PopDialog.create()
                .setHeader("上传图片")
                .setContent(body)
                .bindParent(stage)
                .centerOnNode(rectangle)
                .buttonTypes(ButtonType.CANCEL, change, upload)
                .addButtonClass(change, "button-next")
                .callback(new Callable<Boolean, ButtonType>() {
                    @Override
                    public Boolean apply(ButtonType buttonType) {
                        if (upload.equals(buttonType)) {
                            // 上传
                            toUpload(stage, body.getScene().getWindow(), scene, rectangle);
                        } else if (change.equals(buttonType)) {
                            // 变更设置
                            showConfig();
                            return false;
                        } else if (ButtonType.CANCEL.equals(buttonType)) {

                        }
                        return true;
                    }
                })
                .show();

    }

    public void toUpload(Stage stage, Window window, Scene scene, Rectangle rectangle) {
        ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
        if (canvasProperties == null) {
            return;
        }
        try {
            screenshotsProcess.done(window, screenshotsProcess.snapshot(scene, rectangle));
        } finally {
            stage.close();
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


}
