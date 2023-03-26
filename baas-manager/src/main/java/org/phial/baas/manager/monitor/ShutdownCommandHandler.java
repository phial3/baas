package org.phial.baas.manager.monitor;

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Shutdown application
 * @since 2020/9/7
 * @author mayanjun
 */
@Command(name = "shutdown", description = "Shutdown this application")
public class ShutdownCommandHandler extends CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ShutdownCommandHandler.class);

    public ShutdownCommandHandler(SystemMonitor monitor) {
        super(monitor);
    }

    @Option(name = {"-d", "--delay"}, description = "Shutdown application after delay seconds")
    private int delay = 0;

    @Option(name = {"-f", "--force"}, description = "Shutdown application forcibly")
    private boolean force = false;

    @Override
    public String call() throws Exception {
        LOG.info("========================= Shutdown signal received: delay={} =========================", delay);
        if (delay > 0) {
            new Timer("SHUTDOWN_TIMER", true).schedule(new TimerTask() {
                @Override
                public void run() {
                    systemMonitor().shutdown(force);
                }
            }, delay * 1000);
            return "Server will be shutdown after " + delay + " seconds";
        } else {
            Thread shutdownThread = new Thread(() -> systemMonitor().shutdown(force));
            shutdownThread.setDaemon(true);
            shutdownThread.start();
            return "Server is shutdown";
        }
    }
}
