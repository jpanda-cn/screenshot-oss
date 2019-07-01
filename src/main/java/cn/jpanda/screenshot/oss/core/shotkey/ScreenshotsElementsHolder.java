package cn.jpanda.screenshot.oss.core.shotkey;

import java.util.ArrayList;
import java.util.List;

/**
 * 截图元素持有者
 */
public class ScreenshotsElementsHolder {

    /**
     * 有效元素集合
     */
    private List<ScreenshotsElements> effective = new ArrayList<>();

    /**
     * 无效元素集合(主要是被撤销的)
     */
    private List<ScreenshotsElements> invalid = new ArrayList<>();


    public List<ScreenshotsElements> listEffective() {
        return effective;
    }

    public void putEffectiveElement(ScreenshotsElements screenshotsElements) {
        effective.add(screenshotsElements);
    }

    public ScreenshotsElements popEffectiveElement() {
        if (effective.isEmpty()) {
            return null;
        }
        return effective.remove(effective.size() - 1);
    }

    public List<ScreenshotsElements> listInvalid() {
        return invalid;
    }

    public void putInvalidElement(ScreenshotsElements screenshotsElements) {
        invalid.add(screenshotsElements);
    }

    public ScreenshotsElements popInvalidElement() {
        if (invalid.isEmpty()) {
            return null;
        }
        ScreenshotsElements screenshotsElements;
        for (int i = 1; i <= invalid.size(); i++) {
            screenshotsElements = invalid.get(invalid.size() - i);
            if (screenshotsElements.canActive()) {
                return invalid.remove(invalid.size() - i);
            }
        }
        return null;
    }

    public void  clear(){
        invalid.clear();
        effective.clear();
    }
}
