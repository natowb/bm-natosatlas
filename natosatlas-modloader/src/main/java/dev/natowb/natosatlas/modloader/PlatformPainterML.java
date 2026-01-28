package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.client.access.PainterAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import org.lwjgl.opengl.GL11;

public class PlatformPainterML extends PainterAccess {

    @Override
    public void drawString(String text, int x, int y, int color, boolean shadow) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        mc.textRenderer.draw(text, x, y, color);
    }

    @Override
    public int getStringWidth(String str) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        return mc.textRenderer.getWidth(str);
    }

    @Override
    public void drawString(String text, int x, int y, int color) {
        drawString(text, x, y, color, false);
    }

    @Override
    public int getMinecraftTextureId(String string) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        return mc.textureManager.getTextureId(string);
    }

    @Override
    public void drawCenteredString(String text, int centerX, int y, int color) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        mc.textRenderer.drawWithShadow(text, centerX - mc.textRenderer.getWidth(text) / 2, y, color);
    }
}
