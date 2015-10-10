package com.bearychat.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public Bearychat sendToChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public void push(BearychatMessage message) throws IOException {
        if (message != null) {
            bearychatService.push(webhookUrl, message, channel);
        }
    }

    public void push(BearychatAttachment attachment) throws IOException {
        if (attachment != null) {
        	List<BearychatAttachment> attachments = new ArrayList<BearychatAttachment>();
        	attachments.add(attachment);
        	this.push(attachments);
        }
    }

    public void push(List<BearychatAttachment> attachments) throws IOException {
        bearychatService.push(webhookUrl, new BearychatMessage(), channel, attachments);
    }
}
