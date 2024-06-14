package com.namics.distrelec.b2b.facades.order.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistBaseStoreConverterUnitTest {

    @Mock
    private DistBaseSiteConverter baseSiteConverter;

    @InjectMocks
    private DistBaseStoreConverter distBaseStoreConverter;

    @Test
    public void testPopulate() {
        // given
        BaseStoreModel source = mock(BaseStoreModel.class);
        BaseSiteData baseSiteData = mock(BaseSiteData.class);
        List<BaseSiteModel> cmsSites = new ArrayList<>();
        cmsSites.add(mock(BaseSiteModel.class));
        cmsSites.add(null);
        cmsSites.add(mock(BaseSiteModel.class));
        BaseStoreData target = new BaseStoreData();

        // when
        when(source.getName()).thenReturn("name");
        when(source.getQuotationsEnabled()).thenReturn(true);
        when(source.getOrderApprovalEnabled()).thenReturn(false);
        when(baseSiteConverter.convert(any(BaseSiteModel.class))).thenReturn(baseSiteData);
        when(source.getCmsSites()).thenReturn(cmsSites);

        distBaseStoreConverter.populate(source, target);

        // then
        assertThat(target.getName(), equalTo("name"));
        assertThat(target.isQuotationsEnabled(), is(true));
        assertThat(target.isOrderApprovalEnabled(), is(false));
        assertThat(target.getCmsSites(), hasSize(2));

        verify(baseSiteConverter, times(2)).convert(any(BaseSiteModel.class));
    }
}
