package com.pydawan.pydabot.irc;

public class IrcUtils {
    private IrcUtils() {
    }

    public static String channelOf(String channelName) {
        return "#" + channelName;
    }
}
