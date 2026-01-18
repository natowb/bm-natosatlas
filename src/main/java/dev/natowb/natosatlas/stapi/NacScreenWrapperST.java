package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;

public class NacScreenWrapperST extends Screen {

    private final UIScreen nac;
    private boolean ignoreNextMouseClick = true;

    public NacScreenWrapperST(UIScreen nac) {
        this.nac = nac;
    }

    @Override
    public void init() {
        super.init();
        nac.init(width, height);
        Keyboard.enableRepeatEvents(true);
        ignoreNextMouseClick = true;
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
        nac.onClose();
    }

    @Override
    public void tick() {
        nac.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        nac.render(mouseX, mouseY, delta);
    }

    @Override
    public void onMouseEvent() {

        if (ignoreNextMouseClick) {
            if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2)) {
                return;
            }
            ignoreNextMouseClick = false;
        }

        int x = Mouse.getEventX() * this.width / this.minecraft.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.minecraft.displayHeight - 1;

        int button = Mouse.getEventButton();

        if (button != -1 && Mouse.getEventButtonState()) {
            nac.mouseDown(x, y, button);
        }

        if (button != -1 && !Mouse.getEventButtonState()) {
            nac.mouseUp(x, y, button);
            nac.resetAllButtonsClickState();
        }

        if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2)) {
            if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
                int mx = Mouse.getX() * this.width / this.minecraft.displayWidth;
                int my = this.height - Mouse.getY() * this.height / this.minecraft.displayHeight - 1;

                nac.mouseDrag(mx, my, 0);
            }
        }

        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            nac.mouseScroll(wheel);
        }
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        nac.keyPressed(character, keyCode);
    }


    @Override
    public boolean shouldPause() {
        return false;
    }
}
