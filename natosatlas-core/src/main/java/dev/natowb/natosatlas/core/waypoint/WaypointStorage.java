package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class WaypointStorage {

    private final List<Waypoint> waypoints = new ArrayList<>();

    public List<Waypoint> getAll() {
        return waypoints;
    }

    public void load(File file) {
        waypoints.clear();

        if (!file.exists()) {
            save(file);
            return;
        }

        List<String> lines = FileUtil.readLines(file);

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] p = line.split(":");
            if (p.length < 6) continue;

            String name = p[0];
            int x = parse(p[1]);
            int y = parse(p[2]);
            int z = parse(p[3]);
            boolean visible = Boolean.parseBoolean(p[4]);
            String color = p[5];

            if (color == null || color.isEmpty()) color = "FFFFFF";

            Waypoint wp = new Waypoint(name, x, y, z);
            wp.visible = visible;
            wp.color = color;

            waypoints.add(wp);
        }
    }

    public void save(File file) {
        List<String> lines = new ArrayList<>();

        for (Waypoint wp : waypoints) {
            String color = (wp.color == null || wp.color.isEmpty()) ? "FFFFFF" : wp.color;

            lines.add(
                    wp.name + ":" +
                            wp.x + ":" +
                            wp.y + ":" +
                            wp.z + ":" +
                            wp.visible + ":" +
                            color
            );
        }

        FileUtil.writeLines(file, lines);
    }

    private int parse(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return 0; }
    }
}
