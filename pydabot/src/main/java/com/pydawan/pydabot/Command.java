package com.pydawan.pydabot;

import java.util.function.BiConsumer;

import org.pircbotx.hooks.Event;

/**
 * A command that can be executed by the bot.
 */
public interface Command extends BiConsumer<Event, String[]> {}
