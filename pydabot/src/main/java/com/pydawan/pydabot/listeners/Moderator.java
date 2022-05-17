package com.pydawan.pydabot.listeners;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.pydawan.pydabot.Severity;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import lombok.NonNull;

/**
 * A generic listener for moderating messages.
 * Takes a function that takes a message event and returns a severity.
 * Then, it will respond with a message based on the severity given
 * a second function. This function might return null if no response is needed.
 */
public class Moderator extends ListenerAdapter {

    private final BiFunction<String, Severity, String> responseOfSeverity;
    private final Function<String, Severity> severityOf;

    /**
     * Constructs a new Moderator.
     * The moderator is a listener that responds to messages based on the severity
     * of the message.
     * 
     * @param severityOf         A function that takes a message and returns the
     *                           severity of the message. The function must not
     *                           return null.
     * @param responseOfSeverity A function that takes a user name and a severity
     *                           and returns a response. The function might return
     *                           null if no response is needed.
     */
    public Moderator(
            @NonNull Function<String, Severity> severityOf,
            @NonNull BiFunction<String, Severity, String> responseOfSeverity) {

        this.severityOf = severityOf;
        this.responseOfSeverity = responseOfSeverity;
    }

    /**
     * Constructs a new Moderator.
     * The moderator is a listener that responds to messages based on the severity
     * of the message.
     * 
     * @param severityOf         A function that takes a message and returns the
     *                           severity of the message. The function must not
     *                           return null.
     * @param responseOfSeverity A map of severities to responses. Responses should
     *                           contain a "%s" placeholder for the name of the
     *                           user. The map might contain null values.
     */
    public Moderator(
            @NonNull Function<String, Severity> severityOf,
            @NonNull Map<Severity, String> responseOfSeverity) {
        this(severityOf, (user, severity) -> responseOfSeverity.get(severity).formatted(user));
    }

    /**
     * Moderates the given message.
     * First computes the severity of the message.
     * Then computes a response based on the severity.
     * If the response is not null, it will respond with the response.
     * 
     * @param event The message event to moderate.
     */
    public void moderate(MessageEvent event) {
        String message = event.getMessage();
        String sender = event.getUser().getNick();
        Severity severity = severityOf.apply(message);
        Optional.ofNullable(responseOfSeverity.apply(sender, severity))
                .ifPresent(event::respond);
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        moderate(event);
    }

}
