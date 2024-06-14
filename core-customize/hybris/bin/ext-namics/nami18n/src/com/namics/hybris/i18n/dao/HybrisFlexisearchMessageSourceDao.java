/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.i18n.dao;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.commons.i18n.context.support.Reloadable;
import com.namics.commons.i18n.dao.MessageSourceDao;
import com.namics.commons.i18n.exception.DataAccessException;
import com.namics.commons.i18n.model.MessageCacheKey;
import com.namics.hybris.i18n.jalo.MessageResourceTableEntry;
import com.namics.hybris.i18n.model.MessageResourceTableEntryModel;

/**
 * Reads the message properties from a hybris database table.
 * 
 * @author jweiss, Namics AG
 * @since MGB MEL 1.0
 * 
 */
public class HybrisFlexisearchMessageSourceDao implements MessageSourceDao, Reloadable {
    private static final Logger LOG = Logger.getLogger(HybrisFlexisearchMessageSourceDao.class);

    protected FlexibleSearchService flexibleSearchService;
    protected CommonI18NService commonI18NService;
    protected boolean returnKeyWhenFailing;
    protected ConfigurationService configurationService;

    @Override
    public Map<MessageCacheKey, String> getAllMessages() throws DataAccessException {
        try {
            final Map<MessageCacheKey, String> resultMap = new HashMap<MessageCacheKey, String>();
            final String query = "SELECT {" + MessageResourceTableEntry.PK + "} FROM {" + MessageResourceTableEntryModel._TYPECODE + "} ORDER BY {"
                    + MessageResourceTableEntry.CODE + "} ASC";
            final Map<String, Object> params = new HashMap<String, Object>();
            final SearchResult<MessageResourceTableEntryModel> searchResult = getFlexibleSearchService().search(query, params);

            for (final MessageResourceTableEntryModel messageResourceTableEntry : searchResult.getResult()) {
                for (final Locale locale : getAvailableLanguages()) {
                    final MessageCacheKey messageCacheKey = new MessageCacheKey(locale, messageResourceTableEntry.getCode());
                    final String localizedMessage = messageResourceTableEntry.getMessage(locale);
                    resultMap.put(messageCacheKey, localizedMessage);
                }
            }

            return resultMap;
        } catch (final Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<Locale> getAvailableLanguages() throws DataAccessException {
        try {
            final List<Locale> locales = new ArrayList<Locale>();
            final List<LanguageModel> languages = getCommonI18NService().getAllLanguages();
            for (final LanguageModel language : languages) {
                if (language.getActive().booleanValue()) {
                    language.getIsocode();
                    locales.add(new Locale(language.getIsocode()));
                }
            }
            return locales;
        } catch (final Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public String getResourceMessage(final String key, final Locale locale) throws DataAccessException {
        
        try {

            // this is a very expensive operation, so only do it when enable in the configuration 
            boolean enabled = getConfigurationService().getConfiguration().getBoolean("com.namics.hybris.i18n.useDbMessageSource",false);
            if(!enabled)
            {
                return null;
            }
            
            final String query = "SELECT {" + MessageResourceTableEntry.PK + "} FROM {" + MessageResourceTableEntryModel._TYPECODE + "} WHERE {"
                    + MessageResourceTableEntry.CODE + "} = ?key ORDER BY {" + MessageResourceTableEntry.MODIFIED_TIME + "} DESC";

            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("key", key);

            final SearchResult<MessageResourceTableEntryModel> searchResult = getFlexibleSearchService().search(query, params);
            if (searchResult.getTotalCount() <= 0) {
                LOG.debug("MessageResourceTableEntry with code " + key + " not found! Please create the corresponding MessageResourceTableEntry!");
                return isReturnKeyWhenFailing() ? key : null;
            } else {
                return searchResult.getResult().get(0).getMessage(locale);
            }
        } catch (final Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void reload() {
        // there is no need to do something
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public boolean isReturnKeyWhenFailing() {
        return returnKeyWhenFailing;
    }

    @Required
    public void setReturnKeyWhenFailing(final boolean returnKeyWhenFailing) {
        this.returnKeyWhenFailing = returnKeyWhenFailing;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    
    
}
