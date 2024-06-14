package com.distrelec.solrfacetsearch.provider.manufacturer.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.BRAND_LOGO;
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

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistManufacturerImageValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String MANUFACTURER_BRAND_LOGO_URL = "/Web/WebShopImages/manufacturer_logo/cm/yk/rnd_components_cmyk.jpg";

    @InjectMocks
    private DistManufacturerImageValueResolver distManufacturerImageValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private MediaContainerModel manufacturerMediaContainer;

    @Mock
    private MediaModel manufacturerImageBrandLogo;

    @Mock
    private MediaFormatModel manufacturerMediaFormatBrandLogo;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("manufacturerImageUrl");
        cloneableIndexedProperty.setExportId("manufacturerImageUrl");
        cloneableIndexedProperty.setLocalized(true);

        when(manufacturerMediaFormatBrandLogo.getQualifier()).thenReturn(BRAND_LOGO);
        when(manufacturerImageBrandLogo.getMediaFormat()).thenReturn(manufacturerMediaFormatBrandLogo);
        when(manufacturerImageBrandLogo.getDownloadURL()).thenReturn(MANUFACTURER_BRAND_LOGO_URL);

        when(manufacturerMediaContainer.getMedias()).thenReturn(List.of(manufacturerImageBrandLogo));

        when(manufacturer.getPrimaryImage()).thenReturn(manufacturerMediaContainer);
    }

    @Test
    public void testResolveManufacturerImageUrl() throws FieldValueProviderException {
        distManufacturerImageValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), manufacturer);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(MANUFACTURER_BRAND_LOGO_URL), any());
    }

    @Test
    public void testResolveManufacturerImageNoContainer() throws FieldValueProviderException {
        when(manufacturer.getPrimaryImage()).thenReturn(null);

        distManufacturerImageValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), manufacturer);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

    @Test
    public void testResolveManufacturerImageNoBrandLogo() throws FieldValueProviderException {
        when(manufacturerMediaContainer.getMedias()).thenReturn(List.of());

        distManufacturerImageValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), manufacturer);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

}
