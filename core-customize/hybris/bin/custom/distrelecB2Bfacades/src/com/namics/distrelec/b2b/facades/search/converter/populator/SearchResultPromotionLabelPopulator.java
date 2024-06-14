/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.namics.distrelec.b2b.facades.product.data.DistPromotionLabelData;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Populator for {@link DistPromotionLabelData} on {@link ProductData}.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class SearchResultPromotionLabelPopulator extends AbstractSearchResultPopulator {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResultPromotionLabelPopulator.class);

    private static final Gson GSON = new Gson(); // considered thread-safe

    private static final Type COLLECTION_TYPE = new TypeToken<Collection<DistPromotionLabelData>>() {
        // empty constructor
    }.getType();

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) {
        final List<DistPromotionLabelData> sortedActiveLabels = Lists.newArrayList();
        final String promotionLabelsValue = this.<String> getValue(source, DistFactFinderExportColumns.PROMOTIONLABELS.getValue());

        try {
            final List<DistPromotionLabelData> labels = GSON.fromJson(promotionLabelsValue, COLLECTION_TYPE);
            final List<DistPromotionLabelData> activeLabels = parseActivePromotionLabels(labels);
            // DISTRELEC-6799
            // sortedActiveLabels.addAll(RANK_ORDERING.sortedCopy(activeLabels));
            sortedActiveLabels.addAll(PRIORITY_ORDERING.sortedCopy(activeLabels));
        } catch (final Exception e) {
            LOG.debug("Failed parsing JSON for value [{}].", promotionLabelsValue, e);
        }
        target.setActivePromotionLabels(sortedActiveLabels);
    }

    private List<DistPromotionLabelData> parseActivePromotionLabels(final List<DistPromotionLabelData> promotionLabels) {
        final List<DistPromotionLabelData> activePromotionLabels = Lists.newArrayList();
        if (promotionLabels == null) {
            return activePromotionLabels;
        }
        for (final DistPromotionLabelData label : promotionLabels) {
            if (Boolean.TRUE.equals(label.getActive())) {
                activePromotionLabels.add(label);
            }
        }
        return activePromotionLabels;
    }

    private static final Function<DistPromotionLabelData, Integer> GET_RANK_FUNCTION = new Function<>() {
        @Override
        public Integer apply(final DistPromotionLabelData label) {
            return label.getRank();
        }
    };

    @SuppressWarnings("unused")
    private static final Ordering<DistPromotionLabelData> RANK_ORDERING = Ordering.natural().onResultOf(GET_RANK_FUNCTION);

    // DISTRELEC-6799
    private static final Function<DistPromotionLabelData, Integer> GET_PRIORITY_FUNCTION = new Function<>() {
        @Override
        public Integer apply(final DistPromotionLabelData label) {
            return label.getPriority();
        }
    };

    private static final Ordering<DistPromotionLabelData> PRIORITY_ORDERING = Ordering.natural().onResultOf(GET_PRIORITY_FUNCTION);

}
