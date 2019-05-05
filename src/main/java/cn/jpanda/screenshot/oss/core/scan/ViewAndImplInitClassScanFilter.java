package cn.jpanda.screenshot.oss.core.scan;

import javafx.fxml.Initializable;

public class ViewAndImplInitClassScanFilter extends ViewClassScanFilter {
    @Override
    public boolean doFilter(Class clazz) {
        return super.doFilter(clazz) && Initializable.class.isAssignableFrom(clazz);
    }
}
