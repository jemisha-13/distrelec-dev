/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.order.quotation.converters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.distrelec.webservice.if18.v1.QuotationReadEntryResponse;
import com.distrelec.webservice.if18.v1.ReadQuotationsResponse;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistQuotationStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationEntry;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationHistoryData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import com.namics.distrelec.b2b.facades.order.quotation.utils.QuotationUtil;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;

public class QuotationsPopulator extends AbstractPopulatingConverter<ReadQuotationsResponse, QuotationData> {

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    @Qualifier("currencyConverter")
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    @Qualifier("productConverter")
    private Converter<ProductModel, ProductData> productConverter;

    @Autowired
    @Qualifier("productConfiguredPopulator")
    private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

    @Autowired
    private ProductService productService;

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
    protected QuotationData createTarget() {
        return new QuotationData();
    }

    @Override
    public void populate(final ReadQuotationsResponse source, final QuotationData target) throws ConversionException {
        super.populate(source, target);
        target.setCustomerId(source.getCustomerId());
        target.setQuotationId(source.getQuotationId());
        target.setCustomerName(source.getContactName());
        target.setPoNumber(source.getPOnumber());
        target.setQuotationChannel(source.getQuotationChannel());
        target.setFullQuote(BooleanUtils.isTrue(source.isFullQuote()));
        target.setQuotationNote(source.getQuotationNote());
        target.setQuotationDocURL(source.getQuotationDocURL());
        target.setResubmitted(source.isResubmitted() != null ? source.isResubmitted() : Boolean.FALSE);
        if (StringUtils.isNotBlank(source.getQuotationStatus())) {
            try {
                final DistQuotationStatusModel statusModel = getDistCodelistService().getDistQuotationStatus(source.getQuotationStatus());
                target.setStatus(quoteStatusDataConverter.convert(statusModel));
            } catch (final Exception exp) {
                // NOP
            }
        }
        target.setChildQuote(source.getChildQuote());
        if (source.getCurrencyCode() != null) {
            final CurrencyModel currency = getCommonI18NService().getCurrency(source.getCurrencyCode().value());
            target.setCurrency(getCurrencyConverter().convert(currency));
        }
        if (source.getQuotationExpiryDate() != null && !BigInteger.ZERO.equals(source.getQuotationExpiryDate())) {
            target.setQuotationExpiryDate(SoapConversionHelper.convertDate(source.getQuotationExpiryDate()));
        }
        if (source.getQuotationRequestDate() != null && !BigInteger.ZERO.equals(source.getQuotationRequestDate())) {
            target.setQuotationRequestDate(SoapConversionHelper.convertDate(source.getQuotationRequestDate()));
        }

        if (source.getCurrencyCode() != null) {
            if (source.getTotal() != null) {
                target.setTotal(createPriceData(source.getTotal().doubleValue(), source.getCurrencyCode().value()));
            }
            if (source.getSubtotal1() != null) {
                target.setSubtotal1(
                                    createPriceData(source.getSubtotal1().doubleValue(), source.getCurrencyCode().value()));
            }
            if (source.getSubtotal2() != null) {
                target.setSubtotal2(
                                    createPriceData(source.getSubtotal2().doubleValue(), source.getCurrencyCode().value()));
            }
            if (source.getTax() != null) {
                target.setTax(createPriceData(source.getTax().doubleValue(), source.getCurrencyCode().value()));
            }
        }

        // Set the purchasable flag
        QuotationsResponseConverter.setPurchasable(target);

        // Adding the entries
        addEntries(source, target);

        setWebshopForChildQuote(source, target);
    }

    private void setWebshopForChildQuote(ReadQuotationsResponse source, QuotationHistoryData target) {
        final CountryModel countryModel = getCmsSiteService().getCurrentSite().getCountry();
        final BaseSiteModel baseSite = getBaseSiteService().getBaseSiteForUID("distrelec_" + countryModel.getIsocode());

        String path = null;
        if (!isSubmittedAndHasAChildQuote(source)) {
            path = DistConstants.Quote.UrlConstructs.RESUBMIT_QUOTATION_PATH + source.getQuotationId();
        } else if (isSubmittedAndHasAChildQuote(source)) {
            path = DistConstants.Quote.UrlConstructs.CHILD_QUOTE_DETAILS_PAGE_PATH + source.getChildQuote();
        }

        target.setResubmitOrChildQuotationUrl(StringUtils.isEmpty(path) ? null : getUrl(baseSite, path));
    }

    private boolean isSubmittedAndHasAChildQuote(ReadQuotationsResponse source) {
        return BooleanUtils.isTrue(source.isResubmitted()) && StringUtils.isNotEmpty(source.getChildQuote());
    }

    private String getUrl(final BaseSiteModel baseSite, final String path) {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(baseSite, true, path);
    }

    /**
     * Converting the quotation entries
     *
     * @param source
     * @param target
     */
    private void addEntries(final ReadQuotationsResponse source, final QuotationData target) {
        target.setQuotationEntries(new ArrayList<>());
        if (CollectionUtils.isEmpty(source.getQuotationEntries())) {
            return;
        }

        BigDecimal sum = BigDecimal.ZERO;

        // Create a list of quotation entries
        for (final QuotationReadEntryResponse readEntryResponse : source.getQuotationEntries()) {
            final QuotationEntry quotationEntry = new QuotationEntry();
            quotationEntry.setItemNumber(readEntryResponse.getItemNumber());
            quotationEntry.setDummyItem(BooleanUtils.isTrue(readEntryResponse.isDummyItem()));
            quotationEntry.setArticleDescription(readEntryResponse.getArticleDescription());
            // Phase II
            quotationEntry.setMandatory(BooleanUtils.isTrue(readEntryResponse.isMandatoryItem()));
            quotationEntry.setQuantityModificationType(QuotationUtil.getFromCode(readEntryResponse.getQuantityModification()));
            quotationEntry.setItemNote(readEntryResponse.getItemNote());
            quotationEntry.setManufacturerPartNumber(readEntryResponse.getManufacturerPartNumber());
            quotationEntry.setManufacturerType(readEntryResponse.getManufacturerType());
            // // Phase III
            quotationEntry.setCustomerReference(readEntryResponse.getCustomerReferenceItemLevel());

            if (StringUtils.isNotEmpty(readEntryResponse.getYourItemRef())) {
                quotationEntry.setYourItemRef(readEntryResponse.getYourItemRef());
            }

            // For non dummy items
            if (!quotationEntry.isDummyItem() && StringUtils.isNotBlank(readEntryResponse.getArticleNumber())) {
                try {
                    final ProductModel productSource = getProductService()
                                                                          .getProductForCode(readEntryResponse.getArticleNumber());
                    final ProductData data = getProductConverter().convert(productSource);
                    productConfiguredPopulator.populate(productSource, data,
                                                        Arrays.asList(ProductOption.DIST_MANUFACTURER));
                    quotationEntry.setProduct(data);
                } catch (final UnknownIdentifierException uie) {
                    // NOP
                }
            }
            // For dummy items
            if (quotationEntry.isDummyItem()) {
                final ProductData data = new ProductData();
                data.setCode(readEntryResponse.getArticleNumber());
                data.setDescription(readEntryResponse.getArticleDescription());
                quotationEntry.setProduct(data);
            }

            if (readEntryResponse.getQuantity() != null) {
                quotationEntry.setQuantity(Integer.valueOf(readEntryResponse.getQuantity().intValue()));
            }
            if (source.getCurrencyCode() != null) {
                if (readEntryResponse.getPrice() != null) {
                    quotationEntry.setPrice(createPriceData(readEntryResponse.getPrice().doubleValue(),
                                                            source.getCurrencyCode().value()));
                }
                if (readEntryResponse.getSubtotal1() != null) {
                    quotationEntry.setSubtotal(createPriceData(readEntryResponse.getSubtotal1().doubleValue(),
                                                               source.getCurrencyCode().value()));
                } else if (quotationEntry.getPrice() != null && readEntryResponse.getQuantity() != null) {
                    quotationEntry.setSubtotal(
                                               calculatePrice(quotationEntry.getPrice(), readEntryResponse.getQuantity().intValue()));
                }

                if (target.getSubtotal2() == null && quotationEntry.getSubtotal() != null) {
                    sum = sum.add(quotationEntry.getSubtotal().getValue());
                }
            }

            target.getQuotationEntries().add(quotationEntry);
        }

        if (target.getSubtotal2() == null && source.getCurrencyCode() != null) {
            target.setSubtotal2(createPriceData(sum, source.getCurrencyCode().value()));
        }
    }

    /**
     * Calculate the price from an original price multiplied by the specified
     * quantity.
     *
     * @param origin
     *            the original price
     * @param quantity
     *            the quantity.
     * @return a new instance of {@link PriceData}
     * @see #createPriceData(BigDecimal, String)
     */
    private PriceData calculatePrice(final PriceData origin, final int quantity) {
        return createPriceData(origin.getValue().multiply(BigDecimal.valueOf(quantity)), origin.getCurrencyIso());
    }

    /**
     * Creates a {@code PriceData} from the specified type, value and currency
     *
     * @param priceType
     *            the price type
     * @param value
     *            the price value
     * @param currencyIso
     *            the currency ISO code.
     * @return a new instance of {@code PriceData}
     */
    private PriceData createPriceData(final PriceDataType priceType, final double value, final String currencyIso) {
        return getPriceDataFactory().create(priceType, BigDecimal.valueOf(value), currencyIso);
    }

    /**
     * Creates a {@code PriceData} from the specified value and currency with a
     * default type {@code PriceData.PriceType.BUY}
     *
     * @param value
     *            the price value
     * @param currencyIso
     *            the currency ISO code.
     * @return a new instance of {@code PriceData}
     * @see #createPriceData(PriceDataType, double, String)
     */
    private PriceData createPriceData(final double value, final String currencyIso) {
        return createPriceData(PriceDataType.BUY, value, currencyIso);
    }

    private PriceData createPriceData(final BigDecimal value, final String currencyIso) {
        return getPriceDataFactory().create(PriceDataType.BUY, value, currencyIso);
    }

    // Getters & Setters

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
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

    public Converter<ProductModel, ProductData> getProductConverter() {
        return productConverter;
    }

    public void setProductConverter(final Converter<ProductModel, ProductData> productConverter) {
        this.productConverter = productConverter;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public DistrelecCodelistService getDistCodelistService() {
        return distCodelistService;
    }

    public void setDistCodelistService(final DistrelecCodelistService distCodelistService) {
        this.distCodelistService = distCodelistService;
    }

    public ConfigurablePopulator<ProductModel, ProductData, ProductOption> getProductConfiguredPopulator() {
        return productConfiguredPopulator;
    }

    public void setProductConfiguredPopulator(
                                              final ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator) {
        this.productConfiguredPopulator = productConfiguredPopulator;
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
