package dev.natowb.natosatlas.server;

import dev.natowb.natosatlas.core.storage.TextStorage;
import dev.natowb.natosatlas.core.util.LogUtil;

import java.io.File;

public class ServerConfig extends TextStorage {

    private static final ServerConfig STORAGE = new ServerConfig();
    public static LogUtil.LogLevel logLevel = LogUtil.LogLevel.INFO;
    public static String webHost = "127.0.0.1";
    public static int webPort = 8080;
    public static boolean showPlayers = true;
    public static int regionUpdateTimerMs = 5_000;


    @Override
    protected String getName() {
        return "ServerConfig";
    }

    @Override
    protected void onLoad() {
        webHost = getString("webHost", "127.0.0.1");
        webPort = getInt("webPort", 8080);
        regionUpdateTimerMs = getInt("regionUpdateTimerMs", 5_000);
        logLevel = getEnum("logLevel", LogUtil.LogLevel.class, LogUtil.LogLevel.INFO);
        showPlayers = getBoolean("showPlayers", true);
    }

    @Override
    protected void onSave() {
        put("webHost", webHost);
        put("webPort", webPort);
        put("regionUpdateTimerMs", regionUpdateTimerMs);
        put("logLevel", logLevel);
        put("showPlayers", showPlayers);
    }


    public static void loadConfig(File configFile) {

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            ServerConfig.saveConfig(configFile);
        }

        STORAGE.load(configFile);
    }

    public static void saveConfig(File configFile) {
        STORAGE.save(configFile);
    }


}
