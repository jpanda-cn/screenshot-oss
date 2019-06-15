package cn.jpanda.screenshot.oss.newcore;

import java.util.ArrayList;
import java.util.List;

public class DefaultApplicationContextClassHolder implements ApplicationContextClassHolder {
    private List<Class> classList = new ArrayList<>();

    @Override
    public List<Class> getAllCLass() {
        return classList;
    }

    @Override
    public void addClass(Class c) {
        classList.add(c);
    }

    @Override
    public boolean contain(Class c) {
        return classList.contains(c);
    }
}
