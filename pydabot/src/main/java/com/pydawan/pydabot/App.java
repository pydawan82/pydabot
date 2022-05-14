package com.pydawan.pydabot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.pydawan.pydabot.listeners.SimpleCommandListener;

import org.json.JSONObject;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        JSONObject config = new JSONObject(Files.readString(Path.of("config.json")));

        String hostname = config.getString("server");
        int port = config.getInt("port");
        String nickname = config.getString("name");
        String password = config.getString("token");

        Bot bot = new Bot(hostname, port, nickname, password);
        bot.addListener(new SimpleCommandListener(Map.of(
                "hello", "Hello, world!"), "!"));
        bot.addChannel("#pydawan");
        bot.start();
        System.out.println("Connected");
        Thread.sleep(5000);
        bot.sendMessage("pydawan", "Hi!");
        bot.getHostname();
    }
}
