/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.security;

import com.namics.distrelec.b2b.core.enums.RegistrationType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Strategy for automatic login of a user after registration
 */
public interface AutoLoginStrategy
{
	/**
	 * Login a user
	 * 
	 * @param username
	 * @param password
	 * @param request
	 * @param response
     * @param registrationType
	 */
	void login(String username, String password, HttpServletRequest request, HttpServletResponse response,
            final RegistrationType registrationType);
}
