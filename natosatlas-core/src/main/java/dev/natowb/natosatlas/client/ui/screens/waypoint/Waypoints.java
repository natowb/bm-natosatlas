package dev.natowb.natosatlas.client.ui.screens.waypoint;

import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.client.ui.screens.settings.Settings;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.core.NAPaths;

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
        String fileName = String.format("%s.DIM%d.points", NAPaths.getWorldSaveName(), ClientWorldAccess.get().getWorldInfo().getDimensionId());
        if (!Settings.useReiMinimapWaypointStorage) {
            return new File(NAPaths.getWorldDataPath().toFile(), fileName);
        }

        Path minimapPath = NAPaths.getMinecraftPath().resolve("mods/rei_minimap");

        try {
            Files.createDirectories(minimapPath);
        } catch (IOException ignored) {
        }

        return new File(minimapPath.toFile(), fileName);
    }

    public static void load() {
        STORAGE.load(getFile());
        LogUtil.trace("Loaded {} waypoints", STORAGE.getAll().size());
    }

    public static void save() {
        STORAGE.save(getFile());
        LogUtil.trace("Saved {} waypoints", STORAGE.getAll().size());
    }

    public static List<Waypoint> getAll() {
        return STORAGE.getAll();
    }

    public static void add(Waypoint wp) {
        STORAGE.getAll().add(wp);
        LogUtil.trace("Added waypoint {}", wp.name);
        save();
    }

    public static void remove(Waypoint wp) {
        STORAGE.getAll().remove(wp);
        LogUtil.trace("Removed waypoint {}", wp.name);
        save();
    }

    public static void update(Waypoint oldWp, Waypoint newWp) {
        List<Waypoint> list = STORAGE.getAll();
        int index = list.indexOf(oldWp);

        if (index >= 0) {
            list.set(index, newWp);
            LogUtil.trace("Updated waypoint {} -> {}", oldWp.name, newWp.name);
            save();
        }
    }
}
