package com.pydawan.pydabot;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;

import lombok.Getter;

/**
 * A bot that connects to a server and joins channels.
 */
public class Bot {
    @Getter
    private final String hostname;
    @Getter
    private final int port;
    @Getter
    private final String nickname;
    @Getter
    private final String password;

    private final Set<String> channels = new HashSet<>();
    private final Set<Listener> listeners = new HashSet<>();

    private PircBotX bot;
    private Thread botThread;

    /**
     * Create a new bot instance.
     * 
     * @param configPathStr Path to the config file.
     * @throws JSONException
     * @throws IOException
     */
    public Bot(String hostname, int port, String nickname, String password) {
        this.hostname = hostname;
        this.port = port;
        this.nickname = nickname;
        this.password = password;
    }

    /**
     * Returns whether the bot is connected to the server.
     * 
     * @return <code>true</code> if the bot is connected to the server,
     *         <code>false</code> otherwise.
     */
    public boolean isConnected() {
        return bot != null && bot.isConnected();
    }

    /**
     * Returns whether the bot thread is running.
     * 
     * @return <code>true</code> if the bot thread is running, <code>false</code>
     *         otherwise.
     */
    public boolean isAlive() {
        return botThread != null && botThread.isAlive();
    }

    private Configuration buildConfiguration() {
        return new Configuration.Builder()
                .setName(nickname)
                .addServer(hostname, port)
                .setServerPassword(password)
                .addAutoJoinChannels(channels)
                .addListeners(listeners)
                .buildConfiguration();
    }

    /**
     * Connects to the IRC server and start the bot.
     * Creates a new thread for the bot.
     * 
     * This method should be called if the configuration is changed.
     * 
     * @throws IOException
     * @throws IrcException
     * @throws InterruptedException
     * @throws BotException         If the bot is already running.
     */
    public void start() throws IrcException, IOException, InterruptedException {
        if (isAlive())
            throw new BotException("Bot is already running.");

        bot = new PircBotX(buildConfiguration());
        botThread = new Thread(this::runBot, "Bot-Event-Loop");
        botThread.start();
    }

    /**
     * Runs the bot.
     */
    private void runBot() {
        try {
            bot.startBot();
        } catch (Exception e) {
        }
    }

    /**
     * The maximum number of milliseconds to wait for the server to disconnect the
     * bot.
     */
    private static final long MAX_SAFE_DISCONNECT_TIME = 1000L;

    /**
     * Closes the bot .
     * 
     * @throws InterruptedException
     * @throws BotException         If the bot is not running.
     */
    public void close() throws InterruptedException {
        if (!isAlive())
            throw new BotException("Bot is not running.");

        bot.sendIRC().quitServer("");
        botThread.join(MAX_SAFE_DISCONNECT_TIME);
        bot.close();
        botThread.join();
    }

    /**
     * Returns the listeners of the bot.
     * 
     * @return An unmodifiable set of Li of the bot.
     */
    public Set<Listener> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }

    /**
     * Adds a listener to the bot.
     * The b
     * 
     * @param listener The listener to add.
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener from the bot
     * 
     * @param listener
     */
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns the channels the bot is connected to.
     * 
     * @return A set of channels.
     */
    public Set<String> getChannels() {
        return Collections.unmodifiableSet(channels);
    }

    /**
     * Adds a channel to the bot.
     * 
     * @param channel The channel to add.
     */
    public void addChannel(String channel) {
        channels.add(channel);
    }

    /**
     * Removes a channel from the bot.
     * 
     * @param channel The channel to remove.
     */
    public void removeChannel(String channel) {
        channels.remove(channel);
    }

    /**
     * Sends a message to a channel.
     * 
     * @param channel The channel to send the message to.
     * @param message The message to send.
     */
    public void sendMessage(String channel, String message) {
        bot.send().message("#" + channel, message);
    }
}
