package cn.jpanda.screenshot.oss.core.destroy;

public class DestroyGroupBeanHolder {
    private DestroyGroupBean destroyGroupBean;

    public void set(DestroyGroupBean destroyGroupBean) {
        destroy();
        this.destroyGroupBean = destroyGroupBean;
    }

    public void destroy() {
        if (destroyGroupBean != null) {
            destroyGroupBean.destroy();
        }
    }
}
