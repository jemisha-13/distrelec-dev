package com.namics.distrelec.occ.core.oauth;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.oauth2.jwt.util.KeyStoreHelper;
import de.hybris.platform.oauth2.services.impl.DefaultHybrisOpenIDTokenServices;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.oauth2.client.ClientDetailsDao;
import de.hybris.platform.webservicescommons.oauth2.scope.OpenIDExternalScopesStrategy;

import static com.namics.distrelec.occ.core.constants.YcommercewebservicesConstants.LANGUAGE_ATTRIBUTE;
import static java.util.Collections.singletonMap;

public class DistHybrisOpenIDTokenServices extends DefaultHybrisOpenIDTokenServices {

    private ClientDetailsDao clientDetailsDao;

    private KeyStoreHelper keyStoreHelper;

    private ConfigurationService configurationService;

    private UserService userService;

    private OpenIDExternalScopesStrategy externalScopesStrategy;

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken accessToken = super.createAccessToken(authentication);
        UserModel currentUser = userService.getCurrentUser();
        if (userService.isAnonymousUser(currentUser) || currentUser.getSessionLanguage() == null) {
            return accessToken;
        }
        DefaultOAuth2AccessToken accessTokenWithAdditionalInformation = new DefaultOAuth2AccessToken(accessToken);
        accessTokenWithAdditionalInformation.setAdditionalInformation(singletonMap(LANGUAGE_ATTRIBUTE, currentUser.getSessionLanguage().getIsocode()));
        return accessTokenWithAdditionalInformation;
    }

    public void setExternalScopesStrategy(OpenIDExternalScopesStrategy externalScopesStrategy) {
        this.externalScopesStrategy = externalScopesStrategy;
    }

    protected KeyStoreHelper getKeyStoreHelper() {
        return this.keyStoreHelper;
    }

    @Required
    public void setKeyStoreHelper(KeyStoreHelper keyStoreHelper) {
        this.keyStoreHelper = keyStoreHelper;
    }

    protected ClientDetailsDao getClientDetailsDao() {
        return this.clientDetailsDao;
    }

    @Required
    public void setClientDetailsDao(ClientDetailsDao clientDetailsDao) {
        this.clientDetailsDao = clientDetailsDao;
    }

    protected ConfigurationService getConfigurationService() {
        return this.configurationService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected UserService getUserService() {
        return this.userService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
