package com.bearychat.core;

import com.google.gson.annotations.SerializedName;

/**
 * Ref - https://github.com/bearyinnovative/beary-docs/blob/master/snitch/attachments.md
 */
public class BearychatAttachment {

    public static final String BRIGHT_COLOR = "#D2F3D8";

	@SerializedName("favicon")
	private String favicon;
	@SerializedName("color")
	private String color;
	@SerializedName("title")
	private String title;
	@SerializedName("url")
	private String url;
	@SerializedName("text")
	private String text;

	public BearychatAttachment(String text) {
		text(text);
	}

	public BearychatAttachment color(String color) {
		this.color = color;
		return this;
	}

	public BearychatAttachment title(String title) {
		this.title = title;
		return this;
	}

	public BearychatAttachment url(String url) {
		this.url = url;
		return this;
	}

	public BearychatAttachment imageUrl(String favicon) {
		this.favicon = favicon;
		return this;
	}

	public BearychatAttachment text(String text) {
		this.text = text;
		return this;
	}

	public BearychatAttachment text(BearychatMessage message) {
		return text(message.toString());
	}

	public String getText() {
		return text;
	}
}
