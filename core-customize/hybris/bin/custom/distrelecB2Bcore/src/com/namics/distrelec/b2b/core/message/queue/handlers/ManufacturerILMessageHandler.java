/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.handlers;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.message.queue.Constants;
import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;
import com.namics.distrelec.b2b.core.message.queue.model.CIValue;
import com.namics.distrelec.b2b.core.message.queue.model.CInternalLinkData;
import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.message.queue.model.RelatedDataType;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code ManufacturerILMessageHandler}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.1
 */
public class ManufacturerILMessageHandler extends AbstractILMessageHandler<DistManufacturerModel> {

    private static final Logger LOG = LogManager.getLogger(ManufacturerILMessageHandler.class);


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.handlers.AbstractILMessageHandler#doHandle(com.namics.distrelec.b2b.core.message.queue.
     * message.InternalLinkMessage)
     */
    @Override
    public void doHandle(final InternalLinkMessage message) {
        try {
            final DistManufacturerModel manufacturerModel = getManufacturerService().getManufacturerByCode(message.getCode());
            if (manufacturerModel == null) {
                LOG.warn("No manufacturer found with code {}", message.getCode());
                return;
            }
            final CMSSiteModel siteModel = (CMSSiteModel) getBaseSiteService().getBaseSiteForUID(message.getSite());
            final Set<LanguageModel> supportedLanguages = siteModel.getStores().get(0).getLanguages();
            if (supportedLanguages == null || supportedLanguages.isEmpty()) {
                LOG.warn("No language defined for site {}", message.getSite());
                return;
            }

            final CInternalLinkData iLinkData = lockOrCreate(message);

            if (iLinkData == null) {
                return;
            }

            final List<CategoryModel> categories = getInternalLinkMessageQueueService().fetchRelatedCategoriesForManufacturer(manufacturerModel, siteModel);
            if (categories == null || categories.isEmpty()) {
                LOG.warn("No related categories found for the manufacturer {}", message.getCode());
            }

            final List<DistManufacturerModel> manufacturers = getInternalLinkMessageQueueService().fetchRelatedManufacturers(manufacturerModel, siteModel,
                    categories);
            if (CollectionUtils.isEmpty(manufacturers)) {
                LOG.warn("No related manufacturers found for the manufacturer {}", message.getCode());
            }

            final List<ProductModel> newArrivals = getInternalLinkMessageQueueService().fetchNewArrivalsOfManufacturer(manufacturerModel, siteModel);
            if (CollectionUtils.isEmpty(newArrivals)) {
                LOG.warn("No new Arrivals found for the manufacturer {}", message.getCode());
            }

            final List<ProductModel> topSellers = getInternalLinkMessageQueueService().fetchTopSellersOfManufacturer(manufacturerModel, siteModel);
            if (CollectionUtils.isEmpty(topSellers)) {
                LOG.warn("No Top Sellers found for the manufacturer {}", message.getCode());
            }


            final int maxLinks = getConfigurationService().getConfiguration().getInt(Constants.IL_MAX_LINKS_KEY, 5); // Default value 5

            supportedLanguages.forEach(language -> {
                final Locale locale = getCommonI18NService().getLocaleForLanguage(language);
                iLinkData.setLanguage(locale.getLanguage());
                iLinkData.setDatas(new ArrayList<CRelatedData>());

                // 1) Process the related categories
                final CRelatedData relatedData = new CRelatedData(RelatedDataType.RELATED_CATEGORY);
                relatedData.setValues(categories.stream() //
                        .limit(maxLinks) // Limit to the first 5 elements
                        .map(c -> new CIValue(c.getCode(), c.getName(locale), resolveURL(c, siteModel, locale))) //
                        .collect(toList()));
                iLinkData.getDatas().add(relatedData);

                // 2) Process the related manufacturers
                final CRelatedData relatedManufacturers = new CRelatedData(RelatedDataType.RELATED_MANUFACTURER);
                relatedManufacturers.setValues(manufacturers.stream() //
                        .limit(maxLinks) // Limit to the first 5 elements
                        // TODO Refactor the manufacturerModel to CIValue to a common method, it is used elsewhere
                        .map(man -> new CIValue(man.getCode(), man.getName(), resolveURL(man, siteModel, locale))) //
                        .collect(toList()));
                iLinkData.getDatas().add(relatedManufacturers);

                // 3) Process the "New Arrivals" products
                final CRelatedData newArrivalsProducts = new CRelatedData(RelatedDataType.NEW_ARRIVAL_PRODUCT);
                newArrivalsProducts.setValues(newArrivals.stream() //
                        .limit(maxLinks) // Limit to the first 5 elements
                        .map(p -> new CIValue(p.getCode(), getPName(p, locale), resolveURL(p, siteModel, locale))) //
                        .collect(toList()));
                iLinkData.getDatas().add(newArrivalsProducts);

                // 4) Process the "Top Sellers" products
                final CRelatedData topSellerProducts = new CRelatedData(RelatedDataType.TOP_SELLER_PRODUCT);
                topSellerProducts.setValues(topSellers.stream() //
                        .limit(maxLinks) // Limit to the first 5 elements
                        .map(p -> new CIValue(p.getCode(), getPName(p, locale), resolveURL(p, siteModel, locale))) //
                        .collect(toList()));
                iLinkData.getDatas().add(topSellerProducts);

                // LAST) store the calculated data
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
        return RowType.MANUFACTURER;
    }
}
