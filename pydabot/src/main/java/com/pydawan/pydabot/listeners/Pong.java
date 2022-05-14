package com.pydawan.pydabot.listeners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PingEvent;

/**
 * A listener that handles PING requests.
 * This is used to keep the connection alive.
 */
public class Pong extends ListenerAdapter {
    @Override
    public void onPing(PingEvent event) throws Exception {
        event.respond("PONG");
    }
}
