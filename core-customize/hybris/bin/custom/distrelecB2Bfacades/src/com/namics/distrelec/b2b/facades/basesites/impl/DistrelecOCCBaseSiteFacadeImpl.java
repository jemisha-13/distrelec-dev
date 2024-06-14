/*
 * Copyright 2021 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.basesites.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.basesites.DistrelecOCCBaseSiteFacade;
import com.namics.distrelec.b2b.facades.basesites.seo.DistLink;
import com.namics.distrelec.b2b.facades.basesites.seo.LinkType;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Default Implementation of {@link DistrelecOCCBaseSiteFacade}.
 */
public class DistrelecOCCBaseSiteFacadeImpl implements DistrelecOCCBaseSiteFacade {
    private static final Logger LOG = Logger.getLogger(DistrelecOCCBaseSiteFacadeImpl.class);

    private static final String LINK_ALTERNATE_RELATIONSHIP = "alternate";

    private static final String LINK_CANONICAL_RELATIONSHIP = "canonical";

    private static final Pattern LANG_PATTERN = Pattern.compile("^\\/([a-z]{2})(\\/|$)");

    private static final String URL_QUERY_STRING = "?q=";

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistManufacturerFacade distManufacturerFacade;

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Autowired
    private I18NService i18NService;

    @Override
    public DistLink getCanonicalLink(final String path) {
        if (path.contains(URL_QUERY_STRING)) {
            return makeLink(LINK_CANONICAL_RELATIONSHIP, getSiteBaseUrl() + makeUrlLangPart(), path.substring(0, path.lastIndexOf(URL_QUERY_STRING)));
        }
        return makeLink(LINK_CANONICAL_RELATIONSHIP, getSiteBaseUrl() + makeUrlLangPart(), path);
    }

    private String makeUrlLangPart(){
        return "/" + i18NService.getCurrentLocale().getLanguage() + "/";
    }

    @Override
    public <T> DistLink getCanonicalLink(final String baseSiteId, final T model, final DistUrlResolver<T> urlResolver) {
        return makeLink(LINK_CANONICAL_RELATIONSHIP, getSiteBaseUrl(),
                        urlResolver.resolve(model, baseSiteService.getBaseSiteForUID(baseSiteId), i18NService.getCurrentLocale()));
    }

    @Override
    public List<DistLink> setupBaseSiteLinks() {
        return setupAlternateHreflangLinks(null, null);
    }

    @Override
    public List<DistLink> setupBaseSiteLinksForLang(final String lang) {
        return setupAlternateHreflangLinksForLang(lang, null, null);
    }

    @Override
    public <T> List<DistLink> setupAlternateHreflangLinks(final T model, final DistUrlResolver<T> urlResolver) {
        List<DistLink> links = new ArrayList<>();
        String fullPath;
        if (model != null) {
            if (model instanceof ContentPageModel && ((ContentPageModel) model).isHomepage()) {
                fullPath = getSiteBaseUrl();
            } else {
                fullPath = getSiteBaseUrl()
                           + urlResolver.resolve(model, cmsSiteService.getCurrentSite(), cmsSiteService.getCurrentSite().getDefaultLanguage().getIsocode());
            }
        } else {
            fullPath = siteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteService.getCurrentSite(), true, "");
        }
        final List<String> paths = getBaseUrlAndPath(fullPath);
        // add default link
        final DistLink xDefaultlink = setupHeaderLink(LINK_ALTERNATE_RELATIONSHIP, getSiteBaseUrl(), getNonLocalizedPath(paths.get(1)), "x-default");
        links.add(xDefaultlink);
        // add link per each country language from current store
        List<CMSSiteModel> cmsSites;
        if (model instanceof ProductModel) {
            cmsSites = getProductFacade().getAvailableCMSSitesForProduct((ProductModel) model);
        } else if (model instanceof DistManufacturerModel) {
            cmsSites = distManufacturerFacade.getAvailableCMSSitesForManufacturer((DistManufacturerModel) model);
        } else if (model instanceof CategoryModel) {
            cmsSites = distCategoryFacade.getAvailableCMSSitesForCategory((CategoryModel) model);
        } else {
            cmsSites = new ArrayList<>(getCmsSiteService().getSites());
        }

        for (final CMSSiteModel cmsSite : cmsSites) {
            if ("distrelec_TR".equals(cmsSite.getUid()) || "distrelec".equals(cmsSite.getUid())) {
                continue;
            }

            String domain = this.siteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSite, true, "");

            if (CollectionUtils.isNotEmpty(cmsSite.getStores())) {
                List<LanguageModel> availableLanguages = cmsSite.getStores().get(0).getLanguages().stream()
                                                                .filter(languageModel -> !languageModel.getIsocode().contains("_"))
                                                                .collect(Collectors.toList());
                for (final LanguageModel languageModel : availableLanguages) {
                    String productOrCategoryUrl = (model != null && urlResolver != null ? urlResolver.resolve(model, cmsSite,
                                                                                                              languageModel.getIsocode())
                                                                                        : //
                                                                                        (LANG_PATTERN.matcher(paths.get(1))
                                                                                                     .matches() ? "/" + languageModel.getIsocode()

                                                                                                                : getNonLocalizedPath(paths.get(1))));
                    String url = productOrCategoryUrl;
                    if(model == null){
                        url = "/" + languageModel.getIsocode();
                    }

                    if(cmsSite.equals(cmsSiteService.getCurrentSite())){
                        final DistLink currentSiteAltLink = makeLink(LINK_ALTERNATE_RELATIONSHIP,
                                                                     domain,
                                                                     url,
                                                                     languageModel.getIsocode());
                        links.add(currentSiteAltLink);
                    }

                    final DistLink altLink = makeLink(LINK_ALTERNATE_RELATIONSHIP,
                                                      domain,
                                                      url, //
                                                      cmsSite.getUid().equalsIgnoreCase("distrelec_EX") ? languageModel.getIsocode()
                                                                                                        : languageModel.getIsocode() + "-"
                                                                                                          + cmsSite.getCountry().getIsocode(), //
                                                      cmsSite.getCountry().getName(getCommonI18NService().getLocaleForLanguage(languageModel)));

                    if (!languageModel.equals(cmsSite.getDefaultLanguage())) {
                        altLink.setType(LinkType.HEADER);
                    }
                    links.add(altLink);
                }
            }
        }
        sortDistLinks(links, getI18nService().getCurrentLocale());
        return links;
    }

    @Override
    public <T> List<DistLink> setupAlternateHreflangLinksForLang(final String lang, final T model, final DistUrlResolver<T> urlResolver) {
        List<DistLink> links = new ArrayList<>();
        String fullPath;
        if (model != null) {
            fullPath = urlResolver.resolve(model, cmsSiteService.getCurrentSite(), cmsSiteService.getCurrentSite().getDefaultLanguage().getIsocode());
        } else {
            fullPath = siteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteService.getCurrentSite(), true, "");
        }
        final List<String> paths = getBaseUrlAndPath(fullPath);
        // add default link
        final DistLink xDefaultlink = setupHeaderLink(LINK_ALTERNATE_RELATIONSHIP, getSiteBaseUrl(), paths.get(0) + getNonLocalizedPath(paths.get(1)),
                                                      "x-default");
        links.add(xDefaultlink);
        // add link per each country language from current store
        List<CMSSiteModel> cmsSites;
        if (model instanceof ProductModel) {
            cmsSites = getProductFacade().getAvailableCMSSitesForProduct((ProductModel) model);
        } else if (model instanceof DistManufacturerModel) {
            cmsSites = distManufacturerFacade.getAvailableCMSSitesForManufacturer((DistManufacturerModel) model);
        } else if (model instanceof CategoryModel) {
            cmsSites = distCategoryFacade.getAvailableCMSSitesForCategory((CategoryModel) model);
        } else {
            cmsSites = new ArrayList<>(getCmsSiteService().getSites());
        }

        for (final CMSSiteModel cmsSite : cmsSites) {
            if ("distrelec_TR".equals(cmsSite.getUid()) || "distrelec".equals(cmsSite.getUid())) {
                continue;
            }

            String domain = this.siteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSite, true, "");

            if (CollectionUtils.isNotEmpty(cmsSite.getStores())) {
                Collection<LanguageModel> languages = getCommonI18NService().getAllLanguages();
                // Look for the language with a matching iso code
                LanguageModel languageModel = null;
                for (final LanguageModel language : languages) {
                    if (StringUtils.equals(language.getIsocode(), lang)) {
                        languageModel = language;
                        break;
                    }
                }
                if (languageModel != null) {
                    String productOrCategoryUrl = (model != null && urlResolver != null ? urlResolver.resolve(model, cmsSite,
                                                                                                              languageModel.getIsocode())
                                                                                        : //
                                                                                        (LANG_PATTERN.matcher(paths.get(1))
                                                                                                     .matches() ? "/" + languageModel.getIsocode()
                                                                                                                : getNonLocalizedPath(paths.get(1))));

                    if(cmsSite.equals(cmsSiteService.getCurrentSite())){
                        String url= productOrCategoryUrl;

                        if(model instanceof ContentPageModel){
                            ContentPageModel contentPage = (ContentPageModel) model;
                            if(contentPage.isHomepage()){
                                url = "/" + languageModel.getIsocode();
                            }
                        }

                        final DistLink currentSiteAltLink = makeLink(LINK_ALTERNATE_RELATIONSHIP,
                                                                     domain,
                                                                     url,
                                                                     languageModel.getIsocode());
                        links.add(currentSiteAltLink);
                    }

                    final DistLink altLink = makeLink(LINK_ALTERNATE_RELATIONSHIP, //
                                                      domain,
                                                      productOrCategoryUrl, //
                                                      cmsSite.getUid().equalsIgnoreCase("distrelec_EX") ? languageModel.getIsocode()
                                                                                                        : languageModel.getIsocode() + "-"
                                                                                                          + cmsSite.getCountry().getIsocode(), //
                                                      cmsSite.getCountry().getName(getCommonI18NService().getLocaleForLanguage(languageModel)));

                    altLink.setType(LinkType.FOOTER);
                    links.add(altLink);
                }
            }
        }
        sortDistLinks(links, getI18nService().getCurrentLocale());
        return links;
    }

    private static void sortDistLinks(final List<DistLink> headerResult, final Locale locale) {

        Collections.sort(headerResult, (o1, o2) -> {
            final Collator umlautCollator = Collator.getInstance(locale);
            if (o1.getCountryName() == null && o2.getCountryName() == null) {
                return 0;
            } else if (o1.getCountryName() == null) {
                return -1;
            } else if (o2.getCountryName() == null) {
                return 1;
            } else {
                return umlautCollator.compare(o1.getCountryName(), o2.getCountryName());
            }
        });
    }

    private String getNonLocalizedPath(final String path) {
        return path.matches("^/[a-z]{2}/.*$") ? path.substring(3) : path;
    }

    private String getSiteBaseUrl() {
        return siteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteService.getCurrentSite(), true, "");
    }

    private List<String> getBaseUrlAndPath(final String urlString) {
        final URL url = getURL(urlString);
        final String baseUrl = getBaseUrl(url).toString();
        final String path = StringUtils.isEmpty(url.getPath()) ? "/" : url.getPath();
        return Arrays.asList(baseUrl, path);
    }

    private StringBuilder getBaseUrl(final URL url) {
        final StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(getHomeURL(url));
        if (url.getPort() != 0 && url.getPort() != -1) {
            baseUrl.append(":").append(url.getPort());
        }
        return baseUrl;
    }

    protected URL getURL(final String path) {
        URL url;
        try {
            url = new URL(path);
        } catch (final MalformedURLException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Problems while setting the <link> content. This url is incorrect:{}", e);
            }
            return null;
        }
        return url;
    }

    private String getHomeURL(final URL url) {
        return url.getProtocol() + "://" + url.getHost();
    }

    private DistLink setupHeaderLink(final String rel, String domain, final String href, final String hreflang) {
        final DistLink link = makeLink(rel, domain, href, hreflang);
        link.setType(LinkType.HEADER);
        return link;
    }

    private DistLink makeLink(final String rel, String domain, final String href) {
        return makeLink(rel, domain, href, null, null);
    }

    private DistLink makeLink(final String rel, String domain, final String href, final String hreflang) {
        return makeLink(rel, domain, href, hreflang, null);
    }

    private DistLink makeLink(final String rel, final String domain, final String href, final String hreflang, final String countryName) {
        DistLink distLink = new DistLink();
        distLink.setRel(StringUtils.isEmpty(rel) ? null : rel);
        distLink.setHref(StringUtils.isEmpty(href) ? domain : domain + href);
        distLink.setHreflang(StringUtils.isEmpty(hreflang) ? null : hreflang);
        distLink.setCountryName(countryName);
        distLink.setType(LinkType.ALL);
        return distLink;
    }

    public DistrelecCMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final DistrelecCMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public DistManufacturerFacade getDistManufacturerFacade() {
        return distManufacturerFacade;
    }

    public void setDistManufacturerFacade(final DistManufacturerFacade distManufacturerFacade) {
        this.distManufacturerFacade = distManufacturerFacade;
    }

    public DistCategoryFacade getDistCategoryFacade() {
        return distCategoryFacade;
    }

    public void setDistCategoryFacade(final DistCategoryFacade distCategoryFacade) {
        this.distCategoryFacade = distCategoryFacade;
    }
}
