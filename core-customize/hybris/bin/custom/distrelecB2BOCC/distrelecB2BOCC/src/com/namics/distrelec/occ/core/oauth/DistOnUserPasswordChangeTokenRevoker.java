package com.namics.distrelec.occ.core.oauth;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.oauth2.OnUserPasswordChangeTokenRevoker;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.listener.PasswordChangeEvent;
import de.hybris.platform.webservicescommons.oauth2.token.OAuthRevokeTokenService;

public class DistOnUserPasswordChangeTokenRevoker extends OnUserPasswordChangeTokenRevoker {

    private static final Logger LOG = LoggerFactory.getLogger(DistOnUserPasswordChangeTokenRevoker.class);

    private static final String ENABLED_CONFIG_KEY = "oauth2.revoke.tokens.on.user.password.change";

    private final OAuthRevokeTokenService oauthRevokeTokenService;

    private final ConfigurationService configurationService;

    public DistOnUserPasswordChangeTokenRevoker(OAuthRevokeTokenService oauthRevokeTokenService, ConfigurationService configurationService) {
        super(oauthRevokeTokenService, configurationService);
        this.oauthRevokeTokenService = oauthRevokeTokenService;
        this.configurationService = configurationService;
    }

    @Override
    public void passwordChanged(PasswordChangeEvent event) {
        if (event != null && !this.isDisabled()) {
            LOG.info("User password changed. Removing all their tokens.");
            this.oauthRevokeTokenService.revokeUserAccessTokens(event.getUserId(), List.of());
        }
    }

    private boolean isDisabled() {
        return !this.configurationService.getConfiguration().getBoolean(ENABLED_CONFIG_KEY, true);
    }

}
