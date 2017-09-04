package com.bearychat.components;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRestoreEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostTrashedEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageRestoreEvent;
import com.atlassian.confluence.event.events.content.page.PageTrashedEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.user.User;
import com.bearychat.core.Bearychat;
import com.bearychat.core.BearychatAttachment;
import com.bearychat.core.BearychatMessage;
import com.bearychat.core.ViewUtils;

public class AnnotatedListener implements DisposableBean, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedListener.class);

    private final WebResourceUrlProvider webResourceUrlProvider;
    private final EventPublisher eventPublisher;
    private ConfigurationManager configurationManager;
    private final PersonalInformationManager personalInformationManager;

    public AnnotatedListener(EventPublisher eventPublisher, ConfigurationManager configurationManager,
        PersonalInformationManager personalInformationManager, WebResourceUrlProvider webResourceUrlProvider) {
        this.eventPublisher = checkNotNull(eventPublisher);
        this.configurationManager = checkNotNull(configurationManager);
        this.personalInformationManager = checkNotNull(personalInformationManager);
        this.webResourceUrlProvider = checkNotNull(webResourceUrlProvider);
    }

    @EventListener
    public void blogPostCreateEvent(BlogPostCreateEvent event) {
        String text = event.getBlogPost().getBodyAsStringWithoutMarkup().trim();
        text = ViewUtils.formatMessage(text);
        BearychatAttachment attachment = new BearychatAttachment(text).color(BearychatAttachment.BRIGHT_COLOR);

        sendMessages(event, event.getBlogPost(), attachment, "Blog created");
    }

    @EventListener
    public void blogPostUpdateEvent(BlogPostUpdateEvent event) {
        sendMessages(event, event.getBlogPost(), "Blog updated");
    }

    @EventListener
    public void blogPostRemoveEvent(BlogPostRemoveEvent event) {
        sendMessages(event, event.getBlogPost(), "Blog removed");
    }

    @EventListener
    public void blogPostTrashedEvent(BlogPostTrashedEvent event) {
        sendMessages(event, event.getBlogPost(), "Blog trashed");
    }

    @EventListener
    public void blogPostRestoreEvent(BlogPostRestoreEvent event) {
        sendMessages(event, event.getBlogPost(), "Blog restored");
    }

    @EventListener
    public void pageCreateEvent(PageCreateEvent event) {
        String text = event.getPage().getBodyAsStringWithoutMarkup().trim();
        text = ViewUtils.formatMessage(text);
        BearychatAttachment attachment = new BearychatAttachment(text).color(BearychatAttachment.BRIGHT_COLOR);
        sendMessages(event, event.getPage(), attachment, "Page created");
    }

    @EventListener
    public void pageUpdateEvent(PageUpdateEvent event) {
        sendMessages(event, event.getPage(), "page updated");
    }

    @EventListener
    public void pageTrashedEvent(PageTrashedEvent event) {
        sendMessages(event, event.getPage(), "Page trashed");
    }

    @EventListener
    public void pageRemoveEvent(PageRemoveEvent event) {
        sendMessages(event, event.getPage(), "Page deleted");
    }

    @EventListener
    public void pageRestoreEvent(PageRestoreEvent event) {
        sendMessages(event, event.getPage(), "Page restored");
    }

    @EventListener
    public void commentCreateEvent(CommentCreateEvent event) {

        BearychatMessage message = new BearychatMessage();
        String action = null;
        String channel = null;
        String webhookUrl = null;

        String fullName = event.getComment().getCreator().getFullName();
        String url = webResourceUrlProvider.getBaseUrl(UrlMode.ABSOLUTE) + "/" + personalInformationManager.getOrCreatePersonalInformation(event.getComment().getCreator()).getUrlPath();

        ContentEntityObject owner = event.getComment().getOwner();
        if(owner instanceof AbstractPage) {
            if(owner instanceof Page) {
                action  = "Page commented";
            } else {
                action = "Blog commented";
            }

            message = getMessage((AbstractPage)owner, action, false);

            List<String> channels = this.getChannels((AbstractPage) owner);
            if(channels != null && !channels.isEmpty()) {
                channel = channels.get(0);
            }

            webhookUrl = this.getWebhookUrl((AbstractPage) owner);
        }

        if (webhookUrl == null) {
            webhookUrl = configurationManager.getWebhookUrl();
        }

        message.link(url, fullName);

        String text = event.getComment().getBodyAsStringWithoutMarkup().trim();
        text = ViewUtils.formatMessage(text);
        BearychatAttachment attachment = new BearychatAttachment(text).color(BearychatAttachment.BRIGHT_COLOR);

        this.sendMessage(webhookUrl, channel, attachment, message);
    }

    private void sendMessage(String webhookUrl, String channel, BearychatMessage message) {
        this.sendMessage(webhookUrl, channel, null, message);
    }

    private void sendMessage(String webhookUrl, String channel, BearychatAttachment attachment, BearychatMessage message) {
        LOGGER.info("Sending to {} with message {}.", configurationManager.getWebhookUrl(), message.toString());

        if(attachment != null) {
            message = message.attachments(attachment);
        }

        try {
            new Bearychat(webhookUrl).channel(channel).push(message);
        } catch (Exception e) {
            LOGGER.error("Error when sending BearyChat message", e);
        }
    }

    private void sendMessages(ContentEvent event, AbstractPage page, String action) {
        this.sendMessages(event, page, null, action);
    }

    private void sendMessages(ContentEvent event, AbstractPage page, BearychatAttachment attachment, String action) {
        if (event.isSuppressNotifications()) {
            LOGGER.info("Suppressing notification for {}.", page.getTitle());
            return;
        }

        List<String> channels = this.getChannels(page);
        BearychatMessage message = getMessage(page, action, true);

        String channel = null;
        String webhookUrl = this.getWebhookUrl(page);

        if(channels != null && !channels.isEmpty()) {
            channel = channels.get(0);
        }

        if(attachment != null) {
            message = message.attachments(attachment);
        }

        sendMessage(webhookUrl, channel, message);
    }

    private List<String> getChannels(AbstractPage page) {
        String spaceKey = page.getSpaceKey();
        String spaceChannels = configurationManager.getSpaceChannels(spaceKey);
        if (spaceChannels.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(spaceChannels.split(","));
    }

    private String getWebhookUrl(AbstractPage page) {
        String spaceKey = page.getSpaceKey();
        String spaceWebhook = configurationManager.getSpaceWebhookUrl(spaceKey);
        if (spaceWebhook.isEmpty()) {
            return configurationManager.getWebhookUrl();
        }
        return spaceWebhook;
    }

    private BearychatMessage getMessage(AbstractPage page, String action, boolean appendUser) {
        ConfluenceUser user = page.getLastModifier() != null ? page.getLastModifier() : page.getCreator();
        String rawText = this.getRawText(page, action, user);

        BearychatMessage message = new BearychatMessage();
        message.setNotification(rawText);

        message = appendPageLink(message, page);
        message = message.text(" - " + action + " by ");
        if(appendUser) {
            return appendPersonalSpaceUrl(message, user);
        } else {
            return message;
        }
    }

    private BearychatMessage appendPersonalSpaceUrl(BearychatMessage message, User user) {
        if (null == user) {
            return message.text("unknown user");
        }

        return message.link(webResourceUrlProvider.getBaseUrl(UrlMode.ABSOLUTE) + "/" + personalInformationManager.getOrCreatePersonalInformation(user).getUrlPath(), user.getFullName());
    }

    private BearychatMessage appendPageLink(BearychatMessage message, AbstractPage page) {
        String title = page.getSpace().getDisplayTitle() + " - " + page.getTitle();
        title = ViewUtils.limitLength(title, 80);
        return message.link(tinyLink(page), title);
    }

    private String tinyLink(AbstractPage page) {
        return webResourceUrlProvider.getBaseUrl(UrlMode.ABSOLUTE) + "/x/" + new TinyUrl(page).getIdentifier();
    }

    private String getRawText(AbstractPage page, String action, ConfluenceUser user){
        String title = page.getSpace().getDisplayTitle() + " - " + page.getTitle();
        title = ViewUtils.limitLength(title, 80);
        return title + " - " + action + " by " + user.getFullName();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.debug("Register BearyChat event listener");
        eventPublisher.register(this);
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.debug("Un-register BearyChat event listener");
        eventPublisher.unregister(this);
    }
}
