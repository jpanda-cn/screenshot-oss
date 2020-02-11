package cn.jpanda.screenshot.oss.service.handlers.snapshot;

import cn.jpanda.screenshot.oss.common.toolkit.Bounds;
import cn.jpanda.screenshot.oss.common.toolkit.ExternalComponentBinders;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.CanvasShortcutManager;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.ShortCutExecutorHolder;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.ShortcutMatch;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.snapshot.EveryScreenshotWaitRemoveElement;
import cn.jpanda.screenshot.oss.view.snapshot.WaitRemoveElementsHolder;
import cn.jpanda.screenshot.oss.view.tray.CanvasCutTrayView;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import lombok.Getter;
import lombok.Setter;

import static cn.jpanda.screenshot.oss.common.utils.MathUtils.*;

@Getter
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
    private ExternalComponentBinders externalComponentBinders;
    private boolean start = true;
    private SnapshotRegionKeyEventRegister snapshotRegionKeyEventRegister;
    private final int cursorSize = 40;
    // 快捷键注册器
    public static final String GLOBAL_FLAG = "GLOBAL_FLAG";
    @Setter
    protected ShortcutMatch shortcutMatch;
    @Setter
    protected CanvasShortcutManager canvasShortcutManager;

    private WritableImage backgroundImage;
    private WritableImage computerImage;

    /**
     * 展示截图区域大小
     */
    private Tooltip size;

    public CanvasDrawEventHandler(Paint masking, GraphicsContext graphicsContext, Configuration configuration, WritableImage writableImage, WritableImage computerImage) {
        // 蒙版清晰度
        this.masking = masking;
        // 背景图
        this.backgroundImage = writableImage;
        // 计算使用的图，禁止修改
        this.computerImage = computerImage;
        // Canvas绘图
        this.graphicsContext = graphicsContext;
        // Canvas所属容器
        pane = ((Pane) (graphicsContext.getCanvas().getParent()));

        this.configuration = configuration;

        // 加载工具托盘
        Scene scene = configuration.getViewContext().getScene(CanvasCutTrayView.class, false, true, false);
        toolbar = scene.getRoot();

        // 截图区域快捷键管理器
        canvasShortcutManager = getCanvasShortcutManager();

        shortcutMatch = getShortcutMatch();
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
            if (size != null) {
                size.hide();
            }
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

        // 每次点击都要清理掉原有的工具栏
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

        cutRec.setCursor(Cursor.CROSSHAIR);
        cutRec.setFill(Color.TRANSPARENT);
        // 整个截图区域共享的数据
        CanvasProperties canvasProperties = new CanvasProperties(graphicsContext, cutRec, configuration, backgroundImage, computerImage);

        // 为截图区域重新注册事件
        routingSnapshotCanvasEventHandler = new RoutingSnapshotCanvasEventHandler(canvasProperties, this, canvasShortcutManager);
        cutRec.addEventHandler(MouseEvent.ANY, routingSnapshotCanvasEventHandler);


        // 获取当前窗口
        Window window = pane.getScene().getWindow();
        // 修复重新绘制截图区域为截图窗口多次注册截图快捷键的问题
        if (snapshotRegionKeyEventRegister == null) {
            // 获取截图区域窗口，批量注册快捷键
            snapshotRegionKeyEventRegister = new SnapshotRegionKeyEventRegister(
                    window
                    , configuration
                    , canvasProperties
                    , canvasShortcutManager
            );
            snapshotRegionKeyEventRegister.registry();
        } else {
            snapshotRegionKeyEventRegister.updateCanvasProperties(canvasProperties);
        }
        if (size != null) {
            size.hide();
        }
        size = new Tooltip("0".concat(" * ".concat("0")));
        size.getScene().setFill(Color.TRANSPARENT);
        size.show(pane, startX, startY);

        // 存放截图相关数据
        window.getProperties().put(CanvasProperties.class, canvasProperties);

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
        updateImageView(bounds);
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
        // 绘制完成
        // 鼠标划选了一个区域
        if (last.getWidth() > 2 && last.getHeight() > 2) {
            // 生成需要处理区域的矩形
            cutRec.xProperty().set(last.getX() + 1);
            cutRec.yProperty().set(last.getY() + 1);
            cutRec.widthProperty().set(last.getWidth() - 2);
            cutRec.heightProperty().set(last.getHeight() - 2);
            cutRec.visibleProperty().setValue(true);
        }

        ((WaitRemoveElementsHolder) (pane.getScene().getWindow().getProperties().get(WaitRemoveElementsHolder.class))).add(new EveryScreenshotWaitRemoveElement(group, pane, cutRec, routingSnapshotCanvasEventHandler, externalComponentBinders, cutRec.getScene().getWindow()));
    }

    protected CanvasShortcutManager getCanvasShortcutManager() {
        return configuration.getUniquePropertiesHolder(CanvasShortcutManager.class);

    }

    protected ShortcutMatch getShortcutMatch() {
        return configuration.getUniquePropertiesHolder(ShortcutMatch.class);
    }

    protected void addShortCut(EventTarget target, Object type, ShortCutExecutorHolder holder) {
        canvasShortcutManager.add(target, type, holder);
    }

    protected void addCurrent(EventTarget target, ShortCutExecutorHolder holder) {
        addShortCut(target, configuration.getUniquePropertiesHolder(CutInnerType.class, null), holder);
    }

    protected void addGlobal(EventTarget target, ShortCutExecutorHolder holder) {
        addShortCut(target, null, holder);
    }

    private void updateImageView(Bounds bounds) {
        size.setText(("" + bounds.getWidth()).concat(" * ").concat(("" + bounds.getHeight())));
        // 展示位置依次是外部右测下方，外部下右，外部下左，外部左下，外部上右，外部上左
        // 内部下右
        // 判断内部展示还是外部展示


        Window window = pane.getScene().getWindow();
        // 屏幕的高度
        double windowEndY = window.getY() + window.getHeight();
        // 屏幕的宽度
        double windowEndX = window.getX() + window.getWidth();

        javafx.geometry.Bounds toolBarBounds = group.layoutBoundsProperty().get();
        double toolW = toolBarBounds.getWidth();
        double toolH = toolBarBounds.getHeight();

        // 左上
        double sw = size.getWidth();
        double sh = size.getHeight();

        // 鼠标长度
        double cl = cursorSize / 4;
        // 尽可能在正上方，当x距离屏幕尾端不够展示时，进行缩减
        // double ax = bounds.getX() + cl >= sw ? bounds.getX() + cl - sw : bounds.getX() - cl;

        double ax = windowEndX - cl >= sw ? bounds.getX() - cl : windowEndX - cl;
        double ay = bounds.getY() + cl >= sh ? bounds.getY() + cl - sh : bounds.getY() - cl;
        size.setX(ax);
        size.setY(ay);

    }
}
