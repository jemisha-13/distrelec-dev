/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.i18n.tools;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringUtils;

/**
 * Contains a list of message source strategies. A a message couldn't be found, the next strategy is choosen to search a message.
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class ComposedMessageSource implements MessageSource {

    protected List<MessageSource> messageSourceList;

    @Override
    public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) {
        NoSuchMessageException lastException = null;
        for (final MessageSource messageSource : getMessageSourceList()) {
            try {
                final String result = messageSource.getMessage(resolvable, locale);
                if (StringUtils.hasText(result)) {
                    return result;
                }
            } catch (final NoSuchMessageException e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            throw lastException;
        } else {
            return null;
        }

    }

    @Override
    public String getMessage(final String code, final Object[] args, final Locale locale) {
        NoSuchMessageException lastException = null;
        for (final MessageSource messageSource : getMessageSourceList()) {
            try {
                final String result = messageSource.getMessage(code, args, locale);
                if (StringUtils.hasText(result)) {
                    return result;
                }
            } catch (final NoSuchMessageException e) {
                lastException = e;
            }
        }
        if (lastException != null) {
            throw lastException;
        } else {
            return null;
        }
    }

    @Override
    public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
        for (final MessageSource messageSource : getMessageSourceList()) {
            final String result = messageSource.getMessage(code, args, defaultMessage, locale);
            if (StringUtils.hasText(result) && !result.equals(defaultMessage)) {
                return result;
            }
        }
        return defaultMessage;
    }

    public List<MessageSource> getMessageSourceList() {
        return messageSourceList;
    }

    @Required
    public void setMessageSourceList(final List<MessageSource> messageSourceList) {
        this.messageSourceList = messageSourceList;
    }

}
