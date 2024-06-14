/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.distrelec.smartedit.webservices.setup;

import static com.distrelec.smartedit.webservices.constants.DistrelecsmarteditwebservicesConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.distrelec.smartedit.webservices.constants.DistrelecsmarteditwebservicesConstants;
import com.distrelec.smartedit.webservices.service.DistrelecsmarteditwebservicesService;


@SystemSetup(extension = DistrelecsmarteditwebservicesConstants.EXTENSIONNAME)
public class DistrelecsmarteditwebservicesSystemSetup
{
	private final DistrelecsmarteditwebservicesService distrelecsmarteditwebservicesService;

	public DistrelecsmarteditwebservicesSystemSetup(final DistrelecsmarteditwebservicesService distrelecsmarteditwebservicesService)
	{
		this.distrelecsmarteditwebservicesService = distrelecsmarteditwebservicesService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		distrelecsmarteditwebservicesService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return DistrelecsmarteditwebservicesSystemSetup.class.getResourceAsStream("/distrelecsmarteditwebservices/sap-hybris-platform.png");
	}
}
