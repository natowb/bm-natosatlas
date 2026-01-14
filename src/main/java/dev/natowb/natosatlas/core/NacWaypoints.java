package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.glue.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacWaypoint;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


// FIXME: this is a super dump way to handle this, but im wanting to get something working. and having it readable is a plus.
public final class NacWaypoints {

    private static final List<NacWaypoint> WAYPOINTS = new ArrayList<>();

    private NacWaypoints() {
    }

    public static List<NacWaypoint> getAll() {
        return WAYPOINTS;
    }

    public static void add(NacWaypoint wp) {
        WAYPOINTS.add(wp);
        save();
    }

    public static void remove(NacWaypoint wp) {
        WAYPOINTS.remove(wp);
        save();
    }

    public static void update(NacWaypoint oldWp, NacWaypoint newWp) {
        int index = WAYPOINTS.indexOf(oldWp);
        if (index >= 0) {
            WAYPOINTS.set(index, newWp);
            save();
        }
    }

    public static void load() {
        WAYPOINTS.clear();

        File file = new File(NacPlatformAPI.get().getWorldDirectory(), "waypoints.txt");

        if (!file.exists()) {
            save();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(";", 4);
                if (parts.length != 4) continue;

                String name = parts[0];
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);

                WAYPOINTS.add(new NacWaypoint(name, x, y, z));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void save() {

        File file = new File(NacPlatformAPI.get().getWorldDirectory(), "waypoints.txt");


        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {

            for (NacWaypoint wp : WAYPOINTS) {
                out.println(wp.name + ";" + wp.x + ";" + wp.y + ";" + wp.z);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
