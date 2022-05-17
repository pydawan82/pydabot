package com.pydawan.pydabot.listeners;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pydawan.pydabot.Command;

import org.javatuples.Pair;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import lombok.NonNull;

/**
 * A generic listener for parsing commands.
 * Parses each message and calls the appropriate command.
 * 
 * Commands are defined by the given regex: ^!(\\w+)(?:\\s+(.*))?
 * The first group is the command name, the second group is the arguments.
 * Each command starts with an ! and each part of the command is separated by
 * whitespaces.
 */
public class CommandListener extends ListenerAdapter {

    /**
     * A regex representing a command.
     * The first group is the command names. The second group is the arguments.
     * Commands starts with a '!'. Arguments are splitted by spaces.
     * Examples of Strings matched by the regex:
     * - !command arg1 arg2
     * - !command arg1 arg2 arg3
     * - !command
     */
    private static final Pattern COMMAND_REGEX = Pattern.compile("^!(\\w+)(?:\\s+(.*))?");

    /**
     * A function that takes a command name and returns the corresponding command.
     */
    private final Function<String, Command> commandMap;

    /**
     * Creates a new CommandListener.
     * 
     * @param commandMap A function that takes a command name and returns the
     *                   corresponding command.
     */
    public CommandListener(@NonNull Function<String, Command> commandMap) {
        this.commandMap = commandMap;
    }

    /**
     * A function that takes a message as argument and parses it as a command.
     * Returns a Pair of the command name and the arguments.
     */
    private static final Pair<String, String[]> parseCommand(String message) throws IndexOutOfBoundsException {
        Matcher matcher = COMMAND_REGEX.matcher(message);

        if (!matcher.matches())
            return null;

        return new Pair<>(matcher.group(1), matcher.group(2).split("\\s"));
    }

    @Override
    public void onMessage(MessageEvent event) {
        String message = event.getMessage();
        Pair<String, String[]> command = parseCommand(message);

        if (command == null)
            return;

        Command cmd = commandMap.apply(command.getValue0());

        if (cmd == null)
            return;

        cmd.accept(event, command.getValue1());
    }
}
