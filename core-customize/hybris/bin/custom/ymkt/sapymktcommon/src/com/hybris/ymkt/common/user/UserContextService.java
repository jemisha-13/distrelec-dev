/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.common.user;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Provide utility methods such as {@link #getUserOrigin()} and {@link #getUserId()}.
 *
 */
public class UserContextService
{
	private static final Logger LOG = LoggerFactory.getLogger(UserContextService.class);

	protected String anonymousUserOrigin;
	protected UserService userService;

	/**
	 * @return <code>SAP_HYBRIS_CONSUMER</code> or the property value of
	 *         <code>sapymktcommon.user.origin.SAP_HYBRIS_CONSUMER</code>.
	 */
	public static String getOriginIdSapHybrisConsumer()
	{
		return Config.getString("sapymktcommon.user.origin.SAP_HYBRIS_CONSUMER", "SAP_HYBRIS_CONSUMER");
	}

	/**
	 * @param user
	 * @return {@link CustomerModel#getOriginalUid()} if user is an instance of CustomerModel. {@link UserModel#getUid()}
	 *         otherwise.
	 */
	public static String getUserOriginalUid(final UserModel user)
	{
		if (user instanceof CustomerModel)
		{
			final String originalUid = ((CustomerModel) user).getOriginalUid();
			if (originalUid != null && !originalUid.isEmpty())
			{
				return originalUid;
			}
		}
		return user.getUid();
	}

	public String getAnonymousUserId()
	{
		try
		{
			final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			final Cookie[] cookies = attributes.getRequest().getCookies();

			if (cookies == null)
			{
				return "";
			}

			for (final Cookie cookie : cookies)
			{
				if (cookie.getName().startsWith("_pk_id"))
				{
					return cookie.getValue().substring(0, 16);
				}
			}
		}
		catch (final IllegalStateException e)
		{
			LOG.info("Not executing within a web request", e);
		}
		return "";
	}

	public String getAnonymousUserOrigin()
	{
		return this.anonymousUserOrigin;
	}

	public String getLoggedInUserOrigin()
	{
		return getOriginIdSapHybrisConsumer();
	}

	/**
	 * @return User ID according to the {@link #getUserOrigin()}.
	 */
	public String getUserId()
	{
		return this.isAnonymousUser() ? this.getAnonymousUserId() : getUserOriginalUid(this.userService.getCurrentUser());
	}

	/**
	 * ID Origin is a synonyms of User Type.
	 *
	 * @return COOKIE_ID or SAP_HYBRIS_CONSUMER
	 */
	public String getUserOrigin()
	{
		return this.isAnonymousUser() ? this.anonymousUserOrigin : getOriginIdSapHybrisConsumer();
	}

	public boolean isAnonymousUser()
	{
		final UserModel currentUser = userService.getCurrentUser();
		return currentUser == null || userService.isAnonymousUser(currentUser);
	}

	@Required
	public void setAnonymousUserOrigin(final String anonymousUserOrigin)
	{
		LOG.debug("anonymousUserOrigin={}", anonymousUserOrigin);
		this.anonymousUserOrigin = anonymousUserOrigin.intern();
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
