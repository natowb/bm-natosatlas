package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.glue.NacPlatform;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class NACSettings {

    public enum EntityDisplayMode {
        ALL,
        ONLY_PLAYER,
        NONE;

        public EntityDisplayMode next() {
            int i = this.ordinal() + 1;
            if (i >= values().length) i = 0;
            return values()[i];
        }

        public String getLabel() {
            switch (this) {
                case ONLY_PLAYER: return "Players";
                case ALL: return "All";
                case NONE: return "None";
            }
            return "Unknown";
        }
    }

    private static EntityDisplayMode entityDisplayMode = EntityDisplayMode.ALL;
    private static boolean enableMapGrid = true;
    private static boolean enableMapDebugInfo = false;

    public static boolean isMapGridEnabled() {
        return enableMapGrid;
    }

    public static boolean isMapDebugInfoEnabled() {
        return enableMapDebugInfo;
    }

    public static EntityDisplayMode getEntityDisplayMode() {
        return entityDisplayMode;
    }

    public static boolean toggleMapGrid() {
        enableMapGrid = !enableMapGrid;
        save();
        return enableMapGrid;
    }

    public static boolean toggleDebugInfo() {
        enableMapDebugInfo = !enableMapDebugInfo;
        save();
        return enableMapDebugInfo;
    }

    public static EntityDisplayMode cycleEntityDisplayMode() {
        entityDisplayMode = entityDisplayMode.next();
        save();
        return entityDisplayMode;
    }



    private NACSettings() {}

    public static void load() {

        File file = new File(NacPlatform.get().getDataDirectory(), "settings.txt" );

        if (!file.exists()) {
            save();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Map<String, String> map = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("=")) continue;
                String[] parts = line.split("=", 2);
                map.put(parts[0].trim(), parts[1].trim());
            }

            if (map.containsKey("entityDisplayMode")) {
                try {
                    entityDisplayMode = EntityDisplayMode.valueOf(map.get("entityDisplayMode"));
                } catch (Exception ignored) {}
            }

            if (map.containsKey("showGrid")) {
                enableMapGrid = Boolean.parseBoolean(map.get("showGrid"));
            }

            if (map.containsKey("showDebugInfo")) {
                enableMapDebugInfo = Boolean.parseBoolean(map.get("showDebugInfo"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        File file = new File(NacPlatform.get().getDataDirectory(), "settings.txt" );

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println("entityDisplayMode=" + entityDisplayMode.name());
            out.println("showGrid=" + enableMapGrid);
            out.println("showDebugInfo=" + enableMapDebugInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
