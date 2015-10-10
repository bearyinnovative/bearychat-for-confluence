package com.bearychat.core;

public class BearychatMessage {
    private StringBuilder textBuffer = new StringBuilder();

    public BearychatMessage() {
    }

    public BearychatMessage(String text) {
        text(text);
    }

    public BearychatMessage text(String text) {
        textBuffer.append(text);
        return this;
    }

    public BearychatMessage link(String url) {
        link(url, "");
        return this;
    }

    public BearychatMessage link(String url, String text) {
        if (text != null && !text.trim().equals("")) {
            textBuffer.append("<").append(url).append("|").append(text).append(">");
        } else {
            textBuffer.append("<").append(url).append(">");
        }

        return this;
    }

    public BearychatMessage bold(String text) {
        textBuffer.append("**").append(text).append("**");
        return this;
    }

    public BearychatMessage italic(String text) {
        textBuffer.append("_").append(text).append("_");
        return this;
    }

    public BearychatMessage code(String code) {
        textBuffer.append("`").append(code).append("`");
        return this;
    }

    public BearychatMessage preformatted(String text) {
        textBuffer.append("```").append("\n").append(text).append("```");
        return this;
    }

    public BearychatMessage quote(String text) {
        textBuffer.append("\n> ").append(text).append("\n");
        return this;
    }

    public String toString() {
        return textBuffer.toString();
    }

    public String rawText() {
        // We're not removing link because it's readable the way it is.
        return textBuffer.toString()
            .replaceAll("(.*)\\*\\*(.*)\\*\\*(.*)", "$1$2$3")	// Remove bold formatting
            .replaceAll("(.*)_(.*)_(.*)", "$1$2$3")     		// Remove italic formatting
            .replaceAll("(.*)```(.*)```(.*)", "$1$2$3")			// Remove pretext formatting
            .replaceAll("(.*)`(.*)`(.*)", "$1$2$3")				// Remove code formatting
            .replaceAll("\n>\\s+(.*)\n", "$1");					// Remove Quote formatting
    }
}
