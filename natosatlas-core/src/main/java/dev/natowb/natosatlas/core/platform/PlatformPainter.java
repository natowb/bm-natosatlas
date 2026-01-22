package dev.natowb.natosatlas.core.platform;

public interface PlatformPainter {
    void drawRect(int x1, int y1, int x2, int y2, int argbColor);

    void drawLine(float x1, float y1, float x2, float y2);

    void drawTexture(int textureId, int x, int y, int width, int height);

    int getStringWidth(String text);

    void drawString(String text, int x, int y, int color);

    void drawString(String text, int x, int y, int color, boolean shadow);

    void drawCenteredString(String text, int centerX, int y, int color);

    int getMinecraftTextureId(String string);

    void drawTexturedQuad(float u1, float v1, float u2, float v2);
}