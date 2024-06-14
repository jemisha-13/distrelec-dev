/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.namics.distrelec.b2b.fulfilmentprocess.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.namics.distrelec.b2b.fulfilmentprocess.constants.DistrelecB2BFulfilmentProcessConstants;

@SuppressWarnings("PMD")
public class DistrelecB2BFulfilmentProcessManager extends GeneratedDistrelecB2BFulfilmentProcessManager
{
	public static final DistrelecB2BFulfilmentProcessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (DistrelecB2BFulfilmentProcessManager) em.getExtension(DistrelecB2BFulfilmentProcessConstants.EXTENSIONNAME);
	}
	
}
