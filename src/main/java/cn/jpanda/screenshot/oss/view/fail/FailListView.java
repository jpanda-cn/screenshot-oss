package cn.jpanda.screenshot.oss.view.fail;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Controller;
import cn.jpanda.screenshot.oss.store.ImageStoreResult;
import cn.jpanda.screenshot.oss.store.ImageStoreResultHandler;
import cn.jpanda.screenshot.oss.store.ImageStoreResultWrapper;
import cn.jpanda.screenshot.oss.store.img.ImageStore;
import cn.jpanda.screenshot.oss.store.img.ImageStoreRegisterManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class FailListView implements Initializable {


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
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                Button button = new Button("", imageView);
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    Stage stage = configuration.getViewContext().newStage();
                    stage.initOwner(table.getScene().getWindow());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    ImageView imageView1 = new ImageView(image);
                    stage.setScene(new Scene(new AnchorPane(imageView1)));
                    stage.showAndWait();
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
            Button show = new Button("查看详情");
            Button delete = new Button("忽略并删除");
            Button retry = new Button("重试");
            box.getChildren().addAll(show, delete, retry);
            show.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Platform.runLater(() -> {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("异常信息");
                    info.setHeaderText(c.getValue().getException().get().getMessage());
                    DialogPane pane = info.getDialogPane();
                    TextArea textArea = new TextArea();
                    pane.contentProperty().setValue(textArea);
                    textArea.textProperty().setValue(c.getValue().getException().get().getDetails());
                    textArea.wrapTextProperty().set(true);
                    info.initModality(Modality.APPLICATION_MODAL);
                    info.showAndWait();
                });
            });
            delete.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> configuration.getUniqueBean(ImageStoreResultHandler.class).remove(c.getValue().getPath().get()));
            retry.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
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
                if (!imageStore.retry(wrapper)) {
                    Platform.runLater(() -> {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setTitle("失败");
                        info.setHeaderText("重试失败，新的异常信息已添加到失败任务列表中");
                        info.initModality(Modality.APPLICATION_MODAL);
                        info.showAndWait();
                    });
                } else {
                    Platform.runLater(() -> {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setTitle("成功");
                        info.setHeaderText("重试成功");
                        info.initModality(Modality.APPLICATION_MODAL);
                        info.showAndWait();
                    });
                }


            });
            return new SimpleObjectProperty<>(box);
        });

        table.setItems(configuration.getUniqueBean(ImageStoreResultHandler.class).getImageStoreResults());
    }
}
