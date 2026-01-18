package dev.natowb.natosatlas.core.utils;

public final class Profiler {
    private static final boolean ENABLED = true;

    private long last;
    private long start;
    private final String label;
    private final boolean active;

    public static Profiler start(String label) {
        if (!ENABLED) {
            return NO_OP;
        }
        return new Profiler(label, true);
    }

    private Profiler(String label, boolean active) {
        this.label = label;
        this.active = active;

        if (active) {
            this.last = System.nanoTime();
            this.start = this.last;
            LogUtil.debug("ProfileStart(" + label + ")");
        }
    }

    private static final Profiler NO_OP = new Profiler(null, false);

    public void mark(String name) {
        if (!active) return;
        long now = System.nanoTime();
        long ms = (now - last) / 1_000_000L;
        LogUtil.debug("  ProfileMark(" + name + ") time: " + ms + " ms");
        last = now;
    }

    public void end() {
        if (!active) return;
        long now = System.nanoTime();
        long totalMs = (now - start) / 1_000_000L;
        long lastMs = (now - last) / 1_000_000L;
        LogUtil.debug("ProfileEnd(" + label + ") time: " + lastMs + "ms, total: " + totalMs + "ms");
    }
}
