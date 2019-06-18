package cn.jpanda.screenshot.oss.common.utils;

import cn.jpanda.screenshot.oss.core.annotations.Order;

import java.util.Comparator;

public class OrderComparator implements Comparator<Class> {
    @Override
    public int compare(Class pre, Class nex) {
        Integer preValue = Integer.MIN_VALUE;
        if (ReflectionUtils.hasAnnotation(pre.getClass(), Order.class)) {
            Order order = pre.getClass().getDeclaredAnnotation(Order.class);
            preValue = order.value();
        }
        Integer nexValue = Integer.MIN_VALUE;
        if (ReflectionUtils.hasAnnotation(nex.getClass(), Order.class)) {
            Order order = nex.getClass().getDeclaredAnnotation(Order.class);
            nexValue = order.value();
        }
        return preValue - nexValue;
    }
}
