package dev.natowb.natosatlas.core.data;

public final class NACoord {

    public final int x;
    public final int y;
    public final int z;

    public NACoord(int x, int z) {
        this.x = x;
        this.z = z;
        this.y = -1;
    }

    public NACoord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long toKey() {
        long lx = ((long) x & 0x3FFFFFFL) << 38;
        long ly = ((long) y & 0xFFFL) << 26;
        long lz = ((long) z & 0x3FFFFFFL);
        return lx | ly | lz;
    }

    public static NACoord from(int x, int z) {
        return new NACoord(x, z);
    }

    public static NACoord from(int x, int y, int z) {
        return new NACoord(x, y, z);
    }

    public static NACoord fromKey(long key) {
        int x = (int) ((key >> 38) & 0x3FFFFFFL);
        int y = (int) ((key >> 26) & 0xFFFL);
        int z = (int) (key & 0x3FFFFFFL);
        if (x >= 0x2000000) x -= 0x4000000;
        if (z >= 0x2000000) z -= 0x4000000;
        return new NACoord(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NACoord other)) return false;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(x);
        result = 31 * result + Integer.hashCode(y);
        result = 31 * result + Integer.hashCode(z);
        return result;
    }

    @Override
    public String toString() {
        if (y == -1) {
            return "(" + x + ", " + z + ")";
        } else {
            return "(" + x + ", " + y + ", " + z + ")";
        }
    }
}
