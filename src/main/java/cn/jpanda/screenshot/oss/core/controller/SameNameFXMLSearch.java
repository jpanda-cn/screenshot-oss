package cn.jpanda.screenshot.oss.core.controller;

import lombok.Setter;

public class SameNameFXMLSearch extends AbstractFXMLSearch {
    @Setter
    protected String fxmlSuffix = ".fxml";

    public SameNameFXMLSearch(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected String getFXMLFile(Class source) {
        String dirName = source.getPackage().getName().replaceAll("\\.", "/");
        return mergePath(dirName, source.getSimpleName(), fxmlSuffix);
    }

    protected String mergePath(String dirName, String fileName, String suffix) {
        return (dirName == null ? "" : dirName)+"/" + fileName + suffix;
    }
}
