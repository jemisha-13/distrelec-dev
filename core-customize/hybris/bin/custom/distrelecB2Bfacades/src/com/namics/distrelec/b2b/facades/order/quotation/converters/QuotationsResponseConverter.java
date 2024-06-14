/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.order.quotation.converters;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Quote.UrlConstructs.CHILD_QUOTE_DETAILS_PAGE_PATH;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Quote.UrlConstructs.RESUBMIT_QUOTATION_PATH;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.distrelec.webservice.if18.v1.QuotationsResponse;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistQuotationStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationHistoryData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
public class QuotationsResponseConverter extends AbstractPopulatingConverter<QuotationsResponse, QuotationHistoryData> {

    @Autowired
    @Qualifier("currencyConverter")
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private DistrelecCodelistService distCodelistService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    @Qualifier("distSiteBaseUrlResolutionService")
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Autowired
    private Converter<DistQuotationStatusModel, QuoteStatusData> quoteStatusDataConverter;

    @Override
    protected QuotationHistoryData createTarget() {
        return new QuotationHistoryData();
    }

    @Override
    public void populate(final QuotationsResponse source, final QuotationHistoryData target) {
        super.populate(source, target);

        target.setQuotationId(source.getQuotationId());
        target.setPoNumber(source.getPOnumber());
        target.setCustomerName(source.getCustomerName());
        target.setQuotationDocURL(source.getQuotationDocURL());
        if (StringUtils.isNotBlank(source.getQuotationStatus())) {
            try {
                final DistQuotationStatusModel statusModel = getDistCodelistService().getDistQuotationStatus(source.getQuotationStatus());
                target.setStatus(quoteStatusDataConverter.convert(statusModel));
            } catch (final Exception exp) {
                // NOP
            }
        }

        if (source.getQuotationExpiryDate() != null && !BigInteger.ZERO.equals(source.getQuotationExpiryDate())) {
            target.setQuotationExpiryDate(SoapConversionHelper.convertDate(source.getQuotationExpiryDate()));
        }
        if (source.getQuotationRequestDate() != null && !BigInteger.ZERO.equals(source.getQuotationRequestDate())) {
            target.setQuotationRequestDate(SoapConversionHelper.convertDate(source.getQuotationRequestDate()));
        }
        if (source.getCurrencyCode() != null) {
            if (source.getTotal() != null) {
                target.setTotal(getPriceDataFactory().create(PriceDataType.BUY,
                                                             BigDecimal.valueOf(source.getTotal().doubleValue()), source.getCurrencyCode().value()));
            }
            final CurrencyModel currency = getCommonI18NService().getCurrency(source.getCurrencyCode().value());
            target.setCurrency(getCurrencyConverter().convert(currency));
        }

        // Set the purchasable flag
        setPurchasable(target);

        if (StringUtils.isNotEmpty(source.getYourHeaderRef())) {
            target.setYourHeaderRef(source.getYourHeaderRef());
        }

        if (source.getReqDate() != null) {
            target.setExpireDate(source.getExpireDate());
        }

        if (source.isIsTender() != null) {
            target.setIsTender(source.isIsTender());
        }

        if (StringUtils.isNotEmpty(source.getPreviousQuote())) {
            target.setPreviousQuoteID(source.getPreviousQuote());
        }

        target.setResubmitted(source.isResubmitted() != null ? source.isResubmitted() : Boolean.FALSE);
        target.setChildQuote(source.getChildQuote());

        setWebshopForChildQuote(source, target);
    }

    private void setWebshopForChildQuote(QuotationsResponse source, QuotationHistoryData target) {
        final CountryModel countryModel = getCmsSiteService().getCurrentSite().getCountry();
        final BaseSiteModel baseSite = getBaseSiteService().getBaseSiteForUID("distrelec_" + countryModel.getIsocode());

        String path = null;
        if (!isSubmittedAndHasAChildQuote(source)) {
            path = RESUBMIT_QUOTATION_PATH + source.getQuotationId();
        } else if (isSubmittedAndHasAChildQuote(source)) {
            path = CHILD_QUOTE_DETAILS_PAGE_PATH + source.getChildQuote();
        }
        target.setResubmitOrChildQuotationUrl(StringUtils.isEmpty(path) ? null : getUrl(baseSite, path));
    }

    private boolean isSubmittedAndHasAChildQuote(QuotationsResponse source) {
        return BooleanUtils.isTrue(source.isResubmitted()) && StringUtils.isNotEmpty(source.getChildQuote());
    }

    private String getUrl(final BaseSiteModel baseSite, final String path) {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(baseSite, true, path);
    }

    /**
     * Set the flag purchasable on the target quote. A quote is purchasable
     * when:
     * <ul>
     * <li>The quote is not null and it is either offered or purchased</li>
     * <li>The expiration date is either {@code null} or is after the current
     * date.</li>
     * </ul>
     *
     * @param target
     *            the target quote.
     */
    protected static void setPurchasable(final QuotationHistoryData target) {
        target.setPurchasable(
                              target.getStatus() != null //
                                      && ("02".equals(target.getStatus().getCode()) || "03".equals(target.getStatus().getCode())) //
                                      && (target.getQuotationExpiryDate() == null
                                              || (new Date().before(target.getQuotationExpiryDate()))
                                              || (DateUtils.isSameDay(target.getQuotationExpiryDate(), new Date()))));
    }

    public Converter<CurrencyModel, CurrencyData> getCurrencyConverter() {
        return currencyConverter;
    }

    public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter) {
        this.currencyConverter = currencyConverter;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistrelecCodelistService getDistCodelistService() {
        return distCodelistService;
    }

    public void setDistCodelistService(final DistrelecCodelistService distCodelistService) {
        this.distCodelistService = distCodelistService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService() {
        return siteBaseUrlResolutionService;
    }

    public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }
}
