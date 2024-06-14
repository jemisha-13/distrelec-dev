package com.distrelec.solrfacetsearch.indexer.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.ACTIVE_LABELS_CONTEXT_FIELD;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.BESTSELLER;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.CALIBRATION_SERVICE;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.HIT;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.HOT_OFFER;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.NEW;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.NO_MOVER;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.OFFER;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.PRODUCT_STATUS_CODES;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.SALES_ORG_PRODUCT;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.STOCK_LEVELS;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.TOP;
import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.USED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistProductDocumentContextProviderUnitTest {

    @InjectMocks
    private DistProductDocumentContextProvider distProductDocumentContextProvider;

    @Mock
    private DistPriceService distPriceService;

    @Mock
    private EnumerationService enumerationService;

    @Mock
    private SessionService sessionService;

    @Mock
    private DistProductSearchExportDAO distProductSearchExportDAO;

    @Mock
    private DistrelecCodelistService distrelecCodelistService;

    @Mock
    private IndexerBatchContext indexerBatchContext;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CountryModel country;

    @Mock
    private WarehouseModel deliveryWarehouse;

    @Mock
    private WarehouseModel pickupWarehouse;

    private Set<WarehouseModel> deliveryWarehouses;

    private Set<WarehouseModel> pickupWarehouses;

    @Mock
    private CMSSiteModel cmsSite;

    private Map<String, Object> indexDocumentContext = new HashMap<>();

    @Mock
    private ProductModel product;

    @Mock
    private DistSalesOrgProductModel salesOrgProduct;

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

    @Mock
    private ProductCountryModel productCountry;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        deliveryWarehouses = Set.of(deliveryWarehouse);
        pickupWarehouses = Set.of(pickupWarehouse);

        when(cmsSite.getCountry()).thenReturn(country);
        when(cmsSite.getDeliveryWarehouses()).thenReturn(deliveryWarehouses);
        when(cmsSite.getPickupWarehouses()).thenReturn(pickupWarehouses);
        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);

        setupPromotionLabels();

        // ProductCountryOpt
        Date before30Days = Date.from(LocalDateTime.now().minusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        Date after30Days = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        when(productCountry.getShowHotOfferLabelFromDate()).thenReturn(before30Days);
        when(productCountry.getShowHotOfferLabelUntilDate()).thenReturn(after30Days);
        when(productCountry.getShowNoMoverLabelFromDate()).thenReturn(before30Days);
        when(productCountry.getShowNoMoverLabelUntilDate()).thenReturn(after30Days);
        when(productCountry.getShowTopLabelFromDate()).thenReturn(before30Days);
        when(productCountry.getShowTopLabelUntilDate()).thenReturn(after30Days);
        when(productCountry.getShowHitLabelFromDate()).thenReturn(before30Days);
        when(productCountry.getShowHitLabelUntilDate()).thenReturn(after30Days);
        when(distProductSearchExportDAO.getProductCountry(product, country)).thenReturn(Optional.of(productCountry));

        // sales org
        when(distProductSearchExportDAO.getDistSalesOrgProductModels(product, cmsSite)).thenReturn(salesOrgProduct);
        when(salesOrgProduct.getShowNewLabelFromDate()).thenReturn(before30Days);
        when(salesOrgProduct.getShowNewLabelUntilDate()).thenReturn(after30Days);
        when(salesOrgProduct.getShowBestsellerLabelFromDate()).thenReturn(before30Days);
        when(salesOrgProduct.getShowBestsellerLabelUntilDate()).thenReturn(after30Days);

        // product
        when(product.isShowUsedLabel()).thenReturn(Boolean.TRUE);
        when(product.isCalibrated()).thenReturn(Boolean.TRUE);

        when(distProductSearchExportDAO.getTotalStockForProduct(product, deliveryWarehouses)).thenReturn(50L);
        when(distProductSearchExportDAO.getTotalStockForProduct(product, pickupWarehouses)).thenReturn(150L);
    }

    @Test
    public void testAddDocumentContext() {
        distProductDocumentContextProvider.addDocumentContext(indexDocumentContext, product, indexerBatchContext);

        DistSalesOrgProductModel salesOrgProduct = (DistSalesOrgProductModel) indexDocumentContext.get(SALES_ORG_PRODUCT);
        assertThat(salesOrgProduct).isNotNull()
                                   .isEqualTo(this.salesOrgProduct);

        ImmutablePair<Long, Long> stockLevels = (ImmutablePair<Long, Long>) indexDocumentContext.get(STOCK_LEVELS);
        assertThat(stockLevels).isNotNull();
        assertThat(stockLevels.getLeft()).isNotNull();
        assertThat(stockLevels.getLeft()).isEqualTo(50L);
        assertThat(stockLevels.getRight()).isNotNull();
        assertThat(stockLevels.getRight()).isEqualTo(150L);

        List<DistPromotionLabelModel> activeLabelsForProduct = (List<DistPromotionLabelModel>) indexDocumentContext.get(ACTIVE_LABELS_CONTEXT_FIELD);
        assertThat(activeLabelsForProduct).isNotNull()
                                          .hasSize(8)
                                          .containsAll(List.of(promotionLabelNoMover, promotionLabelHotOffer, promotionLabelHit,
                                                               promotionLabelTop, promotionLabelBestseller, promotionLabelNew,
                                                               promotionLabelUsed, promotionLabelCalibrationService));
        // TODO adjust when add price
        List<String> productStatusCodes = (List<String>) indexDocumentContext.get(PRODUCT_STATUS_CODES);
        assertThat(productStatusCodes).isNotNull()
                                      .hasSize(6)
                                      .containsAll(List.of("AVAILABLEDELIVERY", "AVAILABLEPICKUP", "EXCLUSIVE", "OFFER", "NEW", "CALIBRATION"));
    }

    private void setupPromotionLabels() {
        when(promotionLabelNoMover.getCode()).thenReturn(NO_MOVER);
        when(promotionLabelHotOffer.getCode()).thenReturn(HOT_OFFER);
        when(promotionLabelHit.getCode()).thenReturn(HIT);
        when(promotionLabelTop.getCode()).thenReturn(TOP);
        when(promotionLabelOffer.getCode()).thenReturn(OFFER);
        when(promotionLabelBestseller.getCode()).thenReturn(BESTSELLER);
        when(promotionLabelNew.getCode()).thenReturn(NEW);
        when(promotionLabelUsed.getCode()).thenReturn(USED);
        when(promotionLabelCalibrationService.getCode()).thenReturn(CALIBRATION_SERVICE);
        when(distrelecCodelistService.getAllDistrelecPromotionLabel()).thenReturn(List.of(promotionLabelNoMover, promotionLabelHotOffer,
                                                                                          promotionLabelHit, promotionLabelTop,
                                                                                          promotionLabelOffer, promotionLabelBestseller,
                                                                                          promotionLabelNew, promotionLabelUsed,
                                                                                          promotionLabelCalibrationService));
    }
}
