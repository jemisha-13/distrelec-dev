package com.distrelec.solrfacetsearch.provider.category.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_SMALL;
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

public class DistCategoryImageValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String CATEGORY_PORTRAIT_SMALL_URL = "/Web/WebShopImages/portrait_small/15/46/drivkretsar_21546.jpg";

    @InjectMocks
    private DistCategoryImageValueResolver distCategoryImageValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private MediaContainerModel categoryMediaContainer;

    @Mock
    private MediaModel categoryImagePortraitSmall;

    @Mock
    private MediaFormatModel categoryMediaFormatPortraitSmall;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("categoryImage");
        cloneableIndexedProperty.setExportId("categoryImage");
        cloneableIndexedProperty.setLocalized(false);

        when(categoryMediaFormatPortraitSmall.getQualifier()).thenReturn(PORTRAIT_SMALL);
        when(categoryImagePortraitSmall.getMediaFormat()).thenReturn(categoryMediaFormatPortraitSmall);
        when(categoryImagePortraitSmall.getInternalURL()).thenReturn(CATEGORY_PORTRAIT_SMALL_URL);

        when(categoryMediaContainer.getMedias()).thenReturn(List.of(categoryImagePortraitSmall));

        when(category.getPrimaryImage()).thenReturn(categoryMediaContainer);
    }

    @Test
    public void testResolveCategoryImageUrl() throws FieldValueProviderException {
        distCategoryImageValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(CATEGORY_PORTRAIT_SMALL_URL), any());
    }

    @Test
    public void testResolveCategoryImageUrlNoMedia() throws FieldValueProviderException {
        when(categoryMediaContainer.getMedias()).thenReturn(List.of());

        distCategoryImageValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

    @Test
    public void testResolveCategoryImageUrlNoMediaContainer() throws FieldValueProviderException {
        when(category.getPrimaryImage()).thenReturn(null);

        distCategoryImageValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }
}
