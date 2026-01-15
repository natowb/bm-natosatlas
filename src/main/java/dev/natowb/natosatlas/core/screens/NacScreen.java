package dev.natowb.natosatlas.core.screens;

import dev.natowb.natosatlas.core.NacPlatformAPI;
import org.lwjgl.input.Keyboard;

public abstract class NacScreen {

    protected int width;
    protected int height;
    protected NacScreen parent;

    protected NacScreen(NacScreen parent) {
        this.parent = parent;
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void tick() {
    }

    public void render(int mouseX, int mouseY, float delta) {
    }

    public void mouseDown(int mouseX, int mouseY, int button) {
    }

    public void mouseUp(int mouseX, int mouseY, int button) {
    }

    public void mouseScroll(int amount) {
    }

    public void keyPressed(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            NacPlatformAPI.get().openNacScreen(parent);
        }
    }

    public void handleTab() {
    }

    public void mouseDrag(int mouseX, int mouseY, int button) {
    }

    public void resetAllButtonsClickState() {
    }

    public void onClose() {
    }
}
