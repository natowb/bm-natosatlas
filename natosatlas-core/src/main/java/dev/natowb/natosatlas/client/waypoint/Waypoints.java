package dev.natowb.natosatlas.client.waypoint;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.settings.Settings;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.client.NAClientPaths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Waypoints {


    private static final WaypointStorage STORAGE = new WaypointStorage();

    private Waypoints() {
    }

    private static File getFile() {
        String fileName = String.format("%s.DIM%d.points", NAClientPaths.getWorldSaveName(), NAClient.get().getPlatform().world.getDimensionId());
        if (!Settings.useReiMinimapWaypointStorage) {
            return new File(NAClientPaths.getWorldDataPath().toFile(), fileName);
        }

        Path minimapPath = NAClientPaths.getMinecraftPath().resolve("mods/rei_minimap");

        try {
            Files.createDirectories(minimapPath);
        } catch (IOException ignored) {
        }

        return new File(minimapPath.toFile(), fileName);
    }

    public static void load() {
        STORAGE.load(getFile());
        LogUtil.debug("Loaded {} waypoints", STORAGE.getAll().size());
    }

    public static void save() {
        STORAGE.save(getFile());
        LogUtil.debug("Saved {} waypoints", STORAGE.getAll().size());
    }

    public static List<Waypoint> getAll() {
        return STORAGE.getAll();
    }

    public static void add(Waypoint wp) {
        STORAGE.getAll().add(wp);
        LogUtil.debug("Added waypoint {}", wp.name);
        save();
    }

    public static void remove(Waypoint wp) {
        STORAGE.getAll().remove(wp);
        LogUtil.debug("Removed waypoint {}", wp.name);
        save();
    }

    public static void update(Waypoint oldWp, Waypoint newWp) {
        List<Waypoint> list = STORAGE.getAll();
        int index = list.indexOf(oldWp);

        if (index >= 0) {
            list.set(index, newWp);
            LogUtil.debug("Updated waypoint {} -> {}", oldWp.name, newWp.name);
            save();
        }
    }
}
