package com.pydawan.pydabot;

import java.io.IOException;

import org.pircbotx.exception.IrcException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Hello world!
 *
 */
public class App {

    public static Namespace parse(ArgumentParser parser, String[] args) {
        try {
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
            return null;
        }
    }

    public static void main(String[] args) {
        /* Create an argument parser that takes a 'configuration' argument */
        ArgumentParser parser = ArgumentParsers
                .newFor("App")
                .addHelp(true)
                .build();
        
        /* Add a 'configuration' argument */
        parser.addArgument("configuration")
                .required(true)
                .help("Configuration file");

        
        /* Parse the arguments */
        Namespace namespace = parse(parser, args);
        String configPath = namespace.getString("configuration");
        
        try(Bot bot = new Bot(configPath)){
            bot.start();
        } catch(IOException|IrcException e) {
            e.printStackTrace();
        }
    }
}
