package com.pydawan.pydabot;

import lombok.Data;

@Data
public class Announcement {
    private final String channel;
    private final String message;
    private final double weight;
}
