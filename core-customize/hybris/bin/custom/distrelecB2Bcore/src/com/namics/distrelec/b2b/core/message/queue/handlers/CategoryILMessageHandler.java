/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.handlers;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.message.queue.dao.RelatedItemData;
import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;
import com.namics.distrelec.b2b.core.message.queue.model.CIValue;
import com.namics.distrelec.b2b.core.message.queue.model.CInternalLinkData;
import com.namics.distrelec.b2b.core.message.queue.model.CRelatedData;
import com.namics.distrelec.b2b.core.message.queue.model.RelatedDataType;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import com.namics.distrelec.b2b.core.message.queue.strategies.ProcessCategoryDirectlyOrRecursivelyStrategy;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code CategoryILMessageHandler}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.1
 */
public class CategoryILMessageHandler extends AbstractILMessageHandler<CategoryModel> {

    private static final Logger LOG = LogManager.getLogger(CategoryILMessageHandler.class);

    @Autowired
    private ProcessCategoryDirectlyOrRecursivelyStrategy processCategoryDirectlyOrRecursivelyStrategy;

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
            final CategoryModel categoryModel = getCategoryService().getCategoryForCode(message.getCode());
            if (categoryModel == null) {
                LOG.warn("No category found with code {}", message.getCode());
                return;
            }

            final BaseSiteModel baseSiteModel = getBaseSiteService().getBaseSiteForUID(message.getSite());
            final Set<LanguageModel> supportedLanguages = baseSiteModel.getStores().get(0).getLanguages();
            if (supportedLanguages == null || supportedLanguages.isEmpty()) {
                LOG.warn("No language defined for site {}", message.getSite());
                return;
            }

            CInternalLinkData iLinkData = lockOrCreate(message);

            if (iLinkData == null) {
                return;
            }

            // 2- Check category level
            if (getProcessCategoryDirectlyOrRecursivelyStrategy().shouldProcessDirectly(categoryModel)) {
                // 2.1 Calculation for the PL categories
                processDirectly(categoryModel, baseSiteModel, iLinkData);
            } else {
                // 2.2 Calculation for the non PL categories
                processRecursively(categoryModel, baseSiteModel, iLinkData);
            }

            getDistInternalLinkService().unlockInternalLink(iLinkData);
        } catch (final Exception exp) {
            LOG.error("Something went wrong: ", exp);
        }
    }

    /**
     * Calculate related data recursively on the subCategories
     * 
     * @param categoryModel
     * @param baseSiteModel
     */
    protected void processRecursively(final CategoryModel categoryModel, final BaseSiteModel baseSiteModel, CInternalLinkData referenceCILinkData) {
        if (CollectionUtils.isEmpty(categoryModel.getCategories())) {
            return;
        }

        final Set<LanguageModel> supportedLanguages = baseSiteModel.getStores().get(0).getLanguages();
        if (supportedLanguages == null || supportedLanguages.isEmpty()) {
            LOG.warn("No language defined for site {}", baseSiteModel.getUid());
            return;
        }

        final LanguageModel lang = supportedLanguages.iterator().next();

        // 2.1 Calculation for the non PL categories
        final List<CInternalLinkData> linkDatas = getLinkDataForCategory(categoryModel, baseSiteModel, lang);
        if (linkDatas == null || linkDatas.isEmpty()) {
            return;
        }

        // Group by Language
        final Map<String, List<CInternalLinkData>> langLinkDatas = linkDatas.stream().collect(groupingBy(CInternalLinkData::getLanguage));

        final int MAX_LINKS = getMaxLinks();

        // Pick one from the map for the calculation and group by RelatedDataType
        final Map<RelatedDataType, List<CRelatedData>> relatedDataMap = langLinkDatas.remove(lang.getIsocode()).stream()
                .filter(linkData -> null != linkData.getDatas()).map(CInternalLinkData::getDatas) //
                .flatMap(List::stream) //
                .collect(groupingBy(CRelatedData::getType));

        // For each related data type do the calculation
        relatedDataMap.forEach((relatedDataType, cRelatedDatas) -> {
            // Group by Item Code
            final Map<String, List<CIValue>> ciValuesByUidMap = cRelatedDatas.stream() //
                    .map(CRelatedData::getValues) //
                    .flatMap(List::stream) //
                    .peek(civ -> { // If the UID is missing, then we set it to be used as Key
                        if (StringUtils.isBlank(civ.getUid())) {
                            civ.setUid(civ.getUrl().substring(civ.getUrl().lastIndexOf('/') + 1));
                        }
                    }).collect(groupingBy(CIValue::getUid));

            // Find the best entries having the best COUNT
            final List<Map.Entry<String, Long>> bestEntries = ciValuesByUidMap.entrySet().stream()
                    .filter(entry -> entry != null && entry.getValue() != null && !entry.getValue().isEmpty()) //
                    .collect(toMap(e -> e.getKey(), e -> e.getValue().stream() // each value (list of CIValue) mapped to long as
                                                                               // sum of counts
                            .filter(c -> c.getCount() != null) //
                            .mapToLong(CIValue::getCount).sum())) //
                    .entrySet().stream().sorted(Map.Entry.<String, Long> comparingByValue().reversed()) //
                    .limit(MAX_LINKS) //
                    .collect(toList());

            final AtomicInteger order = new AtomicInteger(0);
            final List<CIValue> ciValues = bestEntries.stream() //
                    .filter(entry -> entry != null && entry.getValue() != null) //
                    .map(entry -> {
                        final CIValue source = ciValuesByUidMap.get(entry.getKey()).get(0);
                        return new CIValue(source.getUid(), source.getName(), source.getUrl(), order.getAndIncrement(), entry.getValue());
                    }).collect(toList());

            // Add the related data to the list of datas of the reference internal link data.
            referenceCILinkData.getDatas().add(new CRelatedData(relatedDataType, ciValues));
        });

        // Save the reference related data to Cassandra
        update(referenceCILinkData);

        // Calculate data for other languages.
        final Map<RelatedDataType, List<CRelatedData>> refRelatedDataMap = referenceCILinkData.getDatas().stream().collect(groupingBy(CRelatedData::getType));
        final Map<RelatedDataType, List<CIValue>> refCIValuesMap = refRelatedDataMap.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry(e.getKey(),
                        e.getValue().stream().map(CRelatedData::getValues).flatMap(List::stream).distinct().collect(toList())))
                .collect(toMap(Map.Entry<RelatedDataType, List<CIValue>>::getKey, Map.Entry<RelatedDataType, List<CIValue>>::getValue));

        langLinkDatas.forEach((key, value) -> {
            final Map<RelatedDataType, List<CRelatedData>> rdMap = value.stream().filter(linkData -> null != linkData.getDatas())
                    .map(CInternalLinkData::getDatas) //
                    .distinct().flatMap(List::stream) //
                    .collect(groupingBy(CRelatedData::getType));

            final Map<RelatedDataType, List<CIValue>> ciValuesMap = rdMap.entrySet().stream().map(e -> {
                return new AbstractMap.SimpleEntry(e.getKey(), e.getValue().stream() //
                        .map(CRelatedData::getValues) //
                        .flatMap(List::stream) //
                        .distinct() // Remove duplicates
                        .peek(civ -> { // If the UID is missing, then we set it to be used as Key
                            if (StringUtils.isBlank(civ.getUid())) {
                                civ.setUid(civ.getUrl().substring(civ.getUrl().lastIndexOf('/') + 1));
                            }
                        }).collect(toList()));
            }).collect(toMap(Map.Entry<RelatedDataType, List<CIValue>>::getKey, Map.Entry<RelatedDataType, List<CIValue>>::getValue));

            refCIValuesMap.forEach((refKey, refValue) -> {
                final CRelatedData cRelatedData = new CRelatedData(refKey, new ArrayList<CIValue>());
                final List<CIValue> values = ciValuesMap.get(refKey).stream() //
                        .filter(civ -> refValue.contains(civ)) //
                        .peek(civ -> {
                            refValue.stream().filter(refCiv -> civ.equals(refCiv)).findFirst().ifPresent(refCiv -> {
                                civ.setCount(refCiv.getCount());
                            });
                        }).collect(toList());
                cRelatedData.setValues(values);
                referenceCILinkData.getDatas().add(cRelatedData);
            });

            update(referenceCILinkData);
        });
    }

    /**
     * Calculate the related data directly
     * 
     * @param categoryModel
     * @param baseSiteModel
     */
    protected void processDirectly(final CategoryModel categoryModel, final BaseSiteModel baseSiteModel, final CInternalLinkData iLinkData) {

        // 1) Retrieve the related manufacturers
        final List<RelatedItemData<DistManufacturerModel>> manufacturers = getInternalLinkMessageQueueService()
                .fetchRelatedManufacturersForCategory(categoryModel.getCode(), baseSiteModel.getUid());

        if (CollectionUtils.isEmpty(manufacturers)) {
            LOG.warn("No related manufacturers found for the category {}", categoryModel.getCode());
        }

        // 2) Retrieve the related categories
        final List<RelatedItemData<CategoryModel>> categories;
        if (CollectionUtils.isNotEmpty(manufacturers)) {
            categories = getInternalLinkMessageQueueService().fetchRelatedCategoriesForCategory(
                manufacturers.stream().filter(m -> m.getItem() != null).map(RelatedItemData::getItem).collect(toList()), baseSiteModel.getUid());
        } else {
            categories = Collections.emptyList();
            LOG.warn("No related categories found for the category {}", categoryModel.getCode());
        }

        // 3) Process the related products
        final List<RelatedItemData<ProductModel>> relatedProducts = getInternalLinkMessageQueueService().fetchRelatedProductsForCategory(categoryModel.getCode(),
                baseSiteModel.getUid());
        if (CollectionUtils.isEmpty(relatedProducts)) {
            LOG.warn("No related products found for the category {}", categoryModel.getCode());
        }

        // 4) Get the Top Sellers in Category
        final CMSSiteModel cmsSiteModel = (CMSSiteModel) baseSiteModel;
        final List<RelatedItemData<ProductModel>> topSellers = getInternalLinkMessageQueueService().fetchTopSellersOfCategory(categoryModel, cmsSiteModel);
        if (CollectionUtils.isEmpty(topSellers)) {
            LOG.warn("No TopSellers for category: {}", categoryModel.getCode());
        }

        // 5) Get the new arrivals
        final List<ProductModel> newArrivals = getInternalLinkMessageQueueService().fetchNewArrivalsOfCategory(categoryModel, cmsSiteModel);
        if (CollectionUtils.isEmpty(newArrivals)) {
            LOG.warn("No new products for category: {}", categoryModel.getCode());
        }

        final Set<LanguageModel> supportedLanguages = baseSiteModel.getStores().get(0).getLanguages();
        final int MAX_LINKS = getMaxLinks();

        supportedLanguages.forEach(language -> {
            final Locale locale = getCommonI18NService().getLocaleForLanguage(language);
            iLinkData.setLanguage(locale.getLanguage());
            iLinkData.setDatas(new ArrayList<CRelatedData>());

            // 1) Populate related products
            final AtomicInteger index = new AtomicInteger(0);
            if (!relatedProducts.isEmpty()) {
                iLinkData.getDatas().add(new CRelatedData(RelatedDataType.RELATED_PRODUCT, //
                        relatedProducts.stream() //
                                .filter(p -> p.getItem() != null) //
                                .sorted((ci1, ci2) -> (int) (ci2.getCount() - ci1.getCount())) //
                                .limit(MAX_LINKS) //
                                .map(pitem -> {
                                    final ProductModel p = pitem.getItem();
                                    return new CIValue(p.getCode(), getPName(p, locale), resolveURL(p, baseSiteModel, locale), index.getAndIncrement(),
                                            pitem.getCount());
                                }) //
                                .collect(toList())));
            }
            // 2) Populate related categories
            if (!categories.isEmpty()) {
                index.set(0);
                final Collection<CategoryModel> allSuperCategories = categoryModel.getAllSupercategories();
                iLinkData.getDatas().add(new CRelatedData(RelatedDataType.RELATED_CATEGORY, //
                        categories.stream() //
                                .filter(citem -> citem.getItem() != null && !allSuperCategories.contains(citem.getItem())) //
                                .sorted((ci1, ci2) -> (int) (ci2.getCount() - ci1.getCount())) //
                                .limit(MAX_LINKS) //
                                .map(citem -> {
                                    final CategoryModel c = citem.getItem();
                                    return new CIValue(c.getCode(), c.getName(locale), resolveURL(c, baseSiteModel, locale),
                                            index.getAndIncrement(), citem.getCount());
                                }).collect(toList())));
            }

            // 3) Populate related manufacturers
            index.set(0);
            final CRelatedData relatedData = new CRelatedData(RelatedDataType.RELATED_MANUFACTURER);
            relatedData.setValues(manufacturers.stream() //
                    .sorted((ci1, ci2) -> (int) (ci2.getCount() - ci1.getCount())) //
                    .limit(MAX_LINKS) //
                    .map(mitem -> {
                        final DistManufacturerModel man = mitem.getItem();
                        return new CIValue(man.getCode(), man.getName(), resolveURL(man, baseSiteModel, locale), index.getAndIncrement(),
                                mitem.getCount());
                    }).collect(toList()));
            iLinkData.getDatas().add(relatedData);

            // 4) Process the "Top Sellers" products
            if (!topSellers.isEmpty()) {
                index.set(0);
                iLinkData.getDatas().add(new CRelatedData(RelatedDataType.TOP_SELLER_PRODUCT, //
                        topSellers.stream() //
                                .limit(MAX_LINKS) // Limit to the first 5 elements
                                .map(relatedItemData -> {
                                    final ProductModel p = relatedItemData.getItem();
                                    return new CIValue(p.getCode(), //
                                            getPName(p, locale), //
                                            resolveURL(p, cmsSiteModel, locale), //
                                            index.getAndIncrement(), //
                                            relatedItemData.getCount());
                                }).collect(toList())));
            }

            // 5) Process the "New Arrivals"
            if (!newArrivals.isEmpty()) {
                index.set(0);
                iLinkData.getDatas().add(new CRelatedData(RelatedDataType.NEW_ARRIVAL_PRODUCT, //
                        newArrivals.stream() //
                                .limit(MAX_LINKS) // Limit to the first 5 elements
                                .map(p -> new CIValue(p.getCode(), getPName(p, locale), resolveURL(p, cmsSiteModel, locale), index.getAndIncrement()))
                                .collect(toList())));
            }
            // LAST) store the calculated data
            update(iLinkData);
        });
    }

    /**
     * Fetch recursively the related data for the specified category and its sub-categories
     * 
     * @param categoryModel
     *            the parent #CategoryModel
     * @param baseSiteModel
     *            the target #BaseSiteModel
     * @param language
     *            the #LanguageModel
     * @return #List<CInternalLinkData>
     */
    protected List<CInternalLinkData> getLinkDataForCategory(final CategoryModel categoryModel, final BaseSiteModel baseSiteModel,
                                                             final LanguageModel language) {
        // If the category is PL, then we try to get the data from Cassandra
        final int MAX_AGE = getMaxAge();
        final List<CInternalLinkData> existingDatas = find(categoryModel.getCode(), baseSiteModel.getUid(), RowType.CATEGORY);
        if (getProcessCategoryDirectlyOrRecursivelyStrategy().shouldProcessDirectly(categoryModel)) {
            if (CollectionUtils.isEmpty(existingDatas) || !isValidLinkData(existingDatas.get(0), MAX_AGE)) {
                sendMessage(categoryModel.getCode(), RowType.CATEGORY, baseSiteModel, language, false);
            }
            return CollectionUtils.isEmpty(existingDatas) ? Collections.emptyList() : existingDatas;
        }

        // For PLs and for categories not having sub-categories, we don't need to go deeper.
        if (CollectionUtils.isEmpty(categoryModel.getCategories())) {
            return Collections.emptyList();
        }

        // If the category is not a PL and it has sub-categories
        final List<CInternalLinkData> subLinkDatas = categoryModel.getCategories().stream().map(category -> {
            final List<CInternalLinkData> linkDatas = getLinkDataForCategory(category, baseSiteModel, language);
            if (CollectionUtils.isEmpty(linkDatas) && canBeUpdated(category)) {
                // only send message if the category is PL or has sub-categories to avoid infinite loops
                sendMessage(category.getCode(), RowType.CATEGORY, baseSiteModel, language, false);
            }
            return linkDatas;
        }).filter(datas -> CollectionUtils.isNotEmpty(datas)).reduce(new ArrayList<CInternalLinkData>(), (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        });

        return subLinkDatas;
    }

    /**
     * Checks whether the specified #CategoryModel can be updated or not. A category can be updated if and only if it is a product line or
     * it has sub-categories.
     * 
     * @param category
     *            the #CategoryModel
     * @return {@code true} if the category can be updated, {@code false} otherwise
     */
    private boolean canBeUpdated(final CategoryModel category) {
        return getCategoryService().isProductLine(category) || CollectionUtils.isNotEmpty(category.getCategories());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.handlers.AbstractILMessageHandler#getRowType()
     */
    @Override
    public RowType getRowType() {
        return RowType.CATEGORY;
    }

    public ProcessCategoryDirectlyOrRecursivelyStrategy getProcessCategoryDirectlyOrRecursivelyStrategy() {
        return processCategoryDirectlyOrRecursivelyStrategy;
    }

    public void setProcessCategoryDirectlyOrRecursivelyStrategy(
            final ProcessCategoryDirectlyOrRecursivelyStrategy processCategoryDirectlyOrRecursivelyStrategy) {
        this.processCategoryDirectlyOrRecursivelyStrategy = processCategoryDirectlyOrRecursivelyStrategy;
    }

}
