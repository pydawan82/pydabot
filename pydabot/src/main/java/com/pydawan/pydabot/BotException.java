package com.pydawan.pydabot;

/**
 * Thrown when an error occurs while running the bot.
 */
public class BotException extends RuntimeException {

    public BotException(String message) {
        super(message);
    }

    public BotException(String message, Throwable cause) {
        super(message, cause);
    }

    public BotException(Throwable cause) {
        super(cause);
    }

    public BotException() {
    }
}
