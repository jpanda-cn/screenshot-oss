package cn.jpanda.screenshot.oss.core.i18n;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.persistences.GlobalConfigPersistence;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 多语言资源加载类
 */
public class I18nResource {
    private static final String I18N_FILE_NAME = "i18n/screenshot";
    private SimpleObjectProperty<Locale> locale;
    private ResourceBundle resourceBundle;
    private Configuration configuration;

    public I18nResource(Configuration configuration) {
        this.configuration = configuration;
        init();
    }

    protected void init() {
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        locale = new SimpleObjectProperty<>(I18nEnums.valueOf(globalConfigPersistence.getLocale()).getLocale());
        locale.addListener((observable, oldValue, newValue) -> resourceBundle = ResourceBundle.getBundle(I18N_FILE_NAME, locale.get()));
        resourceBundle = ResourceBundle.getBundle(I18N_FILE_NAME, locale.get());
    }

    public String get(String name) {
        return resourceBundle.getString(name);
    }

    public void updateI18n(I18nEnums enums) {
        GlobalConfigPersistence globalConfigPersistence = configuration.getPersistence(GlobalConfigPersistence.class);
        locale.set(enums.getLocale());
        globalConfigPersistence.setLocale(enums.toString());
        configuration.storePersistence(globalConfigPersistence);
    }
}
