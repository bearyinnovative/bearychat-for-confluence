package com.bearychat.components;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.bearychat.components.ConfigurationOption;

public class ConfigurationManager {
    private static final ConfluenceBandanaContext GLOBAL_CONTEXT = ConfluenceBandanaContext.GLOBAL_CONTEXT;
    private BandanaManager bandanaManager;

    public ConfigurationManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public String getWebhookUrl() {
        return getGlobalValue(ConfigurationOption.WEBHOOK_URL);
    }

    public void setWebhookUrl(String webhookUrl) {
        setGlobalValue(ConfigurationOption.WEBHOOK_URL, webhookUrl);
    }

    private String getGlobalValue(ConfigurationOption option) {
        return getBandanaValue(GLOBAL_CONTEXT, option);
    }

    private void setGlobalValue(ConfigurationOption option, String webhookUrl) {
        bandanaManager.setValue(GLOBAL_CONTEXT, option.getBandanaKey(), webhookUrl);
    }

    public void setSpaceChannels(String spaceKey, String channels) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
        bandanaManager.setValue(context, ConfigurationOption.CHANNELS.getBandanaKey(), channels);
    }

    public String getSpaceChannels(String spaceKey) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
        return getBandanaValue(context, ConfigurationOption.CHANNELS);
    }

    public void setSpaceWebhookUrl(String spaceKey, String webhookUrl) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
        bandanaManager.setValue(context, ConfigurationOption.WEBHOOK_URL.getBandanaKey(), webhookUrl);
    }

    public String getSpaceWebhookUrl(String spaceKey) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
        return getBandanaValue(context, ConfigurationOption.WEBHOOK_URL);
    }

    public void setSpaceEnabled(String spaceKey, Boolean enabled) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
        bandanaManager.setValue(context, ConfigurationOption.ENABLED.getBandanaKey(), enabled);
    }

    public Boolean getSpaceEnabled(String spaceKey) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
        Object enabled = bandanaManager.getValue(context, ConfigurationOption.ENABLED.getBandanaKey());
        if (enabled == null) {
            return null;
        }
        return (Boolean) enabled;
    }

    private String getBandanaValue(BandanaContext bandanaContext, ConfigurationOption configurationOption) {
        Object fromBandana = bandanaManager.getValue(bandanaContext, configurationOption.getBandanaKey());
        if (fromBandana == null) {
            return "";
        }
        return fromBandana.toString();
    }
}
