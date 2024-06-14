package com.namics.distrelec.b2b.facades.process.email.context;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.model.process.ObsolescenceNotificationEmailModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.data.PIMAlternateResult;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.session.SessionService;

public class ObsolescencePreferenceEmailContext extends AbstractDistEmailContext {

    private static final Logger LOG = LogManager.getLogger(ObsolescencePreferenceEmailContext.class);

    private ObsolescenceNotificationEmailModel obsolNotifyModel;

    private ConfigurationService configurationService;

    private CommonI18NService commonI18NService;

    private String host;

    private final static String LOGO_MEDIA_CODE = "/images/theme/distrelec_logo.png";

    private static final int OFFSET = 0;

    private static final int SIZE = 8;

    private final static String INFO_ICON = "infoIcon";

    @Autowired
    private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

    @Autowired
    private Converter<ProductModel, ProductData> b2bProductConverter;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Resource(name = "erp.orderHistoryFacade")
    private OrderHistoryFacade orderHistoryFacade;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private DistProductService distProductService;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        final List<ProductReferenceTypeEnum> referenceTypes = Arrays.asList(
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_BETTERVALUE,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_DE,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_SIMILAR,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_UPGRADE);
        if (businessProcessModel instanceof ObsolescenceNotificationEmailModel) {
            obsolNotifyModel = (ObsolescenceNotificationEmailModel) businessProcessModel;
        }
        final BaseSiteModel baseSite = getSite(obsolNotifyModel);

        final SiteBaseUrlResolutionService siteBaseUrlResolutionService = getSiteBaseUrlResolutionService();

        put(BASE_URL, siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, false, ""));
        put(SECURE_BASE_URL, siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, ""));
        put(MEDIA_BASE_URL, siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, false));
        put(MEDIA_SECURE_BASE_URL, siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, true));

        put("site", baseSite);
        put("country",
            obsolNotifyModel.getOrderEntries().get(0).getOrder().getPaymentAddress().getCountry().getIsocode());
        put(THEME, null);
        put(FROM_EMAIL, emailPageModel.getFromEmail());
        put(DISPLAY_NAME, obsolNotifyModel.getNotifiedCustomer().getDisplayName());
        put(FROM_DISPLAY_NAME, emailPageModel.getFromName());
        put(EMAIL, obsolNotifyModel.getNotifiedCustomer().getUid());
        put(EMAIL_LANGUAGE, getEmailLanguage(obsolNotifyModel));
        put("urlLang", getEmailLanguage(obsolNotifyModel).getIsocode());
        put("customerName", obsolNotifyModel.getNotifiedCustomer().getName());

        final List<String> productCodes = new ArrayList<>();

        for (final AbstractOrderEntryModel orderEntry : obsolNotifyModel.getOrderEntries()) {
            productCodes.add(orderEntry.getProduct().getCode());
            final PIMAlternateResult pimAlternateResult = getDistProductService()
                                                                                 .getProductsReferencesForAlternative(Arrays.asList(orderEntry.getProduct()),
                                                                                                                      referenceTypes, OFFSET, SIZE);
            final ProductModel alternative = pimAlternateResult.getAlternativeProducts() != null
                    && pimAlternateResult.getAlternativeProducts().size() > 0 ? pimAlternateResult.getAlternativeProducts().get(0) : null;
            if (alternative != null) {
                productCodes.add(alternative.getCode());
            }
        }
        final List<ProductAvailabilityData> availabilityData = getProductFacade().getAvailability(productCodes);

        final Map<String, ProductData> alternatives = new HashMap<>();
        for (final AbstractOrderEntryModel orderEntry : obsolNotifyModel.getOrderEntries()) {
            final PIMAlternateResult pimAlternateResult = getDistProductService()
                                                                                 .getProductsReferencesForAlternative(Arrays.asList(orderEntry.getProduct()),
                                                                                                                      referenceTypes, OFFSET, SIZE);
            final ProductModel alternative = pimAlternateResult.getAlternativeProducts() != null
                    && pimAlternateResult.getAlternativeProducts().size() > 0 ? pimAlternateResult.getAlternativeProducts().get(0) : null;
            if (alternative != null) {
                getB2bUnitService().updateBranchInSession(getSessionService().getCurrentSession(),
                                                          obsolNotifyModel.getNotifiedCustomer());
                final ProductData productData = getb2bProductConverter().convert(alternative);
                alternatives.put(orderEntry.getProduct().getCode(), productData);
            }
        }

        final List<OrderEntryData> orderEntries = getOrderEntryConverter()
                                                                          .convertAll(obsolNotifyModel.getOrderEntries());

        final Map<String, OrderEntryData> map = new HashMap<>();
        for (final OrderEntryData entry : orderEntries) {
            final String key = entry.getProduct().getCode();
            if (!map.containsKey(key)) {
                map.put(key, entry);
            }
        }
        final Collection<OrderEntryData> uniqueEntries = map.values();

        for (final ProductAvailabilityData availableProduct : availabilityData) {
            for (final OrderEntryData orderEntry : uniqueEntries) {
                if (availableProduct.getProductCode().equals(orderEntry.getProduct().getCode())) {
                    getB2bUnitService().updateBranchInSession(getSessionService().getCurrentSession(),
                                                              obsolNotifyModel.getNotifiedCustomer());
                    final ProductData latestProductData = getb2bProductConverter()
                                                                                  .convert(getDistProductService().getProductForCode(orderEntry.getProduct()
                                                                                                                                               .getCode()));
                    orderEntry.setProduct(latestProductData);
                    orderEntry.getProduct().getStock().setStockLevel(availableProduct.getStockLevelTotal().longValue());
                }
            }

            for (final Map.Entry<String, ProductData> entry : alternatives.entrySet()) {
                if (availableProduct.getProductCode().equals(entry.getValue().getCode())) {
                    entry.getValue().getStock().setStockLevel(availableProduct.getStockLevelTotal().longValue());
                }

            }

        }
        put("alternatives", alternatives);
        put("orderEntries", uniqueEntries);
        put("StringEscapeUtils", StringEscapeUtils.class);
        put("uid", obsolNotifyModel.getNotifiedCustomer().getUid());
        setHost(configurationService.getConfiguration().getString("website." + baseSite.getUid() + ".https"));
        put("secureUrl", siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, ""));

        // get logo image URL from media model
        final CMSSiteModel site = (CMSSiteModel) getProcessContextResolutionStrategy().getCmsSite(obsolNotifyModel);
        final MediaModel logoMediaModel = getSiteMedia(site, LOGO_MEDIA_CODE);
        final MediaModel infoIconMediaModel = getSiteMedia(site, INFO_ICON);

        put("logoURL", logoMediaModel.getUrl());
        put("infoIconURL", infoIconMediaModel.getUrl());
    }

    private MediaModel getSiteMedia(final CMSSiteModel siteModel, final String mediaCode) {
        List<ContentCatalogModel> contentCatalogs = siteModel.getContentCatalogs();
        ListIterator<ContentCatalogModel> iterator = contentCatalogs.listIterator(contentCatalogs.size());
        while (iterator.hasPrevious()) {
            ContentCatalogModel contentCatalog = iterator.previous();
            CatalogVersionModel catalogVersion = contentCatalog.getActiveCatalogVersion();
            try {
                return getMediaService().getMedia(catalogVersion, mediaCode);
            } catch (UnknownIdentifierException e) {
                // media is not find, loop
            }
        }

        // media is not found, throw exception
        String errMsg = String.format("Media does not exist with code %s and site %s", mediaCode, siteModel.getUid());
        throw new IllegalArgumentException(errMsg);
    }

    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof ObsolescenceNotificationEmailModel) {
            return ((ObsolescenceNotificationEmailModel) businessProcessModel).getOrderEntries().get(0).getOrder()
                                                                              .getSite();
        }
        return null;
    }

    @Override
    protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof ObsolescenceNotificationEmailModel) {
            return obsolNotifyModel.getNotifiedCustomer().getSessionLanguage();
        }
        return null;
    }

    @Override
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Override
    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        return obsolNotifyModel.getNotifiedCustomer();
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter() {
        return this.orderEntryConverter;
    }

    public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> converter) {
        this.orderEntryConverter = converter;
    }

    public Converter<ProductModel, ProductData> getb2bProductConverter() {
        return b2bProductConverter;
    }

    public void setb2bProductConverter(final Converter<ProductModel, ProductData> b2bProductConverter) {
        this.b2bProductConverter = b2bProductConverter;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public ProcessContextResolutionStrategy getProcessContextResolutionStrategy() {
        return (ProcessContextResolutionStrategy) Registry.getApplicationContext()
                                                          .getBean("processContextResolutionStrategy");
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }

}
