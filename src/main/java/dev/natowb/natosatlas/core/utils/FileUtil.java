package dev.natowb.natosatlas.core.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class FileUtil {

    private FileUtil() {}

    public static List<String> readLines(File file) {
        if (!file.exists()) return Collections.emptyList();

        try {
            return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LogUtil.error("IO", e, "Failed to read file {}", file.getName());
            return Collections.emptyList();
        }
    }

    public static boolean writeLines(File file, List<String> lines) {
        try {
            ensureParent(file);

            Path dir = file.getParentFile().toPath();
            Path temp = Files.createTempFile(dir, "na_tmp_", ".txt");

            Files.write(temp, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);

            Files.move(temp, file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);

            return true;

        } catch (Exception e) {
            LogUtil.error("IO", e, "Failed to write file {}", file.getName());
            return false;
        }
    }


    public static Map<String, String> readKeyValueMap(File file) {
        Map<String, String> map = new HashMap<>();

        for (String line : readLines(file)) {
            if (!line.contains("=")) continue;
            String[] parts = line.split("=", 2);
            map.put(parts[0].trim(), parts[1].trim());
        }

        return map;
    }

    public static boolean writeKeyValueMap(File file, Map<String, String> map) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            lines.add(e.getKey() + "=" + e.getValue());
        }
        return writeLines(file, lines);
    }

    private static void ensureParent(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
}
