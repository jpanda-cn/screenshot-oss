package cn.jpanda.screenshot.oss.view.tray;

import cn.jpanda.screenshot.oss.core.BootStrap;
import cn.jpanda.screenshot.oss.core.configuration.Configuration;
import cn.jpanda.screenshot.oss.core.context.ViewContext;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayColorView;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayFontView;
import cn.jpanda.screenshot.oss.view.tray.subs.TrayPointView;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import com.sun.istack.internal.Nullable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 截图托盘
 */
public class CanvasCutTrayView implements Initializable {

    private Configuration configuration = BootStrap.configuration;
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
    private GlobalConfigPersistence globalConfigPersistence;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bar.getChildren().addListener((ListChangeListener<Node>) c -> c.getList().forEach((n) -> {
            n.visibleProperty().setValue(true);
        }));
    }

    private void initRectangle() {
        canvasProperties = (CanvasProperties) submit.getScene().getWindow().getProperties().get(CanvasProperties.class);
        // 加载配置
        globalConfigPersistence = configuration.getDataPersistenceStrategy().load(GlobalConfigPersistence.class);
    }

    // 画圆
    public void doRoundness() {
        // 尝试初始化
        initRectangle();
        canvasProperties.setCutInnerType(CutInnerType.ROUNDNESS);
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class).getRoot();
        add2Bar(new HBox(points, colors));
        // 为圆形框内的每个元素添加事件
    }

    public void doRectangle() {
        // 尝试初始化
        initRectangle();
        canvasProperties.setCutInnerType(CutInnerType.RECTANGLE);
        // 生成左侧大小按钮
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class).getRoot();
        add2Bar(new HBox(points, colors));
        // 为圆形框内的每个元素添加事件
    }

    public void doArrow() {
        initRectangle();
        canvasProperties.setCutInnerType(CutInnerType.ARROW);
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class).getRoot();
        add2Bar(new HBox(points, colors));
    }

    public void doPen() {
        initRectangle();
        canvasProperties.setCutInnerType(CutInnerType.PEN);
        ViewContext v = configuration.getViewContext();
        Pane points = (Pane) v.getScene(TrayPointView.class).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class).getRoot();
        add2Bar(new HBox(points, colors));
    }

    public void doText() {
        initRectangle();
        canvasProperties.setCutInnerType(CutInnerType.TEXT);
        ViewContext v = configuration.getViewContext();
        Pane fonts = (Pane) v.getScene(TrayFontView.class).getRoot();
        Pane colors = (Pane) v.getScene(TrayColorView.class).getRoot();
        add2Bar(new HBox(fonts, colors));
    }

    // 拖动
    public void doDrag() {
        initRectangle();
        canvasProperties.setCutInnerType(CutInnerType.DRAG);
        add2Bar(new HBox());
    }

    public void doCancel() {
        initRectangle();
        if (canvasProperties == null) {
            return;
        }
        Scene scene = canvasProperties.getCutPane().getScene();
        // 关闭
        ((Stage) scene.getWindow()).close();
    }

    public void doDone() {

        initRectangle();
        // 获取截图区域的图片交由图片处理器来完成保存图片的操作
        if (canvasProperties == null) {
            return;
        }
        Scene scene = canvasProperties.getCutPane().getScene();
        Rectangle rectangle = canvasProperties.getCutRectangle();
        WritableImage wImage = scene.snapshot(null);
        // 将图片转为BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(wImage, null);
        bufferedImage = bufferedImage.getSubimage(rectangle.xProperty().intValue() + 1, rectangle.yProperty().intValue() + 1, rectangle.widthProperty().intValue() - 2, rectangle.heightProperty().intValue() - 2);
        // 将获取到的图片交给图片处理器完成。
        configuration.store(bufferedImage);
        // 关闭
        Stage stage = ((Stage) scene.getWindow());
        stage.close();
        if (globalConfigPersistence.isPreview()) {
            // 从剪切板搞定图片
            stage = new Stage();
            stage.getIcons().add(new Image("/logo.png"));
            stage.setTitle("预览图片,如不需要，请到设置中关闭该功能。");
            stage.setScene(new Scene(new AnchorPane(new ImageView(SwingFXUtils.toFXImage(bufferedImage, null)))));
            stage.showAndWait();
        }
    }

    public void add2Bar(@Nullable Node... nodes) {
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
}
