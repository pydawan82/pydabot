package com.pydawan.pydabot.listeners;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * A listener that handles simple commands.
 * This is used to handle commands that do not require arguments.
 * 
 * A command is of the form [prefix][command] ...
 */
public class SimpleCommandListener extends ListenerAdapter {
    private static final String SPLIT = " ";
    private final String prefix;

    private final Function<String, String> mapping;

    /**
     * Creates a new SimpleCommandListener for the given commands.
     * 
     * @param mapping - A function that takes a command name and returns the
     *                corresponding command.
     * @param prefix  - The prefix of the commands.
     */
    public SimpleCommandListener(Function<String, String> mapping, String prefix) {
        this.mapping = mapping;
        this.prefix = prefix;
    }

    /**
     * Creates a new SimpleCommandListener for the given commands.
     * 
     * @param map    - A map of command names to commands.
     * @param prefix - The prefix of the commands.
     */
    public SimpleCommandListener(Map<String, String> map, String prefix) {
        this(map::get, prefix);
    }

    /**
     * Returns the response for the given command.
     * 
     * @param command - The command to handle.
     * @return The response for the given command. The response is empty if the
     *         command is not handled.
     */
    private Optional<String> getResponse(String command) {
        return Optional.ofNullable(mapping.apply(command));
    }

    @Override
    public void onMessage(MessageEvent event) {
        String message = event.getMessage();
        String command = message.stripLeading().split(SPLIT)[0];

        if (!command.startsWith(prefix)) return;
        
        final String commandName = command.substring(prefix.length());
        getResponse(commandName).ifPresent(event::respond);
    }
}
