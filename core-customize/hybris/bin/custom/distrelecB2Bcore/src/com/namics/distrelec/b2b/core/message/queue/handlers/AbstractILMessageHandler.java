/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import com.namics.distrelec.b2b.core.event.DistInternalLinkEventAsync;
import com.namics.distrelec.b2b.core.internal.link.service.DistInternalLinkService;
import de.hybris.platform.servicelayer.event.EventService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.message.queue.Constants;
import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;
import com.namics.distrelec.b2b.core.message.queue.model.CInternalLinkData;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;


/**
 * {@code AbstractILMessageHandler}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.1
 */
public abstract class AbstractILMessageHandler<T extends ItemModel> implements ILMessageHandler<T> {

    private static final Logger LOG = LogManager.getLogger(AbstractILMessageHandler.class);

    private static final int CONCURRENT_HANDLERS = 3;

    @Autowired
    private InternalLinkMessageQueueService internalLinkMessageQueueService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    @Qualifier("productModelUrlResolver")
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    @Qualifier("catalogPlusProductModelUrlResolver")
    private DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver;

    @Autowired
    @Qualifier("categoryModelUrlResolver")
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    @Qualifier("distManufacturerModelUrlResolver")
    private DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private DistProductService productService;

    @Autowired
    private DistCategoryService categoryService;

    @Autowired
    private DistManufacturerService manufacturerService;

    @Autowired
    private DistInternalLinkService distInternalLinkService;

    @Autowired
    private EventService eventService;

    private final Semaphore semaphore = new Semaphore(CONCURRENT_HANDLERS);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.handlers.ILMessageHandler#handle(com.namics.distrelec.b2b.core.message.queue.message.
     * InternalLinkMessage)
     */
    @Override
    public void handle(final InternalLinkMessage message) {
        try {
            semaphore.acquire();
            if (checkContinue(message)) {
                doHandle(message);
            }
        } catch (InterruptedException e) {
            LOG.warn("semaphore is interrupted", e);
        } finally {
            semaphore.release();
        }
    }

    /**
     * Handle the message.
     * 
     * @param message
     */
    protected abstract void doHandle(final InternalLinkMessage message);

    /**
     * Check whether it is required to process the message or just ignore it.
     * 
     * @param message
     *            the received message.
     * @return {@code true} if it is required to process the message or {@code false} to ignore it.
     */
    protected boolean checkContinue(final InternalLinkMessage message) {
        if (!checkType(message)) {
            LOG.error("Wrong target type, required type: {}, received type: {}", getRowType(), message.getType());
            return false;
        }
        return true;
    }

    /**
     * @return the #RowType of which is supported by the handler
     */
    public abstract RowType getRowType();

    /**
     * Check whether the message has the correct #RowType
     * 
     * @param message
     *            the message to check
     * @return {@code true} if the message has the correct {@link RowType}, {@code false} otherwise
     */
    public boolean checkType(final InternalLinkMessage message) {
        return getRowType().equals(message.getType());
    }

    /**
     * Resolve the Product URL for the given base site and locale
     * 
     * @param source
     *            the target product
     * @param baseSite
     *            the base site
     * @param locale
     *            the target locale
     * @return the final product URL
     */
    protected String resolveURL(final ProductModel source, final BaseSiteModel baseSite, final Locale locale) {
        return getProductUrlResolver(source).resolve(source, baseSite, locale);
    }

    /**
     * Resolve the Category URL for the given base site and locale
     * 
     * @param source
     *            the target category
     * @param baseSite
     *            the base site
     * @param locale
     *            the target locale
     * @return the final category URL
     */
    protected String resolveURL(final CategoryModel source, final BaseSiteModel baseSite, final Locale locale) {
        return getCategoryModelUrlResolver().resolve(source, baseSite, locale);
    }

    /**
     * Resolve the Manufacturer URL for the given base site and locale
     * 
     * @param source
     *            the target manufacturer
     * @param baseSite
     *            the base site
     * @param locale
     *            the target locale
     * @return the final manufacturer URL
     */
    protected String resolveURL(final DistManufacturerModel source, final BaseSiteModel baseSite, final Locale locale) {
        return getDistManufacturerModelUrlResolver().resolve(source, baseSite, locale);
    }

    /**
     * @param productModel
     *            the #ProductModel
     * @return the #ProductModel URL resolver
     */
    protected DistUrlResolver<ProductModel> getProductUrlResolver(final ProductModel productModel) {
        final DistUrlResolver<ProductModel> resolver = productModel.getCatPlusSupplierAID() == null ? getProductModelUrlResolver()
                : getCatalogPlusProductModelUrlResolver();
        return resolver;
    }

    /**
     * Check whether the given link data is valid or not according to the max age (in days).
     * 
     * @param iLinkData
     *            the link data to validate
     * @param maxAge
     *            the max age
     * @return {@code true} if the link data is valid, {@code false} otherwise.
     */
    protected boolean isValidLinkData(final CInternalLinkData iLinkData, final int maxAge) {
        return (iLinkData != null && iLinkData.getTimestamp() != null && iLinkData.getTimestamp().after(DateUtils.addDays(new Date(), -maxAge)));
    }

    /**
     * Build the complete product name as shown on the product detail page.
     * 
     * @param product
     *            the source product
     * @param locale
     *            the locale to use
     * @return the complete product name.
     */
    protected String getPName(final ProductModel product, final Locale locale) {
        if (product == null || locale == null) {
            return StringUtils.EMPTY;
        }
        final StringBuilder productNameBuilder = new StringBuilder(StringUtils.stripToEmpty(product.getName(locale)));

        if (StringUtils.isNotBlank(product.getTypeName())) {
            if (productNameBuilder.length() > 0) {
                productNameBuilder.append(", ");
            }
            productNameBuilder.append(product.getTypeName());
        }
        if (product.getManufacturer() != null && StringUtils.isNotBlank(product.getManufacturer().getName())) {
            if (productNameBuilder.length() > 0) {
                productNameBuilder.append(", ");
            }
            productNameBuilder.append(product.getManufacturer().getName());
        }

        return productNameBuilder.toString();
    }

    /**
     * Send an message for the recalculation of the related data of an ItemModel.
     * 
     * @param code
     *            the code
     * @param type
     *            the #RowType
     * @param cmsSite
     *            the #CMSSiteModel
     * @param language
     *            the #LanguageModel
     * @param force
     *            a boolean flag telling whether we should or not force the recalculation even if the data is still valid.
     */
    public void sendMessage(final String code, final RowType type, final BaseSiteModel cmsSite, final LanguageModel language, final boolean force) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending related message code: {}, site: {}, language: {}", code, cmsSite.getUid(), language.getIsocode());
        }
        getEventService().publishEvent(new DistInternalLinkEventAsync(code, type, cmsSite.getUid(), language.getIsocode(), force));
    }

    /**
     * 
     * @param code
     * @param site
     * @param type
     * @param language
     * @return the {@link CInternalLinkData} for the given {@code code, site, type and language}
     */
    public CInternalLinkData find(final String code, final String site, final RowType type, final String language) {
        return getDistInternalLinkService().findInternalLink(code, site, type.getCode(), language);
    }

    /**
     * Retrieve all related data for the specified {@code code}, {@code site} and {@link RowType}.
     * 
     * @param code
     *            the code
     * @param site
     *            the site
     * @param type
     *            the row type
     * @return all {@link CInternalLinkData}s for the given {@code code, site and type}
     */
    public List<CInternalLinkData> find(final String code, final String site, final RowType type) {
        return getDistInternalLinkService().findInternalLinks(code, site, type.getCode());
    }

    /**
     * Persist the data into the data store.
     */
    public CInternalLinkData lockOrCreate(final InternalLinkMessage message) {
        CInternalLinkData existingData;
        if (message.getLanguage() != null) {
            existingData = find(message.getCode(), message.getSite(), getRowType(), message.getLanguage());
        } else {
            List<CInternalLinkData> existingDatas = find(message.getCode(), message.getSite(), getRowType());
            if (!existingDatas.isEmpty()) {
                existingData = existingDatas.get(0);
            } else {
                existingData = null;
            }
        }

        if (existingData != null) {
            if (!message.isForce()) {
                if (isValidLinkData(existingData, getMaxAge())) {
                    // The data is up to date, thus, no action required
                    return null;
                }
            }

            boolean isLocked = getDistInternalLinkService().lockInternalLink(existingData);
            return isLocked ? existingData : null;
        } else {
            CInternalLinkData iLinkData = new CInternalLinkData(message.getCode(), message.getSite(), getRowType(),
                    message.getLanguage(), new ArrayList<>());
            boolean isLocked = getDistInternalLinkService().createInternalLink(iLinkData);
            return isLocked ? iLinkData : null;
        }
    }

    public void update(CInternalLinkData iLinkData) {
        getDistInternalLinkService().updateInternalLink(iLinkData);
    }

    /**
     * @return the data validity age
     */
    protected int getMaxAge() {
        return getConfigurationService().getConfiguration().getInt(Constants.IL_MAX_AGE_DAYS, 30);
    }

    /**
     * @return the maximum number of links to store.
     */
    protected int getMaxLinks() {
        return getConfigurationService().getConfiguration().getInt(Constants.IL_MAX_LINKS_KEY, 5);
    }

    /**
     * @return the maximum number of categories
     */
    protected int getMaxCategories() {
        return getConfigurationService().getConfiguration().getInt(Constants.IL_MAX_CATEGORY_KEY, 10);
    }

    // Getters & Setters

    public DistUrlResolver<CategoryModel> getCategoryUrlResolver() {
        return getCategoryModelUrlResolver();
    }

    public InternalLinkMessageQueueService getInternalLinkMessageQueueService() {
        return internalLinkMessageQueueService;
    }

    public void setInternalLinkMessageQueueService(final InternalLinkMessageQueueService internalLinkMessageQueueService) {
        this.internalLinkMessageQueueService = internalLinkMessageQueueService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistUrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(final DistUrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public DistUrlResolver<ProductModel> getCatalogPlusProductModelUrlResolver() {
        return catalogPlusProductModelUrlResolver;
    }

    public void setCatalogPlusProductModelUrlResolver(final DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver) {
        this.catalogPlusProductModelUrlResolver = catalogPlusProductModelUrlResolver;
    }

    public DistUrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(final DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    public DistUrlResolver<DistManufacturerModel> getDistManufacturerModelUrlResolver() {
        return distManufacturerModelUrlResolver;
    }

    public void setDistManufacturerModelUrlResolver(final DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver) {
        this.distManufacturerModelUrlResolver = distManufacturerModelUrlResolver;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public DistProductService getProductService() {
        return productService;
    }

    public void setProductService(final DistProductService productService) {
        this.productService = productService;
    }

    public DistCategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(final DistCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public DistManufacturerService getManufacturerService() {
        return manufacturerService;
    }

    public void setManufacturerService(final DistManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    public DistInternalLinkService getDistInternalLinkService() {
        return distInternalLinkService;
    }

    public void setDistInternalLinkService(final DistInternalLinkService distInternalLinkService) {
        this.distInternalLinkService = distInternalLinkService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }
}
