package com.bearychat.core;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Maps;
import com.google.gson.Gson;
import com.bearychat.core.BearychatAttachment;
import com.bearychat.core.BearychatMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class BearychatService {
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();

    public void push(String webHookUrl, BearychatMessage message, String channel) throws IOException {
        Map<String, Object> payload = new HashMap<String, Object>();

        if (channel != null && !"".equals(channel.trim())) {
            payload.put("channel", channel);
        }

        List<BearychatAttachment> attachments = message.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            payload.put("attachments", attachments);
        }

        String notificaiton = message.getNotification();
        if (!StringUtils.isEmpty(notificaiton)){
            payload.put("notification", notificaiton);
        }

        payload.put("text", message.toString());
        execute(webHookUrl, payload);
    }

    public void execute(String webHookUrl, Map<String, Object> payload) throws IOException {
        String jsonEncodedMessage = new Gson().toJson(payload);
        HashMap<Object, Object> payloadToSend = Maps.newHashMap();
        payloadToSend.put("payload", jsonEncodedMessage);

        requestFactory.buildPostRequest(new GenericUrl(webHookUrl), new UrlEncodedContent(payloadToSend)).execute();
    }
}
