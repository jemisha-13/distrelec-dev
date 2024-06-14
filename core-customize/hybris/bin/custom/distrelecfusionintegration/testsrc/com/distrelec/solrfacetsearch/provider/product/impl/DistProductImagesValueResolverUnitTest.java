package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.BRAND_LOGO;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_MEDIUM;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_SMALL;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_MEDIUM;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_SMALL;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.UNDERSCORE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
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
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistProductImagesValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String PORTRAIT_SMALL_URL = "/Web/WebShopImages/portrait_small/7-/01/30132277-01.jpg";

    private static final String LANDSCAPE_SMALL_URL = "/Web/WebShopImages/landscape_medium/7-/01/30132277-01.jpg";

    private static final String LANDSCAPE_MEDIUM_URL = "/Web/WebShopImages/landscape_small/7-/01/30132277-01.jpg";

    private static final String ENERGY_PORTRAIT_MEDIUM_URL = "/Web/WebShopImages/portrait_medium/42/46/EE_Label_1554246.jpg";

    private static final String MEDIA_DOMAIN = "https://media.distrelec.com";

    private static final String THUMBNAIL_IMAGE_URL = "thumbnail";

    private static final String ENERGY_EFFICIENCY_LABEL_IMAGE_URL_FIELD = "energyEfficiencyLabelImageUrl";

    private static final String MANUFACTURER_BRAND_LOGO_URL = "/Web/WebShopImages/manufacturer_logo/cm/yk/rnd_components_cmyk.jpg";

    private static final String MANUFACTURER_IMAGE_URL_FIELD = "manufacturerImageUrl";

    @InjectMocks
    private DistProductImagesValueResolver distProductImagesValueResolver;

    @Mock
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Mock
    private MediaContainerModel mediaContainer;

    @Mock
    private MediaModel imagePortraitSmall;

    @Mock
    private MediaFormatModel mediaFormatPortraitSmall;

    @Mock
    private MediaModel imageLandscapeSmall;

    @Mock
    private MediaFormatModel mediaFormatLandscapeSmall;

    @Mock
    private MediaModel imageLandscapeMedium;

    @Mock
    private MediaFormatModel mediaFormatLandscapeMedium;

    @Mock
    private MediaContainerModel energyMediaContainer;

    @Mock
    private MediaModel energyImagePortraitMedium;

    @Mock
    private MediaFormatModel energyMediaFormatPortraitMedium;

    @Mock
    private MediaContainerModel manufacturerMediaContainer;

    @Mock
    private MediaModel manufacturerImageBrandLogo;

    @Mock
    private MediaFormatModel manufacturerMediaFormatBrandLogo;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("imageURL");
        cloneableIndexedProperty.setExportId("imageURL");
        cloneableIndexedProperty.setLocalized(false);

        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);

        when(mediaFormatPortraitSmall.getQualifier()).thenReturn(PORTRAIT_SMALL);
        when(imagePortraitSmall.getMediaFormat()).thenReturn(mediaFormatPortraitSmall);
        when(imagePortraitSmall.getInternalURL()).thenReturn(PORTRAIT_SMALL_URL);

        when(mediaFormatLandscapeSmall.getQualifier()).thenReturn(LANDSCAPE_SMALL);
        when(imageLandscapeSmall.getMediaFormat()).thenReturn(mediaFormatLandscapeSmall);
        when(imageLandscapeSmall.getInternalURL()).thenReturn(LANDSCAPE_SMALL_URL);

        when(mediaFormatLandscapeMedium.getQualifier()).thenReturn(LANDSCAPE_MEDIUM);
        when(imageLandscapeMedium.getMediaFormat()).thenReturn(mediaFormatLandscapeMedium);
        when(imageLandscapeMedium.getInternalURL()).thenReturn(LANDSCAPE_MEDIUM_URL);

        when(mediaContainer.getMedias()).thenReturn(List.of(imagePortraitSmall, imageLandscapeSmall, imageLandscapeMedium));

        when(energyMediaFormatPortraitMedium.getQualifier()).thenReturn(PORTRAIT_MEDIUM);
        when(energyImagePortraitMedium.getMediaFormat()).thenReturn(energyMediaFormatPortraitMedium);
        when(energyImagePortraitMedium.getInternalURL()).thenReturn(ENERGY_PORTRAIT_MEDIUM_URL);

        when(energyMediaContainer.getMedias()).thenReturn(List.of(energyImagePortraitMedium));

        when(manufacturerMediaFormatBrandLogo.getQualifier()).thenReturn(BRAND_LOGO);
        when(manufacturerImageBrandLogo.getMediaFormat()).thenReturn(manufacturerMediaFormatBrandLogo);
        when(manufacturerImageBrandLogo.getDownloadURL()).thenReturn(MANUFACTURER_BRAND_LOGO_URL);

        when(manufacturerMediaContainer.getMedias()).thenReturn(List.of(manufacturerImageBrandLogo));

        when(product.getPrimaryImage()).thenReturn(mediaContainer);
        when(product.getEnergyEfficiencyLabelImage()).thenReturn(energyMediaContainer);
        when(manufacturer.getPrimaryImage()).thenReturn(manufacturerMediaContainer);
        when(product.getManufacturer()).thenReturn(manufacturer);

        when(distSiteBaseUrlResolutionService.getMediaUrlForSite(cmsSite, true, LANDSCAPE_SMALL_URL)).thenReturn(MEDIA_DOMAIN + LANDSCAPE_SMALL_URL);
    }

    @Test
    public void testResolveImages() throws FieldValueProviderException {
        distProductImagesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(PORTRAIT_SMALL_URL), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty(cloneableIndexedProperty.getExportId() + UNDERSCORE + LANDSCAPE_SMALL)),
                                                         eq(LANDSCAPE_SMALL_URL), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty(THUMBNAIL_IMAGE_URL)),
                                                         eq(MEDIA_DOMAIN + LANDSCAPE_SMALL_URL), any());
        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty(cloneableIndexedProperty.getExportId() + UNDERSCORE
                                                                                        + LANDSCAPE_MEDIUM)),
                                                         eq(LANDSCAPE_MEDIUM_URL), any());

        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty(ENERGY_EFFICIENCY_LABEL_IMAGE_URL_FIELD)),
                                                         eq(ENERGY_PORTRAIT_MEDIUM_URL), any());

        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty(MANUFACTURER_IMAGE_URL_FIELD)), eq(MANUFACTURER_BRAND_LOGO_URL), any());
    }

    @Test
    public void testResolveImagesNull() throws FieldValueProviderException {
        when(product.getPrimaryImage()).thenReturn(null);
        when(product.getIllustrativeImage()).thenReturn(null);
        when(product.getEnergyEfficiencyLabelImage()).thenReturn(null);
        when(product.getManufacturer()).thenReturn(manufacturer);
        when(manufacturer.getPrimaryImage()).thenReturn(null);

        distProductImagesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

    private IndexedProperty createNewIndexedProperty(String attributeName) {
        return distProductImagesValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                       attributeName);
    }

}
