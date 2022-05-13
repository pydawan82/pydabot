package com.pydawan.pydabot.listeners;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class SimpleCommandListener extends ListenerAdapter {
    private static final String SPLIT = " ";
    private final String prefix;

    private final Function<String, String> mapping;
    
    public SimpleCommandListener(Function<String, String> mapping, String prefix) {
        this.mapping = mapping;
        this.prefix = prefix;
    }

    public SimpleCommandListener(Map<String, String> map, String prefix) {
        this(map::get, prefix);
    }

    private Optional<String> getResponse(String command) {
        return Optional.ofNullable(mapping.apply(command));
    }

    @Override
    public void onMessage(MessageEvent event) {
        String message = event.getMessage();
        String command = message.stripLeading().split(SPLIT)[0];
        if(command.startsWith(prefix))
            getResponse(command.substring(prefix.length())).ifPresent(event::respond);
    }
}
