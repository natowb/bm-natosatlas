package dev.natowb.natosatlas.stationapi.client;

import dev.natowb.natosatlas.client.platform.ClientPlatform;
import dev.natowb.natosatlas.client.platform.ScreenAccess;
import dev.natowb.natosatlas.client.ui.elements.UIScaleInfo;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ScreenScaler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class STClientPlatform extends ClientPlatform {
    public STClientPlatform() {
        super(new STPainter(), new ScreenAccess() {
            @Override
            public void openNacScreen(UIScreen screen) {
                Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
                if (screen == null) {
                    mc.setScreen(null);
                } else {
                    mc.setScreen(new Screen() {
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
                            ScreenScaler ss = new ScreenScaler(mc.options, mc.displayWidth, mc.displayHeight);
                            UIScaleInfo info = new UIScaleInfo(ss.scaleFactor, ss.getScaledWidth(), ss.getScaledHeight());
                            screen.render(mouseX, mouseY, delta, info);
                        }

                        @Override
                        public void onMouseEvent() {
                            int x = Mouse.getEventX() * width / minecraft.displayWidth;
                            int y = height - Mouse.getEventY() * height / minecraft.displayHeight - 1;
                            screen.handleRawMouseEvent(x, y, Mouse.getEventButton(), Mouse.getEventButtonState(), Mouse.getEventDWheel());
                        }

                        @Override
                        protected void keyPressed(char character, int keyCode) {
                            screen.keyPressed(character, keyCode);
                        }


                        @Override
                        public void handleTab() {
                            screen.handleTab();
                        }

                        @Override
                        public boolean shouldPause() {
                            return false;
                        }
                    });
                }
            }

            @Override
            public void playSound(String sound, float volume, float pitch) {
                Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
                mc.soundManager.playSound(sound, volume, pitch);

            }
        });
    }
}
