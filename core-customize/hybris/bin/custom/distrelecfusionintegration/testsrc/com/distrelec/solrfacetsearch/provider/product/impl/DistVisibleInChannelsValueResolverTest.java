package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistVisibleInChannelsValueResolverTest extends DistAbstractValueResolverTest {

    @InjectMocks
    private DistVisibleInChannelsValueResolver distVisibleInChannelsValueResolver;

    @Mock
    private EnumerationValueModel siteChannelB2B;

    @Mock
    private EnumerationValueModel siteChannelB2C;

    @Before
    public void init() {
        super.init();

        when(siteChannelB2B.getCode()).thenReturn(SiteChannel.B2B.getCode());
        when(siteChannelB2C.getCode()).thenReturn(SiteChannel.B2C.getCode());
    }

    @Test
    public void testResolveVisibleInB2C() throws FieldValueProviderException {
        when(distVisibleInChannelsValueResolver.getDistProductSearchExportDAO().getChannelsWithPunchOutFilters(product)).thenReturn(List.of(siteChannelB2B));

        distVisibleInChannelsValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(document, times(1)).addField(eq(cloneableIndexedProperty), eq(List.of(SiteChannel.B2C.getCode())), any());
    }

    @Test
    public void testResolveVisibleInB2B() throws FieldValueProviderException {
        when(distVisibleInChannelsValueResolver.getDistProductSearchExportDAO().getChannelsWithPunchOutFilters(product)).thenReturn(List.of(siteChannelB2C));

        distVisibleInChannelsValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(document, times(1)).addField(eq(cloneableIndexedProperty), eq(List.of(SiteChannel.B2B.getCode())), any());
    }

    @Test
    public void testResolveVisibleInNone() throws FieldValueProviderException {
        when(distVisibleInChannelsValueResolver.getDistProductSearchExportDAO().getChannelsWithPunchOutFilters(product)).thenReturn(List.of(siteChannelB2C,
                                                                                                                                            siteChannelB2B));

        distVisibleInChannelsValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(document, times(1)).addField(eq(cloneableIndexedProperty), eq(List.of()), any());
    }

    @Test
    public void testResolveVisibleInAll() throws FieldValueProviderException {
        when(distVisibleInChannelsValueResolver.getDistProductSearchExportDAO().getChannelsWithPunchOutFilters(product)).thenReturn(List.of());

        distVisibleInChannelsValueResolver.resolve(document, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(document, times(1)).addField(eq(cloneableIndexedProperty), eq(List.of(SiteChannel.B2B.getCode(), SiteChannel.B2C.getCode())), any());
    }
}
