package dev.natowb.natosatlas.core.glue;

import java.nio.file.Path;

public interface INacFileProvider {

    Path getDataDirectory();

    Path getRegionDirectory();
}
