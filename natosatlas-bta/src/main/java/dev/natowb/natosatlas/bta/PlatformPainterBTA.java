package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.client.access.PainterAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.Texture;
import org.lwjgl.opengl.GL11;

public class PlatformPainterBTA extends PainterAccess {

    @Override
    public void drawString(String text, int x, int y, int color, boolean shadow) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        mc.font.drawString(text, x, y, color, shadow);
    }

    @Override
    public int getStringWidth(String str) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return mc.font.getStringWidth(str);
    }

    @Override
    public void drawString(String text, int x, int y, int color) {
        drawString(text, x, y, color, false);
    }

    @Override
    public int getMinecraftTextureId(String string) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        Texture texture = mc.textureManager.loadTextureNoDefault(string);
        if (texture == null) return -1;
        return texture.id();
    }

    @Override
    public void drawCenteredString(String text, int centerX, int y, int color) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        mc.font.drawStringWithShadow(text, centerX - getStringWidth(text) / 2, y, color);
    }

}
