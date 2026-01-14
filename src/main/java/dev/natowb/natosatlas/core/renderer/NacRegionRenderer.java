package dev.natowb.natosatlas.core.renderer;

import dev.natowb.natosatlas.core.models.NacRegionData;

public interface NacRegionRenderer {
    void buildPixels(NacRegionData region, int[] outPixels);
}
