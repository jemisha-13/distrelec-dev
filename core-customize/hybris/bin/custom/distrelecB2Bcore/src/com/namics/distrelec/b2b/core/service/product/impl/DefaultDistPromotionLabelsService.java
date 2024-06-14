/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistPromotionLabelsService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.product.ProductCountryService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.DistDateTimeUtils;
import com.namics.distrelec.b2b.core.util.PromoLabelUtil;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * Default implementation of {@link DistPromotionLabelsService}
 * 
 * @author sivakumaran, Namics AG
 * 
 */
public class DefaultDistPromotionLabelsService extends AbstractBusinessService implements DistPromotionLabelsService {

    private static final Logger LOG = Logger.getLogger(DefaultDistPromotionLabelsService.class);

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ProductCountryService productCountryService;

    @Autowired
    private CommercePriceService commercePriceService;

    @Autowired
    private PromoLabelUtil promoLabelUtil;

    @Override
    public List<DistPromotionLabelModel> getActivePromotionLabelsForProduct(final ProductModel product) {
        final DistSalesOrgProductModel salesOrgProduct = getDistSalesOrgProductService().getCurrentSalesOrgProduct(product);
        final ProductCountryModel productCountry = getProductCountryService().getCurrentProductCountry(product);

        List<DistPromotionLabelModel> activePromotionLabels = getActivePromotionLabels(product, salesOrgProduct, productCountry);

        if(CollectionUtils.isNotEmpty(activePromotionLabels)){
            if(promoLabelUtil.onlyContainsNewAndTopLabels(activePromotionLabels)){
                return promoLabelUtil.hideNewToUsLabel(activePromotionLabels);
            }
        }
        return activePromotionLabels;
    }

    @Override
    public List<DistPromotionLabelModel> getActivePromotionLabels(final ProductModel product, final DistSalesOrgProductModel salesOrgProduct,
            final ProductCountryModel productCountry) {
        final List<DistPromotionLabelModel> activePromotionLabels = new ArrayList<>();
        final List<DistPromotionLabelModel> allPromotionLabels = getAllPromotionLabels(null, null);
        if (CollectionUtils.isNotEmpty(allPromotionLabels)) {
            for (final DistPromotionLabelModel promoLabel : allPromotionLabels) {
                try {
                    if (Boolean.TRUE.equals(promoLabel.getMaintainedInDistSalesOrgProduct()) && salesOrgProduct != null) {
                        addIfActive(activePromotionLabels, promoLabel, salesOrgProduct);
                    } else if (Boolean.TRUE.equals(promoLabel.getMaintainedInProductCountry()) && productCountry != null) {
                        addIfActive(activePromotionLabels, promoLabel, productCountry);
                    } else if (isUsed(promoLabel.getCode()) && product.isShowUsedLabel()) {
                        activePromotionLabels.add(promoLabel);
                    } else if (salesOrgProduct != null && isBestSeller(promoLabel.getCode()) && isBestSellerProductValidToday(salesOrgProduct)) {
                        activePromotionLabels.add(promoLabel);
                    } else if (isCalibrationService(promoLabel.getCode()) && isTrue(product.getCalibrationService()) && product.isCalibrated()) {
                        activePromotionLabels.add(promoLabel);
                    } else if (isOffer(promoLabel.getCode())) {
                        final PriceInformation priceInfo = getCommercePriceService().getWebPriceForProduct(product, false);
                        if (priceInfo != null) {
                            final DistPriceRow priceRow = (DistPriceRow) priceInfo.getQualifierValue("pricerow");
                            if (priceRow != null && Boolean.TRUE.equals(priceRow.isSpecialPrice())) {
                                activePromotionLabels.add(promoLabel);
                            }
                        }
                    }
                } catch (final AttributeNotSupportedException e) {
                    LOG.error("Attribute not defined in DistSalesOrgProduct or ProductCountry. Check that the promotion with the code " + promoLabel.getCode()
                            + " is maintained in DistSalesOrgProduct resp. ProductCountry and if not, update the promotion and set the value of "
                            + DistPromotionLabelModel.MAINTAINEDINDISTSALESORGPRODUCT + " resp. " + DistPromotionLabelModel.MAINTAINEDINPRODUCTCOUNTRY
                            + " to false", e);
                }
            }
        }
        return activePromotionLabels;
    }

    @Override
    public List<DistPromotionLabelModel> getAllPromotionLabels(final Boolean maintainedInDistSalesOrgProduct, final Boolean maintainedInProductCountry) {
        return getAllPromotionLabelsFromSession(maintainedInDistSalesOrgProduct, maintainedInProductCountry);
    }

    /**
     * <p>
     * Retrieves all active promotion labels according to the values of the parameters {@code maintainedInDistSalesOrgProduct} and
     * {@code maintainedInProductCountry}. The method checks first if the list exists already in the session, if not, it loads the list and
     * store it in the session using the name {@code all_promotion_labels_} concatenated with the the values of the two parameters. <br/>
     * The goal of this method is to reduce the number of flexible search calls.
     * </p>
     * 
     * @param maintainedInDistSalesOrgProduct
     * @param maintainedInProductCountry
     * @return a list of {@code DistPromotionLabelModel}
     * @see #getAllPromotionLabels(Boolean, Boolean)
     */
    private List<DistPromotionLabelModel> getAllPromotionLabelsFromSession(final Boolean maintainedInDistSalesOrgProduct,
            final Boolean maintainedInProductCountry) {
        final StringBuilder name = new StringBuilder("all_promotion_labels_").append(maintainedInDistSalesOrgProduct).append('_')
                .append(maintainedInProductCountry);

        // Load from the session if already calculated.
        return getSessionService().getOrLoadAttribute(name.toString(), () -> {
            final String PROMOTION_LABEL_TYPECODE = DistPromotionLabelModel._TYPECODE;
            final Map<String, Object> params = new HashMap<>();
            final StringBuilder query = new StringBuilder();

            query.append("SELECT {").append(PROMOTION_LABEL_TYPECODE).append(".").append(DistPromotionLabelModel.PK).append("} FROM {")
                    .append(PROMOTION_LABEL_TYPECODE).append("}");
            if (maintainedInDistSalesOrgProduct != null || maintainedInProductCountry != null) {
                query.append(" WHERE");
            }
            if (maintainedInDistSalesOrgProduct != null) {
                query.append(" {").append(PROMOTION_LABEL_TYPECODE).append(".").append(DistPromotionLabelModel.MAINTAINEDINDISTSALESORGPRODUCT)
                        .append("}=(?").append(DistPromotionLabelModel.MAINTAINEDINDISTSALESORGPRODUCT).append(")");
                params.put(DistPromotionLabelModel.MAINTAINEDINDISTSALESORGPRODUCT, maintainedInDistSalesOrgProduct);
            }
            if (maintainedInProductCountry != null) {
                query.append(" {").append(PROMOTION_LABEL_TYPECODE).append(".").append(DistPromotionLabelModel.MAINTAINEDINPRODUCTCOUNTRY)
                        .append("}=(?").append(DistPromotionLabelModel.MAINTAINEDINPRODUCTCOUNTRY).append(")");
                params.put(DistPromotionLabelModel.MAINTAINEDINPRODUCTCOUNTRY, maintainedInProductCountry);
            }
            query.append(" ORDER BY {").append(PROMOTION_LABEL_TYPECODE).append(".").append(DistPromotionLabelModel.RANK).append("}");

            if (LOG.isDebugEnabled()) {
                LOG.debug(query.toString());
            }
            final SearchResult<DistPromotionLabelModel> promotionLabels = getFlexibleSearchService().search(query.toString(), params);
            return promotionLabels.getResult();
        });
    }

    protected boolean isBestSeller(final String code) {
        return DistConstants.PromotionLabels.BESTSELLER.equals(code);
    }

    // a best seller promotional level is valid if todays date falls in between showBestsellerLabelFromDate & showBestsellerLabelUntilDate
    protected boolean isBestSellerProductValidToday(final DistSalesOrgProductModel salesOrgProduct) {
        final Date showBestsellerLabelFromDate = DistDateTimeUtils.getDateAtMidnightStart(salesOrgProduct.getShowBestsellerLabelFromDate());
        final Date showBestsellerLabelUntilDate = DistDateTimeUtils.getDateAtMidnightEnd(salesOrgProduct.getShowBestsellerLabelUntilDate());
        final Date today = new Date();
        return DistDateTimeUtils.isDateBetweenDateRange(today, showBestsellerLabelFromDate, showBestsellerLabelUntilDate);
    }

    protected boolean isUsed(final String code) {
        return DistConstants.PromotionLabels.USED.equals(code);
    }

    protected boolean isOffer(final String code) {
        return DistConstants.PromotionLabels.OFFER.equals(code);
    }

    protected boolean isCalibrationService(final String code) {
        return DistConstants.PromotionLabels.CALIBRATION_SERVICE.equals(code);
    }

    protected void addIfActive(final List<DistPromotionLabelModel> promoLabels, final DistPromotionLabelModel label, final Object maintainer) {
        final Date fromDate = (Date) getModelService().getAttributeValue(maintainer, buildShowLabelFromDateAttribute(label.getCode()));
        final Date untilDate = (Date) getModelService().getAttributeValue(maintainer, buildShowLabelUntilDateAttribute(label.getCode()));
        final Date today = new Date();
        if (fromDate != null && today.after(fromDate) && untilDate != null && today.before(untilDate)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("active promotion. code: " + label.getCode());
            }
            promoLabels.add(label);
        }
    }

    protected String buildShowLabelUntilDateAttribute(final String code) {
        return "show" + code + "LabelUntilDate";
    }

    protected String buildShowLabelFromDateAttribute(final String code) {
        return "show" + code + "LabelFromDate";
    }

    public DistSalesOrgProductService getDistSalesOrgProductService() {
        return distSalesOrgProductService;
    }

    public void setDistSalesOrgProductService(final DistSalesOrgProductService distSalesOrgProductService) {
        this.distSalesOrgProductService = distSalesOrgProductService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public ProductCountryService getProductCountryService() {
        return productCountryService;
    }

    public void setProductCountryService(final ProductCountryService productCountryService) {
        this.productCountryService = productCountryService;
    }

    public DistCommercePriceService getCommercePriceService() {
        return (DistCommercePriceService) commercePriceService;
    }

    public void setCommercePriceService(final CommercePriceService commercePriceService) {
        this.commercePriceService = commercePriceService;
    }

    public PromoLabelUtil getPromoLabelUtil() {
        return promoLabelUtil;
    }

    public void setPromoLabelUtil(PromoLabelUtil promoLabelUtil) {
        this.promoLabelUtil = promoLabelUtil;
    }
}
