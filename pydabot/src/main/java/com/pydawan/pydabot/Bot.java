package com.pydawan.pydabot;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.pydawan.pydabot.workers.AnnounceWorker;

import org.pircbotx.Colors;
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
    private final ExecutorService botExecutor;
    private Future<?> botFuture;

    private final ScheduledExecutorService taskExecutor;
    private Future<?> announceFuture;
    private AnnounceWorker announceWorker = new AnnounceWorker(bot);

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
        botExecutor = Executors.newSingleThreadExecutor();
        taskExecutor = Executors.newSingleThreadScheduledExecutor();
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
        return botFuture != null && !botFuture.isDone();
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
        botFuture = botExecutor.submit(this::runBot);
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
     * @throws TimeoutException
     * @throws ExecutionException
     * 
     * @throws InterruptedException
     * @throws BotException         If the bot is not running.
     */
    public void close() throws InterruptedException, ExecutionException, TimeoutException {
        if (!isAlive())
            throw new BotException("Bot is not running.");

        bot.sendIRC().quitServer("");

        try {
            botFuture.get(MAX_SAFE_DISCONNECT_TIME, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            bot.close();
            botFuture.get();
        }
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

    public void addTask(String channel) {
        taskExecutor.scheduleAtFixedRate(() -> {
            bot.sendIRC().message("#" + channel, "Hello");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
