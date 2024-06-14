/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.basesites.DistrelecOCCBaseSiteFacade;
import com.namics.distrelec.b2b.facades.basesites.seo.DistLink;
import com.namics.distrelec.b2b.facades.helper.DistLogoUrlHelper;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import com.namics.distrelec.b2b.facades.util.MetaSanitizerUtil;
import com.namics.distrelec.b2b.facades.util.WebpMediaUtil;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.L10NService;

public class DistProductMetaDataPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    private static final String PRODUCT_PAGE_TITLE_BUY_PROPERTY_KEY = "product.page.title.buy";

    private static final int MAX_TITLE_LENGTH = 60;

    private static final String LONG_SEPARATOR = " - ";

    private static final String SHORT_SEPARATOR = " ";

    private static final String REGEX_TO_STRIP_CHARACTERS = "[-._]";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistrelecOCCBaseSiteFacade occBaseSiteFacade;

    @Autowired
    @Qualifier("productModelUrlResolver")
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    @Qualifier("catalogPlusProductModelUrlResolver")
    private DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver;

    @Autowired
    private DistLogoUrlHelper distLogoUrlHelper;

    @Autowired
    private L10NService l10NService;

    @Override
    public void populate(final SOURCE product, final TARGET target) throws ConversionException {
        MetaData metaData = target.getMetaData();
        if (metaData == null) {
            metaData = new MetaData();
            target.setMetaData(metaData);
        }
        final MetaData meta = metaData;

        Optional<ProductModel> productOptional = Optional.of(product);
        productOptional.map(ProductModel::getSeoMetaTitle)
                       .ifPresentOrElse(metaTitle -> meta.setMetaTitle(prepareSeoText(metaTitle)),
                                        () -> meta.setMetaTitle(resolveProductPageTitle(product)));
        productOptional.map(ProductModel::getSeoMetaDescription)
                       .ifPresent(metaDescription -> meta.setMetaDescription(prepareSeoText(metaDescription)));
        productOptional.map(ProductModel::getPrimaryImage).map(getMedia()).map(MediaModel::getURL)
                       .ifPresent(metaImageUrl -> meta.setMetaImage(metaImageUrl));
        productOptional.ifPresent(p -> {
            final DistLink canonicalLink = occBaseSiteFacade.getCanonicalLink(cmsSiteService.getCurrentSite().getUid(), p, getProductUrlResolver(p));
            meta.setCanonicalUrl(canonicalLink.getHref());
            final List<DistLink> alternateHreflangLinks = occBaseSiteFacade.setupAlternateHreflangLinks(p, getProductUrlResolver(p));
            final String alternateHrefLinksCondensed = condenseAlternateHreflangLinks(alternateHreflangLinks);
            meta.setAlternateHreflangUrls(alternateHrefLinksCondensed);
        });

        if (meta.getMetaImage() == null) {
            meta.setMetaImage(distLogoUrlHelper.getLogoUrl(cmsSiteService.getCurrentSite()));
        }
    }

    private String resolveProductPageTitle(final ProductModel product) {
        // get title portions
        final String buy = StringUtils.trimToEmpty(l10NService.getLocalizedString(PRODUCT_PAGE_TITLE_BUY_PROPERTY_KEY, new Object[] { "" }));
        final String productName = StringUtils.defaultString(product.getName(), product.getCode());
        final String manufacturerName = (product.getManufacturer() != null && StringUtils.isNotBlank(product.getManufacturer().getName()))
                                                                                                                                           ? product.getManufacturer()
                                                                                                                                                    .getName()
                                                                                                                                           : StringUtils.EMPTY;
        final String mpn = StringUtils.defaultString(product.getTypeName());
        final StringBuilder titleStringBuilder = new StringBuilder();

        final String separator = (buy.length() + productName.length() + manufacturerName.length()
                                  + mpn.length() <= MAX_TITLE_LENGTH - 3 * (LONG_SEPARATOR.length())) ? LONG_SEPARATOR : SHORT_SEPARATOR;

        // Add MPN
        if (StringUtils.isNotEmpty(mpn)) {
            titleStringBuilder.append(mpn).append(separator);
        }

        // Add buy
        if (buy.length() + productName.length() + manufacturerName.length() + mpn.length() <= MAX_TITLE_LENGTH - 3 * (LONG_SEPARATOR.length())) {
            titleStringBuilder.append(buy).append(SHORT_SEPARATOR);
        }

        // Add product data
        titleStringBuilder.append(productName);
        if (StringUtils.isNotEmpty(manufacturerName)) {
            titleStringBuilder.append(separator).append(manufacturerName);
        }

        // Add shop Data
        appendShopNameSuffix(titleStringBuilder);

        return titleStringBuilder.toString().length() > MAX_TITLE_LENGTH
                                                                         ? StringUtils.removePattern(titleStringBuilder.toString(), REGEX_TO_STRIP_CHARACTERS)
                                                                         : titleStringBuilder.toString();
    }

    private StringBuilder appendShopNameSuffix(final StringBuilder titleStringBuilder) {
        final String siteName = getSiteNameOrEmpty();
        final String siteNameNoCountry = siteName.split(StringUtils.SPACE)[0];
        if (titleStringBuilder.length() + LONG_SEPARATOR.length() + siteName.length() <= MAX_TITLE_LENGTH) {
            titleStringBuilder.append(LONG_SEPARATOR).append(siteName);
        } else {
            if (titleStringBuilder.length() + LONG_SEPARATOR.length() + siteNameNoCountry.length() <= MAX_TITLE_LENGTH) {
                titleStringBuilder.append(LONG_SEPARATOR).append(siteNameNoCountry);
            }
        }
        return titleStringBuilder;
    }

    private String getSiteNameOrEmpty() {
        return (getCmsSiteService().getCurrentSite() != null) ? getCmsSiteService().getCurrentSite().getName() : StringUtils.EMPTY;
    }

    private static String condenseAlternateHreflangLinks(final List<DistLink> alternateHreflangLinks) {
        return alternateHreflangLinks.stream()
                                     .map(link -> link.getHreflang() + "~" + link.getHref())
                                     .collect(Collectors.joining("|"));
    }

    private Function<MediaContainerModel, MediaModel> getMedia() {
        return image -> WebpMediaUtil.getMediaModelByType(DistConstants.MediaFormat.PORTRAIT_MEDIUM_WEBP,
                                                          DistConstants.MediaFormat.PORTRAIT_MEDIUM,
                                                          image);
    }

    private String prepareSeoText(String text) {
        final String cmsSiteName = getCmsSiteService().getCurrentSite().getName();
        text = StringUtils.replaceIgnoreCase(text, "${siteName}", cmsSiteName);
        text = StringUtils.replaceIgnoreCase(text, "$(siteName)", cmsSiteName);
        return MetaSanitizerUtil.sanitize(text);
    }

    public DistUrlResolver<ProductModel> getProductUrlResolver(final ProductModel productModel) {
        return productModel.getCatPlusSupplierAID() == null
                                                            ? productModelUrlResolver
                                                            : catalogPlusProductModelUrlResolver;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }
}
