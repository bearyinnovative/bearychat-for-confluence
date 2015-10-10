package com.bearychat.components;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.user.User;
import com.bearychat.core.Bearychat;
import com.bearychat.core.BearychatMessage;

public class AnnotatedListener implements DisposableBean, InitializingBean {
   private static final Logger              LOGGER = LoggerFactory.getLogger(AnnotatedListener.class);

   private final WebResourceUrlProvider     webResourceUrlProvider;
   private final EventPublisher             eventPublisher;
   private final ConfigurationManager       configurationManager;
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
      sendMessages(event, event.getBlogPost(), "new blog post");
   }

   @EventListener
   public void pageCreateEvent(PageCreateEvent event) {
      sendMessages(event, event.getPage(), "new page created");
   }

   @EventListener
   public void pageUpdateEvent(PageUpdateEvent event) {
      sendMessages(event, event.getPage(), "page updated");
   }

   private void sendMessages(ContentEvent event, AbstractPage page, String action) {
      if (event.isSuppressNotifications()) {
         LOGGER.info("Suppressing notification for {}.", page.getTitle());
         return;
      }
      
      BearychatMessage message = getMessage(page, action);
      for (String channel : getChannels(page)) {
         sendMessage(channel, message);
      }
   }

   private List<String> getChannels(AbstractPage page) {
      String spaceChannels = configurationManager.getSpaceChannels(page.getSpaceKey());
      if (spaceChannels.isEmpty()) {
         return Collections.emptyList();
      }
      return Arrays.asList(spaceChannels.split(","));
   }

   private BearychatMessage getMessage(AbstractPage page, String action) {
      ConfluenceUser user = page.getLastModifier() != null ? page.getLastModifier() : page.getCreator();
      BearychatMessage message = new BearychatMessage();
      message = appendPageLink(message, page);
      message = message.text(" - " + action + " by ");
      return appendPersonalSpaceUrl(message, user);
   }

   private void sendMessage(String channel, BearychatMessage message) {
      LOGGER.info("Sending to {} on channel {} with message {}.", configurationManager.getWebhookUrl(), channel, message.toString());
      
      try {
         new Bearychat(configurationManager.getWebhookUrl()).sendToChannel(channel).push(message);
      }
      catch (IOException e) {
         LOGGER.error("Error when sending BearyChat message", e);
      }
   }

   private BearychatMessage appendPersonalSpaceUrl(BearychatMessage message, User user) {
      if (null == user) {
         return message.text("unknown user");
      }
      return message.link(webResourceUrlProvider.getBaseUrl(UrlMode.ABSOLUTE) + "/"
            + personalInformationManager.getOrCreatePersonalInformation(user).getUrlPath(), user.getFullName());
   }

   private BearychatMessage appendPageLink(BearychatMessage message, AbstractPage page) {
      return message.link(tinyLink(page), page.getSpace().getDisplayTitle() + " - " + page.getTitle());
   }

   private String tinyLink(AbstractPage page) {
      return webResourceUrlProvider.getBaseUrl(UrlMode.ABSOLUTE) + "/x/" + new TinyUrl(page).getIdentifier();
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