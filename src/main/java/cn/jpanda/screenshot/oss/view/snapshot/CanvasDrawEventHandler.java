package cn.jpanda.screenshot.oss.view.snapshot;

import cn.jpanda.screenshot.oss.view.tray.CanvasCutTrayView;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import static cn.jpanda.screenshot.oss.core.BootStrap.configuration;


public class CanvasDrawEventHandler implements EventHandler<MouseEvent> {
    private Paint masking;
    private double startX;
    private double startY;
    private DrawRectangle last;
    private GraphicsContext graphicsContext;
    private Parent toolbar;
    private Pane pane;
    private Rectangle cutRec;

    public CanvasDrawEventHandler(Paint masking, GraphicsContext graphicsContext) {
        this.masking = masking;
        this.graphicsContext = graphicsContext;
        pane = ((Pane) (graphicsContext.getCanvas().getParent()));
        Scene scene = configuration.getViewContext().getScene(CanvasCutTrayView.class, true, false);
        toolbar = scene.getRoot();
        cutRec = new Rectangle(0, 0, 0, 0);
    }

    @Override
    public void handle(MouseEvent event) {

        if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
            initParam(event);
        } else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
            draw(event);
        } else if (MouseEvent.MOUSE_RELEASED.equals(event.getEventType())) {
            // 生成一个截图区域
            // 这个区域下方生成一个工具组
            beforeDone(pane);
        }
    }

    protected void initParam(MouseEvent event) {
        pane.getChildren().remove(toolbar);
        startX = event.getScreenX();
        startY = event.getScreenY();
        drawMasking(new DrawRectangle(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight()));
        last = new DrawRectangle(startX, startY, 0, 0);
        cutRec.xProperty().set(last.getX());
        cutRec.yProperty().set(last.getY());
        cutRec.widthProperty().set(last.getWidth());
        cutRec.heightProperty().set(last.getHeight());
        cutRec.visibleProperty().setValue(true);
        cutRec.visibleProperty().setValue(false);
        // 每次点击都要清理到原有的工具栏
        toolbar.visibleProperty().bind(cutRec.visibleProperty());
    }

    protected void beforeDone(Pane p) {
        if (last.getWidth() > 0 && last.getHeight() > 0) {
            Group group = new Group();
            p.getChildren().add(group);

            // 生成需要处理区域的矩形
            cutRec.xProperty().set(last.getX());
            cutRec.yProperty().set(last.getY());
            cutRec.widthProperty().set(last.getWidth());
            cutRec.heightProperty().set(last.getHeight());
            cutRec.visibleProperty().setValue(true);
            cutRec.setCursor(Cursor.CROSSHAIR);
            cutRec.setFill(Color.rgb(0, 0, 0, 0));

            CanvasProperties canvasProperties = new CanvasProperties(graphicsContext, cutRec);
            // 默认注册拖动事件
            cutRec.addEventHandler(Event.ANY, new DragSizeSnapshotCanvasEventHandler(canvasProperties, this));

            // 设置工具位置
            // 绑定数据
            toolbar.visibleProperty().bind(cutRec.visibleProperty());
            toolbar.layoutXProperty().bind(cutRec.xProperty());
            toolbar.layoutYProperty().bind(Bindings.add(cutRec.yProperty(), cutRec.heightProperty()));
            group.getChildren().addAll(cutRec, toolbar);
            // 存放截图相关数据
            group.getScene().getWindow().getProperties().put(CanvasProperties.class, canvasProperties);
        }
    }

    protected void draw(MouseEvent event) {
        // 判断需要新增和移除的矩形区域
        double x = event.getScreenX();
        double y = event.getScreenY();
        double currentX = min(startX, x);
        double currentY = min(startY, y);
        double width = subAbs(x, startX);
        double height = subAbs(y, startY);
        // 计算需要进行绘制的区域
        DrawRectangle newDraw = new DrawRectangle(currentX, currentY, width, height);
        draw(newDraw);
    }

    public void draw(DrawRectangle drawRectangle) {
        doDraw(drawRectangle);
        last = drawRectangle;
    }

    private void doDraw(DrawRectangle newDraw) {
        drawMasking(new DrawRectangle(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight()));
        drawCut(newDraw);
    }


    private void drawMasking(DrawRectangle rectangle) {
        graphicsContext.clearRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        graphicsContext.setFill(masking);
        graphicsContext.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    private void drawCut(DrawRectangle rectangle) {
        graphicsContext.clearRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        graphicsContext.setFill(Color.TRANSPARENT);
        graphicsContext.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        graphicsContext.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    private double subAbs(double v1, double v2) {
        return v1 > v2 ? v1 - v2 : v2 - v1;
    }

    private double min(double v1, double v2) {
        return v1 > v2 ? v2 : v1;
    }

    private double max(double v1, double v2) {
        return v1 < v2 ? v2 : v1;
    }
}
