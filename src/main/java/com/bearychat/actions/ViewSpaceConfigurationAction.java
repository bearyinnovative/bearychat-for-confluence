package com.bearychat.actions;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.bearychat.components.ConfigurationManager;
import com.opensymphony.xwork.Action;

public final class ViewSpaceConfigurationAction extends AbstractSpaceAdminAction {
    private static final long          serialVersionUID = 5691912273454934901L;

    private final ConfigurationManager configurationManager;
    private String                     channels;
    private String                     webhook;
    private boolean                    successFullUpdate;

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
        setWebhook(configurationManager.getSpaceWebhookUrl(key));
        return Action.SUCCESS;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getWebhook() {
        return this.webhook;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getChannels() {
        return this.channels;
    }

    public boolean isSuccessFullUpdate() {
        return successFullUpdate;
    }
}
