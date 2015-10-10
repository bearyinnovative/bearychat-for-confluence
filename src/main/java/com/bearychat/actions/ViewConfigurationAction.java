package com.bearychat.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.security.PermissionManager;
import com.bearychat.components.ConfigurationManager;
import com.opensymphony.xwork.Action;

public class ViewConfigurationAction extends ConfluenceActionSupport {
   private static final long          serialVersionUID = -6293822785571568727L;

   private final ConfigurationManager configurationManager;
   private final PermissionManager    permissionManager;
   private boolean                    successFullUpdate;

   public ViewConfigurationAction(ConfigurationManager configurationManager, PermissionManager permissionManager) {
      this.configurationManager = configurationManager;
      this.permissionManager = permissionManager;
   }

   public void setResult(String result) {
      if ("success".equals(result)) {
         successFullUpdate = true;
      }
   }

   @Override
   public boolean isPermitted() {
      return permissionManager.isConfluenceAdministrator(getAuthenticatedUser());
   }

   public String getBearychatWebhookUrl() {
      return configurationManager.getWebhookUrl();
   }

   @Override
   public String execute() throws Exception {
      return Action.SUCCESS;
   }

   public boolean isSuccessFullUpdate() {
      return successFullUpdate;
   }
}
