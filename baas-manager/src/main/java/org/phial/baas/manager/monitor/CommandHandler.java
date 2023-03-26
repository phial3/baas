package org.phial.baas.manager.monitor;

import io.airlift.airline.Option;

import java.util.concurrent.Callable;

/**
 * CommandHandler
 * @since 2020/9/7
 * @author mayanjun
 */
public abstract class CommandHandler implements Callable<String> {

    @Option(name = "-v", description = "Verbose mode")
    private boolean verbose;

    @Option(name = "--once", description = "Current connection only be used once and will be closed after executed")
    private boolean once;

    private SystemMonitor monitor;

    public CommandHandler(SystemMonitor monitor) {
        this.monitor = monitor;
    }

    public boolean isVerbose() {
        return verbose;
    }

    protected SystemMonitor systemMonitor() {
        return monitor;
    }

    public boolean isOnce() {
        return once;
    }
}
