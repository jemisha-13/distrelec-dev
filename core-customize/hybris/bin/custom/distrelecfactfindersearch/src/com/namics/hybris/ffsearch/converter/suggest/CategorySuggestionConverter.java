/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.namics.hybris.ffsearch.data.suggest.CategoryExtensionData;
import com.namics.hybris.ffsearch.data.suggest.CategorySuggestion;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;

/**
 * Converter for a {@link ResultSuggestion} to a {@link CategorySuggestion}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class CategorySuggestionConverter extends AbstractSuggestionConverter<ResultSuggestion, CategorySuggestion> {

    private static final Logger LOG = LoggerFactory.getLogger(CategorySuggestionConverter.class);

    private static final Gson GSON = new Gson();
    public static final String SOURCE_FIELD = "sourceField";

    @Override
    public CategorySuggestion convert(final ResultSuggestion source, final CategorySuggestion target) {
        target.setId(getId(source));
        target.setName(source.getName());
        target.setCount(source.getHitCount().intValue());
        target.setL1(getSuperCategory(source));
        target.setImg_url(source.getImage());
        setAdditionalCategoryData(source, target);
        return target;
    }

    @Override
    protected CategorySuggestion createTarget() {
        return new CategorySuggestion();
    }

    protected void setAdditionalCategoryData(final ResultSuggestion source, final CategorySuggestion target) {
        final CategoryExtensionData categoryExtension = getCategoryExtension(source);
        if (categoryExtension != null) {
            target.setUrl(categoryExtension.getUrl());
            target.setImg_url(categoryExtension.getImageUrl());
        }
    }

    protected int getSourceField(final ResultSuggestion source) {
        return Integer.parseInt(StringUtils.right(getValue(SOURCE_FIELD, source), 1));
    }

    /**
     * Get name of the 1st level supercategory, empty if the match is a 1st level supercategory itself.
     */
    private String getSuperCategory(final ResultSuggestion ffResult) {
        String parentCategory = null;
        final CategoryExtensionData categoryExtensionData = getCategoryExtension(ffResult);
        if (categoryExtensionData != null) {
            final String url = categoryExtensionData.getUrl();
            if (StringUtils.isNotBlank(url)) {
                try {
                    final String[] categoryLevels = url.substring(4, url.indexOf("/c/")).split("/");
                    parentCategory = categoryLevels[getSourceField(ffResult) - 2].replaceAll("-", " ");
                    // Category is URL encoded (e.g. %2F for '/').
                    return URLDecoder.decode(parentCategory, "UTF-8");
                } catch (final UnsupportedEncodingException e) {
                    LOG.error("Could not URL decode value [" + parentCategory + "] for field 'superCategory'", e);
                } catch (final IndexOutOfBoundsException e) {
                    LOG.error("Could not get category levels for category url " + url, e);
                }
            }
        }

        return parentCategory;
    }

    protected CategoryExtensionData getCategoryExtension(final ResultSuggestion source) {
        String json = getValue(DistFactFinderExportColumns.CATEGORY_EXTENSIONS.getValue(), source);
        if (StringUtils.isNotBlank(json)) {
            json = StringUtils.strip(json, "..");

            try {
                final List<CategoryExtensionData> categoryExtensions = GSON.fromJson(json, new TypeToken<List<CategoryExtensionData>>() {/* empty */
                }.getType());
                final int sourceCategoryLevel = getSourceField(source);
                if (sourceCategoryLevel > 0) {
                    return categoryExtensions.get(sourceCategoryLevel - 1);
                }
            } catch (final JsonSyntaxException e) {
                LOG.error("Could not convert to CategoryExtension list", e);
            } catch (final IndexOutOfBoundsException e) {
                LOG.error("Could not convert to CategoryExtension list", e);
            }
        }
        return null;
    }

}
