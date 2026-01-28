package dev.natowb.natosatlas.client.ui.layout;

public class UIHorizontalLayout implements UILayout {
    private final int centerY;
    private final int gap;
    private int x;
    private final boolean rtl;

    public UIHorizontalLayout(int startX, int centerY, int gap, boolean rtl) {
        this.x = startX;
        this.centerY = centerY;
        this.gap = gap;
        this.rtl = rtl;
    }

    @Override
    public UIPoint next(int width, int height) {
        int y = centerY - (height / 2);
        UIPoint p = new UIPoint(x, y);

        if (rtl) {
            x -= (width + gap);
        } else {
            x += (width + gap);
        }
        return p;
    }
}
