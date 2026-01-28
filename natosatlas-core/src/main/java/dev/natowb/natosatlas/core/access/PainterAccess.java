package dev.natowb.natosatlas.core.access;

import dev.natowb.natosatlas.core.texture.TextureProvider;
import org.lwjgl.opengl.GL11;

public abstract class PainterAccess {

    private static PainterAccess instance;

    public static void setInstance(PainterAccess instance) {
        PainterAccess.instance = instance;
    }

    public static PainterAccess get() {
        return instance;
    }

    public void drawRect(int x1, int y1, int x2, int y2, int argbColor) {
        if (x1 < x2) {
            int t = x1;
            x1 = x2;
            x2 = t;
        }
        if (y1 < y2) {
            int t = y1;
            y1 = y2;
            y2 = t;
        }

        float a = (argbColor >> 24 & 255) / 255f;
        float r = (argbColor >> 16 & 255) / 255f;
        float g = (argbColor >> 8 & 255) / 255f;
        float b = (argbColor & 255) / 255f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x1, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x2, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }


    public void drawLine(float x1, float y1, float x2, float y2) {
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
    }

    public void drawTexture(int textureId, int x, int y, int width, int height) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0f, 1f);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1f, 1f);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1f, 0f);
        GL11.glVertex2f(x + width, y);
        GL11.glTexCoord2f(0f, 0f);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    public void drawTexturedQuad(int argb, float u1, float v1, float u2, float v2) {
        float a = ((argb >> 24) & 0xFF) / 255f;
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(r, g, b, a);

        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(-1, 1);

        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(1, 1);

        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(1, -1);

        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(-1, -1);

        GL11.glEnd();
    }

    public void drawTexturedQuad(float u1, float v1, float u2, float v2) {
        int argb = 0xFFFFFFFF;
        float a = ((argb >> 24) & 0xFF) / 255f;
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(r, g, b, a);

        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(-1, 1);

        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(1, 1);

        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(1, -1);

        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(-1, -1);

        GL11.glEnd();
    }


    public void drawTextureRegion(int textureId, int x, int y, int u, int v, int w, int h) {
        float texW = 256f;
        float texH = 256f;

        float u1 = u / texW;
        float v1 = v / texH;
        float u2 = (u + w) / texW;
        float v2 = (v + h) / texH;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x, y + h);
        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x + w, y + h);
        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x + w, y);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }


    public void drawIcon(int iconIndex, int x, int y, int size, int argbColor) {
        final int ICON_SIZE = 16;
        final int SHEET_SIZE = 128;
        final int ICONS_PER_ROW = SHEET_SIZE / ICON_SIZE;

        int iconX = (iconIndex % ICONS_PER_ROW) * ICON_SIZE;
        int iconY = (iconIndex / ICONS_PER_ROW) * ICON_SIZE;

        float u1 = iconX / (float) SHEET_SIZE;
        float v1 = iconY / (float) SHEET_SIZE;
        float u2 = (iconX + ICON_SIZE) / (float) SHEET_SIZE;
        float v2 = (iconY + ICON_SIZE) / (float) SHEET_SIZE;

        int a = (argbColor >> 24) & 0xFF;
        int r = (argbColor >> 16) & 0xFF;
        int g = (argbColor >> 8) & 0xFF;
        int b = argbColor & 0xFF;

        float rf = r / 255f;
        float gf = g / 255f;
        float bf = b / 255f;
        float af = a / 255f;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureProvider.getIconTexture());
        GL11.glColor4f(rf, gf, bf, af);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(u1, v2);
        GL11.glVertex2f(x, y + size);

        GL11.glTexCoord2f(u2, v2);
        GL11.glVertex2f(x + size, y + size);

        GL11.glTexCoord2f(u2, v1);
        GL11.glVertex2f(x + size, y);

        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex2f(x, y);
        GL11.glEnd();

        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    public void drawIconWithShadow(int iconIndex, int x, int y, int size, int argbColor) {
        int alpha = argbColor & 0xFF000000;
        int shadowRGB = (argbColor & 0x00FCFCFC) >> 2;
        int shadowColor = alpha | shadowRGB;

        drawIcon(iconIndex, x + 1, y + 1, size, shadowColor);
        drawIcon(iconIndex, x, y, size, argbColor);
    }


    public abstract int getStringWidth(String text);

    public abstract void drawString(String text, int x, int y, int color);

    public abstract void drawString(String text, int x, int y, int color, boolean shadow);

    public abstract void drawCenteredString(String text, int centerX, int y, int color);

    public abstract int getMinecraftTextureId(String string);


    public void drawStringWithShadow(String text, int x, int y, int argbColor) {
        int alpha = argbColor & 0xFF000000;
        int shadowRGB = (argbColor & 0x00FCFCFC) >> 2;
        int shadowColor = alpha | shadowRGB;

        drawString(text, x + 1, y + 1, shadowColor);
        drawString(text, x, y, argbColor);
    }
}