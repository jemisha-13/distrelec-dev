package com.distrelec.solrfacetsearch.indexer.impl;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.product.DistPriceService;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.store.BaseStoreModel;

/***
 * Provides data such as stock-level, priceInfo, etc. to all ValueResolver used to build a document for a ProductModel
 */
public class DistProductDocumentContextProvider implements SolrDocumentContextProvider<ProductModel> {

    private final static Logger LOG = LogManager.getLogger(DistProductDocumentContextProvider.class);

    public static final String HOT_OFFER = "hotOffer";

    public static final String NO_MOVER = "noMover";

    public static final String TOP = "top";

    public static final String HIT = "hit";

    public static final String USED = "used";

    public static final String CALIBRATION_SERVICE = "calibrationService";

    public static final String NEW = "new";

    public static final String BESTSELLER = "bestseller";

    public static final String OFFER = "offer";

    private static final String SPECIAL_PRICE_CONDITION = "ZN00";

    public static final String PRICEINFO_CONTEXT_FIELD = "priceInfo";

    public static final String ACTIVE_LABELS_CONTEXT_FIELD = "activeLabelsForProduct";

    public static final String PRODUCT_STATUS_CODES = "productStatusCode";

    public static final String STOCK_LEVELS = "stockLevels";

    public static final String SALES_ORG_PRODUCT = "salesOrgProduct";

    private DistPriceService distPriceService;

    private EnumerationService enumerationService;

    private SessionService sessionService;

    private DistProductSearchExportDAO distProductSearchExportDAO;

    private DistrelecCodelistService distrelecCodelistService;

    public DistProductDocumentContextProvider(final DistPriceService distPriceService, final EnumerationService enumerationService,
                                              final SessionService sessionService, final DistProductSearchExportDAO distProductSearchExportDAO,
                                              final DistrelecCodelistService distrelecCodelistService) {
        this.distPriceService = distPriceService;
        this.enumerationService = enumerationService;
        this.sessionService = sessionService;
        this.distProductSearchExportDAO = distProductSearchExportDAO;
        this.distrelecCodelistService = distrelecCodelistService;
    }

    @Override
    public void addDocumentContext(Map<String, Object> context, ProductModel product, IndexerBatchContext batchContext) {
        long t1 = System.currentTimeMillis();

        CMSSiteModel cmsSite = batchContext.getFacetSearchConfig().getIndexConfig().getCmsSite();
        List<PriceInformation> priceInfos = getPriceInfos(product, cmsSite);
        if (isNotEmpty(priceInfos)) {
            context.put(PRICEINFO_CONTEXT_FIELD, priceInfos);
        }

        List<DistPromotionLabelModel> promotionLabels = distrelecCodelistService.getAllDistrelecPromotionLabel();

        Optional<ProductCountryModel> productCountryOpt = distProductSearchExportDAO.getProductCountry(product, cmsSite.getCountry());

        DistSalesOrgProductModel salesOrgProduct = distProductSearchExportDAO.getDistSalesOrgProductModels(product, cmsSite);

        context.put(SALES_ORG_PRODUCT, salesOrgProduct);
        List<DistPromotionLabelModel> activeLabelsForProduct = getActiveLabelsForProduct(promotionLabels, productCountryOpt, product, salesOrgProduct,
                                                                                         priceInfos);

        context.put(ACTIVE_LABELS_CONTEXT_FIELD, activeLabelsForProduct);

        long deliverStock = distProductSearchExportDAO.getTotalStockForProduct(product, cmsSite.getDeliveryWarehouses());
        long pickupStock = distProductSearchExportDAO.getTotalStockForProduct(product, cmsSite.getPickupWarehouses());

        ImmutablePair<Long, Long> stockLevels = new ImmutablePair<>(deliverStock, pickupStock);

        context.put(STOCK_LEVELS, stockLevels);

        List<String> productStatusCodes = getProductStatusCodes(product, activeLabelsForProduct, deliverStock, pickupStock);

        context.put(PRODUCT_STATUS_CODES, productStatusCodes);

        LOG.debug("Creating document context for Product[{}] took: {} ms", product.getCode(), System.currentTimeMillis() - t1);
    }

    private List<String> getProductStatusCodes(final ProductModel product, final List<DistPromotionLabelModel> activeLabelsForProduct, final long deliverStock,
                                               final long pickupStock) {

        final List<String> productStatusCodes = new ArrayList<>();

        if (deliverStock > 0) {
            productStatusCodes.add("AVAILABLEDELIVERY");
        }
        if (pickupStock > 0) {
            productStatusCodes.add("AVAILABLEPICKUP");
        }

        List<String> labelCodes = activeLabelsForProduct.stream().map(DistPromotionLabelModel::getCode).collect(toList());
        if (labelCodes.contains(HOT_OFFER)) {
            productStatusCodes.add("EXCLUSIVE");
        }

        if (labelCodes.stream().anyMatch(labelCode -> TOP.equals(labelCode) || NEW.equals(labelCode))) {
            productStatusCodes.add("NEW");
        }

        if (labelCodes.stream().anyMatch(labelCode -> NO_MOVER.equals(labelCode) || OFFER.equals(labelCode))) {
            productStatusCodes.add("OFFER");
        }

        if (labelCodes.contains(CALIBRATION_SERVICE)) {
            productStatusCodes.add("CALIBRATION");
        }
        return productStatusCodes;
    }

    protected List<PriceInformation> getPriceInfos(final ProductModel product, final CMSSiteModel cmsSite) {
        // using array in order to be able to return value from within executeInLocalView-call
        final List<PriceInformation>[] priceInfos = new List[1];
        sessionService.executeInLocalView(new SessionExecutionBody() {

            @Override
            public void executeWithoutResult() {
                sessionService.setAttribute("Europe1PriceFactory_UPG", enumerationService.getEnumerationValue(UserPriceGroup.class, cmsSite.getUserPriceGroup()
                                                                                                                                           .getCode()));

                Optional<CurrencyModel> currency = cmsSite.getStores().stream()
                                                          .filter(store -> store.getChannel().equals(SiteChannel.B2B) && store.getDefaultCurrency() != null)
                                                          .map(BaseStoreModel::getDefaultCurrency).findAny();

                if (currency.isEmpty()) {
                    LOG.warn("Could not find currency for CmsSite[{}], site will be skipped", cmsSite.getUid());
                    return;
                }

                sessionService.setAttribute("currency", currency.get());
                // long t1 = System.currentTimeMillis();
                priceInfos[0] = distPriceService.getPriceInformationNet(product);
                // LOG.info("Fetching priceInfo took:" + (System.currentTimeMillis()-t1));

            }
        });
        return priceInfos[0];
    }

    protected List<DistPromotionLabelModel> getActiveLabelsForProduct(final List<DistPromotionLabelModel> promotionLabels,
                                                                      final Optional<ProductCountryModel> productCountryOpt, ProductModel product,
                                                                      DistSalesOrgProductModel salesOrgProduct, List<PriceInformation> priceInfos) {

        Date now = new Date();
        ArrayList<DistPromotionLabelModel> activeLabels = new ArrayList<>();

        for (final DistPromotionLabelModel promotionLabel : promotionLabels) {
            if (productCountryOpt.isPresent()) {
                ProductCountryModel productCountry = productCountryOpt.get();
                switch (promotionLabel.getCode()) {
                    case HOT_OFFER:
                        if (isDateInBetween(productCountry.getShowHotOfferLabelFromDate(), productCountry.getShowHotOfferLabelUntilDate(), now)) {
                            activeLabels.add(promotionLabel);
                        }
                        break;
                    case NO_MOVER:
                        if (isDateInBetween(productCountry.getShowNoMoverLabelFromDate(), productCountry.getShowNoMoverLabelUntilDate(), now)) {
                            activeLabels.add(promotionLabel);
                        }
                        break;
                    case TOP:
                        if (isDateInBetween(productCountry.getShowTopLabelFromDate(), productCountry.getShowTopLabelUntilDate(), now)) {
                            activeLabels.add(promotionLabel);
                        }
                        break;
                    case HIT:
                        if (isDateInBetween(productCountry.getShowHitLabelFromDate(), productCountry.getShowHitLabelUntilDate(), now)) {
                            activeLabels.add(promotionLabel);
                        }
                        break;
                }
            }

            switch (promotionLabel.getCode()) {
                case USED:
                    if (product.isShowUsedLabel()) {
                        activeLabels.add(promotionLabel);
                    }
                    break;
                case CALIBRATION_SERVICE:
                    if (product.isCalibrated()) {
                        activeLabels.add(promotionLabel);
                    }
            }

            if (salesOrgProduct != null) {
                switch (promotionLabel.getCode()) {
                    case NEW:
                        if (isDateInBetween(salesOrgProduct.getShowNewLabelFromDate(), salesOrgProduct.getShowNewLabelUntilDate(), now)) {
                            activeLabels.add(promotionLabel);
                        }
                        break;
                    case BESTSELLER:
                        if (isDateInBetween(salesOrgProduct.getShowBestsellerLabelFromDate(), salesOrgProduct.getShowBestsellerLabelUntilDate(), now)) {
                            activeLabels.add(promotionLabel);
                        }
                }
            }

            if (promotionLabel.getCode().equals(OFFER)) {
                boolean specialPriceExists = emptyIfNull(priceInfos)
                                                                    .stream()
                                                                    .map(priceInfo -> (DistPriceRow) priceInfo.getQualifierValue("pricerow"))
                                                                    .anyMatch(price -> SPECIAL_PRICE_CONDITION.equals(price.getErpPriceConditionType()
                                                                                                                           .getCode()));
                if (specialPriceExists) {
                    activeLabels.add(promotionLabel);
                }
            }
        }
        return activeLabels;
    }

    protected boolean isDateInBetween(final Date from, final Date to, final Date currentDate) {
        if (from != null && to != null) {
            return from.before(currentDate) && to.after(currentDate);
        }
        return false;

    }

}
