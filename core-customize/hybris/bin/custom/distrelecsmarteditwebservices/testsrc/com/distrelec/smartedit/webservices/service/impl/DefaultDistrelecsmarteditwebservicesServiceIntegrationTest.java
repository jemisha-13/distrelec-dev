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
package com.distrelec.smartedit.webservices.service.impl;

import static org.fest.assertions.Assertions.assertThat;
import static com.distrelec.smartedit.webservices.constants.DistrelecsmarteditwebservicesConstants.PLATFORM_LOGO_CODE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import com.distrelec.smartedit.webservices.service.DistrelecsmarteditwebservicesService;
import com.distrelec.smartedit.webservices.service.impl.DefaultDistrelecsmarteditwebservicesService;


/**
 * This is an example of how the integration test should look like. {@link ServicelayerBaseTest} bootstraps platform so
 * you have an access to all Spring beans as well as database connection. It also ensures proper cleaning out of items
 * created during the test after it finishes. You can inject any Spring service using {@link Resource} annotation. Keep
 * in mind that by default it assumes that annotated field name matches the Spring Bean ID.
 */
@IntegrationTest
public class DefaultDistrelecsmarteditwebservicesServiceIntegrationTest extends ServicelayerBaseTest
{
	@Resource
	private DistrelecsmarteditwebservicesService distrelecsmarteditwebservicesService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		distrelecsmarteditwebservicesService.createLogo(PLATFORM_LOGO_CODE);
	}

	@Test
	public void shouldReturnProperUrlForLogo() throws Exception
	{
		// given
		final String logoCode = "distrelecsmarteditwebservicesPlatformLogo";

		// when
		final String logoUrl = distrelecsmarteditwebservicesService.getHybrisLogoUrl(logoCode);

		// then
		assertThat(logoUrl).isNotNull();
		assertThat(logoUrl).isEqualTo(findLogoMedia(logoCode).getURL());
	}

	private MediaModel findLogoMedia(final String logoCode)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery("SELECT {PK} FROM {Media} WHERE {code}=?code");
		fQuery.addQueryParameter("code", logoCode);

		return flexibleSearchService.searchUnique(fQuery);
	}

}
