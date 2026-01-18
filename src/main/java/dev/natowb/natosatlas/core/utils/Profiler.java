package dev.natowb.natosatlas.core.utils;

public final class Profiler {

    private static volatile boolean ENABLED = true;

    public static void setEnabled(boolean enabled) {
        ENABLED = enabled;
    }

    private long last;
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
            LogUtil.debug("PROF", "-> " + label);
        }
    }

    private static final Profiler NO_OP = new Profiler(null, false);

    public void mark(String name) {
        if (!active) return;

        long now = System.nanoTime();
        long ms = (now - last) / 1_000_000L;
        LogUtil.debug("PROF", "  - " + name + ": " + ms + " ms");
        last = now;
    }

    public void end() {
        if (!active) return;

        long now = System.nanoTime();
        long ms = (now - last) / 1_000_000L;
        LogUtil.debug("PROF", "<- " + label + " total: " + ms + " ms");
    }
}
