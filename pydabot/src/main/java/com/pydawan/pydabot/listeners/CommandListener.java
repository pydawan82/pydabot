package com.pydawan.pydabot.listeners;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pydawan.pydabot.Command;
import com.pydawan.pydabot.Pair;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class CommandListener extends ListenerAdapter {

    /**
     * A regex representing a command.
     * The first group is the command names. The second group is the arguments.
     * Commands starts with a '!'. Arguments are splitted by spaces.
     * Examples of Strings matched by the regex:
     * - !command arg1 arg2
     */
    private static final Pattern COMMAND_REGEX = Pattern.compile("^!(\\w+)(?:\\s+(.*))?");

    /**
     * A function that takes a command name and returns the corresponding command.
     */
    private final Function<String, Command> commandMap;

    /**
     * Creates a new CommandListener.
     * @param commandMap A function that takes a command name and returns the corresponding command.
     */
    public CommandListener(Function<String, Command> commandMap) {
        this.commandMap = commandMap;
    }

    /**
     * A function that takes a message as argument and parses it as a command.
     * Returns a Pair of the command name and the arguments.
     */
    private static final Pair<String, String[]> parseCommand(String message) {
            Matcher matcher = COMMAND_REGEX.matcher(message);
            if (matcher.matches()) {
                return new Pair<>(matcher.group(1), matcher.group(2).split("\\s"));
            } else {
                return null;
            }
        }

    /**
     * Applies the command of the incoming message.
     * @param event The incoming message.
     */
    @Override
    public void onMessage(MessageEvent event) {
        String message = event.getMessage();
        Pair<String, String[]> command = parseCommand(message);
        if (command != null) {
            Command cmd = commandMap.apply(command.first());
            if (cmd != null) {
                cmd.accept(event, command.second());
            }
        }
    }
}
