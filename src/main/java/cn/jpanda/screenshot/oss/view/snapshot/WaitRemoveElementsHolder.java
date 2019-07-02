package cn.jpanda.screenshot.oss.view.snapshot;

import java.util.ArrayList;
import java.util.List;

public class WaitRemoveElementsHolder {

    private List<WaitRemoveElement> waitRemoveElements = new ArrayList<>();

    public void add(WaitRemoveElement waitRemoveElement) {
        waitRemoveElements.add(waitRemoveElement);
    }

    public void clear() {
        waitRemoveElements.forEach(WaitRemoveElement::remove);
        waitRemoveElements.clear();
    }
}
