package cn.jpanda.screenshot.oss.view.fail;

import cn.jpanda.screenshot.oss.common.toolkit.ImageShower;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialog;
import cn.jpanda.screenshot.oss.common.toolkit.PopDialogShower;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import cn.jpanda.screenshot.oss.store.ImageStoreResultWrapper;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

@Controller
public class FailListView implements Initializable {

    public static final String IS_SHOWING = FailListView.class.getCanonicalName() + "-IS_SHOWING";

    private Configuration configuration;

    public FailListView(Configuration configuration) {
        this.configuration = configuration;
    }

    public TableView<ImageStoreResult> table;
    public TableColumn<ImageStoreResult, Button> image;
    public TableColumn<ImageStoreResult, String> store;
    public TableColumn<ImageStoreResult, TextArea> path;
    public TableColumn<ImageStoreResult, Label> exception;
    public TableColumn<ImageStoreResult, VBox> operation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setFixedCellSize(130.0);

        table.editableProperty().set(false);
        image.setResizable(false);
        store.setResizable(false);
        path.setResizable(false);
        exception.setResizable(false);
        operation.setResizable(false);

        image.sortableProperty().set(false);
        store.sortableProperty().set(false);
        path.sortableProperty().set(false);
        exception.setSortable(false);
        operation.setSortable(false);

        image.setCellValueFactory((cell) -> {
            try {

                Image image = new Image(new FileInputStream(new File(cell.getValue().getPath().get())));
                Button button = loadImage(image);
                button.setMinWidth(100);
                button.setMinHeight(100);
                button.getStyleClass().clear();
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    ImageShower imageShower = ImageShower.of((Stage) table.getScene().getWindow()).setTopTitle(cell.getValue().getPath().get());
                    imageShower.show(image);
                });
                return new SimpleObjectProperty<>(button);
            } catch (FileNotFoundException f) {
                return new SimpleObjectProperty<>(new Button());
            }

        });
        store.setCellValueFactory(cell -> cell.getValue().getImageStore());
        path.setCellValueFactory(cell -> {
            TextArea text = new TextArea(cell.getValue().getPath().get());
            text.editableProperty().set(false);
            text.wrapTextProperty().set(true);
            text.setPrefRowCount(5);
            return new SimpleObjectProperty<>(text);
        });
        exception.setCellValueFactory((c) -> {
            Label label = new Label(c.getValue().getException().get().getMessage());
            label.wrapTextProperty().set(true);
            return new SimpleObjectProperty<>(label);
        });
        // 添加查看异常和删除的操作
        operation.setCellValueFactory((c) -> {
            VBox box = new VBox();
            box.setSpacing(10);
            Button show = new Button("异常信息");
            Button delete = new Button("忽略并删除");
            Button retry = new Button("重试");
            box.getChildren().addAll(show, delete, retry);
            show.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Platform.runLater(() -> {

                    TextArea textArea = new TextArea();
                    textArea.editableProperty().set(false);
                    textArea.textProperty().setValue(c.getValue().getException().get().getDetails());
                    textArea.wrapTextProperty().set(true);

                    PopDialogShower.exception(c.getValue().getException().get().getMessage(), c.getValue().getException().get().getDetails())
                            .bindParent(table.getScene().getWindow())
                            .showAndWait();

                });
            });
            // 移除，添加提示，二次确认
            delete.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> configuration.getUniqueBean(ImageStoreResultHandler.class).remove(c.getValue().getPath().get()));
            retry.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                Dialog dialogStage = new Dialog();
                ProgressIndicator progressIndicator = new ProgressIndicator();
                // 窗口父子关系
                dialogStage.initStyle(StageStyle.TRANSPARENT);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                // progress bar
                Label label = new Label("数据加载中, 请稍后...");
                label.setTextFill(Color.BLUE);
                progressIndicator.setProgress(-1F);
                VBox vBox = new VBox();
                vBox.setSpacing(10);
                vBox.setBackground(Background.EMPTY);
                vBox.getChildren().addAll(progressIndicator, label);
                dialogStage.getDialogPane().setContent(vBox);
                dialogStage.show();


                ImageStoreResultWrapper wrapper = new ImageStoreResultWrapper(c.getValue());
                String is = wrapper.getImageStore();
                ImageStoreRegisterManager imageStoreRegisterManager = configuration.getUniqueBean(ImageStoreRegisterManager.class);
                ImageStore imageStore = imageStoreRegisterManager.getImageStore(is);
                if (imageStore == null) {
                    // TODO
                    return;
                }
                // 移除
                ImageStoreResultHandler imageStoreResultHandler = configuration.getUniqueBean(ImageStoreResultHandler.class);
                imageStoreResultHandler.remove(c.getValue().getPath().get());
                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        try {
                            return imageStore.retry(wrapper);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                };
                task.setOnRunning(e -> {
                    dialogStage.show();
                });
                task.setOnSucceeded(e -> {
                    try {
                        if (task.get()) {
                            Platform.runLater(() -> {
                                dialogStage.close();
                                PopDialog.create().setHeader("提示").setContent("重试成功").buttonTypes(new ButtonType("知道了")).showAndWait();
                            });
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                    } finally {
                        System.out.println("succ");
                        dialogStage.close();
                    }
                });
                new Thread(task).start();
            });
            return new SimpleObjectProperty<>(box);
        });

        table.setRowFactory(tv -> new TableRow<ImageStoreResult>() {
            @Override
            public void updateIndex(int i) {
                super.updateIndex(i);
                if (i % 2 == 0) {
                    setStyle("-fx-background-color: white");
                } else {
                    setStyle("-fx-background-color: #e6e6e6");
                }
            }
        });

        table.setItems(configuration.getUniqueBean(ImageStoreResultHandler.class).getImageStoreResults());
    }

    protected Button loadImage(Image image) {
        double stroke = 2;
        Rectangle rect = new Rectangle();
        rect.getStyleClass().add("button-image");
        ImagePattern imagePattern = new ImagePattern(image);
        rect.widthProperty().set(100 + stroke * 2);
        rect.heightProperty().set(100 + stroke * 2);
        rect.setLayoutX(stroke);
        rect.setLayoutY(stroke);
        rect.setFill(imagePattern);
        rect.strokeWidthProperty().set(stroke);
        rect.strokeTypeProperty().set(StrokeType.OUTSIDE);
        Button button = new Button();
        button.setGraphic(rect);
        return button;
    }
}
