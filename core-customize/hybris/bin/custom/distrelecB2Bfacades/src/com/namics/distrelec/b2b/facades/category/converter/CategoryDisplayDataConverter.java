/*
 * Copyright 2000-2016 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.category.converter;

import com.namics.distrelec.b2b.facades.category.dto.FactFinderFacetValueCategoryDisplayDataDto;
import com.namics.hybris.ffsearch.data.facet.CategoryDisplayData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@code CategoryDisplayDataConverter}
 *
 *
 * @author nshandilya, Distrelec
 * @since Distrelec 5.14
 */
public class CategoryDisplayDataConverter
        extends AbstractPopulatingConverter<FactFinderFacetValueCategoryDisplayDataDto, CategoryDisplayData<SearchStateData>> {

    private static final Logger LOG = LogManager.getLogger(CategoryDisplayDataConverter.class);
    @Autowired
    @Qualifier("mediaContainerToImageMapConverter")
    private Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter;

    @Autowired
    @Qualifier("categoryModelUrlResolver")
    private UrlResolver<CategoryModel> categoryModelUrlResolver;

    @Override
    protected CategoryDisplayData<SearchStateData> createTarget() {
        return new CategoryDisplayData<SearchStateData>();
    }

    @Override
    public void populate(final FactFinderFacetValueCategoryDisplayDataDto sourceData, final CategoryDisplayData<SearchStateData> target) {
        final CategoryModel source = sourceData.getCategoryModel();
        target.setCode(source.getCode());
        target.setName(source.getName());
        if (source.getPrimaryImage() != null) {
            target.setImage(getMediaContainerToImageMapConverter().convert(source.getPrimaryImage()));
        }
        target.setUrl(getFilterUrl(sourceData));
        target.setSubcategoryDisplayDataList(new ArrayList<CategoryDisplayData<SearchStateData>>());

        if (source.getLevel() != null) {
            target.setLevel(source.getLevel().intValue());
        }

        target.setCount(sourceData.getFactFinderFacetValueData().getCount());
        target.setPromotionText(source.getPromotionText());
    }

    private String getFilterUrl(final FactFinderFacetValueCategoryDisplayDataDto sourceData) {
        StringBuilder filterurl = new StringBuilder();
        try {
            final String queryUrl = sourceData.getFactFinderFacetValueData().getQuery().getUrl();
            filterurl = new StringBuilder(removeParameterFromQueryString(queryUrl, "filter_categoryCodePathROOT"));

            String campaignUrl = "";
            if ((filterurl.indexOf("&filter_advisor_answerPath") != -1) || (filterurl.indexOf("&filter_advisor_campaignId") != -1)) {
                campaignUrl = Arrays.asList(filterurl.toString().split("&")).stream()
                        .filter(array -> (array.contains("filter_advisor_answerPath") || array.contains("filter_advisor_campaignId")))
                        .collect(Collectors.joining("&"));
            }

            if (queryUrl.indexOf("&filter_categoryCodePathROOT") != -1) {
                String queryFilter = sourceData.getFactFinderFacetValueData().getQueryFilter();
                queryFilter = removeParameterFromQueryString(queryFilter, "filter_PromotionLabels");
                if (StringUtils.contains(queryFilter, "filter_CuratedProducts") && StringUtils.contains(queryUrl, "filter_CuratedProducts")) {
                    queryFilter = removeParameterFromQueryString(queryFilter, "filter_PromotionLabels");
                }

                if (!StringUtils.endsWith(filterurl, "&")) {
                    filterurl.append("&");
                }
                filterurl.append(removeParameterFromQueryString(queryFilter, "filter_CuratedProducts"));
            }

            if (!campaignUrl.isEmpty()) {
                filterurl.append("&").append(campaignUrl);
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return filterurl.toString();
    }

    // DISTRELEC-11365: we should remove &filter_PromotionLabels from facet filters url.
    public static String removeParameterFromQueryString(final String queryString, final String parameterNameToRemove) {
        final String[] params = StringUtils.split(queryString, '&');
        return Arrays.stream(params).filter(p -> !p.startsWith(parameterNameToRemove + "=")).collect(Collectors.joining("&"));
    }

    public UrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(final UrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    public Converter<MediaContainerModel, Map<String, ImageData>> getMediaContainerToImageMapConverter() {
        return mediaContainerToImageMapConverter;
    }

    public void setMediaContainerToImageMapConverter(final Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter) {
        this.mediaContainerToImageMapConverter = mediaContainerToImageMapConverter;
    }

}
