package cn.jpanda.screenshot.oss.newcore.controller;

import cn.jpanda.screenshot.oss.common.utils.StringUtils;
import cn.jpanda.screenshot.oss.newcore.annotations.Controller;

public class ControllerAnnotationSameNameFXMLSearch extends SameNameFXMLSearch {
    public ControllerAnnotationSameNameFXMLSearch(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected String getFXMLFile(Class source) {
        Controller controller = (Controller) source.getDeclaredAnnotation(Controller.class);
        if (null == controller) {
            return super.getFXMLFile(source);
        }
        // 处理Controller注解
        return mergePath(
                StringUtils.isEmpty(controller.dir()) ? source.getPackage().getName().replaceAll("\\.", "/") : controller.dir()
                , StringUtils.isEmpty(controller.file()) ? source.getSimpleName() : controller.file()
                , StringUtils.isEmpty(controller.suffix()) ? fxmlSuffix : controller.suffix()
        );
    }
}
