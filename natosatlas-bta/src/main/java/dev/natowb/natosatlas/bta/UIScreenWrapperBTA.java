package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ScaledResolution;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.util.debug.DebugRender;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;

public class UIScreenWrapperBTA extends Screen {
	private static final Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
	private final UIScreen screen;

	public UIScreenWrapperBTA(UIScreen screen) {
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
		ScaledResolution ss = mc.resolution;
		UIScaleInfo info = new UIScaleInfo(ss.getScale(), ss.getScaledWidthScreenCoords(), ss.getScaledHeightScreenCoords());
		screen.render(mouseX, mouseY, delta, info);
	}

	@Override
	public void updateEvents() {
		int x = Mouse.getEventX() * width / mc.gameWindow.getWidthPixels();
		int y = height - Mouse.getEventY() * height / mc.gameWindow.getHeightPixels() - 1;
		while(Mouse.next()) {
			screen.handleRawMouseEvent(x, y, Mouse.getEventButton(), Mouse.getEventButtonState(), Mouse.getEventDWheel());
		}

		while(Keyboard.next()) {
			if (!Keyboard.getEventKeyState() || !(Boolean)this.mc.gameSettings.showFrameTimes.value || !DebugRender.keyPressed(Keyboard.getEventKey())) {
				int eventKey = Keyboard.getEventKey();
				char eventChar = Keyboard.getEventCharacter();
				if (eventKey == 0 && Character.isDefined(eventChar)) {
					this.keyPressed(eventChar, eventKey, x, y);
					return;
				}

				if (Keyboard.getEventKeyState()) {
					this.keyPressed(eventChar, eventKey, x, y);
				}
			}
		}
	}

	@Override
	public void keyPressed(char eventCharacter, int eventKey, int mx, int my) {
		screen.keyPressed(eventCharacter, eventKey);
	}


	@Override
	public void selectNextField() {
		screen.handleTab();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

