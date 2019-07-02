package cn.jpanda.screenshot.oss.service.handlers.snapshot;

import cn.jpanda.screenshot.oss.common.toolkit.Bounds;
import cn.jpanda.screenshot.oss.common.toolkit.ExternalComponentBinders;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.snapshot.EveryScreenshotWaitRemoveElement;
import cn.jpanda.screenshot.oss.view.snapshot.WaitRemoveElementsHolder;
import cn.jpanda.screenshot.oss.view.tray.CanvasCutTrayView;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

import static cn.jpanda.screenshot.oss.common.utils.MathUtils.*;

public class CanvasDrawEventHandler implements EventHandler<MouseEvent> {
    private Configuration configuration;
    private Paint masking;
    private double startX;
    private double startY;
    private Bounds last;
    private GraphicsContext graphicsContext;
    private Parent toolbar;
    private Pane pane;
    private Rectangle cutRec;
    private Group group;
    private RoutingSnapshotCanvasEventHandler routingSnapshotCanvasEventHandler;
    private SnapshotRegionKeyEventHandler snapshotRegionKeyEventHandler;
    private ExternalComponentBinders externalComponentBinders;
    private boolean start = true;

    private WritableImage backgroundImage;
    private WritableImage computerImage;

    public CanvasDrawEventHandler(Paint masking, GraphicsContext graphicsContext, Configuration configuration, WritableImage writableImage, WritableImage computerImage) {
        // 蒙版清晰度
        this.masking = masking;
        this.backgroundImage = writableImage;
        this.computerImage = computerImage;
        // Canvas绘图
        this.graphicsContext = graphicsContext;
        // Canvas所属容器
        pane = ((Pane) (graphicsContext.getCanvas().getParent()));
        this.configuration = configuration;

        // 加载工具托盘
        Scene scene = configuration.getViewContext().getScene(CanvasCutTrayView.class, true, false);
        toolbar = scene.getRoot();
    }

    @Override
    public void handle(MouseEvent event) {

        if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
            // 理论上,每一次点击，都会生成一个新的区域，同时移除老的截图区域的内容。
            start = true;
        } else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
            if (start) {
                initParam(event);
                start = false;
            }
            draw(event);
        } else if (MouseEvent.MOUSE_RELEASED.equals(event.getEventType())) {
            // 生成一个截图区域
            // 这个区域下方生成一个工具组
            beforeDone(pane);
        } else {
        }
    }

    protected void initParam(MouseEvent event) {
        ((WaitRemoveElementsHolder) (pane.getScene().getWindow().getProperties().get(WaitRemoveElementsHolder.class))).clear();
        // 重新绘制整个蒙版
        drawMasking(new Bounds(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight()));
        // 生成一个截图区域
        // 为截图区域生成一个容器
        cutRec = new Rectangle(0, 0, 0, 0);
        cutRec.visibleProperty().setValue(false);

        // 每次点击都要清理到原有的工具栏
        group = new Group(cutRec, toolbar);
        // 添加工具托盘和截图位置
        pane.getChildren().addAll(group);
        // 绑定托盘和截图区域的关系
        externalComponentBinders = new ExternalComponentBinders(cutRec, toolbar).doRegistry();
        //  在这里，截图区域单属一个容器，工具托盘和截图容器共同存放在一个容器内。
        // 获取鼠标点击位置，该位置用于初始化截图区域的坐标
        startX = event.getSceneX();
        startY = event.getSceneY();
        // 更新上一个截图位置的区域
        last = new Bounds(startX, startY, 0, 0);
    }


    protected void draw(MouseEvent event) {
        // 判断需要新增和移除的矩形区域
        // 获取鼠标当前位置
        // 获取所属坐标
        // 处理鼠标的坐标，不能超出所截图区域
        double x = max(event.getSceneX(), 0);
        double y = event.getSceneY();
        double currentX = min(startX, x);
        double currentY = min(startY, y);
        double width = subAbs(x, startX);
        double height = subAbs(y, startY);
        // 计算需要进行绘制的区域
        Bounds newDraw = new Bounds(currentX, currentY, width, height);
        draw(newDraw);
        last = newDraw;
    }

    public void draw(Bounds bounds) {
        doDraw(bounds);
    }

    private void doDraw(Bounds newDraw) {
        // 绘制蒙版
        drawMasking(new Bounds(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight()));
        // 绘制截图区域
        drawCut(newDraw);
    }


    private void drawMasking(Bounds rectangle) {
        graphicsContext.clearRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        graphicsContext.setFill(masking);
        graphicsContext.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    private void drawCut(Bounds rectangle) {
        graphicsContext.clearRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        graphicsContext.setFill(Color.TRANSPARENT);
        graphicsContext.setStroke(Color.RED);
        graphicsContext.setLineWidth(1);
        graphicsContext.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        graphicsContext.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    protected void beforeDone(Pane p) {
        if (start) {
            return;
        }
        // 鼠标划选了一个区域
        if (last.getWidth() > 2 && last.getHeight() > 2) {
            // 生成需要处理区域的矩形
            cutRec.xProperty().set(last.getX() + 1);
            cutRec.yProperty().set(last.getY() + 1);
            cutRec.widthProperty().set(last.getWidth() - 2);
            cutRec.heightProperty().set(last.getHeight() - 2);
            cutRec.visibleProperty().setValue(true);
            cutRec.setCursor(Cursor.CROSSHAIR);
            cutRec.setFill(Color.TRANSPARENT);
            CanvasProperties canvasProperties = new CanvasProperties(graphicsContext, cutRec, configuration, backgroundImage, computerImage);
            // 为截图区域注册事件
            routingSnapshotCanvasEventHandler = new RoutingSnapshotCanvasEventHandler(canvasProperties, this);
            cutRec.addEventHandler(MouseEvent.ANY, routingSnapshotCanvasEventHandler);


            snapshotRegionKeyEventHandler = new SnapshotRegionKeyEventHandler(
                    canvasProperties.getScreenshotsElementConvertor()
                    , configuration
                    , canvasProperties);

            Window window = cutRec.getScene().getWindow();
            window.addEventHandler(KeyEvent.KEY_PRESSED, snapshotRegionKeyEventHandler);
            // 存放截图相关数据
            window.getProperties().put(CanvasProperties.class, canvasProperties);
        }

        ((WaitRemoveElementsHolder) (pane.getScene().getWindow().getProperties().get(WaitRemoveElementsHolder.class))).add(new EveryScreenshotWaitRemoveElement(group, pane, cutRec, routingSnapshotCanvasEventHandler, snapshotRegionKeyEventHandler, externalComponentBinders, cutRec.getScene().getWindow()));
    }
}
