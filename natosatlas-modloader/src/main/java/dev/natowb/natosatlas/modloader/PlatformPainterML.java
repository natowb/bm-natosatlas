package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.core.platform.PlatformPainterDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import org.lwjgl.opengl.GL11;

public class PlatformPainterML extends PlatformPainterDefault {

    @Override
    public void drawString(String text, int x, int y, int color, boolean shadow) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        ModLoader.getMinecraftInstance().textRenderer.draw(text, x, y, color);
    }

    @Override
    public int getStringWidth(String str) {
        return ModLoader.getMinecraftInstance().textRenderer.getWidth(str);
    }


    @Override
    public int getMinecraftTextureId(String string) {
        return ModLoader.getMinecraftInstance().textureManager.getTextureId(string);
    }

    @Override
    public void drawCenteredString(String text, int centerX, int y, int color) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        mc.textRenderer.drawWithShadow(text, centerX - mc.textRenderer.getWidth(text) / 2, y, color);
    }
}
