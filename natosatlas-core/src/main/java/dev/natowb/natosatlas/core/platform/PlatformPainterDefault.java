package dev.natowb.natosatlas.core.platform;

import org.lwjgl.opengl.GL11;

public class PlatformPainterDefault implements PlatformPainter {

    @Override
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

    @Override
    public void drawGrid(
            double cellSize,
            int canvasW, int canvasH,
            double scrollX, double scrollY,
            double zoom,
            int argbColor
    ) {
        float a = (argbColor >> 24 & 255) / 255f;
        float r = (argbColor >> 16 & 255) / 255f;
        float g = (argbColor >> 8 & 255) / 255f;
        float b = (argbColor & 255) / 255f;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, a);

        double left = scrollX;
        double top = scrollY;
        double right = scrollX + canvasW / zoom;
        double bottom = scrollY + canvasH / zoom;

        double startX = Math.floor(left / cellSize) * cellSize;
        double startY = Math.floor(top / cellSize) * cellSize;

        double maxX = right + cellSize;
        double maxY = bottom + cellSize;

        for (double x = startX; x <= maxX; x += cellSize) {
            drawLine((int) x, (int) top - 16, (int) x, (int) bottom + 16);
        }

        for (double y = startY; y <= maxY; y += cellSize) {
            drawLine((int) left - 16, (int) y, (int) right + 16, (int) y);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
    }

    @Override
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

    public void drawTexturedQuad(float u1, float v1, float u2, float v2) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(1f, 1f, 1f, 1f);
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



    // TODO: these things are implemented by the backend ie station api or ModLoader.
    @Override
    public int getStringWidth(String text) {
        return 0;
    }

    @Override
    public void drawString(String text, int x, int y, int color) {
        drawString(text, x, y, color, false);
    }

    @Override
    public void drawString(String text, int x, int y, int color, boolean shadow) {

    }

    @Override
    public void drawCenteredString(String text, int centerX, int y, int color) {
    }

    @Override
    public int getMinecraftTextureId(String string) {
        return -1;
    }
}
