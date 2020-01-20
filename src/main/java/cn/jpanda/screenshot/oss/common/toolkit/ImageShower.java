package cn.jpanda.screenshot.oss.common.toolkit;

import cn.jpanda.screenshot.oss.common.utils.MathUtils;
import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ConfigurationHolder;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.*;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.view.main.SettingsView;
import cn.jpanda.screenshot.oss.view.tray.subs.ResizeEventHandler;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.collections.ObservableSequentialListWrapper;
import com.sun.javafx.collections.TrackableObservableList;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.*;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/1/3 15:39
 */
@Getter
public class ImageShower extends Stage {
    private double stroke = 5;

    @Getter
    private SimpleStringProperty stylesheets = new SimpleStringProperty("/css/image-shower.css");

    private VBox box;
    private HBox top;
    private AnchorPane body;
    private Rectangle rect;
    private Image image;
    private TextField topTitle;

    /**
     * 快捷键注册表
     */
    private List<ShortCutExecutorHolder> shortCutExecutorHolders = new ArrayList<>();

    private KeyboardShortcutsManager keyboardShortcutsManager = getKeyboardShortcutsManager();

    private ShortcutMatch shortcutMatch = getShortcutMatch();

    {
        stylesheets.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Arrays.asList(body, top, body).forEach(c -> {
                    c.getStylesheets().remove(oldValue);
                    c.getStylesheets().add(newValue);
                });
            }
        });
    }

    public ImageShower setTopTitle(String message) {

        if (StringUtils.isNotEmpty(message)) {
            topTitle.textProperty().set(message);
        }
        return this;
    }

    public ImageShower stylesheets(String stylesheets) {
        this.stylesheets.set(stylesheets);
        return this;
    }

    public ImageShower(Stage stage) {
        this();
        initOwner(stage);
    }

    public ImageShower() {
        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.NONE);
        init();
    }

    public static ImageShower of(Stage stage) {
        return new ImageShower(stage);
    }

    public static ImageShower of() {
        return new ImageShower();
    }

    public void show(Image image) {
        load(image).show();
    }

    public ImageShower load(Image image) {
        init(image);
        return this;
    }

    public void init() {
        top = buildTop();
        body = new AnchorPane();
        body.styleProperty().set(" -fx-background-color: rgba(234,123,123,0.7);");
        box = new VBox(top, body);
        box.styleProperty().set(" -fx-background-color: transparent;");
        top.prefWidthProperty().bind(body.widthProperty());
        top.addEventHandler(MouseEvent.ANY, addDrag(top));
        rect = new Rectangle();

        Arrays.asList(body, top, body).forEach(c -> {
            c.getStylesheets().add(stylesheets.get());
        });
    }

    public void init(Image image) {
        this.image = image;
        loadImage(image);
        body.getChildren().add(rect);

        body.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() == 0) {
                return;
            }
            rect.widthProperty().set(newValue.doubleValue() - ((rect.strokeWidthProperty().multiply(2)).get()));
        });

        body.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() == 0) {
                return;
            }
            rect.heightProperty().set(box.heightProperty().subtract(top.heightProperty()).subtract(rect.strokeWidthProperty().multiply(2)).getValue());

        });

        EventHandler<MouseEvent> resize = new ResizeEventHandler(this, body, rect, Collections.singletonList(top));
        EventHandler<MouseEvent> drag = addDrag(body);
        rect.addEventHandler(MouseEvent.ANY, stageHandler(rect, resize, drag));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem close = new MenuItem("关闭");
        MenuItem onTop = new MenuItem("置顶");
        alwaysOnTopProperty().addListener((observable, oldValue, newValue) -> onTop.setText(newValue ? "取消置顶" : "置顶"));
        onTop.setOnAction(e -> setAlwaysOnTop(alwaysOnTopProperty().not().getValue()));

        close.setOnAction(e -> doDelete(body));
        MenuItem hideTop = new MenuItem("隐藏标题");

        MenuItem hideBorder = new MenuItem("隐藏边框");
        AtomicBoolean isHide = new AtomicBoolean(true);
        hideTop.setOnAction(e -> {
            if (isHide.get()) {
                hideTop.setUserData(top.heightProperty().getValue());
                top.visibleProperty().setValue(false);
                top.setPrefHeight(0);
                hideTop.setText("显示标题");
                isHide.set(false);
                return;
            }
            top.visibleProperty().setValue(true);
            top.setPrefHeight((Double) hideTop.getUserData());
            isHide.set(true);
            hideTop.setText("隐藏标题");
        });
        AtomicBoolean isHideBorder = new AtomicBoolean(true);
        hideBorder.setOnAction(e -> {
            if (isHideBorder.get()) {
                double w = rect.strokeWidthProperty().getValue();
                hideBorder.setUserData(w);
                rect.layoutXProperty().set(0);
                rect.layoutYProperty().set(0);
                this.setWidth(getWidth() - w * 2);
                this.setHeight(getHeight() - w * 2);
                rect.strokeWidthProperty().set(0);
                isHideBorder.set(false);
                hideBorder.setText("展示边框");
                return;
            }
            rect.strokeWidthProperty().set((Double) hideBorder.getUserData());
            double w = rect.strokeWidthProperty().getValue();
            rect.layoutXProperty().set(w);
            rect.layoutYProperty().set(w);
            this.setWidth(getWidth() + w * 2);
            this.setHeight(getHeight() + w * 2);
            isHideBorder.set(true);
            hideBorder.setText("隐藏边框");
        });

        MenuItem saveOther = new MenuItem("图像另存为");
        MenuItem sceneSaveOther = new MenuItem("窗口图像另存为");

        MenuItem copyItem = new MenuItem("复制图像到剪切板(Ctrl+C)");
        MenuItem copyFullItem = new MenuItem("复制当前窗口到剪切板(Ctrl+Alt+C)");
        saveOther.setOnAction((e) -> {
            saveAndChooseFile(image);
        });
        sceneSaveOther.setOnAction((e) -> {
            saveAndChooseFile(this.getScene().snapshot(null));
        });
        copyItem.setOnAction((e) -> {
            saveAndShowTips(image, "图片已复制", rect);
        });
        copyFullItem.setOnAction((e) -> {
            saveAndShowTips(this.getScene().snapshot(null), "窗口已复制", rect);
        });

        MenuItem cImageToUpload = new MenuItem("上传当前图片到云环境");
        MenuItem cFrameToUpload = new MenuItem("上传当前窗口快照到云环境");
        cImageToUpload.setOnAction((e) -> {
            doSave(image);
        });
        cFrameToUpload.setOnAction((e) -> {
            doSave(this.getScene().snapshot(null));
        });
        contextMenu.getItems().addAll(close, onTop, hideTop, hideBorder, saveOther, sceneSaveOther, copyItem, copyFullItem, cImageToUpload, cFrameToUpload);

        body.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                contextMenu.show(body, e.getScreenX(), e.getScreenY() + 10);
            }
        });

        body.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (contextMenu.showingProperty().getValue()) {
                contextMenu.hide();
            }
        });
//        box.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
//            if (e.isControlDown() && e.getCode().equals(KeyCode.C)) {
//                if (e.isShiftDown() || e.isMetaDown()) {
//                    return;
//                }
//                if (e.isAltDown()) {
//                    saveAndShowTips(this.getScene().snapshot(null), "窗口已复制", rect);
//                } else {
//                    saveAndShowTips(image, "图片已复制", rect);
//                }
//            }
//        });

        addShortCut(
                box
                ,ShortCutExecutorHolder
                    .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(true).addCode(KeyCode.C).description("复制当前图片").build())
                        .match(shortcutMatch)
                        .executor(event -> saveAndShowTips(image, "图片已复制", rect))
                    .build()
        );

        addShortCut(
                box
                ,ShortCutExecutorHolder
                        .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(true).alt(true).addCode(KeyCode.C).description("复制当前窗口").build())
                        .match(shortcutMatch)
                        .executor(event -> saveAndShowTips(image, "图片已复制", rect))
                        .build()
        );

        // 展示当前所有快捷键
        addShortCut(
                box
                ,ShortCutExecutorHolder
                        .builder()
                        .shortcut(Shortcut.Builder.create().ctrl(false).alt(false).addCode(KeyCode.SLASH).description("展示快捷键列表").build())
                        .match(shortcutMatch)
                        .executor(event -> ShortcutHelperShower.show(shortCutExecutorHolders,this).show())
                        .build()
        );

        Scene sc = new Scene(box);
        sc.setFill(Color.TRANSPARENT);

        setScene(sc);

        setAlwaysOnTop(true);

    }

    private void addShortCut(EventTarget target, ShortCutExecutorHolder holder) {
        keyboardShortcutsManager.registryShortCut(target, holder);
        shortCutExecutorHolders.add(holder);
    }

    @SneakyThrows
    private void saveAndChooseFile(Image image) {
        // 获取当前地址
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("请选择图片保存地址");
        File file = directoryChooser.showDialog(this);
        if (file == null) {
            return;
        }
        String newPath = file.getAbsolutePath();
        if (StringUtils.isEmpty(newPath)) {
            return;
        }
        // 获取标题
        String title = topTitle.textProperty().getValue();
        if (StringUtils.isEmpty(title)) {
            title = UUID.randomUUID().toString();
        }
        String fileName = title.concat(".png");
        File pngFile = Paths.get(newPath, fileName).toFile();
        while (pngFile.exists()) {
            fileName = UUID.randomUUID().toString().concat(fileName);
            pngFile = Paths.get(newPath, fileName).toFile();
        }
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", pngFile);
        tips("保存成功", body);
    }

    private void saveAndShowTips(Image image, String tips, Rectangle node) {
        setClipboard(image);
        tips(tips, node);
    }

    private void setClipboard(Image image) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putImage(image);
        clipboard.setContent(clipboardContent);
    }

    private void tips(String tips, Node node) {
        // 获取关闭视图
        Tooltip tooltip = new Tooltip(tips);
        tooltip.show(this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(tooltip::hide);
            }
        }, 500L);
    }

    protected void loadImage(Image image) {
        ImagePattern imagePattern = new ImagePattern(image);
        rect.widthProperty().set(image.getWidth() + stroke * 2);
        rect.heightProperty().set(image.getHeight() + stroke * 2);
        rect.setLayoutX(stroke);
        rect.setLayoutY(stroke);
        rect.setFill(imagePattern);
        rect.strokeWidthProperty().set(stroke);
        rect.strokeProperty().set(Color.valueOf("#cfcfcf"));
        rect.strokeTypeProperty().set(StrokeType.OUTSIDE);
    }

    public HBox buildTop() {
        Button button = drawingPin();
        HBox top = new HBox();
        String cssLayout = "-fx-border-color: cfcfcf;\n" +
                "-fx-border-width: " + 3 + ";\n" +
                "-fx-border-style: inset;\n" +
                " -fx-background-color:#f4f4f4;";
        top.styleProperty().set(cssLayout);
        top.addEventHandler(MouseEvent.ANY, addDrag(top));
        topTitle = new TextField();
        topTitle.getStyleClass().add("top-title");
        topTitle.styleProperty().set("-fx-background-color: transparent;-fx-text-fill: BLACK; -fx-font-size: 16px;");
        Button close = drawingClose();
        close.setCursor(Cursor.DEFAULT);


        close.setOnMouseClicked(event -> {
            doDelete(close);

        });
        HBox.setHgrow(topTitle, Priority.ALWAYS);
        top.getChildren().addAll(button, topTitle, close);
        return top;
    }

    public void doDelete(Node node) {
        AnchorPane center = new AnchorPane();
        Label label = new Label("确认删除吗？");
        label.setFont(Font.font(11));
        center.getChildren().addAll(label);
        Tooltip tooltip = new Tooltip();
        VBox box = new VBox();
        box.getStylesheets().add(stylesheets.get());
        box.setSpacing(10);
        box.getChildren().add(center);
        Button ok = new Button("确认");
        ok.fontProperty().set(Font.font(11));
        ok.getStyleClass().addAll("tool-tip-ok");

        Button cancel = new Button("取消");
        cancel.fontProperty().set(Font.font(11));
        cancel.getStyleClass().addAll("tool-tip-cancel");
        ok.setOnMouseClicked((e) -> {
            close();
        });
        cancel.setOnMouseClicked((e) -> {
            tooltip.hide();
        });


        HBox h = new HBox();
        h.setAlignment(Pos.CENTER_RIGHT);
        h.getChildren().addAll(ok, cancel);
        box.getChildren().addAll(h);

        tooltip.setGraphic(box);
        this.box.disableProperty().bind(tooltip.showingProperty());
        tooltip.show(node, this.getX() + this.getWidth(), this.getY());
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

    public Button drawingPin() {

        Group svg = new Group(
                createPath("M864.3584 421.2736a46.08 46.08 0 1 1 41.4208-82.3296c26.5216 13.3632 51.6608 29.5424 75.2128 48.5376a81.92 81.92 0 0 1 6.3488 121.3952l-206.4896 206.4896 9.1648 9.1648c150.272 149.1456 213.7088 212.6336 219.0848 219.4944 1.6384 2.2016 1.6384 2.2016-1.024 57.9584l-61.3376 8.3968c-2.4064-1.6896-2.4064-1.6896-4.096-3.1232l-1.7408-1.536-1.1776-1.1776-3.072-3.072-11.9808-11.8784-48.0768-48.0256a343675.648 343675.648 0 0 1-160.768-160.8704l-205.6192 207.2064c-15.616 15.5648-36.352 24.0128-57.856 24.0128-24.3712 0-47.616-10.8032-63.5904-30.208a421.888 421.888 0 0 1-80.5376-373.0432L159.4368 421.0176a245.6576 245.6576 0 0 1-117.7088-39.168 80.896 80.896 0 0 1-12.8-124.928L257.536 28.7744a80.8448 80.8448 0 0 1 125.0304 13.056c22.8864 35.328 36.096 75.6224 38.8608 117.1456l187.6992 148.8384a423.2192 423.2192 0 0 1 107.1616-13.824 46.08 46.08 0 1 1 0 92.16c-34.816 0-69.4784 5.5296-102.8096 16.384l-23.552 7.68-260.9152-206.8992 0.7168-23.1424c0.8192-26.5216-5.12-52.736-17.3056-75.9296L104.3456 311.808a153.8048 153.8048 0 0 0 75.3664 17.4592l23.7056-1.1776 207.2576 261.376-7.68 23.552a329.8816 329.8816 0 0 0 50.176 301.4656l197.5296-199.0656 32.2048-32.4608 32.3584-32.6656 198.656-198.3488a327.424 327.424 0 0 0-49.5616-30.72z")
        );

        Group checkedSvg = new Group(
                createPath("M751.658359 439.429142c-28.742944-20.31996-61.917879-37.100928-97.45181-48.652905l-2.618995-190.043629c12.707975-6.362988 23.487954-12.883975 33.704934-20.144961 48.997904-34.780932 76.368851-82.416839 76.368851-133.781738 0-25.83895-24.140953-46.805909-53.929894-46.805909H316.253209c-29.788942 0-53.929895 20.966959-53.929894 46.805909 0 51.3639 27.407946 99.055807 77.165849 134.306737 9.437982 6.736987 20.218961 13.256974 31.483938 18.957963l1.441998 189.968629c-38.152925 12.287976-71.328861 29.068943-101.237803 50.169902C204.646427 487.415048 167.3645 552.011922 167.3645 621.297787c0 25.83895 24.140953 46.805909 53.929895 46.805908h236.777537v309.089396c0 25.83895 24.140953 46.805909 53.929895 46.805909s54.021894-20.947959 54.021894-46.805909V668.122695h236.684538c29.788942 0 53.929895-20.947959 53.929895-46.805909 0-69.321865-37.281927-133.919738-104.979795-181.869644z")
        );
        Bounds checkedSvgBounds = svg.getBoundsInParent();
        double checkedSvgScale = Math.min(20 / checkedSvgBounds.getWidth(), 20 / checkedSvgBounds.getHeight());
        checkedSvg.setScaleX(checkedSvgScale);
        checkedSvg.setScaleY(checkedSvgScale);

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(20 / bounds.getWidth(), 20 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        Button btn = new Button();

        btn.setGraphic(checkedSvg);
        btn.setMaxSize(30, 30);
        btn.setMinSize(30, 30);
        btn.setLayoutX(0);
        btn.setLayoutY(0);
        btn.getStyleClass().add("drawing-pin");
        alwaysOnTopProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    btn.setGraphic(checkedSvg);
                } else {
                    btn.setGraphic(svg);
                }
            }
        });
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setAlwaysOnTop(!alwaysOnTopProperty().getValue());
            }
        });


        return btn;

    }

    public Button drawingClose() {
        Group svg = new Group(
                createPath("M212.992 526.336 212.992 526.336 212.992 526.336 215.04 526.336 212.992 526.336Z    M233.472 346.112 233.472 346.112l542.72 0 0 0 49.152 0 0-90.112L182.272 256l0 90.112L233.472 346.112 233.472 346.112 233.472 346.112zM348.16 73.728 348.16 73.728 348.16 73.728l311.296 0c18.432 0 34.816 14.336 34.816 32.768l0 0 0 79.872 165.888 0c18.432 0 34.816 14.336 34.816 32.768l0 0 0 157.696c0 18.432-14.336 32.768-34.816 32.768l0 0-49.152 0 0 499.712c0 18.432-14.336 32.768-34.816 32.768l0 0L233.472 942.08c-18.432 0-32.768-14.336-32.768-32.768l0 0L200.704 413.696 149.504 413.696c-18.432 0-32.768-14.336-32.768-32.768l0 0 0-157.696c0-18.432 14.336-32.768 32.768-32.768l0 0 163.84 0 0-81.92C315.392 88.064 329.728 73.728 348.16 73.728L348.16 73.728zM626.688 139.264 626.688 139.264 382.976 139.264l0 43.008 243.712 0L626.688 139.264 626.688 139.264zM385.024 413.696 385.024 413.696l0 389.12c0 10.24-10.24 20.48-20.48 20.48-10.24 0-20.48-8.192-20.48-20.48l0-389.12L266.24 413.696l0 464.896 475.136 0L741.376 413.696l-77.824 0 0 389.12c0 10.24-8.192 20.48-20.48 20.48-10.24 0-20.48-8.192-20.48-20.48l0-389.12-100.352 0 0 389.12c0 10.24-8.192 20.48-20.48 20.48-12.288 0-20.48-8.192-20.48-20.48l0-389.12L385.024 413.696 385.024 413.696z")
        );

        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(20 / bounds.getWidth(), 20 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);

        Button btn = new Button();
        btn.setGraphic(svg);
        btn.setMaxSize(30, 30);
        btn.setMinSize(30, 30);
        btn.setLayoutX(0);
        btn.setLayoutY(0);
        btn.getStyleClass().add("close-button");
        return btn;
    }

    private static SVGPath createPath(String d) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg");
        path.setContent(d);
        return path;
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

    public void doSave(Image image) {
        Configuration configuration = ConfigurationHolder.getInstance().getConfiguration();
        if (configuration == null) {
            return;
        }
        // 执行销毁操作
        DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
        destroyGroupBeanHolder.destroy();

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

        SimpleStringProperty imageProperty = configuration.getUniquePropertiesHolder(GlobalConfigPersistence.class.getCanonicalName() + "-" + "image-save");
        SimpleStringProperty cliProperty = configuration.getUniquePropertiesHolder(GlobalConfigPersistence.class.getCanonicalName() + "-" + "clipboard-save");
        storeWay.textProperty().bind(Bindings.createStringBinding(() -> String.format("存储方式:【%s】", imageProperty.get()), imageProperty));
        clipboardContent.textProperty().bind(Bindings.createStringBinding(() -> String.format("剪切板内容：【%s】", cliProperty.get()), cliProperty));


        body.getChildren().addAll(storeWay, clipboardContent);

        PopDialog.create()
                .setHeader("上传图片")
                .setContent(body)
                .bindParent(this)
                .buttonTypes(ButtonType.CANCEL, change, upload)
                .addButtonClass(change, "button-next")
                .callback(new Callable<Boolean, ButtonType>() {
                    @Override
                    public Boolean apply(ButtonType buttonType) {
                        if (upload.equals(buttonType)) {
                            // 上传
                            toUpload(configuration, body.getScene().getWindow(), SwingFXUtils.fromFXImage(image, null));
                        } else if (change.equals(buttonType)) {
                            // 变更设置
                            showConfig(configuration, body.getScene().getWindow());
                            return false;
                        } else if (ButtonType.CANCEL.equals(buttonType)) {

                        }
                        return true;
                    }
                })
                .show();

    }

    public void showConfig(Configuration configuration, Window window) {
        // 截图配置窗口
        Scene setting = configuration.getViewContext().getScene(SettingsView.class, true, false);
        PopDialog
                .create()
                .setHeader("设置")
                .setContent(setting.getRoot())
                .buttonTypes(ButtonType.CLOSE)
                .bindParent(window)
                .showAndWait();
    }

    public void toUpload(Configuration configuration, Window window, BufferedImage image) {
        ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
        screenshotsProcess.done(window, image);
    }

    protected KeyboardShortcutsManager getKeyboardShortcutsManager() {
        return new KeyboardShortcutsManager();
    }

    protected ShortcutMatch getShortcutMatch() {
        return new SimpleShortcutMatch();
    }
}
