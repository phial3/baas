package org.phial.baas.manager.monitor;

import io.airlift.airline.Cli;
import io.airlift.airline.CommandFactory;
import io.airlift.airline.DefaultCommandFactory;
import io.airlift.airline.ParseArgumentsUnexpectedException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @author admin
 */
public class TerminalCommandHandler extends SimpleChannelInboundHandler<String> implements CommandFactory<Callable<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(TerminalCommandHandler.class);

    public static final String[] EXIT_COMMANDS = new String[] {"quit", "exit"};

    private SystemMonitor monitor;
    private String applicationName;

    private Cli<Callable<String>> commandClient;

    private CommandFactory<Callable<String>> defaultCommandFactory;

    public TerminalCommandHandler(SystemMonitor monitor) {
        this.monitor = monitor;
        ConfigurableEnvironment env = monitor.context().getEnvironment();
        applicationName = env.getProperty("application.name") + " : " + env.getProperty("application.version");
        defaultCommandFactory = new DefaultCommandFactory<>();

        Cli.CliBuilder<Callable<String>> builder = Cli.<Callable<String>>builder("\0")
                .withDescription("the stupid content tracker")
                .withDefaultCommand(CommandHelpHandler.class)
                .withCommandFactory(this)
                .withCommands(
                        CommandHelpHandler.class,
                        ShutdownCommandHandler.class,
                        ConfigCommandHandler.class,
                        ExitCommandHandler.class
                );
        commandClient = builder.build();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String command = msg;

        if (StringUtils.isBlank(command)) {
            return;
        } else {
            String remote = ctx.channel().remoteAddress().toString();
            String server = ctx.channel().localAddress().toString();
            LOG.info("Receive command({} <==> {}) > {}",remote, server,  command);
            String ct = command.trim();
            if ("quit".equalsIgnoreCase(ct) || "exit".equalsIgnoreCase(ct)) {
                ctx.writeAndFlush("Bye Bye!\r\n");
                ctx.close();
                return;
            }
        }

        try {
            Callable<String> callable = commandClient.parse(TerminalCommandHandler.this, command.split("\\s"));
            String result = callable.call();
            if (result != null && result.length() > 0) {
                ctx.write("--------------- " + applicationName + " : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ---------------\r\n");
                ctx.write(result);
                ctx.flush();
            }
            if (callable instanceof CommandHandler) {
                if (((CommandHandler) callable).isOnce()) ctx.close();
            }
        } catch (ParseArgumentsUnexpectedException e) {
            ctx.writeAndFlush("Unknown command: " + command + "\r\n");
        } catch (Exception e) {
            ctx.writeAndFlush("Execute error:" + e.getMessage());
        }
    }

    @Override
    public Callable<String> createInstance(Class<?> type) {
        if (CommandHandler.class.isAssignableFrom(type)) {
            try {
                Constructor<?> c = type.getConstructor(SystemMonitor.class);
                return (Callable<String>) c.newInstance(monitor);
            } catch (Exception e) {
                LOG.error("Can't instantiate instance of type " + type.getCanonicalName(), e);
            }
        }
        return defaultCommandFactory.createInstance(type);
    }
}
