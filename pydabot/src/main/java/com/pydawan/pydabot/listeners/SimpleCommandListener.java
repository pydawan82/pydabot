package com.pydawan.pydabot.listeners;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class SimpleCommandListener extends ListenerAdapter {
    private static final String SPLIT = " ";
    private static final String PREFIX = "!";

    private final Function<String, String> mapping;
    
    public SimpleCommandListener(Function<String, String> mapping) {
        this.mapping = mapping;
    }

    public SimpleCommandListener(Map<String, String> map) {
        this(map::get);
    }           

    private Optional<String> getResponse(String command) {
        return Optional.ofNullable(mapping.apply(command));
    }

    @Override
    public void onMessage(MessageEvent event) {
        String message = event.getMessage();
        String command = message.stripLeading().split(SPLIT)[0];
        if(command.startsWith(PREFIX))
            getResponse(command.substring(PREFIX.length())).ifPresent(event::respond);
    }
}
