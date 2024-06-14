package com.namics.distrelec.populator;

import static com.namics.distrelec.occ.core.constants.YcommercewebservicesConstants.SEOConstants.ALTERNATE_HREFLANG_URLS;
import static com.namics.distrelec.occ.core.constants.YcommercewebservicesConstants.SEOConstants.CANONICAL_URL;
import static com.namics.distrelec.occ.core.constants.YcommercewebservicesConstants.SEOConstants.META_DESCRIPTION;
import static com.namics.distrelec.occ.core.constants.YcommercewebservicesConstants.SEOConstants.META_IMAGE;
import static com.namics.distrelec.occ.core.constants.YcommercewebservicesConstants.SEOConstants.META_TITLE;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import com.namics.distrelec.b2b.core.service.data.impl.DefaultDistRestrictionData;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.basesites.DistrelecOCCBaseSiteFacade;
import com.namics.distrelec.b2b.facades.basesites.seo.DistLink;
import com.namics.distrelec.b2b.facades.helper.DistLogoUrlHelper;
import com.namics.distrelec.b2b.facades.seo.DistSeoFacade;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import com.namics.distrelec.b2b.facades.util.MetaSanitizerUtil;
import com.namics.distrelec.b2b.facades.util.WebpMediaUtil;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cmsfacades.cmsitems.properties.CMSItemPropertiesSupplier;
import de.hybris.platform.cmsfacades.rendering.RestrictionContextProvider;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.L10NService;

public class SEOPropertiesSupplier implements CMSItemPropertiesSupplier {

    private static final String FAMILY_TYPE_CODE = "Familie";

    private static final String PAGE_TITLE_ONLINE_SHOP_PROPERTY_KEY = "page.title.online.shop";

    private static final int MAX_TITLE_LENGTH = 60;

    private static final String LONG_SEPARATOR = " - ";

    private static final String SHORT_SEPARATOR = " ";

    private final String seoGroupName;

    private final Predicate<CMSItemModel> itemModelPredicate;

    private final RestrictionContextProvider restrictionContextProvider;

    private final DistrelecOCCBaseSiteFacade occBaseSiteFacade;

    @Autowired
    @Qualifier("distManufacturerModelUrlResolver")
    private DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver;

    @Autowired
    @Qualifier("categoryModelUrlResolver")
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    @Qualifier("contentPageUrlResolver")
    private DistUrlResolver<ContentPageModel> contentPageModelDistUrlResolver;

    private final CMSSiteService cmsSiteService;

    private final DistLogoUrlHelper distLogoUrlHelper;

    private final DistSeoFacade distSeoFacade;

    @Autowired
    private L10NService l10NService;

    public SEOPropertiesSupplier(final String seoGroupName, final Predicate<CMSItemModel> itemModelPredicate,
                                 final RestrictionContextProvider restrictionContextProvider,
                                 final CMSSiteService cmsSiteService, final DistLogoUrlHelper distLogoUrlHelper, final DistSeoFacade distSeoFacade,
                                 final DistrelecOCCBaseSiteFacade occBaseSiteFacade) {
        this.seoGroupName = seoGroupName;
        this.itemModelPredicate = itemModelPredicate;
        this.restrictionContextProvider = restrictionContextProvider;
        this.cmsSiteService = cmsSiteService;
        this.distLogoUrlHelper = distLogoUrlHelper;
        this.distSeoFacade = distSeoFacade;
        this.occBaseSiteFacade = occBaseSiteFacade;
    }

    @Override
    public boolean isEnabled(final CMSItemModel itemModel) {
        // must not be used for product pages
        boolean isNotProductPage = !ProductPageModel.class.isAssignableFrom(itemModel.getClass());
        return isNotProductPage;
    }

    /**
     * The {@link Predicate} to test whether this supplier can be applied to provided {@link CMSItemModel}
     *
     * @return the {@link Predicate}
     */
    @Override
    public Predicate<CMSItemModel> getConstrainedBy() {
        return getItemModelPredicate();
    }

    @Override
    public Map<String, Object> getProperties(final CMSItemModel itemModel) {
        final Map<String, Object> properties = new HashMap<>();
        final Optional<AbstractPageModel> abstractPage = Optional.ofNullable(itemModel).filter(AbstractPageModel.class::isInstance)
                                                                 .map(AbstractPageModel.class::cast);

        if (abstractPage.isPresent()) {
            final AbstractPageModel abstractPageModel = abstractPage.get();
            addMetaTagsForContentPage(abstractPageModel, properties);
            addMetaTagsForPLP(abstractPageModel, properties);
            addMetaTagsForCategoryPage(abstractPageModel, properties);
            addMetaTagsForManufacturerPage(abstractPageModel, properties);
            addMetaTagsForProductFamilyPage(abstractPageModel, properties);
            properties.putIfAbsent(META_IMAGE, distLogoUrlHelper.getLogoUrl(cmsSiteService.getCurrentSite()));
        }
        return properties;
    }

    private void addMetaTagsForCategoryPage(final AbstractPageModel abstractPageModel, final Map<String, Object> properties) {
        if (abstractPageModel instanceof CategoryPageModel) {
            final Optional<CategoryModel> category = Optional.ofNullable(getRestrictionContextProvider().getRestrictionInContext())
                                                             .filter(RestrictionData::hasCategory)
                                                             .map(RestrictionData::getCategory);

            category.ifPresent(c -> {
                final DistLink canonicalLink = occBaseSiteFacade.getCanonicalLink(cmsSiteService.getCurrentSite().getUid(), c, categoryModelUrlResolver);
                properties.put(CANONICAL_URL, canonicalLink.getHref());
                final List<DistLink> alternateHreflangLinks = occBaseSiteFacade.setupAlternateHreflangLinks(c, categoryModelUrlResolver);
                final String alternateHrefLinksCondensed = condenseAlternateHreflangLinks(alternateHreflangLinks);
                properties.put(ALTERNATE_HREFLANG_URLS, alternateHrefLinksCondensed);
                final MetaData metaData = distSeoFacade.getMetaDataForCategory(category.get());
                properties.put(META_DESCRIPTION, metaData != null ? metaData.getDescription() : StringUtils.EMPTY);
                final String metaDescription = l10NService.getLocalizedString("meta.description.category", new String[] { category.get().getName() })
                                                          .replace("\"", "&quot;");
                properties.put(META_DESCRIPTION,
                               null != category.get().getSeoMetaDescription() ? prepareSeoText(category.get().getSeoMetaDescription()) : metaDescription);
            });
        }
    }

    private void addMetaTagsForProductFamilyPage(final AbstractPageModel abstractPageModel, final Map<String, Object> properties) {
        if (abstractPageModel instanceof ProductFamilyPageModel) {
            final Optional<CategoryModel> pfCategory = Optional.ofNullable(getRestrictionContextProvider().getRestrictionInContext())
                                                               .filter(RestrictionData::hasCategory)
                                                               .map(RestrictionData::getCategory)
                                                               .filter(this::isProductFamily);

            pfCategory.map(CategoryModel::getSeoMetaTitle)
                      .ifPresent(metaTitle -> properties.put(META_TITLE, prepareSeoText(metaTitle)));
            pfCategory.map(CategoryModel::getSeoMetaDescription)
                      .ifPresent(metaDesc -> properties.put(META_DESCRIPTION, prepareSeoText(metaDesc)));
            pfCategory.map(CategoryModel::getPrimaryImage)
                      .map(getMedia())
                      .map(MediaModel::getURL)
                      .ifPresent(metaImageUrl -> properties.put(META_IMAGE, metaImageUrl));
        }
    }

    private boolean isProductFamily(final CategoryModel category) {
        return category != null
                && category.getPimCategoryType() != null
                && FAMILY_TYPE_CODE.equals(category.getPimCategoryType().getCode());
    }

    private void addMetaTagsForManufacturerPage(final AbstractPageModel abstractPageModel, final Map<String, Object> properties) {
        if (abstractPageModel instanceof DistManufacturerPageModel) {
            final Optional<DistManufacturerModel> manufacturer = Optional.ofNullable(getRestrictionContextProvider().getRestrictionInContext())
                                                                         .filter(DefaultDistRestrictionData.class::isInstance)
                                                                         .map(DefaultDistRestrictionData.class::cast)
                                                                         .filter(DefaultDistRestrictionData::hasManufacturer)
                                                                         .map(DefaultDistRestrictionData::getManufacturer);
            manufacturer.map(DistManufacturerModel::getSeoMetaTitle)
                        .ifPresentOrElse(metaTitle -> properties.put(META_TITLE, prepareSeoText(metaTitle)),
                                         () -> properties.put(META_TITLE, resolveManufacturerPageTitle(manufacturer.get())));
            manufacturer.map(DistManufacturerModel::getSeoMetaDescription)
                        .ifPresent(metaDesc -> properties.put(META_DESCRIPTION, prepareSeoText(metaDesc)));
            manufacturer.map(DistManufacturerModel::getPrimaryImage)
                        .map(getManufacturerMedia())
                        .map(MediaModel::getURL)
                        .ifPresent(metaImageUrl -> properties.put(META_IMAGE, metaImageUrl));
            manufacturer.ifPresent(m -> {
                final DistLink canonicalLink = occBaseSiteFacade.getCanonicalLink(cmsSiteService.getCurrentSite().getUid(), m,
                                                                                  distManufacturerModelUrlResolver);
                properties.put(CANONICAL_URL, canonicalLink.getHref());
                final List<DistLink> alternateHreflangLinks = occBaseSiteFacade.setupAlternateHreflangLinks(m, distManufacturerModelUrlResolver);
                final String alternateHrefLinksCondensed = condenseAlternateHreflangLinks(alternateHreflangLinks);
                properties.put(ALTERNATE_HREFLANG_URLS, alternateHrefLinksCondensed);
            });
        }
    }

    private String resolveManufacturerPageTitle(final DistManufacturerModel manufacturer) {
        final StringBuilder builder = new StringBuilder();
        final String name = isNull(manufacturer) ? StringUtils.EMPTY : manufacturer.getName();
        if (StringUtils.isNotBlank(name)) {
            builder.append(name).append(SHORT_SEPARATOR);
        }
        builder.append(l10NService.getLocalizedString(PAGE_TITLE_ONLINE_SHOP_PROPERTY_KEY));

        // Add shop Data
        appendShopNameSuffix(builder);

        return StringEscapeUtils.escapeHtml(builder.toString());
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

    protected void addMetaTagsForContentPage(final AbstractPageModel abstractPageModel, final Map<String, Object> properties) {
        if (abstractPageModel instanceof ContentPageModel) {
            final String siteId = getCmsSiteService().getCurrentSite().getUid();
            final ContentPageModel contentPage = (ContentPageModel) abstractPageModel;

            if (contentPage.isHomepage()) {
                properties.put(CANONICAL_URL, occBaseSiteFacade.getCanonicalLink("").getHref());
            } else {
                properties.put(CANONICAL_URL, occBaseSiteFacade.getCanonicalLink(siteId, contentPage, contentPageModelDistUrlResolver).getHref());
            }

            List<DistLink> alternateHreflangLinks;
            if (contentPage.isHomepage()) {
                alternateHreflangLinks = occBaseSiteFacade.setupBaseSiteLinks();
            } else {
                alternateHreflangLinks = occBaseSiteFacade.setupAlternateHreflangLinks(contentPage, contentPageModelDistUrlResolver);
            }
            final String alternateHrefLinksCondensed = condenseAlternateHreflangLinks(alternateHreflangLinks);
            properties.put(ALTERNATE_HREFLANG_URLS, alternateHrefLinksCondensed);

            if (isNotBlank(abstractPageModel.getSeoMetaTitle())) {
                properties.put(META_TITLE, prepareSeoText(abstractPageModel.getSeoMetaTitle()));
            } else {
                properties.put(META_TITLE, prepareSeoText(resolveContentPageTitle((ContentPageModel) abstractPageModel)));
            }

            if (contentPage.isHomepage()) {
                if (isNotBlank(abstractPageModel.getSeoMetaDescription())) {
                    properties.put(META_DESCRIPTION, prepareSeoText(abstractPageModel.getSeoMetaDescription()));
                } else {
                    final String metaDescription = l10NService.getLocalizedString("meta.description.homepage");
                    properties.put(META_DESCRIPTION, metaDescription);
                }

            }

        }
    }

    private String resolveContentPageTitle(final ContentPageModel contentPage) {
        final String title = contentPage.getTitle();
        return contentPage.isHomepage() ? resolveHomePageTitle(title) : resolveContentPageTitle(title);
    }

    private String resolveContentPageTitle(final String title) {
        final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();

        final StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(title)) {
            builder.append(title).append(" - ");
        }
        builder.append(currentSite.getName());
        return builder.toString();
    }

    private String resolveHomePageTitle(final String title) {
        final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
        final StringBuilder builder = new StringBuilder();
        builder.append(currentSite.getName());

        if (!StringUtils.isEmpty(title)) {
            builder.append(LONG_SEPARATOR).append(title);
        }

        return StringEscapeUtils.escapeHtml(builder.toString());
    }

    protected void addMetaTagsForPLP(final AbstractPageModel abstractPageModel, final Map<String, Object> properties) {
        if (abstractPageModel instanceof CategoryPageModel) {
            final Optional<CategoryModel> category = Optional.ofNullable(getRestrictionContextProvider().getRestrictionInContext())
                                                             .filter(RestrictionData::hasCategory)
                                                             .map(RestrictionData::getCategory);
            category.map(CategoryModel::getSeoMetaTitle).ifPresent(metaTitle -> properties.put(META_TITLE, prepareSeoText(metaTitle)));
            category.map(CategoryModel::getSeoMetaDescription).ifPresent(metaDesc -> properties.put(META_DESCRIPTION, prepareSeoText(metaDesc)));
            category.map(CategoryModel::getPrimaryImage).map(getMedia()).map(MediaModel::getURL)
                    .ifPresent(metaImageUrl -> properties.put(META_IMAGE, metaImageUrl));
        }
    }

    private Function<MediaContainerModel, MediaModel> getMedia() {
        return image -> WebpMediaUtil.getMediaModelByType(DistConstants.MediaFormat.PORTRAIT_MEDIUM_WEBP,
                                                          DistConstants.MediaFormat.PORTRAIT_MEDIUM,
                                                          image);
    }

    private Function<MediaContainerModel, MediaModel> getManufacturerMedia() {
        return image -> WebpMediaUtil.getMediaModelByType(DistConstants.MediaFormat.BRAND_LOGO_WEBP,
                                                          DistConstants.MediaFormat.BRAND_LOGO,
                                                          image);
    }

    private String prepareSeoText(String text) {
        final String cmsSiteName = getCmsSiteService().getCurrentSite().getName();
        text = StringUtils.replaceIgnoreCase(text, "${siteName}", cmsSiteName);
        text = StringUtils.replaceIgnoreCase(text, "$(siteName)", cmsSiteName);
        return MetaSanitizerUtil.sanitize(text);
    }

    /**
     * Method to provide the properties' group name.
     *
     * @return the properties' group name.
     */
    @Override
    public String groupName() {
        return getSeoGroupName();
    }

    public String getSeoGroupName() {
        return seoGroupName;
    }

    public Predicate<CMSItemModel> getItemModelPredicate() {
        return itemModelPredicate;
    }

    public RestrictionContextProvider getRestrictionContextProvider() {
        return restrictionContextProvider;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }
}
