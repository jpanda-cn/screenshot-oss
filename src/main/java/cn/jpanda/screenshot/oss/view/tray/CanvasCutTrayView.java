package cn.jpanda.screenshot.oss.view.tray;

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
import cn.jpanda.screenshot.oss.view.tray.subs.TrayColorView;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayFontView;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayPointView;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.image.BufferedImage;
import java.net.URL;
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
        Pane points = (Pane) v.getScene(TrayPointView.class, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, true, false).getRoot();
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
        Pane points = (Pane) v.getScene(TrayPointView.class, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, true, false).getRoot();
        add2Bar(new HBox(points, colors));
        // 为圆形框内的每个元素添加事件
    }

    public void doArrow() {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.ARROW);
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, true, false).getRoot();
        add2Bar(new HBox(points, colors));
    }

    public void doPen() {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.PEN);
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, true, false).getRoot();
        add2Bar(new HBox(points, colors));
    }

    public void doText() {
        initRectangle();
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();
        canvasProperties.setCutInnerType(CutInnerType.TEXT);
        ViewContext v = configuration.getViewContext();
        Pane fonts = (Pane) v.getScene(TrayFontView.class, true, false).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class, true, false).getRoot();
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
        add2Bar(new HBox((Pane) configuration.getViewContext().getScene(TrayPointView.class, true, false).getRoot()));
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
        ScreenCapture screenCapture=configuration.getUniqueBean(ScreenCapture.class);
        alert.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                //  转换为全局坐标
                double aw = alert.widthProperty().get();
                double offsetW = (rectangle.getWidth() - aw) / 2;
                alert.setX(screenCapture.minx()+rectangle.getX() + offsetW);
                double ah = alert.heightProperty().get();
                double offsetH = (rectangle.getHeight() - ah) / 2;
                alert.setY(screenCapture.miny()+rectangle.getY() + offsetH);
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
}
