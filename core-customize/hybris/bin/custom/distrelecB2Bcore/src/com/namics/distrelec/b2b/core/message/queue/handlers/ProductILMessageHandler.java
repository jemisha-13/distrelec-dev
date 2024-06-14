/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.handlers;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;
import com.namics.distrelec.b2b.core.message.queue.model.CIValue;
import com.namics.distrelec.b2b.core.message.queue.model.CInternalLinkData;
import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.message.queue.model.RelatedDataType;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code ProductILMessageHandler}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.1
 */
public class ProductILMessageHandler extends AbstractILMessageHandler<ProductModel> {

    private static final Logger LOG = LogManager.getLogger(ProductILMessageHandler.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.handlers.AbstractILMessageHandler#doHandle(com.namics.distrelec.b2b.core.message.queue.
     * message.InternalLinkMessage)
     */
    @Override
    protected void doHandle(final InternalLinkMessage message) {
        try {
            final ProductModel product = getProductService().getProductForCode(message.getCode());
            if (product == null) {
                LOG.warn("No product found with code {}", message.getCode());
                return;
            }
            final CategoryModel category = product.getPrimarySuperCategory() != null ? product.getPrimarySuperCategory()
                    : (CollectionUtils.isNotEmpty(product.getSupercategories()) ? product.getSupercategories().iterator().next() : null);
            if (category == null) {
                LOG.warn("No Primary Super Category defined for product {}", product.getCode());
                return;
            }
            final DistManufacturerModel manufacturer = product.getManufacturer();
            if (manufacturer == null) {
                LOG.warn("No Manufacturer defined for product {}", product.getCode());
                return;
            }

            final BaseSiteModel site = getBaseSiteService().getBaseSiteForUID(message.getSite());
            final Set<LanguageModel> supportedLanguages = site.getStores().get(0).getLanguages();
            if (supportedLanguages == null || supportedLanguages.isEmpty()) {
                LOG.warn("No language defined for site {}", message.getSite());
                return;
            }

            final Locale loc = getCommonI18NService().getLocaleForLanguage(supportedLanguages.iterator().next());
            final String ilCode = new StringBuilder(manufacturer.getCode()).append("_").append(category.getCode()).toString();
            final CInternalLinkData existingData = find(ilCode, message.getSite(), RowType.PRODUCT, loc.getLanguage());
            if (isValidLinkData(existingData, getMaxAge())) {
                // The data is up to date, thus, no action required
                return;
            }

            CInternalLinkData iLinkData = lockOrCreate(message);

            if (iLinkData == null) {
                return;
            }

            final int maxLinks = getMaxLinks(); // Default value 5
            final int maxCategories = getMaxCategories();

            // 1) Process the related products
            final List<ProductModel> relatedProducts = getInternalLinkMessageQueueService().fetchRelatedProductsForProduct(product.getCode(), site.getUid());
            // 2) Process the related categories
            final List<CategoryModel> categories = getInternalLinkMessageQueueService().fetchRelatedCategoriesForProduct(product.getCode(), site.getUid()) //
                    .stream().filter(c -> c.getClass() == CategoryModel.class).collect(toList()); // Exclude sub-types
            // 3) Process the related manufacturers
            final List<DistManufacturerModel> manufacturers = getInternalLinkMessageQueueService().fetchRelatedManufacturersForProduct(product.getCode(),
                    site.getUid(), categories != null ? categories.stream().limit(maxCategories).collect(toList()) : null);

            supportedLanguages.forEach(language -> {
                final Locale locale = getCommonI18NService().getLocaleForLanguage(language);
                iLinkData.setLanguage(locale.getLanguage());
                iLinkData.setDatas(new ArrayList<CRelatedData>());
                // 1) Populate related products
                final AtomicInteger index = new AtomicInteger(0);
                if (relatedProducts != null && !relatedProducts.isEmpty()) {
                    iLinkData.getDatas().add(new CRelatedData(RelatedDataType.RELATED_PRODUCT, //
                            relatedProducts.stream().limit(maxLinks + 1) //
                                    .map(p -> new CIValue(p.getCode(), getPName(p, locale), resolveURL(p, site, locale), index.getAndIncrement())) //
                                    .collect(toList())));
                }
                // 2) Populate related categories
                if (categories != null && !categories.isEmpty()) {
                    final Collection<CategoryModel> allSuperCategories = category.getAllSupercategories();
                    iLinkData.getDatas().add(new CRelatedData(RelatedDataType.RELATED_CATEGORY, //
                            categories.stream() //
                                    .filter(c -> !allSuperCategories.contains(c)) //
                                    .limit(maxLinks) // Limit to the first 5 elements
                                    .map(c -> new CIValue(c.getCode(), c.getName(locale), resolveURL(c, site, locale))) //
                                    .collect(toList())));
                }
                // 3) Populate related manufacturers
                if (manufacturers != null && !manufacturers.isEmpty()) {
                    iLinkData.getDatas().add(new CRelatedData(RelatedDataType.RELATED_MANUFACTURER, //
                            manufacturers.stream() //
                                    .limit(maxLinks) //
                                    .map(man -> new CIValue(man.getCode(), man.getName(), resolveURL(man, site, locale))) //
                                    .collect(toList())));
                }
                // 4) store the calculated data
                update(iLinkData);
            });

            getDistInternalLinkService().unlockInternalLink(iLinkData);
        } catch (final Exception exp) {
            LOG.error("Something went wrong: ", exp);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.handlers.AbstractILMessageHandler#getRowType()
     */
    @Override
    public RowType getRowType() {
        return RowType.PRODUCT;
    }
}
