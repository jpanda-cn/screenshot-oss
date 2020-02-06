package cn.jpanda.screenshot.oss.view.main;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.common.toolkit.ImageShower;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.Snapshot;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.core.imageshower.ImageShowerManager;
import cn.jpanda.screenshot.oss.core.persistence.BootstrapPersistence;
import cn.jpanda.screenshot.oss.core.persistence.Persistence;
import cn.jpanda.screenshot.oss.core.persistence.PersistenceBeanCatalogManagement;
import cn.jpanda.screenshot.oss.core.shotkey.HotKey2CutPersistence;
import cn.jpanda.screenshot.oss.core.shotkey.SettingsHotKeyPropertyHolder;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import cn.jpanda.screenshot.oss.store.clipboard.ClipboardCallbackRegistryManager;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import cn.jpanda.screenshot.oss.view.fail.FailListView;
import cn.jpanda.screenshot.oss.view.password.modify.ModifyPassword;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class IndexCutView implements Initializable {

    /**
     * 选型
     */
    public MenuButton options;
    /**
     * 截图时是否隐藏该菜单
     */
    public RadioButton hidden;
    /**
     * 快捷键
     */
    public Label shotKeyShower;
    /**
     * 截图按钮
     */
    public Button cutBtn;
    /**
     * 主容器
     */
    public AnchorPane mainContain;

    @FXML
    public AnchorPane containTop;
    public Button exit;
    /**
     * 全局配置对象
     */
    private Configuration configuration;

    /**
     * 快捷键输入框
     */
    public TextField hotKey;

    public AnchorPane setting;
    /**
     * 剪切板内容复选框
     */
    @FXML
    public ComboBox clipboard;
    /**
     * 快键键
     */
    public Label shotKeyL;

    private ImageStoreRegisterManager imageStoreRegisterManager;
    private ClipboardCallbackRegistryManager clipboardCallbackRegistryManager;
    private GlobalConfigPersistence globalConfigPersistence;

    public IndexCutView(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initExit();

        // 加载配置
        imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
        clipboardCallbackRegistryManager = configuration.getUniqueBean(ClipboardCallbackRegistryManager.class);
        globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        initSettings();
        initHidden();
        loadShotKey();

        loadSettings();
        loadHotKey();

        addDrag();

    }

    private void loadSettings() {
        Scene set = configuration.getViewContext().getScene(SettingsView.class, true, false);
        setting.getChildren().add(set.getRoot());
    }

    private void initExit() {
        exit.setPickOnBounds(true);
    }


    private void loadShotKey() {
        shotKeyShower.setMouseTransparent(true);
        SettingsHotKeyPropertyHolder settingsHotKeyPropertyHolder = configuration.getUniqueBean(SettingsHotKeyPropertyHolder.class);
        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
            if (!newValue) {
                updateShotKey();
            }
        };
        settingsHotKeyPropertyHolder.isSettings.removeListener(listener);
        settingsHotKeyPropertyHolder.isSettings.addListener(listener);
        updateShotKey();

    }

    private void updateShotKey() {
        String shot = getShotKeyShower();
        // 计算文字宽度
        ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                shotKeyShower.setLayoutX(cutBtn.widthProperty().subtract(newValue.doubleValue()).divide(2).add(cutBtn.layoutXProperty()).get());
            });
        };
        shotKeyShower.widthProperty().removeListener(listener);
        shotKeyShower.widthProperty().addListener(listener);
        shotKeyShower.textProperty().setValue(shot);
    }

    private String getShotKeyShower() {
        HotKey2CutPersistence hotKey2CutPersistence = configuration.getPersistence(HotKey2CutPersistence.class);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        if (hotKey2CutPersistence.isCtrl()) {
            stringBuilder.append(KeyCode.CONTROL.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isShift()) {
            stringBuilder.append(KeyCode.SHIFT.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isAlt()) {
            stringBuilder.append(KeyCode.ALT.getName()).append(" + ");
        }
        stringBuilder.append(hotKey2CutPersistence.getCode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }

    public void initHidden() {

        GlobalConfigPersistence configPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        hidden.selectedProperty().set(configPersistence.isHideIndexScreen());
        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
            GlobalConfigPersistence configPersistence1 = configuration.getPersistence(GlobalConfigPersistence.class);
            configPersistence1.setHideIndexScreen(newValue);
            configuration.storePersistence(configPersistence1);
        };
        hidden.selectedProperty().removeListener(listener);
        hidden.selectedProperty().addListener(listener);
    }

    private void initSettings() {

        Menu pwd = new Menu("密码管理");
        MenuItem stopUsePwd = new MenuItem("停用密码");
        MenuItem usePwd = new MenuItem("启用密码");
        MenuItem cpwd = new MenuItem("修改密码");

        stopUsePwd.setOnAction(event -> {
            BootstrapPersistence bootstrapPersistence = configuration.getPersistence(BootstrapPersistence.class);
            // 取消密码，重新存储一下数据
            PersistenceBeanCatalogManagement persistenceBeanCatalogManagement = configuration.getUniqueBean(PersistenceBeanCatalogManagement.class);
            // 将所有数据重新加载
            List<Persistence> list =
                    persistenceBeanCatalogManagement.list().stream().filter((p) -> !BootstrapPersistence.class.isAssignableFrom(p)).map((p) -> configuration.getPersistence(p)).collect(Collectors.toList());
            // 重置使用密码标记
            bootstrapPersistence.setUsePassword(false);
            configuration.storePersistence(bootstrapPersistence);
            configuration.setPassword(null);
            // 重新存储
            list.forEach((p) -> {
                configuration.storePersistence(p);
            });
            // 完成
            pwd.getItems().remove(stopUsePwd);
            pwd.getItems().remove(cpwd);
            pwd.getItems().add(0, usePwd);
        });
        EventHandler<ActionEvent> usePwdAction = event -> {

            PersistenceBeanCatalogManagement persistenceBeanCatalogManagement = configuration.getUniqueBean(PersistenceBeanCatalogManagement.class);
            // 将所有数据重新加载
            List<Persistence> list =
                    persistenceBeanCatalogManagement.list().stream().filter((p) -> !BootstrapPersistence.class.isAssignableFrom(p)).map((p) -> configuration.getPersistence(p)).collect(Collectors.toList());
            // 跳转到初始化密码页面
            // 将密码页面放置到舞台中央
            Scene scene = configuration.getViewContext().getScene(ModifyPassword.class, true, false);

            HBox header = new HBox();
            Label main = new Label("密码管理");
            main.setStyle(" -fx-underline: true;-fx-font-weight: bold;");
            header.getChildren().addAll(main);
            Callable<Boolean, ButtonType> callable = configuration.getUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + ModifyPassword.class.getCanonicalName());
            PopDialog.create()
                    .setHeader(header)
                    .setContent(scene.getRoot())
                    .bindParent(setting.getScene().getWindow())
                    .buttonTypes(ButtonType.CANCEL, ButtonType.APPLY)
                    .callback(callable).showAndWait();


            if (configuration.getPersistence(BootstrapPersistence.class).isUsePassword()) {
                // 将所有配置重新保存一下
                list.forEach((p) -> {
                    configuration.storePersistence(p);
                });
                // 完成
                pwd.getItems().remove(usePwd);
                pwd.getItems().add(0, stopUsePwd);
                pwd.getItems().add(1, cpwd);
            }
        };


        usePwd.setOnAction(usePwdAction);
        cpwd.setOnAction(usePwdAction);
        BootstrapPersistence bootstrapPersistence = configuration.getPersistence(BootstrapPersistence.class);
        pwd.getItems().addAll(bootstrapPersistence.isUsePassword() ? new MenuItem[]{stopUsePwd, cpwd} : new MenuItem[]{usePwd});

        options.getItems().add(pwd);
        MenuItem failList = new MenuItem("失败任务列表");
        options.getItems().add(failList);
        failList.setOnAction(event -> {
            // 展示失败列表设置页面
            toFailList();
        });
        MenuItem drawingPinManager = new MenuItem("图钉管理");
        options.getItems().add(drawingPinManager);
        drawingPinManager.setOnAction(event -> {
            // 展示失败列表设置页面
            toDrawingPinManager();
        });

    }

    private void toDrawingPinManager() {
        String showingKey = "IMAGESHOWERMANAGER-IS-SHOWING";
        boolean showing = configuration.getUniquePropertiesHolder(showingKey, false);
        if (showing) {
            return;
        }
        ImageShowerManager showers = configuration.getUniqueBean(ImageShowerManager.class);

        ListView<HBox> listView = new ListView<>();
        listView.setPrefWidth(480);
        ObservableList<ImageShower> ss = showers.getShowers();
        ObservableList<HBox> sh = FXCollections.observableList(ss.stream().map(s -> hBox(listView, s)).collect(Collectors.toList()));

        ss.addListener((ListChangeListener<ImageShower>) c -> {
            sh.clear();
            sh.addAll(FXCollections.observableList(ss.stream().map(s -> hBox(listView, s)).collect(Collectors.toList())));
        });
        listView.itemsProperty().bind(new SimpleObjectProperty<>(sh));

        configuration.registryUniquePropertiesHolder(showingKey, true);
        PopDialog popDialog = PopDialog.create()

                .setHeader("图钉管理")
                .setContent(listView)
                .callback(buttonType -> {
                    configuration.registryUniquePropertiesHolder(showingKey, false);
                    return true;
                })
                .buttonTypes(ButtonType.CLOSE);
        popDialog.initModality(Modality.WINDOW_MODAL);
        popDialog.showAndWait();
    }


    public void toFailList() {
        // 使用动画，加载至右侧
        configuration.registryUniquePropertiesHolder(FailListView.IS_SHOWING, true);

        PopDialog.create()
                .setHeader("失败任务列表")
                .setContent(configuration.getViewContext().getScene(FailListView.class, true, false).getRoot())
                .bindParent(setting.getScene().getWindow())
                .callback(buttonType -> {
                    configuration.registryUniquePropertiesHolder(FailListView.IS_SHOWING, false);
                    return true;
                })
                .buttonTypes(ButtonType.CLOSE)
                .showAndWait();

    }

    public void doCut() {
        configuration.getUniqueBean(Snapshot.class).cut();
    }

    public void doKeyCut(KeyEvent event) {
        if (event.getCode().equals(KeyCode.SPACE) || event.getCode().equals(KeyCode.ENTER)) {
            doCut();
        }
    }


    private void loadHotKey() {
        SettingsHotKeyPropertyHolder settingsHotKeyPropertyHolder = configuration.getUniqueBean(SettingsHotKeyPropertyHolder.class);
        settingsHotKeyPropertyHolder.isSettings.unbind();
        settingsHotKeyPropertyHolder.isSettings.bind(hotKey.focusedProperty());

        hotKey.editableProperty().setValue(false);
        HotKey2CutPersistence hotKey2CutPersistence = configuration.getPersistence(HotKey2CutPersistence.class);

        StringBuilder stringBuilder = new StringBuilder();
        if (hotKey2CutPersistence.isCtrl()) {
            stringBuilder.append(KeyCode.CONTROL.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isShift()) {
            stringBuilder.append(KeyCode.SHIFT.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isAlt()) {
            stringBuilder.append(KeyCode.ALT.getName()).append(" + ");
        }
        stringBuilder.append(hotKey2CutPersistence.getCode());
        hotKey.textProperty().setValue(stringBuilder.toString());
    }


    public void back() {
        // 取消
        ((Stage) setting.getScene().getWindow()).close();
    }

    public void keyBack(KeyEvent e) {
        if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.SPACE)) {
            // 执行保存操作
            back();
        }
    }

    public void changeHotKey(KeyEvent event) {

        HotKey2CutPersistence hotKey2CutPersistence = configuration.getPersistence(HotKey2CutPersistence.class);
        StringBuilder stringBuilder = new StringBuilder();
        hotKey2CutPersistence.setShift(event.isShiftDown());
        hotKey2CutPersistence.setAlt(event.isAltDown());
        hotKey2CutPersistence.setCtrl(event.isControlDown());
        if (hotKey2CutPersistence.isCtrl()) {
            stringBuilder.append(KeyCode.CONTROL.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isShift()) {
            stringBuilder.append(KeyCode.SHIFT.getName()).append(" + ");
        }
        if (hotKey2CutPersistence.isAlt()) {
            stringBuilder.append(KeyCode.ALT.getName()).append(" + ");
        }

        hotKey.textProperty().setValue(stringBuilder.toString());
        if (event.getCode().equals(KeyCode.SHIFT)
                || event.getCode().equals(KeyCode.ALT)
                || event.getCode().equals(KeyCode.CONTROL)) {
            return;
        }
        String name = event.getCode().getName();
        stringBuilder.append(name).append(" ");
        hotKey2CutPersistence.setCode(name);
        hotKey.textProperty().setValue(stringBuilder.toString());
        configuration.storePersistence(hotKey2CutPersistence);
    }

    public void addDrag() {

        containTop.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            private double xOffset = 0;
            private double yOffset = 0;
            Stage stage;

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {

                    stage = (Stage) containTop.getScene().getWindow();
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            }
        });


    }

    public void doClose() {
        // 获取关闭视图

        VBox b = new VBox();
        b.setSpacing(10);
        RadioButton min = new RadioButton("最小化");
        RadioButton close = new RadioButton("关闭");
        b.getChildren().addAll(min, close);
        b.setStyle(" -fx-alignment: left;");
        min.setUserData("min");
        close.setUserData("close");

        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(min, close);
        group.selectToggle(min);

        ButtonType back = new ButtonType("再想想");
        ButtonType ok = new ButtonType("我意已决");
        Stage window = configuration.getViewContext().getStage();
        if (ok.equals(PopDialog.create()
                .setHeader("确认关闭吗？")
                .setContent(b)
                .bindParent(window)
                .addButtonClass(back, "button-light")
                .buttonTypes(back, ok)
                .showAndWait().orElse(back))) {
            if (group.getSelectedToggle().getUserData().equals("close")) {
                Platform.exit();
            } else {
                window.setIconified(true);
            }
        }

    }


    public void toMin(ActionEvent actionEvent) {
        ((Stage) containTop.getScene().getWindow()).setIconified(true);
    }

    public HBox hBox(ListView listView, ImageShower imageShower) {

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        Text num = new Text();
        num.setWrappingWidth(35);
        TextField textField = new TextField();
        textField.setEditable(false);
        textField.textProperty().bind(imageShower.getTopTitle().textProperty());
        HBox vBox = new HBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        Button show = drawingShower(imageShower);
        show.getStyleClass().add("svg-button");

        Button onTop = drawingPin(imageShower);
        onTop.getStyleClass().add("svg-button");

        Button remove = drawingClose(listView, imageShower);
        remove.getStyleClass().add("svg-button");

        vBox.getChildren().addAll(show, onTop, remove);
        ImageView imageView = new ImageView(imageShower.getImage());
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        hBox.getChildren().addAll(num, imageView, textField, vBox);
        num.textProperty().bind(Bindings.createStringBinding(() -> listView.getItems().indexOf(hBox) + 1 + "", listView.getItems()));
        return hBox;
    }

    public Button drawingClose(ListView listView, ImageShower imageShower) {
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
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doDeleteDrawingPin(listView, imageShower);
            }
        });
        return btn;
    }

    private void doDeleteDrawingPin(ListView listView, ImageShower imageShower) {
        AnchorPane center = new AnchorPane();
//        center.setStyle("-fx-background-color: rgba(255,255,255,0);");
        Label label = new Label("确认删除吗？");
        label.setStyle("-fx-background-color: rgba(255,255,255,0);-fx-text-fill: white;-fx-font-size: 11px;");

        center.getChildren().addAll(label);
        Tooltip tooltip = new Tooltip();
        VBox box = new VBox();
        box.getStylesheets().add(imageShower.getStylesheets().get());
        box.setSpacing(10);
        box.getChildren().add(center);
        Button ok = new Button("确认");
        ok.setStyle("-fx-font-size: 11px;");

        Button cancel = new Button("取消");
        cancel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(198,47,47,1);-fx-background-color: #e6e6e6;");
        ok.setOnMouseClicked((e) -> {
            imageShower.doClose();
            tooltip.hide();
        });
        ok.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                imageShower.doClose();
                tooltip.hide();
            }
        });
        cancel.setOnMouseClicked((e) -> {
            tooltip.hide();

        });


        HBox h = new HBox();
        h.setAlignment(Pos.CENTER_RIGHT);
        h.getChildren().addAll(ok, cancel);
        box.getChildren().addAll(h);

        tooltip.setGraphic(box);
        Window window = listView.getParent().getScene().getWindow();
        ChangeListener<Boolean> c = (observable, oldValue, newValue) -> {
            tooltip.setAnchorX(window.xProperty().add(window.widthProperty().subtract(tooltip.widthProperty()).divide(2)).get());
            tooltip.setAnchorY(window.yProperty().add(window.heightProperty().subtract(tooltip.heightProperty()).divide(2)).get());
        };
        window.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                tooltip.hide();
            }
        });
        tooltip.showingProperty().addListener(c);
        tooltip.show(window, window.getX() + window.getWidth(), window.getY());
        ok.requestFocus();

    }

    public Button drawingShower(ImageShower imageShower) {
        Group hide = new Group(
                createPath("M512 258.368c-17.984 0-35.264 1.28-51.84 3.84a38.208 38.208 0 1 1-11.456-75.648c20.352-3.136 41.472-4.736 63.296-4.736 157.824 0 273.024 84.736 346.816 165.504a708.352 708.352 0 0 1 103.36 148.224c2.304 4.48 4.544 9.088 6.72 13.696l0.384 0.832 0.128 0.32v0.128l-35.008 15.552 35.136 15.616-0.128 0.064v0.256l-0.384 0.64a587.904 587.904 0 0 1-20.352 38.784c-18.112 31.424-38.528 61.44-61.056 89.6a38.528 38.528 0 0 1-67.84-19.52 38.208 38.208 0 0 1 7.744-28.096 640.64 640.64 0 0 0 64.064-97.28 631.68 631.68 0 0 0-89.6-127.296C736 326.592 639.936 258.368 512 258.368z m422.4 267.712l35.072 15.616a38.208 38.208 0 0 0 0-31.168l-35.072 15.552zM219.52 344.832a38.144 38.144 0 0 1 2.496 54.016 631.872 631.872 0 0 0-89.6 127.232c24.576 46.144 54.72 88.896 89.6 127.296C287.936 725.568 384 793.856 512 793.856a344.704 344.704 0 0 0 120.96-21.632 38.208 38.208 0 1 1 26.88 71.68A421.888 421.888 0 0 1 512 870.4c-157.824 0-273.024-84.8-346.816-165.568A708.224 708.224 0 0 1 61.824 556.672a480.128 480.128 0 0 1-6.72-13.696L54.72 542.08l-0.128-0.32v-0.064L89.6 526.08l-35.136-15.552 0.064-0.128 0.128-0.32 0.384-0.832 1.472-3.008a620.8 620.8 0 0 1 25.984-47.616c23.936-39.808 51.648-77.12 82.688-111.296a38.4 38.4 0 0 1 54.272-2.56zM89.6 526.08l-35.072-15.552a38.208 38.208 0 0 0 0 31.168L89.6 526.08z m384.448-176.64c0-21.248 17.088-38.4 38.208-38.4 115.264 0 206.272 97.152 206.272 214.016a38.528 38.528 0 0 1-19.072 33.28 38.016 38.016 0 0 1-38.208 0 38.4 38.4 0 0 1-19.136-33.28c0-77.184-59.52-137.216-129.92-137.216a38.336 38.336 0 0 1-38.144-38.4zM356.224 449.024a38.4 38.4 0 0 1 29.312 45.696 145.28 145.28 0 0 0-3.2 30.336c0 77.184 59.52 137.216 129.92 137.216 10.048 0 19.84-1.216 29.184-3.456a38.4 38.4 0 1 1 17.92 74.688c-15.36 3.712-31.232 5.568-47.104 5.568-115.328 0-206.336-97.216-206.336-214.08 0-15.872 1.664-31.488 4.864-46.464a38.4 38.4 0 0 1 45.44-29.504z m-214.848-335.36a38.08 38.08 0 0 1 54.016 0l687.68 691.584a38.592 38.592 0 0 1-0.448 53.888 38.08 38.08 0 0 1-53.568 0.512L141.44 168a38.336 38.336 0 0 1 0-54.4z")
        );
        Group show = new Group(
                createPath("M511.753 359.901c-91.511 0-165.917 67.834-165.917 151.806 0 83.994 74.406 152.391 165.917 152.391s165.939-68.397 165.939-152.391c0-83.971-74.428-151.806-165.939-151.806z m1.102 248.899c-61.082 0-111.722-44.428-111.722-97.34 0-53.408 49.537-96.26 110.619-96.26 61.104 0 110.642 43.37 110.642 96.26-0.562 52.912-48.456 97.34-109.539 97.34z m467.887-102.877c-0.586-1.17 0-2.903-0.586-4.051 0-0.585-0.585-1.17-0.585-1.17-0.585-0.563 0-1.733-0.585-2.318-85.435-176.023-271.202-276.762-467.233-276.762-196.009 0-381.799 100.739-467.211 276.762-0.585 0.585-0.585 1.755-0.585 2.318 0 0.585-0.585 0.585-0.585 1.17-0.585 1.148 0 2.881-0.585 4.051-0.585 1.733-1.17 4.051-1.17 6.932 0 2.341 0.585 4.636 1.17 6.954 0.585 1.171 0 2.903 0.585 4.052 0 0.585 0.585 0.585 0.585 1.17 0.585 0.585 0 1.733 0.585 2.318 85.412 176.022 271.202 275.028 467.211 275.028 196.031 0 381.214-99.591 466.648-275.028 0.585-0.585 0.585-1.733 0.585-2.318s0.585-0.585 0.585-1.17c0.585-1.148 0-2.881 0.585-4.052 0.586-2.318 1.171-4.613 1.171-6.954 0.585-2.295 0-4.614-0.585-6.932zM511.753 747.08c-166.165 0-329.719-76.14-409.887-234.787C181.449 354.208 345.003 276.92 511.753 276.92c166.772 0 330.89 77.872 410.472 235.935C842.643 670.94 677.94 747.08 511.753 747.08z")
        );

        Bounds bounds = hide.getBoundsInParent();
        double scale = Math.min(20 / bounds.getWidth(), 20 / bounds.getHeight());
        hide.setScaleX(scale);
        hide.setScaleY(scale);


        Bounds showBounds = show.getBoundsInParent();
        double showScale = Math.min(20 / showBounds.getWidth(), 20 / showBounds.getHeight());
        show.setScaleX(showScale);
        show.setScaleY(showScale);

        Button btn = new Button();
        btn.setGraphic(hide);
        btn.setMaxSize(30, 30);
        btn.setMinSize(30, 30);
        btn.setLayoutX(0);
        btn.setLayoutY(0);
        btn.getStyleClass().add("close-button");
        imageShower.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                btn.setGraphic(hide);
                return;
            }
            btn.setGraphic(show);
        });
        btn.setOnMouseClicked(event -> {
            if (imageShower.isShowing()) {
                imageShower.hide();
                return;
            }
            imageShower.show();
        });
        return btn;
    }

    public Button drawingPin(ImageShower imageShower) {

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
        imageShower.alwaysOnTopProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                btn.setGraphic(checkedSvg);
            } else {
                btn.setGraphic(svg);
            }
        });
        btn.setOnMouseClicked(event -> imageShower.setAlwaysOnTop(!imageShower.alwaysOnTopProperty().getValue()));

        return btn;

    }

    private static SVGPath createPath(String d) {
        SVGPath path = new SVGPath();
        path.getStyleClass().add("svg");
        path.setContent(d);
        return path;
    }
}
