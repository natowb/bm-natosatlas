package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.utils.LogUtil;

import java.util.List;

public final class Waypoints {

    private static final WaypointsStorage STORAGE = new WaypointsStorage();

    private Waypoints() {
    }

    public static void load() {
        STORAGE.load();
        LogUtil.info("Waypoints", "Loaded {} waypoints", STORAGE.getAll().size());
    }

    public static void save() {
        STORAGE.save();
        LogUtil.info("Waypoints", "Saved {} waypoints", STORAGE.getAll().size());
    }

    public static List<Waypoint> getAll() {
        return STORAGE.getAll();
    }

    public static void add(Waypoint wp) {
        STORAGE.getAll().add(wp);
        LogUtil.info("Waypoints", "Added waypoint {}", wp.name);
        save();
    }

    public static void remove(Waypoint wp) {
        STORAGE.getAll().remove(wp);
        LogUtil.info("Waypoints", "Removed waypoint {}", wp.name);
        save();
    }

    public static void update(Waypoint oldWp, Waypoint newWp) {
        List<Waypoint> list = STORAGE.getAll();
        int index = list.indexOf(oldWp);

        if (index >= 0) {
            list.set(index, newWp);
            LogUtil.info("Waypoints", "Updated waypoint {} -> {}", oldWp.name, newWp.name);
            save();
        }
    }
}
