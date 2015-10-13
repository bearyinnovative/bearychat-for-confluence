package com.bearychat.core;

import java.io.IOException;

public class Bearychat {
    private String webhookUrl;
    private String channel;
    
    private BearychatService bearychatService = new BearychatService();

    public Bearychat(String webhookUrl) {
        if (webhookUrl == null || webhookUrl.trim().equals("")) {
            throw new IllegalArgumentException("Webhook url is not provided");
        }
        
        this.webhookUrl = webhookUrl;
    }

    public Bearychat channel(String channel) {
        this.channel = channel;
        return this;
    }

    public void push(BearychatMessage message) throws IOException {
        if (message != null) {
            bearychatService.push(webhookUrl, message, channel);
        }
    }
}
