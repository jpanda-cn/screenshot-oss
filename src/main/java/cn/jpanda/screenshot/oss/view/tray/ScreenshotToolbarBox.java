package cn.jpanda.screenshot.oss.view.tray;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.EventHelper;
import cn.jpanda.screenshot.oss.common.toolkit.ImageShower;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.ScreenshotsProcess;
import cn.jpanda.screenshot.oss.core.destroy.DestroyGroupBeanHolder;
import cn.jpanda.screenshot.oss.core.imageshower.ImageShowerManager;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.KeyboardShortcutsManager;
import cn.jpanda.screenshot.oss.core.shotkey.shortcut.ShortCutExecutorHolder;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallback;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.clipboard.instances.ImageClipboardCallback;
import cn.jpanda.screenshot.oss.view.main.SettingsView;
import cn.jpanda.screenshot.oss.view.snapshot.CanvasProperties;
import cn.jpanda.screenshot.oss.view.tray.toolkits.CutInnerType;
import cn.jpanda.screenshot.oss.view.tray.toolkits.TrayConfig;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.Getter;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 截图工具栏
 */
public class ScreenshotToolbarBox extends VBox {
    /**
     * 被选中元素的样式
     */
    private static final String SELECT_BUTTON_CLASS = "tool-bar-button-selected";
    private static final String SELECT_OPTIONS_BUTTON_CLASS = "options-button-selected";
    private static final String SELECT_OPTIONS_BUTTON_DOT__CLASS = "options-dot-button-selected";
    /**
     * 当前被选中的按钮
     */
    private final ObjectProperty<Button> selectButtonProperty = new SimpleObjectProperty<>();

    private final Map<Object, Node> optionsSelectedNodeProperty = new HashMap<>();
    @Getter
    private final ObjectProperty<Long> flushProperty = new SimpleObjectProperty<>(0L);

    private final ObjectProperty<CutInnerType> currentCutInnerType = new SimpleObjectProperty<>(CutInnerType.DRAG);

    private Map<CutInnerType, Button> innerTypeButtonMap = new HashMap<>();

    {
        selectButtonProperty.addListener((observable, oldValue, newValue) -> {
            Optional.ofNullable(oldValue).ifPresent(b -> {
                b.getStyleClass().remove(SELECT_BUTTON_CLASS);
            });
            newValue.getStyleClass().add(SELECT_BUTTON_CLASS);
        });
    }


    /**
     * 全局配置对象
     */
    private Configuration configuration;

    private volatile CanvasProperties canvasProperties;

    public ScreenshotToolbarBox(Configuration configuration) {
        this.configuration = configuration;

        init();
    }


    /**
     * 默认按钮颜色
     */
    private ObjectProperty<Color> buttonColor = new SimpleObjectProperty<>(Color.WHITE);

    private KeyboardShortcutsManager keyboardShortcutsManager;

    // 拖拽
    private Button drag;
    // 圆形
    private Button roundness;
    // 矩形
    private Button rectangle;
    // 箭头
    private Button arrow;
    // 画笔
    private Button pen;
    // 文字
    private Button text;
    // 马赛克
    private Button mosaic;
    // 取色器
    private Button rgb;
    // 图钉
    private Button drawingPin;
    // 设置
    private Button settings;
    // 上传
    private Button upload;
    // 关闭
    private Button cancel;
    // 保存
    private Button submit;

    // 按钮盒子
    private HBox buttonsBox;
    // 选项盒子
    private HBox optionsBox;

    private void initCanvasProperties() {
        canvasProperties = (CanvasProperties) this.getScene().getWindow().getProperties().get(CanvasProperties.class);
    }

    public void init() {
        // 加载CSS
        loadCss();
        // 加载按钮容器
        loadButtonsBox();
        // 加载选项容器
        loadOptionsBox();

        loadButtons();

    }

    private void loadCss() {
        getStylesheets().add("/css/screenshotToolbarBox.css");
    }

    private void loadButtons() {
        // 拖拽
        createDrag();
        // 圆形
        createRoundness();
        // 矩形
        createRectangle();
        // 箭头
        createArrow();
        // 画笔
        createPen();
        // 文本
        createText();
        // 马赛克
        createMosaic();
        // 取色器
        createColorPicker();
        // 图钉
        createDrawingPin();
        // 设置
        createSettings();
        // 上传
        createUpload();
        // 关闭
        createClose();
        // 保存
        createSave();

    }

    private void createSave() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.SAVE))
                .tooltip(new Tooltip("保存"))
                .actionEventEventHandler(event -> {
                    doDone();
                })
                .registry(this);
    }

    private void createClose() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.CLOSE))
                .tooltip(new Tooltip("关闭"))
                .actionEventEventHandler(event -> {
                    doCancel();
                })
                .registry(this);
    }

    private void createUpload() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.UPLOAD))
                .tooltip(new Tooltip("上传"))
                .actionEventEventHandler(event -> {
                    doSave();
                })
                .registry(this);
    }

    private void createSettings() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.SETTING))
                .tooltip(new Tooltip("设置"))
                .actionEventEventHandler(event -> {
                    showConfig();
                })
                .registry(this);
    }

    private void createDrawingPin() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.DRAWING_PIN))
                .tooltip(new Tooltip("图钉"))
                .actionEventEventHandler(event -> {
                    doDrawingPin();
                })
                .registry(this);
    }


    private void createColorPicker() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.COLOR_PICKER))
                .tooltip(new Tooltip("取色器"))
                .type(CutInnerType.RGB)
                .actionEventEventHandler(event -> {
                })
                .registry(this);
    }

    private void createMosaic() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.MOSAIC))
                .tooltip(new Tooltip("马赛克"))
                .type(CutInnerType.MOSAIC)
                .addOption(createMosicSizeToolbar())
                .actionEventEventHandler(event -> {
                })
                .registry(this);
    }

    private void createText() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.TEXT))
                .tooltip(new Tooltip("文本"))
                .type(CutInnerType.TEXT)
                .addOption(createTextColorSizeToolbar(CutInnerType.TEXT))
                .actionEventEventHandler(event -> {
                })
                .registry(this);
    }

    private void createPen() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.PEN))
                .tooltip(new Tooltip("画笔"))
                .type(CutInnerType.PEN)
                .addOption(createColorSizeToolbar(CutInnerType.PEN))
                .actionEventEventHandler(event -> {
                })
                .registry(this);
    }

    private void createArrow() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.ARROW))
                .tooltip(new Tooltip("箭头"))
                .type(CutInnerType.ARROW)
                .addOption(createColorSizeToolbar(CutInnerType.ARROW))
                .actionEventEventHandler(event -> {
                })
                .registry(this);
    }

    private void createRectangle() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.RECTANGLE))
                .tooltip(new Tooltip("长方形"))
                .type(CutInnerType.RECTANGLE)
                .addOption(createColorSizeToolbar(CutInnerType.RECTANGLE))
                .actionEventEventHandler(event -> {
                })
                .registry(this);
    }

    private void createRoundness() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.ROUND))
                .tooltip(new Tooltip("圆形"))
                .addOption(createColorSizeToolbar(CutInnerType.ROUNDNESS))
                .type(CutInnerType.ROUNDNESS)
                .actionEventEventHandler(event -> {


                })
                .registry(this);
    }


    private void loadOptionsBox() {
        optionsBox = new HBox();
        optionsBox.getStyleClass().add("button-bar");
        optionsBox.setVisible(false);
        optionsBox.getChildren().addListener((ListChangeListener<Node>) c -> optionsBox.setVisible(!c.getList().isEmpty()));

        getChildren().add(optionsBox);
    }

    private void loadButtonsBox() {
        buttonsBox = new HBox();
        buttonsBox.setSpacing(5);
        buttonsBox.setPadding(new Insets(5, 10, 5, 10));
        buttonsBox.getStyleClass().add("button-box");

        getChildren().add(buttonsBox);
    }

    /**
     * 创建拖动按钮
     */
    private void createDrag() {
        ScreenshotToolbarBoxButtonHolder
                .of()
                .button(createButton(SVGPathHolder.DRAG))
                .tooltip(new Tooltip("拖动"))
                .type(CutInnerType.DRAG)
                .isDefault(true)
                .actionEventEventHandler(event -> {
                })
                .registry(this);


    }


    private Button createButton(SVGPathHolder s) {
        return createButton(s, "tool-bar-button");
    }

    private Button createButton(SVGPathHolder s, String styleClass) {
        SVGPath path = s.to(buttonColor.get());
        path.getStyleClass().add("svg");
        Group svg = new Group(path);
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
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.getStyleClass().add(styleClass);
        return btn;
    }

    private Button createDot(CutInnerType type, SVGPathHolder s, Color fill, Color hoverFill, double size, double showSize) {
        String fillStr = coverRgba(fill);
        String hoverFillStr = coverRgba(hoverFill);
        SVGPath path = s.to(buttonColor.get());
        path.getStyleClass().add("svg");
        path.setStyle("-fill:" + fillStr + ";-hover-fill:" + hoverFillStr + ";-background-color-selected:" + coverRgba(Color.WHITE) + ";");
        Group svg = new Group(path);
        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(showSize / bounds.getWidth(), showSize / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        Button btn = new Button();
        btn.setGraphic(svg);
        btn.setMaxSize(30, 30);
        btn.setMinSize(30, 30);
        btn.setLayoutX(0);
        btn.setLayoutY(0);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.getStyleClass().add("options-dot-button");
        btn.setOnAction(event -> {
            updateSelectOptionsDot(type, btn);
            updateSize(size);
        });
        return btn;
    }

    private Button createOptionsButton(CutInnerType type, SVGPathHolder s, Color fill, Color hoverFill) {
        String cssLayout = "-fx-border-color: WHITE;\n" +
                "-fx-border-width: 1;\n";
        String fillStr = coverRgba(fill);
        String hoverFillStr = coverRgba(hoverFill);


        SVGPath path = s.to(buttonColor.get());
        // 添加样式
        path.setStyle("-fill:" + fillStr + ";-hover-fill:" + hoverFillStr + ";-background-color-selected:" + coverRgba(Color.WHITE) + ";");

        path.getStyleClass().add("svg");
        Group svg = new Group(path);
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
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.getStyleClass().add("options-button");

        btn.getProperties().put("unchecked", svg);
        btn.setStyle(cssLayout + "-fill:" + fillStr + ";-background-color-selected:" + coverRgba(fill.invert().desaturate()) + ";");
        SVGPath checkPath = SVGPathHolder.RADIO_RECTANGLE_CHECKED.to(buttonColor.get());
        // 添加样式
        checkPath.setStyle("-fill:" + fillStr + ";-hover-fill:" + hoverFillStr);

        checkPath.getStyleClass().add("svg");
        Group checkPathSvg = new Group(checkPath);
        Bounds checkPathBounds = checkPathSvg.getBoundsInParent();
        double checkScale = Math.min(20 / checkPathBounds.getWidth(), 20 / checkPathBounds.getHeight());
        checkPathSvg.setScaleX(checkScale);
        checkPathSvg.setScaleY(checkScale);

        btn.getProperties().put("checked", checkPathSvg);
        // 添加hover样式

        btn.getStyleClass().addListener((ListChangeListener<String>) c -> {
            if (c.getList().contains(SELECT_OPTIONS_BUTTON_CLASS)) {
                btn.setGraphic((Node) btn.getProperties().get("checked"));
            } else {
                btn.setGraphic((Node) btn.getProperties().get("unchecked"));
            }
        });
        btn.setOnAction(event -> {
            updateSelectOptions(type, btn);
            updateColor(fill);
        });
        return btn;
    }

    private void updateColor(Color fill) {
        if (canvasProperties != null) {
            canvasProperties.getCurrentConfig().getStrokeColor().set(fill);
        }
    }

    private void updateSize(double size) {
        if (canvasProperties != null) {
            canvasProperties.getCurrentConfig().getStroke().set(size);
        }
    }

    public void updateSelectButton(Button button) {
        selectButtonProperty.set(button);

    }

    public void updateSelectOptions(Object key, Node button) {
        if (optionsSelectedNodeProperty.containsKey(key)) {
            optionsSelectedNodeProperty.get(key).getStyleClass().remove(SELECT_OPTIONS_BUTTON_CLASS);
        }
        optionsSelectedNodeProperty.put(key, button);
        button.getStyleClass().add(SELECT_OPTIONS_BUTTON_CLASS);
    }

    public void updateSelectOptionsDot(Object key, Node button) {
        key = key + "dot";
        if (optionsSelectedNodeProperty.containsKey(key)) {
            optionsSelectedNodeProperty.get(key).getStyleClass().remove(SELECT_OPTIONS_BUTTON_DOT__CLASS);
        }
        optionsSelectedNodeProperty.put(key, button);
        button.getStyleClass().add(SELECT_OPTIONS_BUTTON_DOT__CLASS);
    }

    public void registryButton(ScreenshotToolbarBoxButtonHolder holder) {
        Button button = holder.button();
        if (holder.type() != null) {
            innerTypeButtonMap.put(holder.type(), button);
        }

        buttonsBox.getChildren().add(button);
        button.setTooltip(holder.tooltip());

        // 对快捷键进行包装
        button.setOnAction(event -> {
            // 准备基础数据
            initCanvasProperties();
            DestroyGroupBeanHolder destroyGroupBeanHolder = configuration.getUniqueBean(DestroyGroupBeanHolder.class);
            if (destroyGroupBeanHolder != null) {
                destroyGroupBeanHolder.destroy();
            }


            // 展示选项组
            addOptions(holder.options());

            // 更新当前选中按钮
            updateSelectButton(button);

            // 切换当前模式
            if (holder.type() != null) {
                currentCutInnerType.set(holder.type());
                canvasProperties.setCutInnerType(holder.type());
            }

            // 实际处理事件
            holder.actionEventEventHandler().handle(event);

            // 回退模式
            if (holder.type() == null) {
                innerTypeButtonMap.get(currentCutInnerType.get()).fire();
            }
        });

        // 注册快捷键
        ShortCutExecutorHolder shortCutExecutorHolder = holder.shortCutExecutorHolder();
        if (null != shortCutExecutorHolder) {
            keyboardShortcutsManager.registryShortCut(this.getScene(), shortCutExecutorHolder);
        }
        if (holder.isDefault()) {
            updateSelectButton(button);
        }

    }

    /**
     * 添加选项集合
     *
     * @param nodes 选项集合
     */
    public void addOptions(List<Node> nodes) {
        optionsBox.getChildren().clear();
        if (null != nodes && nodes.size() > 0) {
            optionsBox.getChildren().addAll(nodes);
        }
    }

    public HBox createColorSizeToolbar(CutInnerType type) {
        // 窗口
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(5, 10, 5, 10));

        // 三个圆点
        addDots(type, hBox);
        // 分隔符
        Separator separator = new Separator(Orientation.VERTICAL);

        separator.getStyleClass().add("separator");
        hBox.getChildren().add(separator);
        // 颜色
        addColorss(type, hBox);

        return hBox;
    }

    public HBox createTextColorSizeToolbar(CutInnerType type) {
        // 窗口
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(5, 10, 5, 10));
        addFontChose(type,hBox);
        // 分隔s符
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.getStyleClass().add("separator");
        hBox.getChildren().add(separator);
        // 颜色
        Button red = createOptionsButton(type, SVGPathHolder.RADIO_RECTANGLE, Color.RED, Color.RED);
        Button black = createOptionsButton(type, SVGPathHolder.RADIO_RECTANGLE, Color.BLACK, Color.BLACK);
        Node picker = createColorPickerButton(type);
        hBox.getChildren().addAll(red, black,picker);
        updateSelectOptions(type, red);
        return hBox;
    }

    public void addFontChose(CutInnerType type, HBox box) {
        ChoiceBox<String> fontFamily = new ChoiceBox<>(FXCollections.observableArrayList(Font.getFamilies()));
        fontFamily.getSelectionModel().select(Font.getDefault().getFamily());
        fontFamily.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TrayConfig config = canvasProperties.getCurrentConfig();
            Font font = config.getFont().get();
            config.getFont().set(Font.font(newValue, font.getSize()));
        });

        ChoiceBox<Integer> sizes = new ChoiceBox<>(FXCollections.observableArrayList(IntStream.range(10, 73).boxed().collect(Collectors.toList())));
        sizes.getSelectionModel().select((int) Font.getDefault().getSize());
        sizes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TrayConfig config = canvasProperties.getCurrentConfig();
            Font font = config.getFont().get();
            config.getFont().set(Font.font(font.getFamily(), newValue));
        });
        box.getChildren().addAll(fontFamily,sizes);
    }

    private HBox createMosicSizeToolbar() {
        CutInnerType type = CutInnerType.MOSAIC;
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(5, 10, 5, 10));
        Button min = createDot(type, SVGPathHolder.DOT, Color.GRAY, Color.BLACK, 2, 2);
        Button middle = createDot(type, SVGPathHolder.DOT, Color.GRAY, Color.BLACK, 4, 8);
        Button big = createDot(type, SVGPathHolder.DOT, Color.GRAY, Color.BLACK, 6, 12);
        hBox.getChildren().addAll(min, middle, big);
        updateSelectOptionsDot(type, min);
        return hBox;
    }

    public void addDots(CutInnerType type, HBox box) {
        Button min = createDot(type, SVGPathHolder.DOT, Color.GRAY, Color.BLACK, 1, 2);
        Button middle = createDot(type, SVGPathHolder.DOT, Color.GRAY, Color.BLACK, 4, 8);
        Button big = createDot(type, SVGPathHolder.DOT, Color.GRAY, Color.BLACK, 6, 12);
        box.getChildren().addAll(min, middle, big);
        updateSelectOptionsDot(type, min);

    }

    public void addColorss(CutInnerType type, HBox box) {
        Button red = createOptionsButton(type, SVGPathHolder.RADIO_RECTANGLE, Color.RED, Color.RED);
        Button yellow = createOptionsButton(type, SVGPathHolder.RADIO_RECTANGLE, Color.YELLOW, Color.YELLOW);
        Button blue = createOptionsButton(type, SVGPathHolder.RADIO_RECTANGLE, Color.BLUE, Color.BLUE);
        Button green = createOptionsButton(type, SVGPathHolder.RADIO_RECTANGLE, Color.GREEN, Color.GREEN);
        Button black = createOptionsButton(type, SVGPathHolder.RADIO_RECTANGLE, Color.BLACK, Color.BLACK);
        Button white = createOptionsButton(type, SVGPathHolder.RADIO_RECTANGLE, Color.WHITE, Color.WHITE);
        Node picker = createColorPickerButton(type);
        box.getChildren().addAll(red, yellow, blue, green, black, white, picker);
        updateSelectOptions(type, red);
    }

    private Node createColorPickerButton(CutInnerType type) {
        String cssLayout = "-fx-border-color: WHITE;\n" +
                "-fx-border-width: 0;\n";
        HBox hBox = new HBox();
        hBox.setPrefWidth(120);
        SVGPath path = SVGPathHolder.RADIO_RECTANGLE.to(buttonColor.get());

        Text colorText = new Text();
        colorText.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));


        path.getStyleClass().add("svg");
        Group svg = new Group(path);
        Bounds bounds = svg.getBoundsInParent();
        double scale = Math.min(20 / bounds.getWidth(), 20 / bounds.getHeight());
        svg.setScaleX(scale);
        svg.setScaleY(scale);
        Button picker = new Button();
        picker.setGraphic(svg);
        picker.setMaxSize(30, 30);
        picker.setMinSize(30, 30);
        picker.setLayoutX(0);
        picker.setLayoutY(0);
        picker.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        picker.getStyleClass().add("options-button");
        picker.getStyleClass().add("options-color-picker");

        picker.getProperties().put("unchecked", svg);

        SVGPath checkPath = SVGPathHolder.RADIO_RECTANGLE_CHECKED.to(buttonColor.get());
        // 添加样式


        checkPath.getStyleClass().add("svg");
        Group checkPathSvg = new Group(checkPath);
        Bounds checkPathBounds = checkPathSvg.getBoundsInParent();
        double checkScale = Math.min(20 / checkPathBounds.getWidth(), 20 / checkPathBounds.getHeight());
        checkPathSvg.setScaleX(checkScale);
        checkPathSvg.setScaleY(checkScale);


        picker.getProperties().put("checked", checkPathSvg);
        // 添加hover样式

        picker.getStyleClass().addListener((ListChangeListener<String>) c -> {
            if (c.getList().contains(SELECT_OPTIONS_BUTTON_CLASS)) {
                picker.setGraphic((Node) picker.getProperties().get("checked"));
            } else {
                picker.setGraphic((Node) picker.getProperties().get("unchecked"));
            }
        });

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            // 添加样式
            path.setStyle("-fill:" + coverRgba(newValue) + ";-hover-fill:" + coverRgba(newValue) + ";-background-color-selected:" + coverRgba(Color.WHITE) + ";");
            picker.setStyle("-background-color-selected:" + coverRgba(newValue.invert().desaturate()) + ";");
            checkPath.setStyle("-fill:" + coverRgba(newValue) + ";-hover-fill:" + coverRgba(newValue));
            colorText.setText(coverHex(newValue));
            colorText.setStyle("-fx-background-color: " + coverHex(newValue.invert()) + ";-fx-fill:" + coverHex(newValue) + ";");
            hBox.setStyle(cssLayout + "-fx-background-color: " + coverHex(newValue.invert()) + ";-fx-text-fill:" + coverHex(newValue.invert()) + ";");
            updateColor(newValue);
        });
        colorPicker.setValue(Color.PINK);

        colorPicker.setVisible(false);
        colorPicker.setMaxWidth(0);
        colorText.setMouseTransparent(true);
        picker.setMouseTransparent(true);
        hBox.setCursor(Cursor.HAND);

        hBox.setOnMousePressed(e -> {
            updateSelectOptions(type, picker);
            updateColor(colorPicker.getValue());
            colorPicker.show();

        });


//        PseudoClass hov = getHoverClass(picker);
        PseudoClass hov = PseudoClass.getPseudoClass("hover");
        hBox.hoverProperty().addListener((observable, oldValue, newValue) -> picker.pseudoClassStateChanged(hov, newValue));
        // 添加选中样式

        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        hBox.getChildren().addAll(picker, colorText, colorPicker);
        return hBox;
    }

    @SneakyThrows
    public PseudoClass getHoverClass(Node node) {
        Field f = getAllFields(node, "HOVER_PSEUDOCLASS_STATE");
        if (f == null) {
            return null;
        }
        f.setAccessible(true);
        return (PseudoClass) f.get(node);
    }

    public static Field getAllFields(Object object, String fieldName) {
        Class clazz = object.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    private String coverRgba(Color color) {
        return String.format("rgba(%d,%d,%d,%d)", Math.round(color.getRed() * 255), Math.round(color.getGreen() * 255), Math.round(color.getBlue() * 255), Math.round(color.getOpacity()));
    }

    private String coverHex(Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x",
                    Math.round(c.getRed() * 255),
                    Math.round(c.getGreen() * 255),
                    Math.round(c.getBlue() * 255));
        } else {
            return null;
        }
    }

    public void doDrawingPin() {

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
        ImageShower imageShower = ImageShower.hidenTaskBar().setTopTitle(text).registySelf(configuration.getUniqueBean(ImageShowerManager.class));
        imageShower.setX(rectangle.getX());
        imageShower.setY(rectangle.getY());
        imageShower.show(showImage);
        doCancel();
    }

    public void doCancel() {
        if (canvasProperties == null) {
            return;
        }
        Scene scene = canvasProperties.getCutPane().getScene();
        // 关闭
        ((Stage) scene.getWindow()).close();
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
                .bindParent(getScene().getWindow())
                .centerOnNode(canvasProperties.getCutRectangle())
                .showAndWait();
    }

    public void doSave() {

        // 执行销毁操作
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

    public void doDone() {
        // 获取
        // 执行销毁操作

        ScreenshotsProcess screenshotsProcess = configuration.getUniqueBean(ScreenshotsProcess.class);
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
