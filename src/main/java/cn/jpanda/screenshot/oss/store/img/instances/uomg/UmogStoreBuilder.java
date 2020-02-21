package cn.jpanda.screenshot.oss.store.img.instances.uomg;

import cn.jpanda.screenshot.oss.common.toolkit.Callable;
import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.store.ImageStoreConfigBuilder;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * @author HanQi [Jpanda@aliyun.com]
 * @version 1.0
 * @since 2020/2/21 11:33
 */
public class UmogStoreBuilder implements ImageStoreConfigBuilder {
    private Configuration configuration;

    public UmogStoreBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Parent config() {

        // 创建一个combox
        ComboBox<EFigureBed> comboBox = new ComboBox<>();
        comboBox.setConverter(new StringConverter<EFigureBed>() {
            @Override
            public String toString(EFigureBed object) {
                return object.getName();
            }

            @Override
            public EFigureBed fromString(String string) {
                return null;
            }
        });
        comboBox.getItems().addAll(EFigureBed.values());
        UmogPersistence umogPersistence = configuration.getPersistence(UmogPersistence.class);
        comboBox.getSelectionModel().select(EFigureBed.valueOf(umogPersistence.getType()));
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.getChildren().addAll(new Label("选择图床:"), comboBox);

        Callable<Boolean, ButtonType> callable = buttonType -> {
            if (ButtonType.CANCEL.equals(buttonType)) {
                return true;
            }
            umogPersistence.setType(comboBox.getSelectionModel().getSelectedItem().name());
            configuration.storePersistence(umogPersistence);
            return true;
        };

        configuration.registryUniquePropertiesHolder(Callable.class.getCanonicalName() + "-" + UmogCloudStore.NAME, callable);


        return hBox;
    }
}
