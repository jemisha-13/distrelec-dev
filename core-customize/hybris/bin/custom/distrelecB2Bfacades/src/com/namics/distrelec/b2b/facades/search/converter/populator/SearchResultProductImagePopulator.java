/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Populator for {@link ImageData} on {@link ProductData}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class SearchResultProductImagePopulator extends AbstractSearchResultPopulator {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResultProductImagePopulator.class);

    private final Gson GSON = new Gson();

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) {
        final Map<String, ImageData> image = new HashMap<>();

        populateDefaultImage(source, image);
        populateAdditionalImages(source, image);

        if (!image.isEmpty()) {
            final List<Map<String, ImageData>> productImages = new ArrayList<>();
            productImages.add(image);
            target.setProductImages(productImages);
            target.setImages(image.values());
        }
    }

    private void populateDefaultImage(final SearchResultValueData source, final Map<String, ImageData> image) {
        final String title = getValue(source, TITLE.getValue());
        final String imageUrl = getValue(source, IMAGE_URL.getValue());
        final String imageWebpUrl = getValue(source, IMAGE_WEBP_URL.getValue());

        if (StringUtils.isNotBlank(imageUrl)) {
            String format = DistConstants.MediaFormat.PORTRAIT_SMALL;
            image.put(format, createImageData(title, imageUrl, format));
        }
        if (StringUtils.isNotBlank(imageWebpUrl)) {
            String format = DistConstants.MediaFormat.PORTRAIT_SMALL_WEBP;
            image.put(format, createImageData(title, imageWebpUrl, format));
        }
    }

    private void populateAdditionalImages(final SearchResultValueData source, final Map<String, ImageData> image) {
        final String title = getValue(source, TITLE.getValue());
        final String json = getValue(source, DistFactFinderExportColumns.ADDITIONAL_IMAGE_URLS.getValue());
        if (StringUtils.isNotBlank(json)) {
            try {
                final Map<String, String> additionalImageUrls = GSON.fromJson(json, Map.class);
                for (final String format : additionalImageUrls.keySet()) {
                    final String url = additionalImageUrls.get(format);
                    if (StringUtils.isNotBlank(format) && StringUtils.isNotBlank(url)) {
                        image.put(format, createImageData(title, url, format));
                    }
                }
            } catch (final JsonSyntaxException e) {
                LOG.error("Could not parse JSON of AdditionalImageURLs", e);
            }
        }
    }

    private ImageData createImageData(final String title, final String imageUrl, final String format) {
        final ImageData imageData = new ImageData();
        imageData.setAltText(title);
        imageData.setUrl(imageUrl);
        imageData.setFormat(format);
        return imageData;
    }

}
