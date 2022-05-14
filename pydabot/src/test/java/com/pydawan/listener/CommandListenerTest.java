package com.pydawan.listener;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.pydawan.RedirectOutput;
import com.pydawan.pydabot.Command;
import com.pydawan.pydabot.listeners.CommandListener;

import org.junit.Test;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.MessageEvent;

public class CommandListenerTest {

    private void command(Event event, String[] args) {
        System.out.print(String.join(" ", args));
    }

    private MessageEvent messageEventWithMessage(String message) {
        Channel channel = new Channel(null, message) {
        };
        UserHostmask userHostmask = new UserHostmask(null, "pydawan") {
        };
        User user = new User(userHostmask) {
        };
        ImmutableMap<String, String> tags = ImmutableMap.of();
        return new MessageEvent(
                null,
                channel,
                "#pydawan",
                userHostmask,
                user,
                message,
                tags);
    }

    @Test
    public void test() {
        final CommandListener listener = new CommandListener(Map.of("command", (Command) this::command)::get);
        final MessageEvent event = messageEventWithMessage("!command arg1 arg2");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (RedirectOutput redirectOutput = new RedirectOutput(new PrintStream(out))) {
            listener.onMessage(event);
        }

        assertEquals("arg1 arg2", out.toString());
    }
}
