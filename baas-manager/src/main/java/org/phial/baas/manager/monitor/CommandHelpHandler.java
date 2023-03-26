package org.phial.baas.manager.monitor;

import io.airlift.airline.*;
import io.airlift.airline.model.CommandGroupMetadata;
import io.airlift.airline.model.CommandMetadata;
import io.airlift.airline.model.GlobalMetadata;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Display help information
 * @since 2020/9/7
 * @author mayanjun
 */
@Command(name = "help", description = "Display help information")
public class CommandHelpHandler implements Callable<String> {
    @Inject
    public GlobalMetadata global;

    @Arguments
    public List<String> command = new ArrayList<>();

    @Override
    public String call() {
        return help(global, command);
    }

    public static void help(CommandMetadata command) {
        StringBuilder stringBuilder = new StringBuilder();
        help(command, stringBuilder);
        System.out.println(stringBuilder.toString());
    }

    public static void help(CommandMetadata command, StringBuilder out) {
        new CommandUsage().usage(null, null, command.getName(), command, out);
    }

    public static String help(GlobalMetadata global, List<String> commandNames) {
        StringBuilder stringBuilder = new StringBuilder();
        help(global, commandNames, stringBuilder);
        return stringBuilder.toString();
    }

    public static void help(GlobalMetadata global, List<String> commandNames, StringBuilder out) {
        if (commandNames.isEmpty()) {
            new GlobalUsageSummary().usage(global, out);
            return;
        }

        String name = commandNames.get(0);

        // main program?
        if (name.equals(global.getName())) {
            new GlobalUsage().usage(global, out);
            return;
        }

        // command in the default group?
        for (CommandMetadata command : global.getDefaultGroupCommands()) {
            if (name.equals(command.getName())) {
                new CommandUsage().usage(global.getName(), null, command.getName(), command, out);
                return;
            }
        }

        // command in a group?
        for (CommandGroupMetadata group : global.getCommandGroups()) {
            if (name.endsWith(group.getName())) {
                // general group help or specific command help?
                if (commandNames.size() == 1) {
                    new CommandGroupUsage().usage(global, group, out);
                    return;
                } else {
                    String commandName = commandNames.get(1);
                    for (CommandMetadata command : group.getCommands()) {
                        if (commandName.equals(command.getName())) {
                            new CommandUsage().usage(global.getName(), group.getName(), command.getName(), command, out);
                            return;
                        }
                    }
                    out.append("Unknown command " + name + " " + commandName);
                }
            }
        }

        if (Arrays.asList(TerminalCommandHandler.EXIT_COMMANDS).contains(name)) {
            out.append("NAME\r\n\tquit/exit - Exit current session");
        } else {
            out.append("Unknown command " + name);
        }
    }
}