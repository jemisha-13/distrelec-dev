package com.namics.distrelec.b2b.facades.storesession.impl;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.COMMA_COUNTRIES;
import static com.namics.distrelec.b2b.core.constants.GeneratedNamb2bacceleratorCoreConstants.Enumerations.DistErpSystem.SAP;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Environment;
import com.namics.distrelec.b2b.core.enums.DistSalesOrgEnum;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.version.GitVersionService;
import com.namics.distrelec.b2b.facades.constants.Namb2bacceleratorFacadesConstants;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ChannelData;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.occ.core.basesite.data.*;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

public class DefaultDistrelecStoreSessionFacade extends DefaultNamicsStoreSessionFacade implements DistrelecStoreSessionFacade {

    private static final String ITEMS_PER_PAGE = "itemsPerPage";

    private static final String ALL_OTHER_COUNTRIES = "EX";

    private static final String SITE_OTHER_COUNTRIES = "otherCountries";

    private static final String SITE_INT_SHOP = "EX";

    private static final String COUNTRY = "country";

    private static final String CHANNELS = "channels";

    private static final String CURRENCIES = "currencies";

    private static final String LANGUAGES = "languages";

    private static final String SITE_PREFIX = "distrelec_%s";

    private static final String EXPORT_SHOP = "distrelec_EX";

    private static final String CONFIG_FF_RECO_URL = "factfinder.json.reco.url";

    private static final String CONFIG_FF_SUGGEST_URL = "factfinder.json.suggest.url";

    private static final String INTERNATIONAL_SITE_UID = "distrelec";

    private static final String EN_LANG_ISOCODE = "en";

    private static final String CHANNEL_NOT_FOUND_MSG = "No supported channel found for the current site.";

    private static final List<String> EXPORT_COUNTRIES_WITHOUT_B2C_CHANNEL = List.of("GB", "XI");

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistrelecStoreSessionFacade.class);

    @Autowired
    @Qualifier("salesOrgConverter")
    private Converter<DistSalesOrgModel, SalesOrgData> salesOrgConverter;

    @Autowired
    @Qualifier("channelConverter")
    private Converter<SiteChannel, ChannelData> channelConverter;

    @Autowired
    @Qualifier("distBaseStoreConverter")
    private Converter<BaseStoreModel, BaseStoreData> baseStoreConverter;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private DistrelecCMSSiteService siteService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FactFinderChannelService factFinderChannelService;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    private DistCommerceCommonI18NService distCommerceCommonI18NService;

    @Autowired
    private GitVersionService gitVersionService;

    @Override
    public Locale getCurrentLocale() {
        return getCommonI18NService().getLocaleForLanguage(getCommonI18NService().getCurrentLanguage());
    }

    @Override
    public SalesOrgData getCurrentSalesOrg() {
        DistSalesOrgModel salesOrgModel = distSalesOrgService.getCurrentSalesOrg();
        return salesOrgModel != null ? salesOrgConverter.convert(salesOrgModel) : null;
    }

    @Override
    public ChannelData getCurrentChannel() {
        SiteChannel currentChannel = getBaseStoreService().getCurrentChannel(baseSiteService.getCurrentBaseSite());
        ChannelData channelData = new ChannelData();
        channelData.setNet(getBaseStoreService().getCurrentBaseStore().isNet());
        channelConverter.convert(Objects.requireNonNullElse(currentChannel, SiteChannel.B2B), channelData);
        return channelData;
    }

    private List<BaseSiteModel> getAllBaseSitesForSelection() {
        return baseSiteService.getAllBaseSites().stream()
                              .map(CMSSiteModel.class::cast)
                              .filter(cmsSite -> !cmsSite.getUid().equalsIgnoreCase(EXPORT_SHOP)
                                      && cmsSite.getRedirectURL() == null
                                      && !cmsSite.isHidden())
                              .collect(Collectors.toList());
    }

    @Override
    public Collection<Map<String, Map<String, String>>> getAllSiteSettings() {
        List<Map<String, Map<String, String>>> settings = new ArrayList<>();
        Map<String, String> channels = null;
        Map<String, String> exportCurrencies = new HashMap<>();

        CMSSiteModel currentSite = (CMSSiteModel) baseSiteService.getCurrentBaseSite();
        List<CountryModel> extraCountries = getSiteSettingsExtraCountriesModels();
        List<CMSSiteModel> sites = getSites(currentSite);

        for (CMSSiteModel cmsSite : sites) {
            Set<LanguageModel> languagesSet = new LinkedHashSet<>();
            Set<CurrencyModel> currencySet = new LinkedHashSet<>();
            Collection<CountryModel> deliveryCountries = new ArrayList<>();
            Collection<CountryModel> registerCountries = new ArrayList<>();
            channels = new HashMap<>();

            if (StringUtils.isNotEmpty(cmsSite.getRedirectURL()) && cmsSite.getCountry() != null) {
                deliveryCountries.add(cmsSite.getCountry());

            } else {
                for (BaseStoreModel baseStore : cmsSite.getStores()) {
                    if (!baseStore.isNet() && isChannelSwitchAllowed(SiteChannel.B2C)) {
                        channels.put(SiteChannel.B2C.getCode(), baseStore.getName());
                    } else if (baseStore.isNet() && isChannelSwitchAllowed(SiteChannel.B2B)) {
                        channels.put(SiteChannel.B2B.getCode(), baseStore.getName());
                    }
                    languagesSet = getLanguages(baseStore, cmsSite);
                    currencySet = baseStore.getCurrencies();
                    deliveryCountries = baseStore.getDeliveryCountries();
                    registerCountries = baseStore.getRegisterCountries();
                }
            }
            populateExportCurrencies(cmsSite, currencySet, exportCurrencies);
            Set<CountryModel> allCountries = getAllCountries(deliveryCountries, extraCountries, registerCountries, cmsSite);
            for (CountryModel country : allCountries) {
                Map<String, String> countryChannels = new HashMap<>(channels);
                if (EXPORT_SHOP.equalsIgnoreCase(cmsSite.getUid())
                        && EXPORT_COUNTRIES_WITHOUT_B2C_CHANNEL.contains(country.getIsocode())) {
                    countryChannels.remove(SiteChannel.B2C.getCode());
                }
                settings.add(createSiteMap(countryChannels, languagesSet, currencySet, country));
            }
        }
        sortCountries(settings);
        populateStaticSites(currentSite, settings, channels, exportCurrencies);
        return settings;
    }

    private Set<CountryModel> getAllCountries(Collection<CountryModel> deliveryCountries,
                                              List<CountryModel> extraCountries,
                                              Collection<CountryModel> registerCountries,
                                              CMSSiteModel cmsSite) {
        return Stream.concat(deliveryCountries.stream(), extraCountries.stream())
                     .filter(c -> isFalse(cmsSite.getUid().equalsIgnoreCase(EXPORT_SHOP)
                             && !extraCountries.contains(c)
                             && isNotEmpty(registerCountries)
                             && !registerCountries.contains(c)))
                     .collect(Collectors.toSet());
    }

    private void populateExportCurrencies(CMSSiteModel cmsSite, Set<CurrencyModel> currencySet, Map<String, String> exportCurrencies) {
        if (EXPORT_SHOP.equals(cmsSite.getUid())) {
            exportCurrencies.putAll(currencySet.stream()
                                               .collect(toMap(currency -> currency.getIsocode().toUpperCase(), CurrencyModel::getName)));
        }
    }

    private Map<String, Map<String, String>> createSiteMap(Map<String, String> channels,
                                                           Set<LanguageModel> languagesSet,
                                                           Set<CurrencyModel> currencySet,
                                                           CountryModel country) {
        Map<String, Map<String, String>> site = new HashMap<>();
        site.put(CHANNELS, channels);
        site.put(LANGUAGES, languagesSet.stream()
                                        .collect(toMap(lang -> lang.getIsocode().toLowerCase(), LanguageModel::getName)));
        site.put(CURRENCIES, currencySet.stream()
                                        .collect(toMap(curr -> curr.getIsocode().toLowerCase(), CurrencyModel::getName)));
        site.put(COUNTRY, singletonMap(country.getIsocode(), getCountryName(country, languagesSet)));
        return site;
    }

    private Set<LanguageModel> getLanguages(BaseStoreModel baseStore, CMSSiteModel cmsSite) {
        boolean isInternalUser = getSessionService().getAttribute(Environment.INTERNAL_USER) != null;
        Set<LanguageModel> languages = new HashSet<>(baseStore.getLanguages());
        if (INTERNATIONAL_SITE_UID.equals(cmsSite.getUid())) {
            return languages.stream()
                            .filter(lang -> EN_LANG_ISOCODE.equals(lang.getIsocode()))
                            .collect(Collectors.toSet());
        }

        if (isInternalUser) {
            languages.addAll(baseStore.getUnpublishedLanguages());
        }
        return languages;
    }

    private List<CountryModel> getSiteSettingsExtraCountriesModels() {
        return getSiteSettingsExtraCountries().stream()
                                              .filter(StringUtils::isNotBlank)
                                              .map(getCommonI18NService()::getCountry)
                                              .collect(Collectors.toList());
    }

    private String getCountryName(CountryModel country, Set<LanguageModel> languagesSet) {
        Locale english = new Locale(EN_LANG_ISOCODE);
        String countryNameEnglish = country.getName(english);
        StringBuilder countryName = new StringBuilder(countryNameEnglish);
        for (LanguageModel language : languagesSet) {
            Locale locale = LocaleUtils.toLocale(language.getIsocode());
            if (!language.getIsocode().equalsIgnoreCase(EN_LANG_ISOCODE) && !StringUtils.equals(country.getName(locale), countryNameEnglish)) {
                countryName.append(" / ").append(country.getName(locale));
            }
        }
        return countryName.toString();
    }

    private List<CMSSiteModel> getSites(CMSSiteModel currentSite) {
        List<BaseSiteModel> baseSites = currentSite.getUid().equalsIgnoreCase(EXPORT_SHOP) ? singletonList(currentSite) : getAllBaseSitesForSelection();
        return baseSites.stream()
                        .map(CMSSiteModel.class::cast)
                        .filter(cmsSite -> isFalse(currentSite.getSalesOrg() != null
                                && cmsSite.getSalesOrg() != null
                                && !currentSite.getSalesOrg().getBrand().equals(cmsSite.getSalesOrg().getBrand())))
                        .collect(Collectors.toList());
    }

    private void populateStaticSites(CMSSiteModel currentSite,
                                     List<Map<String, Map<String, String>>> settings,
                                     Map<String, String> channels,
                                     Map<String, String> exportCurrencies) {
        if (currentSite.getUid().equalsIgnoreCase(EXPORT_SHOP)) {
            addStaticSite(settings, SITE_INT_SHOP, "International Shop", channels, exportCurrencies);
        }
        addStaticSite(settings, SITE_OTHER_COUNTRIES, "Other countries", channels, exportCurrencies);
    }

    private void sortCountries(List<Map<String, Map<String, String>>> settings) {
        settings.sort((countryOneMap, countryTwoMap) -> {
            try {
                final String countryOne = countryOneMap.get(COUNTRY).get(countryOneMap.get(COUNTRY).keySet().iterator().next());
                final String countryTwo = countryTwoMap.get(COUNTRY).get(countryTwoMap.get(COUNTRY).keySet().iterator().next());
                if (countryOne == null || countryTwo == null) {
                    return 0;
                }
                // Ensure ALL_OTHER_COUNTRIES is last element
                final String countryOneIsocode = countryOneMap.get(COUNTRY).keySet().iterator().next();
                final String countryTwoIsocode = countryTwoMap.get(COUNTRY).keySet().iterator().next();
                if (countryOneIsocode.equals(ALL_OTHER_COUNTRIES)) {
                    return 1;
                } else if (countryTwoIsocode.equals(ALL_OTHER_COUNTRIES)) {
                    return -1;
                }
                return Normalizer.normalize(countryOne, Normalizer.Form.NFD).compareTo(Normalizer.normalize(countryTwo, Normalizer.Form.NFD));
            } catch (final NullPointerException e) {
                return 0;
            }
        });
    }

    @Override
    public Set<String> getSiteSettingsExtraCountries() {
        final Configuration config = configurationService.getConfiguration();
        final String[] extraCountries = config.getStringArray("sitesettings." + getCmsSiteService().getCurrentSite().getUid() + ".extraCountries");
        return new HashSet<>(Arrays.asList(extraCountries));
    }

    private void addStaticSite(List<Map<String, Map<String, String>>> settings,
                               String key,
                               String value,
                               Map<String, String> channels,
                               Map<String, String> currencies) {
        Map<String, Map<String, String>> site = new HashMap<>();
        site.put(CHANNELS, key.equalsIgnoreCase(SITE_INT_SHOP) ? channels : new HashMap<>());
        site.put(LANGUAGES, key.equalsIgnoreCase(SITE_INT_SHOP) ? singletonMap(EN_LANG_ISOCODE, "English") : new HashMap<>());
        site.put(CURRENCIES, currencies);
        site.put(COUNTRY, singletonMap(key, value));
        settings.add(site);
    }

    @Override
    public boolean isChannelSwitchAllowed(final SiteChannel channel) {
        final UserModel currentUser = getUserService().getCurrentUser();
        final boolean isAnonymousUser = getUserService().isAnonymousUser(currentUser);
        if (isAnonymousUser) {
            return true;
        }

        final B2BCustomerModel currentCustomer = (currentUser instanceof B2BCustomerModel) ? (B2BCustomerModel) currentUser : null;
        if (SiteChannel.B2B.equals(channel)) {
            return currentCustomer != null
                    && (currentCustomer.getCustomerType().equals(CustomerType.B2B)
                            || currentCustomer.getCustomerType().equals(CustomerType.B2B_KEY_ACCOUNT)
                            || currentCustomer.getCustomerType().equals(CustomerType.B2E));
        } else if (SiteChannel.B2C.equals(channel)) {
            return currentCustomer != null
                    && currentCustomer.getCustomerType().equals(CustomerType.B2C);
        }
        return false;
    }

    @Override
    public void setCurrentChannel(final String channel) {
        // get the channels (stores) of the site
        final Collection<BaseStoreModel> stores = getCmsSiteService().getCurrentSite().getStores();
        Assert.notEmpty(stores, CHANNEL_NOT_FOUND_MSG);
        SiteChannel currentChannel = null;
        for (final BaseStoreModel store : stores) {
            if (store.getChannel().equals(SiteChannel.valueOf(StringUtils.upperCase(channel)))) {
                currentChannel = store.getChannel();
                break;
            }
        }
        Assert.notNull(channel, "Channel to set is not supported.");

        getSessionService().setAttribute(Namb2bacceleratorFacadesConstants.CHANNEL_SESSION_ATTR_KEY, currentChannel);
    }

    @Override
    public void initializeSession(final List<Locale> preferredLocales) {
        super.initializeSession(preferredLocales);
        initializeSessionChannel();
        initializeSessionCountry();
    }

    protected void initializeSessionChannel() {
        final ChannelData defaultChannel = getDefaultChannel();
        if (defaultChannel != null) {
            setCurrentChannel(defaultChannel.getType());
        } else {
            // No default channel, just use the first channel
            final Collection<ChannelData> storeChannels = getAllChannels();
            if (storeChannels != null && !storeChannels.isEmpty()) {
                setCurrentChannel(storeChannels.iterator().next().getType());
            }
        }
    }

    protected void initializeSessionCountry() {
        final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
        final Collection<BaseStoreModel> stores = currentSite.getStores();
        Assert.notEmpty(stores, CHANNEL_NOT_FOUND_MSG);

        final boolean isExportSalesOrg = DistSalesOrgEnum.EXPORT.getCode().equals(currentSite.getSalesOrg().getCode());
        final boolean isExportSite = EXPORT_SHOP.equals(currentSite.getUid());
        if (!isExportSalesOrg || !isExportSite) {
            for (final BaseStoreModel store : stores) {
                if (store.getChannel().equals(currentSite.getChannel())) {
                    final Collection<CountryModel> deliveryCountries = store.getDeliveryCountries();
                    setCurrentCountry(deliveryCountries.iterator().next().getIsocode());
                }
            }
        } else {
            setCurrentCountry(SITE_INT_SHOP);
        }
    }

    @Override
    public Collection<ChannelData> getAllChannels() {
        final Collection<BaseStoreModel> stores = getCmsSiteService().getCurrentSite().getStores();
        Assert.notEmpty(stores, CHANNEL_NOT_FOUND_MSG);
        final Collection<ChannelData> channels = new ArrayList<>();
        for (final BaseStoreModel store : stores) {
            final ChannelData channelData = new ChannelData();
            channelData.setNet(store.isNet());
            channels.add(channelConverter.convert(store.getChannel(), channelData));
        }
        Assert.notNull(channels, "no channels found.");
        return channels;
    }

    protected ChannelData getDefaultChannel() {
        final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
        final Collection<BaseStoreModel> stores = currentSite.getStores();
        Assert.notEmpty(stores, CHANNEL_NOT_FOUND_MSG);
        SiteChannel defaultChannel = null;
        ChannelData channelData = null;
        for (final BaseStoreModel store : stores) {
            if (store.getChannel().equals(currentSite.getChannel())) {
                defaultChannel = store.getChannel();
                channelData = new ChannelData();
                channelData.setNet(store.isNet());
                return channelConverter.convert(defaultChannel, channelData);
            }
        }
        Assert.notNull(defaultChannel, "Channel to set is not supported.");
        return channelData;
    }

    @Override
    public Boolean isUseTechnicalView() {
        if (getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.TECHNICAL_VIEW_SESSION_ATTR_KEY) == null) {
            setUseTechnicalView(Boolean.TRUE);
        }

        return getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.TECHNICAL_VIEW_SESSION_ATTR_KEY);
    }

    @Override
    public void setUseTechnicalView(final Boolean useTechnicalView) {
        getSessionService().setAttribute(Namb2bacceleratorFacadesConstants.TECHNICAL_VIEW_SESSION_ATTR_KEY, useTechnicalView);
    }

    @Override
    public Boolean isUseIconView() {
        if (getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.ICON_VIEW_SESSION_ATTR_KEY) == null) {
            setUseIconView(Boolean.FALSE);
        }

        return getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.ICON_VIEW_SESSION_ATTR_KEY);
    }

    @Override
    public void setItemsperPage(final String itemsPerPage) {
        getSessionService().setAttribute(ITEMS_PER_PAGE, itemsPerPage);
    }

    @Override
    public String getItemsperpage() {
        if (getSessionService().getAttribute(ITEMS_PER_PAGE) == null) {
            setItemsperPage("10"); // Default
        }
        return getSessionService().getAttribute(ITEMS_PER_PAGE);
    }

    @Override
    public void setUseIconView(final Boolean useIconView) {
        getSessionService().setAttribute(Namb2bacceleratorFacadesConstants.ICON_VIEW_SESSION_ATTR_KEY, useIconView);
    }

    @Override
    public Boolean isUseListView() {
        if (getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.LIST_VIEW_SESSION_ATTR_KEY) == null) {
            setUseListView(Boolean.FALSE);
        }

        return getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.LIST_VIEW_SESSION_ATTR_KEY);
    }

    @Override
    public void setUseDetailView(final Boolean useDetailView) {
        getSessionService().setAttribute(Namb2bacceleratorFacadesConstants.PLP_USE_DETAIL_VIEW, useDetailView);

    }

    @Override
    public Boolean isUseDetailView() {
        if (getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.PLP_USE_DETAIL_VIEW) == null) {
            setUseListView(Boolean.TRUE);
        }

        return getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.PLP_USE_DETAIL_VIEW);
    }

    @Override
    public void setUseListView(final Boolean useListView) {
        getSessionService().setAttribute(Namb2bacceleratorFacadesConstants.LIST_VIEW_SESSION_ATTR_KEY, useListView);
    }

    @Override
    public Boolean isAutoApplyFilter() {
        if (getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.AUTO_APPLY_FILTER_SESSION_ATTR_KEY) == null) {
            setAutoApplyFilter(Boolean.TRUE);
        }

        return getSessionService().getAttribute(Namb2bacceleratorFacadesConstants.AUTO_APPLY_FILTER_SESSION_ATTR_KEY);
    }

    @Override
    public void setAutoApplyFilter(final Boolean autoApplyFilter) {
        getSessionService().setAttribute(Namb2bacceleratorFacadesConstants.AUTO_APPLY_FILTER_SESSION_ATTR_KEY, autoApplyFilter);
    }

    @Override
    public Map<String, String> getPageViewPerCategoryMap() {
        SessionAttributeWrapper<HashMap<String, String>> wrapper = getSessionService()
                                                                                      .getAttribute(Namb2bacceleratorFacadesConstants.PAGE_VIEW_PER_CATEGORY_MAP);
        if (wrapper == null) {
            wrapper = new SessionAttributeWrapper(new HashMap<String, String>());
            getSessionService().setAttribute(Namb2bacceleratorFacadesConstants.PAGE_VIEW_PER_CATEGORY_MAP, wrapper);
        }

        return wrapper.getElement();
    }

    @Override
    public LanguageData getCurrentLanguage() {
        return getLanguageConverter().convert(getLanguageModel());
    }

    @Override
    public String getCurrentLanguageIsoCode() {
        return getLanguageModel().getIsocode();
    }

    private LanguageModel getLanguageModel() {
        LanguageModel currentLanguage = getCommonI18NService().getCurrentLanguage();
        return distCommerceCommonI18NService.getBaseLanguage(currentLanguage);
    }

    @Override
    public BaseStoreData getCurrentBaseStore() {
        return baseStoreConverter.convert(getBaseStoreService().getCurrentBaseStore());
    }

    @Override
    public DistrelecSiteSettingsDataList getAllSiteSettingsForOCC() {
        DistrelecSiteSettingsDataList distrelecSiteSettingsDataList = new DistrelecSiteSettingsDataList();
        distrelecSiteSettingsDataList.setSettings(this.getAllSiteSettings().stream()
                                                      .map(this::convertMapToSiteSettingsData)
                                                      .collect(Collectors.toList()));
        distrelecSiteSettingsDataList.setFactFinderSearchExpose(getFactFinderSearchExposeData());
        distrelecSiteSettingsDataList.setFusionSearchExpose(getFusionSearchExposeData());
        distrelecSiteSettingsDataList.setSalesOrg(getCurrentSalesOrg());
        distrelecSiteSettingsDataList.setDecimalCommaCountries(Stream.of(Objects.requireNonNullElse(configurationService.getConfiguration()
                                                                                                                        .getString(COMMA_COUNTRIES),
                                                                                                    EMPTY)
                                                                                .split(","))
                                                                     .collect(Collectors.toList()));
        distrelecSiteSettingsDataList.setYmktTrackingEnabled(Boolean.FALSE);
        distrelecSiteSettingsDataList.setGitInfo(getGitInfoData());
        return distrelecSiteSettingsDataList;
    }

    private DistrelecSiteSettingData convertMapToSiteSettingsData(Map<String, Map<String, String>> map) {
        DistrelecSiteSettingData siteSettingsData = new DistrelecSiteSettingData();
        map.entrySet().forEach(entry -> populateSiteSettingsData(siteSettingsData, entry));
        return siteSettingsData;
    }

    private void populateSiteSettingsData(DistrelecSiteSettingData siteSettingsData, Map.Entry<String, Map<String, String>> entry) {
        if (entry.getKey().equals(COUNTRY)) {
            siteSettingsData.setCountry(entry.getValue());
            Set<String> countrySet = entry.getValue().keySet();
            String isoCode = countrySet.iterator().next();

            if (SITE_OTHER_COUNTRIES.equals(isoCode)) {
                siteSettingsData.setDomain(configurationService.getConfiguration().getString("other.countries.site.navigation.link"));
            } else {
                BaseSiteModel baseSite = getBaseSite(isoCode);
                if (baseSite != null) {
                    siteSettingsData.setMediaDomain(distSiteBaseUrlResolutionService.getMediaUrlForSite(baseSite, true, null));
                    siteSettingsData.setDomain(distSiteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, null));
                    siteSettingsData.setStorefrontDomain(distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(baseSite, true, null));
                }
            }
        }
        if (entry.getKey().equals(CHANNELS)) {
            siteSettingsData.setChannels(entry.getValue());
        }
        if (entry.getKey().equals(LANGUAGES)) {
            siteSettingsData.setLanguages(entry.getValue());
        }
        if (entry.getKey().equals(CURRENCIES)) {
            siteSettingsData.setCurrencies(entry.getValue());
        }
    }

    private BaseSiteModel getBaseSite(String isoCode) {
        BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(String.format(SITE_PREFIX, isoCode));
        if (baseSite == null) {
            try {
                baseSite = siteService.getSiteForCountryAndBrand(isoCode, getCurrentSalesOrg().getBrand());
            } catch (final CMSItemNotFoundException e) {
                LOG.warn("Unable to resolve base site for country: " + isoCode, e);
            }
        }
        return baseSite;
    }

    private DistFactFinderSearchExposeData getFactFinderSearchExposeData() {
        DistFactFinderSearchExposeData distFactFinderSearchExposeData = new DistFactFinderSearchExposeData();
        distFactFinderSearchExposeData.setFfsearchChannel(factFinderChannelService.getCurrentFactFinderChannel());

        Configuration config = configurationService.getConfiguration();
        distFactFinderSearchExposeData.setFfrecoUrl(config.getString(CONFIG_FF_RECO_URL));
        distFactFinderSearchExposeData.setFfsuggestUrl(config.getString(CONFIG_FF_SUGGEST_URL));

        return distFactFinderSearchExposeData;
    }

    private DistFusionSearchExposeData getFusionSearchExposeData() {
        DistFusionSearchExposeData fusionSearchExposeData = new DistFusionSearchExposeData();
        Configuration config = configurationService.getConfiguration();
        fusionSearchExposeData.setFusionBaseUrl(config.getString(DistConstants.Fusion.FUSION_SEARCH_URL));
        fusionSearchExposeData.setFusionSearchAPIKey(config.getString(DistConstants.Fusion.FUSION_SEARCH_API_KEY));
        fusionSearchExposeData.setFusionProfileSuffix(config.getString(DistConstants.Fusion.FUSION_PROFILE_SUFFIX));
        return fusionSearchExposeData;
    }

    private DistGitInfoData getGitInfoData() {
        DistGitInfoData gitInfoData = new DistGitInfoData();
        gitInfoData.setGitVersion(gitVersionService.getGitVersion());
        gitInfoData.setRevision(gitVersionService.getRevision());
        return gitInfoData;
    }

    @Override
    public boolean isCurrentShopExport() {
        SalesOrgData salesOrg = getCurrentSalesOrg();
        return salesOrg != null && StringUtils.equalsIgnoreCase(DistSalesOrgEnum.EXPORT.getCode(), salesOrg.getCode());
    }

    @Override
    public boolean isCurrentShopElfa() {
        SalesOrgData salesOrgData = getCurrentSalesOrg();
        return salesOrgData != null
                && StringUtils.equalsIgnoreCase(salesOrgData.getErpSystem(), SAP)
                && !StringUtils.equals(salesOrgData.getCode(), DistSalesOrgEnum.GERMANY.getCode());
    }

    @Override
    public boolean isCurrentShopItaly() {
        SalesOrgData salesOrg = getCurrentSalesOrg();
        return salesOrg != null
                && StringUtils.equalsIgnoreCase(DistSalesOrgEnum.ITALY.getCode(), salesOrg.getCode());
    }

    @Override
    public boolean areQuotationsEnabledForCurrentBaseStore() {
        final BaseStoreModel store = getBaseStoreService().getCurrentBaseStore();
        return (nonNull(store) && BooleanUtils.isTrue(store.getQuotationsEnabled())) || getUserService().isCurrentCustomerErpSelected();
    }

    @Override
    protected DistrelecBaseStoreService getBaseStoreService() {
        return (DistrelecBaseStoreService) super.getBaseStoreService();
    }

}
