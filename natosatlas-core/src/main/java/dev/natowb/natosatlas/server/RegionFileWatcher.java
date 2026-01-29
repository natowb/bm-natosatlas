package dev.natowb.natosatlas.server;

import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NAPaths;
import dev.natowb.natosatlas.core.NARegionGenerator;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.util.LogUtil;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RegionFileWatcher implements Runnable {

    private final NAServerPlatform platform;
    private volatile boolean running;
    private Thread thread;
    private WatchService watcher;
    private Path regionDir;
    private int dim = 0;

    private final Queue<NARegionFile> changedQueue = new ArrayDeque<>();
    private final Map<Path, Long> debounce = new HashMap<>();

    public RegionFileWatcher(NAServerPlatform platform) {
        this.platform = platform;
    }

    public void start(int dim) {
        this.dim = dim;
        startWatcherThread();
    }

    public void setDimension(int newDim) {
        if (this.dim == newDim) return;
        this.dim = newDim;
        running = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        startWatcherThread();
    }

    private void startWatcherThread() {
        running = true;
        thread = new Thread(this, "RegionFileWatcher-" + dim);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        try {
            watcher = FileSystems.getDefault().newWatchService();
            regionDir = platform.getRegionDirectory(dim);
            regionDir.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE
            );

            rescanAllRegions();
            processChangedRegions();

            while (running) {
                WatchKey key = watcher.poll(ServerConfig.regionUpdateTimerMs, TimeUnit.MILLISECONDS);
                if (key != null) {
                    handleWatchEvents(key);
                    key.reset();
                }
                if (!changedQueue.isEmpty()) {
                    processChangedRegions();
                }
            }

        } catch (Exception e) {
            LogUtil.error("RegionFileWatcher crashed for dim {}", dim, e);
        }
    }

    private void handleWatchEvents(WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                rescanAllRegions();
                continue;
            }

            Path relative = (Path) event.context();
            Path fullPath = regionDir.resolve(relative);
            String name = fullPath.getFileName().toString();
            if (!name.endsWith(".mca") && !name.endsWith(".mcr")) return;

            long now = System.currentTimeMillis();
            Long prev = debounce.get(fullPath);
            if (prev != null && now - prev < 200) return;
            debounce.put(fullPath, now);

            if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                deleteOutputFilesForRegion(fullPath);
                return;
            }

            NARegionFile region = platform.getRegionFile(fullPath.toFile());
            if (region != null) changedQueue.add(region);
        }
    }

    private ChunkWrapper getChunk(NACoord chunkCoord) {
        return platform.getChunk(dim, chunkCoord);
    }

    private void processChangedRegions() {
        List<NARegionFile> batch = new ArrayList<>();
        while (!changedQueue.isEmpty()) batch.add(changedQueue.poll());

        NARegionGenerator generator = new NARegionGenerator(
                batch,
                this::getChunk,
                this::buildOutputFile
        );

        generator.generateAll();
        LogUtil.debug("Updated {} regions (dim {})", batch.size(), dim);
    }

    private void rescanAllRegions() {
        changedQueue.addAll(platform.getRegionFiles(dim));
    }

    private void deleteOutputFilesForRegion(Path regionPath) {
        String fileName = regionPath.getFileName().toString();
        String core = fileName.substring(2, fileName.length() - 4);
        String[] parts = core.split("\\.");
        if (parts.length != 2) return;

        int rx = Integer.parseInt(parts[0]);
        int rz = Integer.parseInt(parts[1]);
        NACoord coord = new NACoord(rx, rz);

        for (NALayer layer : LayerRegistry.getLayers()) {
            File out = buildOutputFile(layer.id, coord);
            if (out.exists()) out.delete();
        }
    }

    private File buildOutputFile(int layerId, NACoord regionCoord) {
        File baseDir = NAPaths.getWorldMapStoragePath(layerId, dim, true).toFile();
        if (!baseDir.exists()) baseDir.mkdirs();
        return new File(baseDir, "region_" + regionCoord.x + "_" + regionCoord.z + ".png");
    }
}
