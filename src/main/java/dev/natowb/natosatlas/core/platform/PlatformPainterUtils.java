package dev.natowb.natosatlas.core.platform;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public final class PlatformPainterUtils {

    public static BufferedImage pixelsToBufferedImage(int[] pixels, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, width, height, pixels, 0, width);
        return img;
    }

    public static int createBlankTexture(int width, int height) {
        int[] pixels = new int[width * height];

        for (int z = 0; z < width; z++) {
            for (int x = 0; x < height; x++) {
                pixels[x + z * width] = 0xFFFFFFFF;
            }
        }
        return createTextureFromBufferedImage(pixelsToBufferedImage(pixels, width, height));
    }

    public static int createTextureFromBufferedImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = convertToByteBuffer(pixels, width, height);

        int textureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                buffer
        );

        return textureId;
    }

    public static void updateTexture(int textureId, int width, int height, int[] pixels) {
        ByteBuffer buffer = convertToByteBuffer(pixels, width, height);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glTexSubImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                0,
                0,
                width,
                height,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                buffer
        );
    }

    public static void deleteTexture(int textureId) {
        GL11.glDeleteTextures(textureId);
    }

    private static ByteBuffer convertToByteBuffer(int[] pixels, int width, int height) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[x + y * width];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();
        return buffer;
    }
}
