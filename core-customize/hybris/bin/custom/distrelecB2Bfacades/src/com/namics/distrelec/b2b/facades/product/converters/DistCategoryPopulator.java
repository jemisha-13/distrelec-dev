/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.web.util.HtmlUtils;

import com.namics.distrelec.b2b.core.model.CategorySEOSectionModel;
import com.namics.distrelec.b2b.facades.category.CategorySEOSectionData;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.converters.populator.CategoryPopulator;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Custom Converter for CategoryModel.
 *
 * @author pbueschi, Namics AG
 */
public class DistCategoryPopulator extends CategoryPopulator {

    @Autowired
    @Qualifier("mediaContainerToImageMapConverter")
    private Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter;

    protected CategoryData createTarget() {
        return new CategoryData();
    }

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public void populate(final CategoryModel source, final CategoryData target) {
        if (source.getPrimaryImage() != null) {
            target.setImages(mediaContainerToImageMapConverter.convert(source.getPrimaryImage()));
        }
        target.setPromotionText(source.getPromotionText());
        if (source.getLevel() != null) {
            target.setLevel(source.getLevel().intValue());
        }
        target.setNameEN(source.getName(Locale.ENGLISH));
        target.setIntroText(source.getIntroText());
        target.setSeoMetaTitle(processText(source.getSeoMetaTitle()));
        target.setSeoMetaDescription(processText(source.getSeoMetaDescription()));

        List<CategorySEOSectionData> seoSectionsData = source.getSeoSections().stream()
                                                             .map(this::convertSeoSection)
                                                             .collect(Collectors.toList());
        target.setSeoSections(seoSectionsData);

        super.populate(source, target);
    }

    private CategorySEOSectionData convertSeoSection(CategorySEOSectionModel sectionModel) {
        CategorySEOSectionData sectionData = new CategorySEOSectionData();
        String header = sectionModel.getHeader();
        if (isNotBlank(header)) {
            sectionData.setHeader(HtmlUtils.htmlUnescape(header));
        }
        String text = sectionModel.getText();
        if (isNotBlank(text)) {
            sectionData.setText(HtmlUtils.htmlUnescape(text));
        }
        return sectionData;
    }

    private String processText(String text) {
        if (StringUtils.isNotEmpty(text)) {
            PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("${", "}");
            return placeholderHelper.replacePlaceholders(text, placeholder -> {
                switch (placeholder) {
                    case "siteName":
                        return cmsSiteService.getCurrentSite().getName();
                    default:
                        return "${" + placeholder + "}";
                }
            });
        } else {
            return null;
        }
    }

}
