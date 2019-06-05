package cn.jpanda.screenshot.oss.view.snapshot.handlers;

/**
 * 重置类型
 */
public enum ResizeType {
    W_CROSSWISE(true, false, true, false)/*左横向*/,
    E_CROSSWISE(true, false, false, false)/*右横向*/,
    N_VERTICAL(false, true, false, true)/*上竖向*/,
    S_VERTICAL(false, true, false, false)/*下竖向*/,
    NE_OPPOSITE(true, true, false, true)/*右上对角*/,
    NW_OPPOSITE(true, true, true, true)/*左上对角*/,
    SE_OPPOSITE(true, true, false, false)/*右下对角*/,
    SW_OPPOSITE(true, true, true, false)/*左下对角*/;
    private boolean Across;
    private boolean Vertical;
    private boolean Left;
    private boolean Top;


    ResizeType(boolean across, boolean vertical, boolean left, boolean top) {
        Across = across;
        Vertical = vertical;
        Left = left;
        Top = top;
    }

    public boolean isAcross() {
        return Across;
    }

    public boolean isVertical() {
        return Vertical;
    }

    public boolean isLeft() {
        return Left;
    }

    public boolean isTop() {
        return Top;
    }
}
