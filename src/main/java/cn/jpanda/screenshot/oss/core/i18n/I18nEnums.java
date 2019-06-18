package cn.jpanda.screenshot.oss.core.i18n;

import java.util.Locale;

public enum I18nEnums {
    CHINESE("中文", Locale.CHINA),
    ENGLISH("ENGLISH", Locale.ENGLISH);
    private String name;
    private Locale locale;

    I18nEnums(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public Locale getLocale() {
        return locale;
    }
}
