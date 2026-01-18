package dev.natowb.natosatlas.core.persistence;


import dev.natowb.natosatlas.core.utils.FileUtil;

import java.io.File;
import java.util.*;

public abstract class TextStorage {

    protected final Map<String, String> values = new HashMap<>();
    private final File file;

    protected TextStorage(File file) {
        this.file = file;
    }

    public final void load() {
        values.clear();
        if (!file.exists()) {
            save();
            return;
        }

        List<String> lines = FileUtil.readLines(file);
        for (String line : lines) {
            if (!line.contains("=")) continue;
            String[] p = line.split("=", 2);
            values.put(p[0].trim(), p[1].trim());
        }

        onLoad();
    }

    public final void save() {
        onSave();

        List<String> lines = new ArrayList<>();
        values.forEach((k, v) -> lines.add(k + "=" + v));

        FileUtil.writeLines(file, lines);
    }

    protected abstract String getName();

    protected abstract void onLoad();

    protected abstract void onSave();

    protected boolean getBoolean(String key, boolean def) {
        return Boolean.parseBoolean(values.getOrDefault(key, Boolean.toString(def)));
    }

    protected int getInt(String key, int def) {
        try { return Integer.parseInt(values.getOrDefault(key, Integer.toString(def))); }
        catch (Exception e) { return def; }
    }

    protected float getFloat(String key, float def) {
        try { return Float.parseFloat(values.getOrDefault(key, Float.toString(def))); }
        catch (Exception e) { return def; }
    }

    protected <E extends Enum<E>> E getEnum(String key, Class<E> clazz, E def) {
        try { return Enum.valueOf(clazz, values.getOrDefault(key, def.name())); }
        catch (Exception e) { return def; }
    }

    protected void put(String key, Object value) {
        values.put(key, String.valueOf(value));
    }
}
