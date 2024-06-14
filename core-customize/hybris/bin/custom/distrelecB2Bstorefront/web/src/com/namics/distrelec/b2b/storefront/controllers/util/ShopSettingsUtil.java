/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.util;

import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for reading and writing shop settings cookie.
 *
 * @author daehusir, Distrelec
 * @since Distrelec 1.1
 */
public class ShopSettingsUtil {

    private static final Logger LOG = Logger.getLogger(ShopSettingsUtil.class);

    private static final String SETTINGS_DELIMITER = "|";
    private static final String SPLIT_SETTINGS_DELIMITER = "\\|";
    private static final String KEY_VALUE_DELIMITER = ":";

    public static ShoppingSettingsCookieData readShopSettingsCookie(final String shopSettingsString) {
        if (shopSettingsString != null) {
            final Map<ShopSettingEnum, String> settings = getShopSettingsFromCoockie(shopSettingsString);
            final ShoppingSettingsCookieData shoppingSettingsCookieData = new ShoppingSettingsCookieData();
            shoppingSettingsCookieData.setChannel(getShopSettingsCookieValue(settings, ShopSettingEnum.CHANNEL));
            shoppingSettingsCookieData.setLanguage(getShopSettingsCookieValue(settings, ShopSettingEnum.LANGUAGE));
            shoppingSettingsCookieData.setCountry(getShopSettingsCookieValue(settings, ShopSettingEnum.COUNTRY));
            shoppingSettingsCookieData.setItemsPerPage(getShopSettingsCookieValue(settings, ShopSettingEnum.ITEMS_PER_PAGE));
            shoppingSettingsCookieData
                    .setCookieMessageConfirmed(BooleanUtils.toBooleanObject(getShopSettingsCookieValue(settings, ShopSettingEnum.COOKIE_MESSAGE_CONFIRMED)));

            shoppingSettingsCookieData.setUseIconView(BooleanUtils.toBooleanObject(getShopSettingsCookieValue(settings, ShopSettingEnum.USE_ICON_VIEW)));

            shoppingSettingsCookieData.setUseListView(BooleanUtils.toBooleanObject(getShopSettingsCookieValue(settings, ShopSettingEnum.USE_LIST_VIEW)));
            shoppingSettingsCookieData.setUseDetailView(BooleanUtils.toBooleanObject(getShopSettingsCookieValue(settings, ShopSettingEnum.USE_DETAIL_VIEW)));
            return shoppingSettingsCookieData;
        }
        return null;
    }

    private static Map<ShopSettingEnum, String> getShopSettingsFromCoockie(final String shopSettingsString) {
        final Map<ShopSettingEnum, String> shopSettings = new HashMap<>();
        final String[] settingsArray = shopSettingsString.split(SPLIT_SETTINGS_DELIMITER);
        List<String> settings = Arrays.asList(settingsArray);
        if (CollectionUtils.isNotEmpty(settings)) {
            for (final String setting : settings) {
                final String[] keyValuePair = setting.split(KEY_VALUE_DELIMITER);
                if (keyValuePair.length == 2) {
                    final ShopSettingEnum key = ShopSettingEnum.fromValue(keyValuePair[0]);
                    if (key != null) {
                        shopSettings.put(key, keyValuePair[1]);
                    }
                }
            }

        }
        return shopSettings;
    }

    private static String getShopSettingsCookieValue(final Map<ShopSettingEnum, String> settings, final ShopSettingEnum shopSetting) {
        return settings.get(shopSetting);
    }

    public static void updateItemsPerPageShopSettingsCookie(final HttpServletRequest request, final HttpServletResponse response, final String itemsPerPage) {
        // get settings from cookie if available
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        if (shopSettingsString != null) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            if (cookieData != null) {

                String channel = cookieData.getChannel();
                String language = cookieData.getLanguage();
                String country = cookieData.getCountry();
                channel = cookieData.getChannel();
                language = cookieData.getLanguage();
                country = cookieData.getCountry();
                final Boolean cookieMessageConfirmed = cookieData.getCookieMessageConfirmed();
                final Boolean useIconView = cookieData.getUseIconView();
                final Boolean useListView = cookieData.getUseListView();
                final Boolean autoApplyFilter = cookieData.getAutoApplyFilter();
                final Boolean useDetailView=cookieData.getUseDetailView();
                Attributes.SHOP_SETTINGS.setValue(request, response, ShopSettingsUtil.createCookieWithSessionValues(channel, language, country,
                        cookieMessageConfirmed, useIconView, useListView,useDetailView, autoApplyFilter, itemsPerPage));
            } else {
                LOG.error("Cannot parse shop settings cookie! Use current session values instead.");
            }

        }
    }

    public static String createCookieWithSessionValues(final String channel, final String language, final String country, final Boolean cookieMessageConfirmed,
            final Boolean useIconView, final Boolean useListView,final Boolean useDetailView, final Boolean autoApplyFilter, final String itemsPerPage) {
        final ShoppingSettingsCookieData cookie = new ShoppingSettingsCookieData();
        cookie.setChannel(channel);
        cookie.setLanguage(language);
        cookie.setCountry(country);
        cookie.setCookieMessageConfirmed(cookieMessageConfirmed);
        cookie.setUseIconView(useIconView);
        cookie.setUseListView(useListView);
        cookie.setAutoApplyFilter(autoApplyFilter);
        cookie.setItemsPerPage(itemsPerPage);
        cookie.setUseDetailView(useDetailView);
        return createShopSettingCoockieString(cookie);
    }

    private static String createShopSettingCoockieString(final ShoppingSettingsCookieData cookie) {
        final StringBuffer buffer = new StringBuffer(100);
        buffer.append(ShopSettingEnum.CHANNEL.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getChannel());
        buffer.append(SETTINGS_DELIMITER);
        buffer.append(ShopSettingEnum.LANGUAGE.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getLanguage());
        buffer.append(SETTINGS_DELIMITER);
        buffer.append(ShopSettingEnum.COUNTRY.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getCountry());
        buffer.append(SETTINGS_DELIMITER);
        buffer.append(ShopSettingEnum.COOKIE_MESSAGE_CONFIRMED.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getCookieMessageConfirmed() != null ? cookie.getCookieMessageConfirmed().toString() : "false");
        buffer.append(SETTINGS_DELIMITER);

        buffer.append(ShopSettingEnum.USE_ICON_VIEW.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getUseIconView() != null ? cookie.getUseIconView().toString() : "false");
        buffer.append(SETTINGS_DELIMITER);

        buffer.append(ShopSettingEnum.USE_LIST_VIEW.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getUseListView() != null ? cookie.getUseListView().toString() : "false");
        buffer.append(SETTINGS_DELIMITER);
        
        buffer.append(ShopSettingEnum.USE_DETAIL_VIEW.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getUseDetailView() != null ? cookie.getUseDetailView().toString() : "true");
        buffer.append(SETTINGS_DELIMITER);

        buffer.append(ShopSettingEnum.AUTO_APPLY_FILTER.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getAutoApplyFilter() != null ? cookie.getAutoApplyFilter().toString() : "false");
        buffer.append(SETTINGS_DELIMITER);

        buffer.append(ShopSettingEnum.ITEMS_PER_PAGE.getKey());
        buffer.append(KEY_VALUE_DELIMITER);
        buffer.append(cookie.getItemsPerPage() != null ? cookie.getItemsPerPage() : "10");

        return buffer.toString();
    }

    public static void updateIconView(final Boolean useIconView, final HttpServletRequest request, final HttpServletResponse response) {
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        if (shopSettingsString != null) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            if (cookieData != null) {
                final String channel = cookieData.getChannel();
                final String language = cookieData.getLanguage();
                final String country = cookieData.getCountry();
                final String itemsPerPage = cookieData.getItemsPerPage();
                final Boolean autoApplyFilter = cookieData.getAutoApplyFilter();
                final Boolean cookieMessageConfirmed = cookieData.getCookieMessageConfirmed();
                final Boolean currentUseIconView = cookieData.getUseIconView() != null ? cookieData.getUseIconView() : Boolean.TRUE;
                final Boolean currentUseListView = cookieData.getUseListView() != null ? cookieData.getUseListView() : Boolean.TRUE;
                final Boolean useDetailView=cookieData.getUseDetailView();
                Attributes.SHOP_SETTINGS.setValue(request, response,
                        ShopSettingsUtil.createCookieWithSessionValues(channel, language, country, cookieMessageConfirmed,
                                useIconView != null ? useIconView : currentUseIconView, currentUseListView,useDetailView, autoApplyFilter, itemsPerPage));
            } else {
                LOG.error("Cannot parse shop settings cookie! The technical view flag will not be set in the cookie.");
            }
        }
    }

    public static void updateListView(final Boolean useListView, final HttpServletRequest request, final HttpServletResponse response) {
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        if (shopSettingsString != null) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            if (cookieData != null) {
                final String channel = cookieData.getChannel();
                final String language = cookieData.getLanguage();
                final String country = cookieData.getCountry();
                final String itemsPerPage = cookieData.getItemsPerPage();
                final Boolean autoApplyFilter = cookieData.getAutoApplyFilter();
                final Boolean cookieMessageConfirmed = cookieData.getCookieMessageConfirmed();
                final Boolean currentUseIconView = cookieData.getUseIconView() != null ? cookieData.getUseIconView() : Boolean.TRUE;
                final Boolean currentUseListView = cookieData.getUseListView() != null ? cookieData.getUseListView() : Boolean.TRUE;
                final Boolean useDetailView=cookieData.getUseDetailView();
                Attributes.SHOP_SETTINGS.setValue(request, response,
                        ShopSettingsUtil.createCookieWithSessionValues(channel, language, country, cookieMessageConfirmed,
                                currentUseIconView, useListView != null ? useListView : currentUseListView,useDetailView, autoApplyFilter, itemsPerPage));
            } else {
                LOG.error("Cannot parse shop settings cookie! The technical view flag will not be set in the cookie.");
            }
        }
    }
    
    
    public static void updateDetailView(final Boolean useDetailView, final HttpServletRequest request, final HttpServletResponse response) {
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        if (shopSettingsString != null) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            if (cookieData != null) {
                final String channel = cookieData.getChannel();
                final String language = cookieData.getLanguage();
                final String country = cookieData.getCountry();
                final String itemsPerPage = cookieData.getItemsPerPage();
                final Boolean autoApplyFilter = cookieData.getAutoApplyFilter();
                final Boolean cookieMessageConfirmed = cookieData.getCookieMessageConfirmed();
                final Boolean currentUseIconView = cookieData.getUseIconView() != null ? cookieData.getUseIconView() : Boolean.TRUE;
                final Boolean currentUseListView = cookieData.getUseListView() != null ? cookieData.getUseListView() : Boolean.TRUE;
                final Boolean currentUseDetailView=cookieData.getUseDetailView();
                Attributes.SHOP_SETTINGS.setValue(request, response,
                        ShopSettingsUtil.createCookieWithSessionValues(channel, language, country, cookieMessageConfirmed,
                                currentUseIconView, currentUseListView,useDetailView !=null ? useDetailView :currentUseDetailView , autoApplyFilter, itemsPerPage));
            } else {
                LOG.error("Cannot parse shop settings cookie! The technical view flag will not be set in the cookie.");
            }
        }
    }
    
    public static void updateViews(final Boolean useTechnicalView, final Boolean useIconView, final Boolean useListView, Boolean useDetailView , final HttpServletRequest request,
            final HttpServletResponse response) {
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        if (shopSettingsString != null) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            if (cookieData != null) {
                final String channel = cookieData.getChannel();
                final String language = cookieData.getLanguage();
                final String country = cookieData.getCountry();
                final String itemsPerPage = cookieData.getItemsPerPage();
                final Boolean autoApplyFilter = cookieData.getAutoApplyFilter();
                final Boolean cookieMessageConfirmed = cookieData.getCookieMessageConfirmed();
                final Boolean currentUseIconView = cookieData.getUseIconView() != null ? cookieData.getUseIconView() : Boolean.TRUE;
                final Boolean currentUseListView = cookieData.getUseListView() != null ? cookieData.getUseListView() : Boolean.TRUE;
                final Boolean currentUseDetailView=cookieData.getUseDetailView();
                final String value = ShopSettingsUtil.createCookieWithSessionValues(channel, language, country, cookieMessageConfirmed, //
                        useIconView != null ? useIconView : currentUseIconView, //
                        useListView != null ? useListView : currentUseListView, //
                                useDetailView !=null ? useDetailView :currentUseDetailView,autoApplyFilter, itemsPerPage);

                Attributes.SHOP_SETTINGS.setValue(request, response, value);
            } else {
                LOG.error("Cannot parse shop settings cookie! The technical view flag will not be set in the cookie.");
            }
        }
    }

    public static void updateAutoApplyFilter(final Boolean autoApplyFilter, final HttpServletRequest request, final HttpServletResponse response) {
        final String shopSettingsString = Attributes.SHOP_SETTINGS.getValueFromCookies(request);
        if (shopSettingsString != null) {
            final ShoppingSettingsCookieData cookieData = ShopSettingsUtil.readShopSettingsCookie(shopSettingsString);
            if (cookieData != null) {
                final String channel = cookieData.getChannel();
                final String language = cookieData.getLanguage();
                final String country = cookieData.getCountry();
                final String itemsPerPage = cookieData.getItemsPerPage();
                final Boolean cookieMessageConfirmed = cookieData.getCookieMessageConfirmed();
                final Boolean currentUseIconView = cookieData.getUseIconView();
                final Boolean currentUseListView = cookieData.getUseListView();
                final Boolean useDetailView=cookieData.getUseDetailView();
                Attributes.SHOP_SETTINGS.setValue(request, response, ShopSettingsUtil.createCookieWithSessionValues(channel, language, country,
                        cookieMessageConfirmed, currentUseIconView, currentUseListView,useDetailView, autoApplyFilter, itemsPerPage));
            } else {
                LOG.error("Cannot parse shop settings cookie! The technical view flag will not be set in the cookie.");
            }
        }
    }

    public enum ShopSettingEnum {
        CHANNEL("channel"), LANGUAGE("language"), COUNTRY("country"), COOKIE_MESSAGE_CONFIRMED("cookieMessageConfirmed"), //
        USE_ICON_VIEW("useIconView"), USE_LIST_VIEW("useListView"),USE_DETAIL_VIEW("useDetailView"), AUTO_APPLY_FILTER(
                "autoApplyFilter"), ITEMS_PER_PAGE("itemsPerPage");

        private final String key;

        private ShopSettingEnum(final String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public static ShopSettingEnum fromValue(final String value) {
            for (final ShopSettingEnum setting : ShopSettingEnum.values()) {
                if (setting.getKey().equals(value)) {
                    return setting;
                }
            }

            return null;
        }
    }
}
