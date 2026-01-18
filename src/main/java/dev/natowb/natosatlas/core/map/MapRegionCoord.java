package dev.natowb.natosatlas.core.map;

public final class MapRegionCoord {

    private final int x;
    private final int z;

    public MapRegionCoord(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public long toKey() {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    public static MapRegionCoord fromKey(long key) {
        int x = (int) (key >> 32);
        int z = (int) (key & 0xFFFFFFFFL);
        return new MapRegionCoord(x, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapRegionCoord)) return false;
        MapRegionCoord other = (MapRegionCoord) o;
        return x == other.x && z == other.z;
    }

    @Override
    public int hashCode() {
        return (x * 7340033) ^ z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + z + ")";
    }
}
