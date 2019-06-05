package cn.jpanda.screenshot.oss.common.utils;

public final class MathUtils {

    public static boolean offset(double v1, double v2, double of) {
        return v1 >= v2 - of && v1 <= v2 + of;
    }
    public static double subAbs(double v1, double v2) {
        return v1 > v2 ? v1 - v2 : v2 - v1;
    }

    public static double min(double v1, double v2) {
        return v1 > v2 ? v2 : v1;
    }

    public static double max(double v1, double v2) {
        return v1 < v2 ? v2 : v1;
    }
}
