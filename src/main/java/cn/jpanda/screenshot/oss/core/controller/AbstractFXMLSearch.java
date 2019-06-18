package cn.jpanda.screenshot.oss.core.controller;

import java.net.URL;

public abstract class AbstractFXMLSearch implements FXMLSearch {
    /**
     * 类加载器
     */
    private ClassLoader classLoader;

    public AbstractFXMLSearch(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public URL search(Class source) {
        return classLoader.getResource(getFXMLFile(source));
    }

    protected abstract String getFXMLFile(Class source);
}
