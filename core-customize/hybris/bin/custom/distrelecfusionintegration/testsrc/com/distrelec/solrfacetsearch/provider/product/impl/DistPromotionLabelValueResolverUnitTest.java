package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.ACTIVE_LABELS_CONTEXT_FIELD;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.CALIBRATION_SERVICE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;

import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistPromotionLabelValueResolverUnitTest extends DistAbstractValueResolverTest {

    @InjectMocks
    private DistPromotionLabelValueResolver distPromotionLabelValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    private Map<String, Object> indexDocumentContext = new HashMap<>();

    @Mock
    private DistPromotionLabelModel promotionLabelNoMover;

    @Mock
    private DistPromotionLabelModel promotionLabelHotOffer;

    @Mock
    private DistPromotionLabelModel promotionLabelHit;

    @Mock
    private DistPromotionLabelModel promotionLabelTop;

    @Mock
    private DistPromotionLabelModel promotionLabelNew;

    @Mock
    private DistPromotionLabelModel promotionLabelOffer;

    @Mock
    private DistPromotionLabelModel promotionLabelUsed;

    @Mock
    private DistPromotionLabelModel promotionLabelBestseller;

    @Mock
    private DistPromotionLabelModel promotionLabelCalibrationService;

    @Before
    public void init() {
        super.init();

        indexedProperty.setName("promotionLabel");
        indexedProperty.setExportId("promotionLabel");
        indexedProperty.setLocalized(false);

        setupPromotionLabels();

        when(distSolrInputDocument.getIndexDocumentContext()).thenReturn(indexDocumentContext);
        indexDocumentContext.put(ACTIVE_LABELS_CONTEXT_FIELD,
                                 List.of(promotionLabelNoMover, promotionLabelHotOffer, promotionLabelHit,
                                         promotionLabelTop, promotionLabelOffer, promotionLabelBestseller,
                                         promotionLabelNew, promotionLabelUsed, promotionLabelCalibrationService));
    }

    @Test
    public void testResolvePromotionLabels() throws FieldValueProviderException {
        distPromotionLabelValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(indexedProperty), product);

        String expectedLabel = getLabelValue(promotionLabelCalibrationService);
        verify(distSolrInputDocument, times(1)).addField(eq(indexedProperty), eq(expectedLabel), any());
    }

    protected String getLabelValue(DistPromotionLabelModel label) {
        return label.getCode() + "|" + label.getName();
    }

    private void setupPromotionLabels() {
        when(promotionLabelNoMover.getPriority()).thenReturn(1);
        when(promotionLabelHotOffer.getPriority()).thenReturn(2);
        when(promotionLabelHit.getPriority()).thenReturn(7);
        when(promotionLabelTop.getPriority()).thenReturn(4);
        when(promotionLabelOffer.getPriority()).thenReturn(3);
        when(promotionLabelBestseller.getPriority()).thenReturn(5);
        when(promotionLabelNew.getPriority()).thenReturn(6);
        when(promotionLabelUsed.getPriority()).thenReturn(8);
        when(promotionLabelCalibrationService.getCode()).thenReturn(CALIBRATION_SERVICE);
        when(promotionLabelCalibrationService.getName()).thenReturn("+cal");
        when(promotionLabelCalibrationService.getPriority()).thenReturn(0);
    }
}
