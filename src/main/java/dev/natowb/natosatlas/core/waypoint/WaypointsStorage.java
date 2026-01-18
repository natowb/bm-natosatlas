package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.persistence.TextStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class WaypointsStorage extends TextStorage {

    private final List<Waypoint> waypoints = new ArrayList<>();

    public WaypointsStorage() {
        super(new File(NatosAtlas.get().getWorldDataPath().toFile(), "waypoints.txt"));
    }

    public List<Waypoint> getAll() {
        return waypoints;
    }

    @Override
    protected String getName() {
        return "Waypoints";
    }

    @Override
    protected void onLoad() {
        waypoints.clear();

        int index = 0;
        while (true) {
            String prefix = "wp." + index + ".";
            if (!values.containsKey(prefix + "name")) break;

            String name = values.get(prefix + "name");
            int x = getInt(prefix + "x", 0);
            int y = getInt(prefix + "y", 0);
            int z = getInt(prefix + "z", 0);

            waypoints.add(new Waypoint(name, x, y, z));
            index++;
        }
    }

    @Override
    protected void onSave() {
        values.clear();

        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint wp = waypoints.get(i);
            String prefix = "wp." + i + ".";
            put(prefix + "name", wp.name);
            put(prefix + "x", wp.x);
            put(prefix + "y", wp.y);
            put(prefix + "z", wp.z);
        }
    }
}
