package cn.jpanda.screenshot.oss.core.shotkey;

import cn.jpanda.screenshot.oss.core.annotations.Component;

@Component
public class DefaultScreenshotsElementConvertor implements ScreenshotsElementConvertor {
    private ScreenshotsElementsHolder screenshotsElementsHolder;

    public DefaultScreenshotsElementConvertor(ScreenshotsElementsHolder screenshotsElementsHolder) {
        this.screenshotsElementsHolder = screenshotsElementsHolder;
    }

    @Override
    public void activateOne() {
        // 激活一个
        ScreenshotsElements elements=screenshotsElementsHolder.popInvalidElement();
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
        if (elements == null) {
            return;
        }
        elements.destroy();
        screenshotsElementsHolder.putInvalidElement(elements);
    }
}
