package cn.jpanda.screenshot.oss.core.context;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;

public class FXAnnotationSameNameFXMLSearch extends SameNameFXMLSearch {
    public FXAnnotationSameNameFXMLSearch(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected String getFXMLFile(Class source) {
        FX fx = (FX) source.getDeclaredAnnotation(FX.class);
        if (null == fx) {
            return super.getFXMLFile(source);
        }

        return mergePath(
                StringUtils.isEmpty(fx.dir()) ? source.getPackage().getName().replaceAll("\\.", "/") : fx.dir()
                , StringUtils.isEmpty(fx.fxmlName()) ? source.getSimpleName() : fx.fxmlName()
                , StringUtils.isEmpty(fx.suffix()) ? fxmlSuffix : fx.suffix()
        );
    }
}
