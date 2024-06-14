package com.namics.distrelec.b2b.core.jms.service.impl;

import static com.namics.distrelec.b2b.core.jms.constants.DistrelecB2BjmsConstants.PLATFORM_LOGO_CODE;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.jms.service.DistrelecB2BjmsService;

/**
 * This is an example of how the integration test should look like. {@link ServicelayerBaseTest} bootstraps platform so you have an access
 * to all Spring beans as well as database connection. It also ensures proper cleaning out of items created during the test after it
 * finishes. You can inject any Spring service using {@link Resource} annotation. Keep in mind that by default it assumes that annotated
 * field name matches the Spring Bean ID.
 */
@IntegrationTest
public class DefaultDistrelecB2BjmsServiceIntegrationTest extends ServicelayerBaseTest {
    @Resource
    private DistrelecB2BjmsService distrelecB2BjmsService;
    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp() throws Exception {
        distrelecB2BjmsService.createLogo(PLATFORM_LOGO_CODE);
    }

    @Test
    public void shouldReturnProperUrlForLogo() throws Exception {
        // given
        final String logoCode = "distrelecB2BjmsPlatformLogo";

        // when
        final String logoUrl = distrelecB2BjmsService.getHybrisLogoUrl(logoCode);

        // then
        assertThat(logoUrl).isNotNull();
        assertThat(logoUrl).isEqualTo(findLogoMedia(logoCode).getURL());
    }

    private MediaModel findLogoMedia(final String logoCode) {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery("SELECT {PK} FROM {Media} WHERE {code}=?code");
        fQuery.addQueryParameter("code", logoCode);

        return flexibleSearchService.searchUnique(fQuery);
    }

}
