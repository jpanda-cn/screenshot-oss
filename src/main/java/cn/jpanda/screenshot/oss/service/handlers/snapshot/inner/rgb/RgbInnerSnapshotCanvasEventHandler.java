package cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.rgb;

import cn.jpanda.screenshot.oss.core.shotkey.shortcut.CanvasShortcutManager;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.ShortCutExecutorHolder;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.Shortcut;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.CanvasDrawEventHandler;
import cn.jpanda.screenshot.oss.service.handlers.snapshot.inner.InnerSnapshotCanvasEventHandler;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Window;

import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2019/12/30 14:43
 */
public class RgbInnerSnapshotCanvasEventHandler extends InnerSnapshotCanvasEventHandler {
    private Group group;
    private ImageView imageView;
    private BufferedImage imageCache;
    private Rectangle cursorRectangle;
    private Text pos;
    private Text rgba;
    private Text hex;
    private final int imageSize = 130;
    private final int cursorSize = 40;
    private StringProperty posStr = new SimpleStringProperty();
    private StringProperty rgbaStr = new SimpleStringProperty();
    private StringProperty hexStr = new SimpleStringProperty();


    public RgbInnerSnapshotCanvasEventHandler(CanvasProperties canvasProperties, CanvasDrawEventHandler canvasDrawEventHandler, CanvasShortcutManager canvasShortcutManager) {
        super(canvasProperties, canvasDrawEventHandler, canvasShortcutManager);
        // 拦截外部事件
//        canvasDrawEventHandler.getPane().addEventHandler(MouseEvent.ANY, this);
        // 注册快捷键
        canvasShortcutManager.clear(CutInnerType.RGB);

        addShortCut(canvasDrawEventHandler.getPane(),
                CutInnerType.RGB
                , ShortCutExecutorHolder
                        .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(true).alt(false).addCode(KeyCode.P).description("复制当前鼠标位置").build())
                        .match(getShortcutMatch())
                        .executor(e -> setValue(posStr.getValue(), String.format("已复制(POS):%s", posStr.getValue())))
                        .build());
        addShortCut(canvasDrawEventHandler.getPane(),
                CutInnerType.RGB
                , ShortCutExecutorHolder
                        .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(true).alt(false).addCode(KeyCode.R).description("复制RGBA色值").build())
                        .match(getShortcutMatch())
                        .executor(e -> {
                            setValue(rgbaStr.getValue(), String.format("已复制(RGBA):%s", rgbaStr.getValue()));
                        })
                        .build());
        addShortCut(canvasDrawEventHandler.getPane(),
                CutInnerType.RGB
                , ShortCutExecutorHolder
                        .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(true).alt(false).addCode(KeyCode.H).description("复制HEX色值").build())
                        .match(getShortcutMatch())
                        .executor(e -> setValue(hexStr.getValue(), String.format("已复制(HEX):%s", hexStr.getValue())))
                        .build());
        addShortCut(canvasDrawEventHandler.getPane(),
                CutInnerType.RGB
                , ShortCutExecutorHolder
                        .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(true).alt(false).addCode(KeyCode.A).description("复制全部数据").build())
                        .match(getShortcutMatch())
                        .executor(e -> {
                            String copyValue = String.format("%s\n%s\n%s\n", pos.textProperty().getValue(), rgba.textProperty().getValue(), hex.textProperty().getValue());
                            setValue(copyValue, String.format("已复制:\n%s", copyValue));
                        })
                        .build());

        // 补偿： 移除图片预览图
        canvasProperties.getCutRectangle().addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            exit();
        });
    }

    public void setValue(String text, String tips) {
        if (imageView == null) {
            return;
        }
        setClipboard(text);
        tips(tips);
    }

    public void setClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(text);
        clipboard.setContent(clipboardContent);
    }

    public void tips(String tips) {
        // 获取关闭视图
        Tooltip tooltip = new Tooltip(tips);
        tooltip.show(imageView, imageView.layoutXProperty().doubleValue(), imageView.layoutYProperty().doubleValue());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(tooltip::hide);
            }
        }, 500L);
    }

    protected void enter() {
        rectangle.cursorProperty().set(Cursor.DEFAULT);
        if (imageCache == null) {
            imageCache = SwingFXUtils.fromFXImage(canvasProperties.getComputerImage(), null);
        }
        if (imageView == null) {
            imageView = new ImageView();
            imageView.fitWidthProperty().setValue(imageSize);
            imageView.fitHeightProperty().setValue(imageSize);
            // 添加十字准星
        }
        if (group == null) {
            group = new Group();
            group.setMouseTransparent(true);
            group.getChildren().addAll(imageView);
            // 添加十字准星
            addAim(imageView);
            // 添加十字准星下面的展示框
            newTextShower();
            // 处理展示
            addShower();

            // 将鼠标指针包装成矩形
            cursorRectangle = new Rectangle(cursorSize, cursorSize);
            cursorRectangle.fillProperty().set(Color.TRANSPARENT);
            cursorRectangle.setMouseTransparent(true);
            canvasProperties.getCutPane().getChildren().addAll(group, cursorRectangle);
        }
    }

    protected void exit() {
        if (group != null) {
            canvasProperties.getCutPane().getChildren().remove(group);

        }
        if (cursorRectangle != null) {
            canvasProperties.getCutPane().getChildren().remove(cursorRectangle);
        }
        imageView = null;
        group = null;
        cursorRectangle = null;
    }

    @Override
    protected void move(MouseEvent event) {
        if (!canvasProperties.getCutInnerType().equals(CutInnerType.RGB)) {
            return;
        }

        // 获取当前截图区域
        Rectangle rectangle = canvasProperties.getCutRectangle();
        if (!rectangle.contains(event.getSceneX(), event.getSceneY())) {
            exit();
            return;
        }
        enter();

        //
        double x = event.getSceneX();
        double y = event.getSceneY();
        // 更新图像
        imageView.setImage(createPreImage((int) (x), (int) (y)));

        updateCourserRectangle(event);
        UpdateImageView();
        // 获取鼠标位置的像素点
        Color color = canvasProperties.getComputerImage().getPixelReader().getColor((int) (event.getSceneX()), (int) (event.getSceneY()));

        setPos(event.getSceneX() + 20, event.getSceneY() + 20);
        setRGBA(color);
        setHex(color);
    }

    // 生成图片,绘制一个宽度3px的准星
    public WritableImage createPreImage(int x, int y) {
        final int imageSize = 20;
        int minx = 0;
        int miny = 0;
        int max = (int) (0 + canvasProperties.getComputerImage().getWidth());
        int maxy = (int) (0 + canvasProperties.getComputerImage().getHeight());
        WritableImage show = new WritableImage(imageSize, imageSize);
        PixelReader reader = canvasProperties.getComputerImage().getPixelReader();
        PixelWriter writer = show.getPixelWriter();
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                int xs = x - imageSize / 2 + i;
                int ys = y - imageSize / 2 + j;
                int argb = -16777216;

                if (
                        xs > minx
                                && ys > miny
                                && xs < max
                                && ys < maxy


                ) {
                    argb = reader.getArgb(xs, ys);
                }
                writer.setArgb(i, j, argb);
            }
        }

        return show;
    }

    private void setPos(double x, double y) {
        posStr.setValue(String.format("%d,%d", Math.round(x), Math.round(y)));
    }

    private void setRGBA(Color color) {
        rgbaStr.setValue(String.format("%d,%d,%d,%d", Math.round(color.getRed() * 255), Math.round(color.getGreen() * 255), Math.round(color.getBlue() * 255), Math.round(color.getOpacity())));
    }

    private void setHex(Color color) {
        hexStr.setValue(String.format("%s%s%s", toHexString(color.getRed()), toHexString(color.getGreen()), toHexString(color.getBlue())));
    }

    private Text newText() {
        Text text = new Text();
        text.fontProperty().set(Font.font(12));
        text.fillProperty().set(Color.WHITE);
        return text;
    }

    private String toHexString(double d) {
        return Integer.toHexString(toInteger(d));
    }

    private Integer toInteger(double d) {
        return Math.toIntExact(Math.round(d * 255));
    }

    private void bindProperty() {
        pos.textProperty().bind(Bindings.createStringBinding(() -> String.format("POS:(%s)", posStr.getValue()), posStr));
        rgba.textProperty().bind(Bindings.createStringBinding(() -> String.format("RGBA:(%s)", rgbaStr.getValue()), rgbaStr));
        hex.textProperty().bind(Bindings.createStringBinding(() -> String.format("HEX:(%s)", hexStr.getValue()), hexStr));

    }

    private void addAim(ImageView imageView) {
        Line vLine = new Line();
        vLine.startXProperty().bind(imageView.layoutXProperty().add(imageSize / 2));
        vLine.endXProperty().bind(imageView.layoutXProperty().add(imageSize / 2));

        vLine.startYProperty().bind(imageView.layoutYProperty());
        vLine.endYProperty().bind(imageView.layoutYProperty().add(imageView.fitHeightProperty()));
        vLine.strokeWidthProperty().setValue(2);

        Line hLine = new Line();
        hLine.startXProperty().bind(imageView.layoutXProperty());
        hLine.startYProperty().bind(imageView.layoutYProperty().add(imageSize / 2));
        hLine.endXProperty().bind(imageView.layoutXProperty().add(imageView.fitWidthProperty()));
        hLine.endYProperty().bind(imageView.layoutYProperty().add(imageSize / 2));
        hLine.strokeWidthProperty().setValue(2);
        group.getChildren().addAll(vLine, hLine);
    }

    private void addShower() {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(pos, rgba, hex);
        vBox.layoutXProperty().bind(imageView.layoutXProperty());
        vBox.layoutYProperty().bind(imageView.layoutYProperty().add(imageView.fitHeightProperty()));
        vBox.prefWidthProperty().bind(imageView.fitWidthProperty());
        vBox.styleProperty().set("-fx-background-color: rgba(0,0,0,0.7);");
        group.getChildren().addAll(vBox);
    }

    private void newTextShower() {
        pos = newText();
        rgba = newText();
        hex = newText();
        bindProperty();
    }


    private void updateCourserRectangle(MouseEvent event) {
        if (cursorRectangle != null) {
            cursorRectangle.xProperty().set(event.getSceneX() - (cursorSize >> 1));
            cursorRectangle.yProperty().set(event.getSceneY() - (cursorSize >> 1));
        }
    }

    private void UpdateImageView() {
        // 展示位置依次是外部右测下方，外部下右，外部下左，外部左下，外部上右，外部上左
        // 内部下右
        // 判断内部展示还是外部展示
        Window window = canvasProperties.getCutPane().getScene().getWindow();
        double y = cursorRectangle.yProperty().getValue();
        double h = cursorRectangle.heightProperty().get();
        double x = cursorRectangle.xProperty().get();
        double w = cursorRectangle.widthProperty().get();
        // 截图区域的左下角 x
        double endX = x + w;
        // 截图区域的左下角 y
        double endY = y + h;
        // 屏幕的高度
        double windowEndY = window.getY() + window.getHeight();
        // 屏幕的宽度
        double windowEndX = window.getX() + window.getWidth();

        Bounds toolBarBounds = group.layoutBoundsProperty().get();
        double toolW = toolBarBounds.getWidth();
        double toolH = toolBarBounds.getHeight();
        if (endX + toolW <= windowEndX) {
            // 右下
            imageView.layoutXProperty().set(endX);
        } else if (x - toolW >= 0) {
            imageView.layoutXProperty().set(x - toolW);
        } else if (x + toolW <= windowEndX) {
            imageView.layoutXProperty().set(x);
        } else {
            imageView.layoutXProperty().set(0);
        }

        if (endY + toolH <= windowEndY) {
            // 右下
            imageView.layoutYProperty().set(endY);
        } else if (y - toolH >= 0) {
            imageView.layoutYProperty().set(y - toolH);
        } else if (y + toolH <= windowEndY) {
            imageView.layoutYProperty().set(y);
        } else {
            imageView.layoutYProperty().set(0);
        }


    }
}
