/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.i18n;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.AbstractMessageSource;

import com.namics.commons.i18n.dao.MessageSourceDao;
import com.namics.commons.i18n.exception.DataAccessException;

/**
 * A implementation without any caching mechanism. Used in hybris for hybris has already an internal cache.
 * 
 * Otherwise there is no way to reload the message source.
 * 
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.2
 * 
 */
public class UncachedDaoMessageSource extends AbstractMessageSource {

    private static final Logger LOG = Logger.getLogger(UncachedDaoMessageSource.class.getName());

    protected MessageSourceDao messageSourceDao;

    @Override
    protected MessageFormat resolveCode(final String code, final Locale locale) {
        MessageFormat resultMessageFormat = null;
        String unresolvedResult = null;
        try {
            unresolvedResult = getMessageSourceDao().getResourceMessage(code, locale);
        } catch (final DataAccessException e) {
            LOG.error("DataAccessException during resolving of resource message.", e);
        }

        if (unresolvedResult != null) {
            resultMessageFormat = new MessageFormat(unresolvedResult, locale);
        }
        return resultMessageFormat;
    }

    @Override
    protected String resolveCodeWithoutArguments(final String code, final Locale locale) {
        String result = null;
        try {
            result = getMessageSourceDao().getResourceMessage(code, locale);
        } catch (final DataAccessException e) {
            LOG.error("DataAccessException during resolving of resource message.", e);
        }
        return result;
    }

    public MessageSourceDao getMessageSourceDao() {
        return messageSourceDao;
    }

    @Required
    public void setMessageSourceDao(final MessageSourceDao messageSourceDao) {
        this.messageSourceDao = messageSourceDao;
    }
}
