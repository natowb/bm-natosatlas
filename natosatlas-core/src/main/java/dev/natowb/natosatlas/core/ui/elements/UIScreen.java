package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import org.lwjgl.input.Keyboard;

public abstract class UIScreen {

    protected int width;
    protected int height;
    protected UIScreen parent;

    private boolean ignoreNextClick = true;
    private final boolean[] mouseButtons = new boolean[3];

    protected UIScreen(UIScreen parent) {
        this.parent = parent;
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        ignoreNextClick = true;
    }

    public void tick() {
    }

    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
    }

    public void handleRawMouseEvent(int x, int y, int button, boolean pressed, int wheel) {
        if (wheel != 0) {
            mouseScroll(wheel);
        }

        if (ignoreNextClick) {
            if (!pressed) {
                ignoreNextClick = false;
            }
            return;
        }

        if (button != -1) {
            if (pressed) {
                mouseButtons[button] = true;
                mouseDown(x, y, button);
            } else {
                mouseButtons[button] = false;
                mouseUp(x, y, button);
                resetAllButtonsClickState();
            }
        }

        for (int i = 0; i < mouseButtons.length; i++) {
            if (mouseButtons[i]) {
                mouseDrag(x, y, i);
            }
        }
    }

    public void mouseDown(int mouseX, int mouseY, int button) {
    }

    public void mouseUp(int mouseX, int mouseY, int button) {
    }

    public void mouseScroll(int amount) {
    }

    public void mouseDrag(int mouseX, int mouseY, int button) {

    }

    public void keyPressed(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            NatosAtlas.get().platform.openNacScreen(parent);
        }
    }

    public void handleTab() {

    }

    public void resetAllButtonsClickState() {
    }

    public void onClose() {
    }
}

