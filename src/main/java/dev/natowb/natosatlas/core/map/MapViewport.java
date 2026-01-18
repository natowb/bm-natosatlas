package dev.natowb.natosatlas.core.map;

import org.lwjgl.opengl.GL11;

public class MapViewport {

    public void begin(MapContext ctx, int scaleFactor, int scaledHeight) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        GL11.glScissor(
                ctx.canvasX * scaleFactor,
                (scaledHeight - (ctx.canvasY + ctx.canvasH)) * scaleFactor,
                ctx.canvasW * scaleFactor,
                ctx.canvasH * scaleFactor
        );

        GL11.glPushMatrix();
        GL11.glTranslatef(ctx.canvasX, ctx.canvasY, 0);
        GL11.glScalef(ctx.zoom, ctx.zoom, 1);
        GL11.glTranslatef(-ctx.scrollX, -ctx.scrollY, 0);
    }

    public void end() {
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
