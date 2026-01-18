package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.platform.PlatformPainterDefault;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class NacPainterST extends PlatformPainterDefault {

    @Override
    public void drawString(String text, int x, int y, int color, boolean shadow) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        mc.textRenderer.draw(text, x, y, color, shadow);
    }

    @Override
    public int getStringWidth(String str) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return mc.textRenderer.getWidth(str);
    }

    @Override
    public int getMinecraftTextureId(String string) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return mc.textureManager.getTextureId(string);
    }

    @Override
    public void drawCenteredString(String text, int centerX, int y, int color) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        mc.textRenderer.drawWithShadow(text, centerX - mc.textRenderer.getWidth(text) / 2, y, color);
    }
}
