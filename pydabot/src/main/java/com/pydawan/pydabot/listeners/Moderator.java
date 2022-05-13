package com.pydawan.pydabot.listeners;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.pydawan.pydabot.Severity;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class Moderator extends ListenerAdapter {

    private final BiFunction<String, Severity, String> p;
    private final Function<String, Severity> severityOf;

    public Moderator(
            Function<String, Severity> severityOf,
            BiFunction<String, Severity, String> p
        ) {
        this.severityOf = Objects.requireNonNull(severityOf);
        this.p = Objects.requireNonNull(p);
    }

    public Moderator(
            Function<String, Severity> severityOf,
            Map<Severity, String> map
        ) {
        this(severityOf, (user, severity) -> map.get(severity).formatted(user));
    }

    public void moderate(MessageEvent event) {
        String message = event.getMessage();
        String sender = event.getUser().getNick();
        Severity severity = severityOf.apply(message);
        Optional.ofNullable(p.apply(sender, severity)).ifPresent(event::respond);
    }
    
    public void onMessage(MessageEvent event) throws Exception {
        moderate(event);
    }

}
