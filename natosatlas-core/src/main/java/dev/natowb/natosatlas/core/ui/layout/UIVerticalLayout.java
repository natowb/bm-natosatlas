package dev.natowb.natosatlas.core.ui.layout;

public class UIVerticalLayout implements UILayout {
    private final int centerX;
    private final int gap;
    private int y;

    public UIVerticalLayout(int centerX, int startY, int gap) {
        this.centerX = centerX;
        this.y = startY;
        this.gap = gap;
    }

    @Override
    public UIPoint next(int width, int height) {
        int x = centerX - (width / 2);
        UIPoint p = new UIPoint(x, y);
        y += height + gap;
        return p;
    }
}
