/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Populator for {@link DistManufacturerData} on {@link ProductData}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class SearchResultManufacturerPopulator extends AbstractSearchResultPopulator {

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) {
        final DistManufacturerData manufacturer = new DistManufacturerData();

        manufacturer.setName(getStringValue(source, DistFactFinderExportColumns.MANUFACTURER.getValue()));
        manufacturer.setUrl(getStringValue(source, DistFactFinderExportColumns.MANUFACTURER_URL.getValue()));
        manufacturer.setImage(getImage(source));

        target.setDistManufacturer(manufacturer);
    }

    private Map<String, ImageData> getImage(final SearchResultValueData source) {
        final String imageUrl = getStringValue(source, DistFactFinderExportColumns.MANUFACTURER_IMAGE_URL.getValue());
        if (StringUtils.isNotBlank(imageUrl)) {
            final ImageData imageData = new ImageData();
            imageData.setUrl(imageUrl);
            return Collections.singletonMap(DistConstants.MediaFormat.BRAND_LOGO, imageData);
        }
        return null;
    }

}
