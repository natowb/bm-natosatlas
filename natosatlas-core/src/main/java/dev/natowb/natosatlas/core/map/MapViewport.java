package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import org.lwjgl.opengl.GL11;

public class MapViewport {

    public void begin(MapContext ctx, UIScaleInfo scaleInfo) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        GL11.glScissor(
                ctx.canvasX * scaleInfo.scaleFactor,
                (scaleInfo.scaledHeight - (ctx.canvasY + ctx.canvasH)) * scaleInfo.scaleFactor,
                ctx.canvasW * scaleInfo.scaleFactor,
                ctx.canvasH * scaleInfo.scaleFactor
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
