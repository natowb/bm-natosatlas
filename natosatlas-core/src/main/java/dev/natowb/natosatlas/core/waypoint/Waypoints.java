package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;

import java.io.File;
import java.util.List;

public final class Waypoints {

    private static final WaypointsStorage STORAGE = new WaypointsStorage();

    private Waypoints() {
    }

    public static void load() {
        STORAGE.load(new File(NAPaths.getWorldDataPath().toFile(), "waypoints.txt"));
        LogUtil.info("Loaded {} waypoints", STORAGE.getAll().size());
    }

    public static void save() {
        STORAGE.save(new File(NAPaths.getWorldDataPath().toFile(), "waypoints.txt"));
        LogUtil.info("Saved {} waypoints", STORAGE.getAll().size());
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
