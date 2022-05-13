package com.pydawan.pydabot;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;

/**
 * A bot that connects to a server and joins channels.
 */
public class Bot {

    private String hostname;
    private int port;
    private String nickname;
    private String password;

    private Set<String> channels = new HashSet<>();
    private Set<Listener> listeners = new HashSet<>();

    private PircBotX bot;
    private Thread botThread;

    /**
     * Create a new bot instance.
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
     */
    public void start() throws IrcException, IOException, InterruptedException {
        close();

        bot = new PircBotX(buildConfiguration());
        botThread = new Thread(() -> {
            try {
                bot.startBot();
            } catch (Exception e) {}
        }, "Bot-Event-Loop");
        botThread.start();
    }

    /**
     * Closes the bot.
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        if(bot == null)
            return;

        bot.close();
        botThread.join();
    }

    /**
     * Adds a listener to the bot.
     * @param listener The listener to add.
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Adds a channel to the bot.
     * @param channel The channel to add.
     */
    public void addChannel(String channel) {
        channels.add(channel);
    }

    /**
     * Removes a channel from the bot.
     * @param channel The channel to remove.
     */
    public void removeChannel(String channel) {
        channels.remove(channel);
    }

    /**
     * Sends a message to a channel.
     * @param channel The channel to send the message to.
     * @param message The message to send.
     */
    public void sendMessage(String channel, String message) {
        bot.send().message("#"+channel, message);
    }
}
