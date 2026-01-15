package dev.natowb.natosatlas.core.config;

import dev.natowb.natosatlas.core.glue.NacPlatformAPI;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class NacConfigStorage {

    private static final Map<String, String> values = new HashMap<>();

    public static void load() {
        File file = new File(NacPlatformAPI.get().getDataDirectory(), "settings.txt");

        if (!file.exists()) {
            save();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            values.clear();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.contains("=")) continue;
                String[] parts = line.split("=", 2);
                values.put(parts[0].trim(), parts[1].trim());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        File file = new File(NacPlatformAPI.get().getDataDirectory(), "settings.txt");

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> e : values.entrySet()) {
                out.println(e.getKey() + "=" + e.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Boolean
    public static void setBoolean(String key, boolean value) {
        values.put(key, Boolean.toString(value));
        save();
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(values.getOrDefault(key, "false"));
    }

    // Enum
    public static <E extends Enum<E>> void setEnum(String key, E value) {
        values.put(key, value.name());
        save();
    }

    public static <E extends Enum<E>> E getEnum(String key, Class<E> clazz) {
        try {
            return Enum.valueOf(clazz, values.get(key));
        } catch (Exception e) {
            return clazz.getEnumConstants()[0];
        }
    }

    public static <E extends Enum<E>> void cycleEnum(String key, Class<E> clazz) {
        E current = getEnum(key, clazz);
        E[] all = clazz.getEnumConstants();
        int next = (current.ordinal() + 1) % all.length;
        setEnum(key, all[next]);
    }
}
