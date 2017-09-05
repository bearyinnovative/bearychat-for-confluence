package com.bearychat.actions;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.bearychat.components.ConfigurationManager;
import com.opensymphony.xwork.Action;

public final class ViewSpaceConfigurationAction extends AbstractSpaceAdminAction {
    private static final long          serialVersionUID = 5691912273454934901L;

    private final ConfigurationManager configurationManager;
    private String channels;
    private String webhookUrl;
    private Boolean enabled;
    private boolean successFullUpdate;

    public ViewSpaceConfigurationAction(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    public void setResult(String result) {
        if ("success".equals(result)) {
            successFullUpdate = true;
        }
    }

    @Override
    public String execute() {
        setChannels(configurationManager.getSpaceChannels(key));
        setWebhookUrl(configurationManager.getSpaceWebhookUrl(key));
        setEnabled(configurationManager.getSpaceEnabled(key));
        return Action.SUCCESS;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getWebhookUrl() {
        return this.webhookUrl;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getChannels() {
        return this.channels;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled () {
        return this.enabled;
    }

    public boolean isSuccessFullUpdate() {
        return successFullUpdate;
    }
}
