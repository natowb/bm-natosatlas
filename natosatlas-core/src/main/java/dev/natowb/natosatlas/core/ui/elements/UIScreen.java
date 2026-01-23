package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public abstract class UIScreen {

    protected int width;
    protected int height;
    protected UIScreen parent;

    private boolean ignoreNextClick = true;
    private final boolean[] mouseButtons = new boolean[3];
    protected final List<UIElementButton> buttons = new ArrayList<>();
    protected final List<UIElementSlider> sliders = new ArrayList<>();
    protected final List<UIElementTextField> textFields = new ArrayList<>();

    protected void addButton(UIElementButton btn) {
        buttons.add(btn);
    }

    protected void addSlider(UIElementSlider slider) {
        sliders.add(slider);
    }

    protected void addTextField(UIElementTextField tf) {
        textFields.add(tf);
    }

    protected UIScreen(UIScreen parent) {
        this.parent = parent;
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        ignoreNextClick = true;
        this.buttons.clear();
        this.textFields.clear();
        this.sliders.clear();
    }

    public void tick() {
        for (UIElementTextField tf : textFields) {
            tf.tick();
        }
    }

    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        for (UIElementButton btn : buttons) {
            btn.render(mouseX, mouseY);
        }

        for (UIElementSlider slider : sliders) {
            slider.render(mouseX, mouseY);
        }

        for (UIElementTextField tf : textFields) {
            tf.render();
        }
    }


    public void handleRawMouseEvent(int x, int y, int button, boolean pressed, int wheel) {
        if (wheel != 0) {
            mouseScroll(wheel);
            return;
        }

        if (ignoreNextClick) {
            if (!pressed) ignoreNextClick = false;
            return;
        }

        if (button >= 0 && button <= 2) {

            if (pressed) {
                mouseButtons[button] = true;
                mouseDown(x, y, button);
                return;
            }

            mouseButtons[button] = false;
            mouseUp(x, y, button);

            if (button == 0) {
                handleClickDispatch(x, y);
            }
            return;
        }

        for (int b = 0; b < 3; b++) {
            if (mouseButtons[b]) {
                mouseDrag(x, y, b);
            }
        }
    }


    private void handleClickDispatch(int mouseX, int mouseY) {
        for (UIElementButton btn : buttons) {
            if (btn.active && btn.isInside(mouseX, mouseY)) {
                NatosAtlas.get().platform.playSound("random.click", 1.0F, 1.0F);
                onClick(btn);
                return;
            }
        }
    }

    protected void onClick(UIElementButton button) {

    }

    protected void onSliderChanged(UIElementSlider slider) {

    }

    public void mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        for (UIElementSlider slider : sliders) {
            float oldValue = slider.getValue();
            slider.mouseDown(mouseX, mouseY);
            if (slider.getValue() != oldValue) {
                onSliderChanged(slider);
            }
        }

        for (UIElementTextField tf : textFields) {
            tf.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseUp(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        for (UIElementSlider slider : sliders) {
            slider.mouseUp();
        }

        for (UIElementTextField tf : textFields) {
            tf.mouseUp(mouseX, mouseY, button);
        }
    }

    public void mouseDrag(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        for (UIElementSlider slider : sliders) {
            float oldValue = slider.getValue();
            slider.mouseDrag(mouseX);
            if (slider.getValue() != oldValue) {
                onSliderChanged(slider);
            }
        }

        for (UIElementTextField tf : textFields) {
            tf.mouseDragged(mouseX, mouseY, button);
        }
    }

    public void keyPressed(char character, int keyCode) {
        for (UIElementTextField tf : textFields) {
            if (tf.focused) {
                tf.keyPressed(character, keyCode);
                return;
            }
        }

        if (keyCode == Keyboard.KEY_ESCAPE) {
            NatosAtlas.get().platform.openNacScreen(parent);
        }
    }

    public void mouseScroll(int amount) {
    }

    public void handleTab() {

    }

    public void onClose() {
    }

}

