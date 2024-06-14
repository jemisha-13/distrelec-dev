package com.namics.distrelec.b2b.facades.order.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistBaseSiteConverterUnitTest {

    DistBaseSiteConverter distBaseSiteConverter = new DistBaseSiteConverter();

    @Test
    public void testPopulate() {
        // given
        BaseSiteModel source = mock(BaseSiteModel.class);
        BaseSiteData target = new BaseSiteData();

        // when
        when(source.getName()).thenReturn("name");

        distBaseSiteConverter.populate(source, target);

        // then
        assertThat(target.getName(), equalTo("name"));
    }
}
