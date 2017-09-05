package com.bearychat.actions;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.xwork.RequireSecurityToken;
import com.bearychat.components.ConfigurationManager;
import com.opensymphony.xwork.Action;

public class SaveConfigurationAction extends ConfluenceActionSupport {
    private static final long    serialVersionUID = 1704624386670934630L;

    private ConfigurationManager configurationManager;
    private PermissionManager    permissionManager;

    private String               spaceKey;
    private String               bearychatWebhookUrl;

    @Override
    public boolean isPermitted() {
        return permissionManager.isConfluenceAdministrator(getAuthenticatedUser());
    }

    public void setBearychatWebhookUrl(String bearychatWebhookUrl) {
        this.bearychatWebhookUrl = bearychatWebhookUrl;
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(bearychatWebhookUrl)) {
            addActionError(getText("bearychat.webhookurl.form.invalid"));
        }
    }

    @Override
    @RequireSecurityToken(true)
    public String execute() throws Exception {
        configurationManager.setWebhookUrl(bearychatWebhookUrl);

        if (StringUtils.isNotBlank(spaceKey)) {
            return "redirect";
        }
        return Action.SUCCESS;
    }

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setConfigurationManager(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @Override
    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}
