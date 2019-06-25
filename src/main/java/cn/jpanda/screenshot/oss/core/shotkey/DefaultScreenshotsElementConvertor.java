package cn.jpanda.screenshot.oss.core.shotkey;

import cn.jpanda.screenshot.oss.core.Configuration;
import cn.jpanda.screenshot.oss.core.annotations.Component;

@Component
public class DefaultScreenshotsElementConvertor implements ScreenshotsElementConvertor {
    private ScreenshotsElementsHolder screenshotsElementsHolder;

    public DefaultScreenshotsElementConvertor(Configuration configuration) {
        this.screenshotsElementsHolder = configuration.getUniqueBean(ScreenshotsElementsHolder.class);
    }

    @Override
    public void activateOne() {
        // 激活一个
        ScreenshotsElements elements = screenshotsElementsHolder.popInvalidElement();
        if (elements == null) {
            return;
        }
        elements.active();
        screenshotsElementsHolder.putEffectiveElement(elements);
    }

    @Override
    public void destroyOne() {
        // 销毁一个
        ScreenshotsElements elements = screenshotsElementsHolder.popEffectiveElement();
        System.out.println("eff");
        screenshotsElementsHolder.listEffective().forEach(System.out::println);
        System.out.println(elements);
        if (elements == null) {
            return;
        }
        elements.destroy();
        System.out.println("invalid");
        screenshotsElementsHolder.putInvalidElement(elements);
        screenshotsElementsHolder.listInvalid().forEach(System.out::println);
    }
}
