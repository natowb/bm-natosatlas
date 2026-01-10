package dev.natowb.natosatlas.core.storage;

import dev.natowb.natosatlas.core.models.NacRegionData;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public interface INacRegionStorage {

    Path getRegionFile(int rx, int rz);

    void saveRegion(int rx, int rz, NacRegionData region);

    Optional<NacRegionData> loadRegion(int rx, int rz);

    Map<Long, NacRegionData> loadAllRegions();
}
