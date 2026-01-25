package dev.natowb.natosatlas.core.ui.layout;

public class UIHorizontalLayout implements UILayout {
    private final int centerY;
    private final int gap;
    private int x;

    public UIHorizontalLayout(int startX, int centerY, int gap) {
        this.x = startX;
        this.centerY = centerY;
        this.gap = gap;
    }

    @Override
    public UIPoint next(int width, int height) {
        int y = centerY - (height / 2);
        UIPoint p = new UIPoint(x, y);
        x += width + gap;
        return p;
    }
}
