package com.namics.distrelec.b2b.storefront.security.cookie;

import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.distrelec.b2b.storefront.interceptors.AbstractOncePerRequestPreHandleInterceptor;
import com.namics.distrelec.b2b.storefront.security.impl.DefaultDYServerCookieStrategy;
import com.namics.distrelec.b2b.storefront.security.impl.DefaultFFCookieStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DistCookieInterceptor extends AbstractOncePerRequestPreHandleInterceptor{

    private static final String WARN_MESSAGE_COOKIES_ARE_IGNORED = "The servlet response is already committed. Changes to cookies are ignored! Affected cookie key: {}";
    protected static final String ROOT_PATH = "/";
    private static final Logger LOG = LoggerFactory.getLogger(DistCookieInterceptor.class);
    public static final int MAX_AGE_DEFAULT_VALUE = 3600 * 24 * 7;
    
    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private DefaultFFCookieStrategy ffCookieStrategy;
    
    @Autowired
    private BaseSiteService baseSiteService;
    
    @Autowired
    private DefaultDYServerCookieStrategy dyServerCookieStrategy;

    @Override
    protected boolean preHandleOnce(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.isSecure()) {
            addFFtrackingCookie(request, response);
            addDYServerCookie(request, response);
        } else {
            LOG.warn("Unsecure request: " + request.getRequestURI() + " " + request.getMethod() + " " + request.getHeader("X-Forwarded-For") + " " + request.getServerName());
        }
        return true;
    }
    
    protected void addFFtrackingCookie(final HttpServletRequest request, final HttpServletResponse response) {

        //Add FF cookie if it doesn't exist
         boolean alreadyExists =false;
         
         String countryFromCookie =StringUtils.EMPTY;
         boolean isStatisticEnabled =false;
         if (request.getCookies() != null) {
             for (final Cookie cookie : request.getCookies()) {
                 if (cookie.getName().equalsIgnoreCase(Attributes.FF_TRACKING_COOKIE.getName())) {
                     alreadyExists= true;
                 }
                 if (cookie.getName().equalsIgnoreCase(Attributes.FF_ENSIGTHEN_STATS_COOKIE.getName())) {
                	 if(cookie.getValue().equalsIgnoreCase("1")){
                		 isStatisticEnabled= true;	 
                	 }
                     
                 }
                 if (cookie.getName().equalsIgnoreCase(Attributes.SHOP_SETTINGS.getName())) {
                	 final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(cookie.getValue());
                	 countryFromCookie = (null!=cookieData && null!=cookieData.getCountry() )? cookieData.getCountry() : StringUtils.EMPTY;
                 }
                 
                 
             }
         }
         final String[] consentCheckCountries = getConfigurationService().getConfiguration().getString("skip.cookie.consent.check.country")
                 .split(",");
         List<String> baseSiteList = (null!=consentCheckCountries && consentCheckCountries.length >0) ? Arrays.asList(consentCheckCountries):Collections.EMPTY_LIST;
         if(StringUtils.isNotEmpty(countryFromCookie) && CollectionUtils.isNotEmpty(baseSiteList) && baseSiteList.contains(countryFromCookie)) {
        	 isStatisticEnabled = true;
         }
         if(!alreadyExists && isStatisticEnabled) {
             if (response.isCommitted()) {
                 LOG.warn(WARN_MESSAGE_COOKIES_ARE_IGNORED, Attributes.FF_TRACKING_COOKIE.getName());
                 return;
             }
             getFfCookieStrategy().setCookie(request, response);
            
         }else if(!isStatisticEnabled) {
        	 getFfCookieStrategy().deleteCookie(request, response);
         }
     }  
    protected void addDYServerCookie(final HttpServletRequest request, final HttpServletResponse response) {

        //Add _dyid_server cookie if it doesn't exist
         boolean alreadyExists =false;
         if (request.getCookies() != null) {
             for (final Cookie cookie : request.getCookies()) {
                 if (cookie.getName().equalsIgnoreCase(Attributes.DYID_SERVER_COOKIE.getName())) {
                     alreadyExists= true;
                 }
             }
         }
         if(!alreadyExists) {
             if (response.isCommitted()) {
                 LOG.warn(WARN_MESSAGE_COOKIES_ARE_IGNORED, Attributes.DYID_SERVER_COOKIE.getName());
                 return;
             }
             getDyServerCookieStrategy().setCookie(request, response);
            
         }
    }  
    private String getCurrentBaseSiteUid() {
        if (getBaseSiteService() != null && getBaseSiteService().getCurrentBaseSite() != null) {
            return getBaseSiteService().getCurrentBaseSite().getUid();
        }
        LOG.warn("no current base site available");
        return null;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }


    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    public DefaultFFCookieStrategy getFfCookieStrategy() {
        return ffCookieStrategy;
    }


    public void setFfCookieStrategy(DefaultFFCookieStrategy ffCookieStrategy) {
        this.ffCookieStrategy = ffCookieStrategy;
    }

    public DefaultDYServerCookieStrategy getDyServerCookieStrategy() {
        return dyServerCookieStrategy;
    }

    public void setDyServerCookieStrategy(DefaultDYServerCookieStrategy dyServerCookieStrategy) {
        this.dyServerCookieStrategy = dyServerCookieStrategy;
    }

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	/**
	 * @param baseSiteService the baseSiteService to set
	 */
	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

    
}
