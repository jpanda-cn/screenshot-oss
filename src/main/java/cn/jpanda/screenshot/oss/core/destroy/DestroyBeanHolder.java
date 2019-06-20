package cn.jpanda.screenshot.oss.core.destroy;

public class DestroyBeanHolder {
    private DestroyBean destroyBean;

    public void set(DestroyBean destroyBean) {
        destroy();
        this.destroyBean = destroyBean;
    }

    public void destroy() {
        if (destroyBean != null) {
            destroyBean.destroy();
        }
    }
}
