package dev.natowb.natosatlas.server;

import dev.natowb.natosatlas.core.NASession;
import dev.natowb.natosatlas.core.io.LogUtil;

public class NAServer implements NASession {

    private boolean started;
    private NAServerPlatform platform;

    public NAServer(NAServerPlatform platform) {
        this.platform = platform;
    }


    @Override
    public void tick() {
        if (!started) {
            start();
            started = true;
        }
    }

    private void start() {
        LogUtil.info("Server Started");
        Thread heartbeatThread = new Thread(new Heartbeat(), "NAServer - testing");
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }

    private static class Heartbeat implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    LogUtil.info("Test test, hope you are not actually trying to use this atm");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
