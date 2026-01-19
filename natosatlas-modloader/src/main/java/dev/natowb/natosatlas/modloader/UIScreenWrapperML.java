package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.src.ModLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class UIScreenWrapperML extends Screen {
    private final Minecraft minecraft = ModLoader.getMinecraftInstance();
    private final UIScreen screen;

    public UIScreenWrapperML(UIScreen screen) {
        this.screen = screen;
    }

    @Override
    public void init() {
        super.init();
        screen.init(width, height);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
        screen.onClose();
    }

    @Override
    public void tick() {
        screen.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        ScreenScaler ss = new ScreenScaler(minecraft.options, minecraft.displayWidth, minecraft.displayHeight);
        screen.render(mouseX, mouseY, delta, new UIScaleInfo(ss.scaleFactor, ss.getScaledWidth(), ss.getScaledHeight()));
    }

    @Override
    public void onMouseEvent() {
        int x = Mouse.getEventX() * width / minecraft.displayWidth;
        int y = height - Mouse.getEventY() * height / minecraft.displayHeight - 1;
        screen.handleRawMouseEvent(x, y, Mouse.getEventButton(), Mouse.getEventButtonState(), Mouse.getEventDWheel());
    }

    @Override
    protected void keyPressed(char var1, int var2) {
        screen.keyPressed(var1, var2);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
    }

    @Override
    protected void mouseReleased(int var1, int var2, int var3) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
