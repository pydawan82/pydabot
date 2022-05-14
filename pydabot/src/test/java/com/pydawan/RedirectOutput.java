package com.pydawan;

import java.io.PrintStream;

public class RedirectOutput implements AutoCloseable {

    private final PrintStream originalOut;

    public RedirectOutput(PrintStream newOutput) {
        originalOut = System.out;
        System.setOut(newOutput);
    }

    @Override
    public void close() {
        System.setOut(originalOut);
    }

}
