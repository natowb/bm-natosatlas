package dev.natowb.natosatlas.client.waypoint;

import dev.natowb.natosatlas.core.util.FileUtil;

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

            int color = parseColor(p[5]);

            Waypoint wp = new Waypoint(name, x, y, z);
            wp.visible = visible;
            wp.color = color;

            waypoints.add(wp);
        }
    }

    public void save(File file) {
        List<String> lines = new ArrayList<>();

        for (Waypoint wp : waypoints) {
            int c = wp.color == 0 ? 0xFFFFFF : wp.color;
            String hex = String.format("%06X", c & 0xFFFFFF);
            lines.add(wp.name + ":" + wp.x + ":" + wp.y + ":" + wp.z + ":" + wp.visible + ":" + hex);
        }

        FileUtil.writeLines(file, lines);
    }

    private int parse(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseColor(String s) {
        if (s == null || s.isEmpty()) return 0xFFFFFF;
        try {
            return Integer.parseInt(s, 16) & 0xFFFFFF;
        } catch (Exception e) {
            return 0xFFFFFF;
        }
    }
}
