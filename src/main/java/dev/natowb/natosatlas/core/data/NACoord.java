package dev.natowb.natosatlas.core.data;

public final class NACoord {

    public final int x;
    public final int z;

    public NACoord(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public long toKey() {
        long lx = ((long) x & 0x3FFFFFFL) << 26;
        long lz = ((long) z & 0x3FFFFFFL);
        return lx | lz;
    }

    public static NACoord from(int x, int z) {
        return new NACoord(x, z);
    }

    public static NACoord fromKey(long key) {
        int x = (int) ((key >> 26) & 0x3FFFFFFL);
        int z = (int) (key & 0x3FFFFFFL);

        if (x >= 0x2000000) x -= 0x4000000;
        if (z >= 0x2000000) z -= 0x4000000;

        return new NACoord(x, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NACoord)) return false;
        NACoord other = (NACoord) o;
        return x == other.x && z == other.z;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(x);
        result = 31 * result + Integer.hashCode(z);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + z + ")";
    }
}
