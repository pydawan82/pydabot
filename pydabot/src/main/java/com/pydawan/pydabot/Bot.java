package com.pydawan.pydabot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.pydawan.pydabot.listeners.CommandListener;
import com.pydawan.pydabot.listeners.Moderator;
import com.pydawan.pydabot.listeners.Pong;
import com.pydawan.pydabot.listeners.SimpleCommandListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Listener;

public class Bot implements AutoCloseable{
    PircBotX bot;

    /**
     * Create a new bot instance.
     * @param configPathStr Path to the config file.
     * @throws JSONException
     * @throws IOException
     */
    public Bot(String configPathStr) throws JSONException, IOException {
        /* Create a JSONObject from a given file path */
        Path configPath = Paths.get(configPathStr);
        JSONObject configJson = new JSONObject(new String(Files.readAllBytes(configPath), "UTF-8"));
        String server = configJson.getString("server");
        List<String> channels = configJson
                .getJSONArray("channels")
                .toList()
                .stream()
                .map(Object::toString)
                .toList();

        String name = configJson.getString("name");
        String token = configJson.getString("token");

        List<Listener> listeners = List.of(
            new Pong(),
            new Moderator((a) -> null, (a, b) -> null),
            new SimpleCommandListener((c) -> "Hi"),
            new CommandListener((c) -> null)
        );

        /* Create a new PircBotX configuration */
        Configuration config = new Configuration.Builder()
            .setServerPassword(token)
            .setName(name)
            .addAutoJoinChannels(channels)
            .addServer(server)
            .addListeners(listeners)
            .buildConfiguration();
        
        bot = new PircBotX(config);
    }

    /**
     * Connect to the IRC server and start the bot.
     * @throws IOException
     * @throws IrcException
     */
    public void start() throws IOException, IrcException {
        bot.startBot();
    }

    /**
     * Close the bot.
     */
    @Override
    public void close() {
        bot.close();
    }
}
