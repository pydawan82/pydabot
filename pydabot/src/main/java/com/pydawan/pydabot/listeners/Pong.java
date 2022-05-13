package com.pydawan.pydabot.listeners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PingEvent;

public class Pong extends ListenerAdapter {
    @Override
    public void onPing(PingEvent event) throws Exception {
        event.respond("PONG");
    }
}
