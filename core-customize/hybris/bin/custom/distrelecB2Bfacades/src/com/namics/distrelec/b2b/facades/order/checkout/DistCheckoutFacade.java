/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.checkout;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;

import de.hybris.platform.b2bacceleratorfacades.order.B2BCheckoutFacade;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;

@SuppressWarnings("deprecation")
public interface DistCheckoutFacade extends B2BCheckoutFacade {

    CountryData getCountryForIsocode(String countryIso);

    boolean setDeliveryAddress(final AddressData addressData, final boolean forceRecalculate);

    boolean removeDeliveryMode(final boolean forceRecalculate);

    boolean setDeliveryMode(String deliveryModeCode, final boolean forceRecalculate);

    boolean setPaymentAddressIfAvailable();

    boolean setPaymentAddress(AddressData address);

    DeliveryModeData getDeliveryModeForCode(String code);

    String getPickupDeliveryModeCode();

    List<String> getStandardDeliveryModeCodes();

    List<CountryData> getDeliveryCountriesAndRegions();

    RegionData getRegionForIsocode(final String countryIso, final String regionIso);

    List<AddressData> getShippingAddresses();

    List<AddressData> getBillingAddresses();

    DistPaymentModeData getPaymentModeForCode(final String paymentModeCode);

    DistPaymentModeData getCreditCardPaymentMode();

    boolean setPaymentMode(final DistPaymentModeData paymentModeData);

    void setPickupLocation(String code);

    void calculateOrder(final boolean simulate) throws CalculationException;

    CustomerData getCurrentCheckoutCustomer();

    boolean isAnonymousCheckout();

    void finishAnonymousCheckout();

    void setRicevutaBancaria(final String bankCollectionCAB, final String bankCollectionABI, final String bankCollectionInstitute);

    String setOrderNote(String note);

    String setCostCenter(String costCenter);

    String setProjectNumber(String projectNumber);

    String getParameterizedPaymentUrl(final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent);

    Map<String, String> getPaymentParameters(final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent);

    Map<String, String> handlePaymentNotify(final String requestBody, final String currencyCode);

    String handlePaymentSuccessFailure(final Map<String, String> parameters) throws InvalidCartException;

    boolean modifyPaymentModesIfBudgetApplied();

    void removePaymentInfo();

    boolean isCancellationPolicyConfirmationRequired();

    void requestDeliveryDate(final Date deliveryDate);

    boolean isRequestedDeliveryDateEnabledForCurrentCart();

    Date getMinimumRequestedDeliveryDateForCurrentCart();

    Date getMaximumRequestedDeliveryDateForCurrentCart();

    List<CountryData> getRegisterCountries(CMSSiteModel cmsSite);

    boolean isCompleteDeliveryPossible();

    Date fetchNextWorkingDayIfWeekend(Date date);

    boolean isErrorHandled(String paymentErrorDescription);

    OrderData placeOrderForGivenCart(final CartModel cartModel) throws InvalidCartException;

    boolean isValidFastCheckout();

    String getDefaultDeliveryMode();

    boolean hasUnallowedBackorders();

    void setCompleteDeliveryOnCart(boolean completeDelivery);

    void saveCustomerVatDetails(String vat4, String legalEmail);

    void saveOrderCodiceDetails(String codiceCUP, String codiceCIG);

    void saveReevooEligibleFlag(final Boolean isReevooEligible);

    boolean isCurrentCustomerBlocked();

    boolean isUserAddressBlocked(String postalCode, String streetName, String streetNumber, String city, String countryIso);

    boolean isAddressValid(AddressData addressData);

    boolean isGuestCheckoutEnabledForSite();

    List<CountryData> getDeliveryCountriesForGuestCheckout(SiteChannel siteChannel);

    Date calculateScheduledDeliveryDate(String date, String dateFormat) throws ParseException;

    boolean isScheduleDeliveryAllowedForCurrentCart();

    boolean isPickupAvailableForCart(CartData cartData);

    boolean isPickupAllowedForCart(CartData cartData);

    boolean isPickupAllowedForCurrentCart();

    boolean isScheduleDeliveryAllowed(CartData cartData);

    boolean isNotLimitedUserType();

    boolean areDeliveryModeAndAddressesSet();

    boolean isBillingAddressShippable(AddressData addressData);

    boolean shouldDisplayCreditWarningForExportShop(CartData cartData);

    boolean shouldDisplayVatWarningForExportShop(CustomerData customer);

    boolean shouldAllowEditBilling(CustomerType customerType);

    boolean isCreditCardPaymentAllowed(CartData cartData);

    boolean isCodiceDestinatarioShown(CustomerData customer);

    boolean isSupportedExportCountry(String countryIsoCode);

    boolean isDeliveryModePriceVisible();

    void checkForPurchaseBlockedProducts();

    CCPaymentInfoData getPaymentInfo();

    List<DeliveryModeData> getValidDeliveryModesForCurrentCart(boolean isPickupAllowed);

    boolean isDeliveryAddressNotSetForCurrentCart();

    boolean isMinimumOrderValueNotReached(CartModel cart);

    void updateGuestAddress(AddressData addressData) throws CalculationException;

    void createGuestAddress(AddressData addressData);
}
