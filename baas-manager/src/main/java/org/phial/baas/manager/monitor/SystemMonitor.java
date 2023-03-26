package org.phial.baas.manager.monitor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.phial.baas.manager.config.app.StandardThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * System monitor
 * @since 2020/9/7
 * @author mayanjun
 */
public class SystemMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(SystemMonitor.class);

    private int port;
    private ConfigurableApplicationContext context;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public SystemMonitor(int port, ConfigurableApplicationContext context) {
        this.port = port;
        this.context = context;
    }

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(2, new StandardThreadFactory("COMMAND-WORKER", true));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new SystemMonitorInitializer(this));
            b.bind("127.0.0.1", port).sync();
            LOG.info("System monitor started on port: {}", port);
        } catch (Exception e) {
            throw e;
        }
    }

    public void shutdown(boolean force) {
        context.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        LOG.info("========================= Application shutdown(force={}) =========================", force);
        if (force) {
            System.exit(1);
        }
    }

    public ConfigurableApplicationContext context() {
        return context;
    }

    public ConfigurableEnvironment environment() {
        return context.getEnvironment();
    }
}