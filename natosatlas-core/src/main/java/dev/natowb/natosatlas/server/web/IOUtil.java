package dev.natowb.natosatlas.server.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class IOUtil {
    public static byte[] readAll(InputStream in) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    public static InputStream getWebInputStream() {
        return IOUtil.class.getResourceAsStream("/assets/natosatlas/www/index.html");
    }
}
