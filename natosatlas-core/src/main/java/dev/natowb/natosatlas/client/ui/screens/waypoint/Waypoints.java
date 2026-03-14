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
        return new File(NAPaths.getWorldDataPath().toFile(), fileName);
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

        List<Waypoint> existing = STORAGE.getAll();

        boolean alreadyExists = existing.stream().anyMatch(w ->
                w.name.equals(wp.name) &&
                        w.x == wp.x &&
                        w.y == wp.y &&
                        w.z == wp.z &&
                        w.color == wp.color
        );

        if (alreadyExists) {
            LogUtil.warn(
                    "Skipped adding waypoint '{}' because a waypoint with identical details already exists",
                    wp.name
            );
            return;
        }

        existing.add(wp);
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
