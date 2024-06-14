package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Shipping.METHOD_PICKUP;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants.SeoRobots.META_ROBOTS;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.http.ResponseEntity.ok;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;
import com.namics.distrelec.b2b.storefront.annotations.RequireHardLogin;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.AddressFormHelper;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.*;
import com.namics.distrelec.b2b.storefront.response.GlobalMessageResponse;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.order.exceptions.CalculationException;

@Controller
@RequestMapping("/checkout/delivery")
@RequireHardLogin
public class DistCheckoutDeliveryController extends AbstractDistCheckoutController {
    private static final Logger LOG = LoggerFactory.getLogger(DistCheckoutDeliveryController.class);

    private static final String CONF_VOUCHER_ERROR_KEY_PREFIX = "checkoutvoucherbox.voucherError.error";

    private static final String ERP_COMMUNICATION_ERROR = "Creating address failed because of an ERP communication error!";

    private static final String DELIVERY_MODES = "deliveryModes";

    private static final String DEFAULT_ERROR_MESSAGE = "support.failed";

    @Autowired
    private Populator<AddressData, B2CAddressForm> b2cBillingAddressFormPopulator;

    @Autowired
    private Populator<AddressData, B2BBillingAddressForm> b2bBillingAddressFormPopulator;

    @Autowired
    private Populator<AddressData, GuestAddressForm> guestAddressFormPopulator;

    @Resource(name = "distFastCheckoutUrls")
    private Set<String> distFastCheckoutUrls;

    @ModelAttribute("countries")
    public List<CountryData> getCountries() {
        if (getDistCheckoutFacade().isAnonymousCheckout() && isCurrentShopExport()) {
            return getDistCheckoutFacade().getDeliveryCountriesForGuestCheckout(SiteChannel.B2C);
        }
        return getDistCheckoutFacade().getDeliveryCountriesAndRegions();
    }

    @GetMapping
    public String checkoutDelivery(HttpServletRequest request, Model model, RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        String anonymousVoucherCode = getSessionService().getAttribute("anonymousVoucherCode");
        if (isNotBlank(anonymousVoucherCode)) {
            getB2bCartFacade().setVoucherCodeToRedeem(anonymousVoucherCode);
        }

        String redirectString = validateCheckout(model, redirectModel);
        if (isNotBlank(redirectString)) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + redirectString);
        }

        if (isNotBlank(anonymousVoucherCode)) {
            checkVoucherReturnCode(model);
        }

        if (isFastCheckoutUrl(request.getHeader(HttpHeaders.REFERER)) && getDistCheckoutFacade().isValidFastCheckout()) {
            getSessionService().setAttribute(DistConstants.Checkout.FAST_CHECKOUT, Boolean.TRUE);
            forwardGlobalMessages(model, redirectModel);
            LOG.info("Current Customer is eligible for Fast-Checkout process.");
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);
        }

        getDistCheckoutFacade().setPaymentAddressIfAvailable();
        getDistCheckoutFacade().setDeliveryAddressIfAvailable();

        CartData cart = getDistCheckoutFacade().getCheckoutCart();
        CustomerData customer = getDistCheckoutFacade().getCurrentCheckoutCustomer();

        addDoubleOptInReminder(model);
        addDeliveryModeData(model, cart);
        addBillingAddressData(model, cart, customer);
        addShippingAddressData(model, cart, customer);

        model.addAttribute("enableContinueButton", getDistCheckoutFacade().areDeliveryModeAndAddressesSet());
        model.addAttribute("supportedRegisterCountries", getBaseStoreService().getCurrentBaseStore().getRegisterCountries());

        addGlobalModelAttributes(model, request);
        storeCmsPageInModel(model, getContentPageForLabelOrId(PAGE_CHECKOUT_DELIVERY));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PAGE_CHECKOUT_DELIVERY));
        model.addAttribute(META_ROBOTS, NOINDEX_NOFOLLOW);
        DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageCategoryAndPageType(digitalDatalayer,
                                                                        DigitalDatalayerConstants.PageName.Checkout.CHECKOUT,
                                                                        DigitalDatalayerConstants.PageName.Checkout.ORDERDETAIL,
                                                                        DigitalDatalayerConstants.PageType.CHECKOUTPAGE);
        addPageEventOrLoginRegisterEvents(request, digitalDatalayer, DigitalDatalayer.EventName.CHECKOUT_DELIVERY);
        getDistDigitalDatalayerFacade().storeCheckoutStep(digitalDatalayer, PAGE_CHECKOUT_DELIVERY);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);

        return ControllerConstants.Views.Pages.Checkout.CheckoutDeliveryPage;
    }

    private void addDoubleOptInReminder(Model model) {
        Boolean showDoubleOptInPopup = getSessionService().getAttribute(DistConstants.Session.DOUBLE_OPT_IN_POPUP);
        if (showDoubleOptInPopup != null) {
            model.addAttribute("showDoubleOptInInfoMessage", showDoubleOptInPopup);
            getSessionService().removeAttribute(DistConstants.Session.DOUBLE_OPT_IN_POPUP);
        }
    }

    @GetMapping("/order-summary")
    public ResponseEntity<CartData> getOrderSummary() {
        return ok().body(getB2bCartFacade().getSessionMiniCart());
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateDeliveryMode(@Valid UpdateDeliveryModeForm updateDeliveryModeForm,
                                                BindingResult bindingResult) throws CalculationException {
        if (bindingResult.hasErrors()) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }
        DeliveryModeData deliveryMode = getDistCheckoutFacade().getDeliveryModeForCode(updateDeliveryModeForm.getDeliveryModeCode());
        if (deliveryMode == null) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }

        if (isDeliveryModePickup(deliveryMode)) {
            if (isBlank(updateDeliveryModeForm.getWarehouseCode())) {
                return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
            }
            getDistCheckoutFacade().setPickupLocation(updateDeliveryModeForm.getWarehouseCode());
        } else {
            getDistCheckoutFacade().setPickupLocation(null);
        }

        getDistCheckoutFacade().setDeliveryMode(deliveryMode.getCode());
        if (!getDistCheckoutFacade().isScheduleDeliveryAllowedForCurrentCart()) {
            resetRequestedDeliveryDate();
        }

        getDistCheckoutFacade().calculateOrder(true);

        return ok().body(getB2bCartFacade().getSessionMiniCart());
    }

    private boolean isDeliveryModePickup(DeliveryModeData selectedDeliveryMode) {
        return getDistCheckoutFacade().getPickupDeliveryModeCode().equals(selectedDeliveryMode.getCode());
    }

    @PutMapping("/complete")
    public ResponseEntity<?> setCompleteDelivery(@RequestBody String isCompleteDelivery) throws CalculationException {
        if (getDistCheckoutFacade().isCompleteDeliveryPossible()) {
            getDistCheckoutFacade().setCompleteDeliveryOnCart(BooleanUtils.toBoolean(isCompleteDelivery));
            getDistCheckoutFacade().calculateOrder(true);
            return ok().body(getB2bCartFacade().getSessionMiniCart());
        }
        return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
    }

    @PutMapping("/schedule")
    public ResponseEntity<?> scheduleRequestDeliveryDate(@RequestBody(required = false) String date) throws CalculationException {
        if (isNotBlank(date)) {
            if (!getDistCheckoutFacade().isScheduleDeliveryAllowedForCurrentCart()) {
                return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
            }

            try {
                String dateFormatForLanguage = getDataFormatForCurrentCmsSite();
                Date deliveryDate = getDistCheckoutFacade().calculateScheduledDeliveryDate(date, dateFormatForLanguage);
                setRequestDateAndCalculateOrder(deliveryDate);
                return ok(formatDate(dateFormatForLanguage, deliveryDate));
            } catch (ParseException e) {
                return getGlobalErrorResponse("checkout.deliveryDate.parseError", date);
            }
        }
        setRequestDateAndCalculateOrder(null);
        return ok().build();
    }

    private void setRequestDateAndCalculateOrder(Date deliveryDate) throws CalculationException {
        getDistCheckoutFacade().requestDeliveryDate(deliveryDate);
        getDistCheckoutFacade().calculateOrder(true);
    }

    private String formatDate(String dateFormatForLanguage, Date deliveryDate) {
        return new SimpleDateFormat(dateFormatForLanguage).format(deliveryDate);
    }

    private void resetRequestedDeliveryDate() {
        getDistCheckoutFacade().requestDeliveryDate(null);
    }

    @PostMapping("/billing/b2c")
    public ResponseEntity<?> billingAddressB2C(@Valid @ModelAttribute B2CAddressForm b2CAddressForm, BindingResult bindingResult) {
        CustomerData customer = getDistCheckoutFacade().getCurrentCheckoutCustomer();
        if (!isB2CCustomer(customer)) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }

        populatePhoneNumberOnForm(b2CAddressForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return getFieldErrorResponse(bindingResult);
        }

        AddressData formAddressData = getAddressData(b2CAddressForm.getAddressId());
        populateAddressData(formAddressData, b2CAddressForm, customer.getEmail());

        AddressData addressData = createOrEditAddress(formAddressData, Boolean.TRUE);
        return ok().body(addressData);
    }

    private AddressData getAddressData(String addressId) {
        return isNotEmpty(addressId) ? getDistUserFacade().getAddressForCode(addressId)
                                     : new AddressData();
    }

    @PostMapping(value = "/billing/b2e")
    public ResponseEntity<?> billingAndShippingAddressB2E(@Valid @ModelAttribute B2EAddressForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getFieldErrorResponse(bindingResult);
        }
        getSessionService().setAttribute(DistConstants.Session.B2E_ADDRESS_FORM, form);
        AddressData formAddressData = getAddressData(form.getAddressId());
        populateB2EAddressData(form, formAddressData);

        AddressData addressData = createOrEditAddress(formAddressData, Boolean.FALSE);
        getDistCheckoutFacade().setPaymentAddress(addressData);
        getDistCheckoutFacade().setDeliveryAddress(addressData);

        return ok().body(addressData);
    }

    private void populateB2EAddressData(B2EAddressForm form, AddressData formAddressData) {
        AddressFormHelper.populateB2EAddressData(form, formAddressData);
        formAddressData.setCountry(getDistCheckoutFacade().getCountryForIsocode(form.getCountryCode()));
        if (isNotBlank(form.getRegionCode())) {
            formAddressData.setRegion(getDistCheckoutFacade().getRegionForIsocode(form.getCountryCode(), form.getRegionCode()));
        }
    }

    @PostMapping("/billing/guest")
    public ResponseEntity<?> billingAndShippingAddressGuest(@Valid @ModelAttribute GuestAddressForm guestAddressForm, BindingResult bindingResult) {
        CustomerData customer = getDistCheckoutFacade().getCurrentCheckoutCustomer();
        if (!CustomerType.GUEST.equals(customer.getCustomerType())) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }

        populatePhoneNumberOnForm(guestAddressForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return getFieldErrorResponse(bindingResult);
        }

        AddressData formAddressData = getAddressData(guestAddressForm.getAddressId());
        populateAddressData(formAddressData, guestAddressForm, customer.getEmail());

        getB2bCustomerFacade().updateVatIdForGuest(guestAddressForm.getCodiceFiscale());
        AddressData addressData = createOrEditAddress(formAddressData, Boolean.FALSE);
        getDistCheckoutFacade().setPaymentAddress(addressData);
        getDistCheckoutFacade().setDeliveryAddress(addressData);

        return ok().body(addressData);
    }

    private AddressData createOrEditAddress(AddressData formAddressData, boolean updateInERP) {
        return (isNotBlank(formAddressData.getId()) ? getDistUserFacade().editAddress(formAddressData,
                                                                                      updateInERP)
                                                    : getDistUserFacade().addAddress(formAddressData,
                                                                                     updateInERP));
    }

    @PostMapping("/billing/b2b")
    public ResponseEntity<?> billingAddressB2B(@Valid @ModelAttribute B2BBillingAddressForm b2BBillingAddressForm, BindingResult bindingResult) {
        CustomerData customer = getDistCheckoutFacade().getCurrentCheckoutCustomer();
        if (!CustomerType.B2B.equals(customer.getCustomerType()) && !CustomerType.B2B_KEY_ACCOUNT.equals(customer.getCustomerType())) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }

        populatePhoneNumberOnForm(b2BBillingAddressForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return getFieldErrorResponse(bindingResult);
        }

        AddressData formAddressData = getAddressData(b2BBillingAddressForm.getAddressId());
        populateAddressData(formAddressData, b2BBillingAddressForm, customer.getEmail());
        AddressData addressData = createOrEditAddress(formAddressData, Boolean.TRUE);
        return ok().body(addressData);
    }

    private void populatePhoneNumberOnForm(AbstractDistAddressForm form, BindingResult bindingResult) {
        try {
            String countryIsoCode = getCountryIsoCode(form.getCountryIso());
            String telNumber = AddressFormHelper.getPhoneNumber(form);
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            if (isNotBlank(telNumber)) {
                Phonenumber.PhoneNumber numberProto = phoneUtil.parse(telNumber, countryIsoCode);
                boolean isValidForRegion = isValidForRegion(countryIsoCode, phoneUtil, numberProto);
                boolean isPossibleNumber = phoneUtil.isPossibleNumber(numberProto);
                PhoneNumberUtil.PhoneNumberType phoneType = phoneUtil.getNumberType(numberProto);
                if (isValidForRegion && isPossibleNumber) {
                    if (PhoneNumberUtil.PhoneNumberType.MOBILE.equals(phoneType)) {
                        AddressFormHelper.setMobileNumber(form, telNumber);
                    } else {
                        AddressFormHelper.setPhoneNumber(form, telNumber);
                    }
                } else {
                    setValidationError(phoneUtil, bindingResult, countryIsoCode);
                }
            }
        } catch (Exception e) {
            LOG.error("Error while setting up phone number", e);
        }
    }

    private String getCountryIsoCode(String countryIso) {
        if (StringUtils.equals(countryIso, DistConstants.CountryIsoCode.NORTHERN_IRELAND)) {
            return DistConstants.CountryIsoCode.GREAT_BRITAIN;
        }
        return countryIso;
    }

    private void setValidationError(PhoneNumberUtil phoneUtil, BindingResult bindingResult, String countryIsoCode) {
        Phonenumber.PhoneNumber exNr = phoneUtil.getExampleNumber(countryIsoCode);
        String exampleInt = exNr == null ? EMPTY : phoneUtil.format(exNr, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        String exampleNational = exNr == null ? EMPTY : phoneUtil.format(exNr, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        Object[] messageParams = { exampleInt, exampleNational };
        bindingResult.rejectValue("contactPhone",
                                  "register.phoneNumber.invalid",
                                  messageParams,
                                  getMessageSource().getMessage("register.phoneNumber.invalid", messageParams, getI18nService().getCurrentLocale()));
    }

    private boolean isValidForRegion(String countryIsoCode, PhoneNumberUtil phoneUtil, Phonenumber.PhoneNumber numberProto) {
        if (DistConstants.CountryIsoCode.SWITZERLAND.equals(countryIsoCode) || DistConstants.CountryIsoCode.LIECHTENSTEIN.equals(countryIsoCode)) {
            return phoneUtil.isValidNumberForRegion(numberProto, DistConstants.CountryIsoCode.SWITZERLAND)
                    || phoneUtil.isValidNumberForRegion(numberProto, DistConstants.CountryIsoCode.LIECHTENSTEIN);
        } else {
            return phoneUtil.isValidNumberForRegion(numberProto, countryIsoCode);
        }
    }

    private void populateAddressData(AddressData addressData, AbstractDistAddressForm addressForm, String email) {
        addressForm.setAddressId(addressData.getId());
        AddressFormHelper.populateAddressData(addressForm, addressData);
        addressData.setCountry(getDistCheckoutFacade().getCountryForIsocode(addressForm.getCountryIso()));
        addressData.setRegion(isNotBlank(addressForm.getRegionIso()) ? getDistCheckoutFacade().getRegionForIsocode(addressForm.getCountryIso(),
                                                                                                                   addressForm.getRegionIso())
                                                                     : null);
        addressData.setEmail(email);
    }

    private void addBillingAddressData(Model model, CartData cart, CustomerData customer) {
        List<AddressData> billingAddresses = getDistCheckoutFacade().getBillingAddresses();
        Set<CountryData> billingAddressCountries = billingAddresses.stream()
                                                                   .map(AddressData::getCountry)
                                                                   .collect(Collectors.toSet());

        AddressData selectedBillingAddress = getBillingAddress(cart, customer, billingAddresses);
        if (isMultipleAddresses(billingAddresses) && getDistCheckoutFacade().isNotLimitedUserType()) {
            model.addAttribute("multipleBillingAddresses", Boolean.TRUE);
            model.addAttribute("billingAddresses", billingAddresses);
        } else {
            model.addAttribute("showBillingEdit", getDistCheckoutFacade().shouldAllowEditBilling(customer.getCustomerType()));
            model.addAttribute("isBillingAndShippingAddress", isBillingAndShippingAddress(cart));
            model.addAttribute("isBillingAddressShippable", getDistCheckoutFacade().isBillingAddressShippable(selectedBillingAddress));
            model.addAttribute("showBillingInfoMode", getDistCheckoutFacade().isAddressValid(selectedBillingAddress));
            model.addAttribute("isDefaultBillingAddressInvalid", isDefaultAddressInvalid(customer, selectedBillingAddress));
        }
        model.addAttribute("billingAddress", selectedBillingAddress);
        model.addAttribute("billingAddressCountries", billingAddressCountries);
        addBillingFormData(model, customer, selectedBillingAddress);
    }

    private boolean isDefaultAddressInvalid(CustomerData customer, AddressData selectedBillingAddress) {
        // this flag is used on first load to trigger inline validation (existing customer),
        // for new customer we don't show validation (they won't have default address set)
        return !getDistCheckoutFacade().isAddressValid(selectedBillingAddress) && customer.getDefaultBillingAddress() != null && selectedBillingAddress != null
                && StringUtils.equals(customer.getDefaultBillingAddress().getId(), selectedBillingAddress.getId());
    }

    private boolean isBillingAndShippingAddress(CartData cart) {
        return cart.getBillingAddress() != null && cart.getDeliveryAddress() != null
                && StringUtils.equals(cart.getBillingAddress().getId(), cart.getDeliveryAddress().getId());
    }

    private void addBillingFormData(Model model, CustomerData customer, AddressData billingAddress) {
        if (getDistCheckoutFacade().isAnonymousCheckout()) {
            addTitles(model);
            addGuestAddressForm(model, customer, billingAddress);
        }

        if (isB2BCustomer(customer) && !isEShopGroup()) {
            // some B2E customers have B2B customer type
            addB2BAddressForm(model, customer, billingAddress);
        }

        if (isB2CCustomer(customer)) {
            addTitles(model);
            addB2CAddressForm(model, customer, billingAddress);
        }

        if (isEShopGroup()) {
            addTitles(model);
            addB2EAddressForm(model);
        }
    }

    private void addShippingAddressData(Model model, CartData cart, CustomerData customer) {
        List<AddressData> shippingAddresses = getDistCheckoutFacade().getShippingAddresses()
                                                                     .stream()
                                                                     .peek(shippingAddress -> shippingAddress.setIsValid(getDistCheckoutFacade().isAddressValid(shippingAddress)))
                                                                     .collect(Collectors.toList());
        model.addAttribute("shippingAddresses", shippingAddresses);
        model.addAttribute("shippingAddress", getShippingAddress(cart, customer));
    }

    private void addGuestAddressForm(Model model, CustomerData customer, AddressData billingAddress) {
        GuestAddressForm billingAddressForm = new GuestAddressForm();
        billingAddressForm.setBillingAddress(Boolean.TRUE);
        billingAddressForm.setShippingAddress(Boolean.TRUE);
        if (billingAddress != null) {
            guestAddressFormPopulator.populate(billingAddress, billingAddressForm);
            if (getStoreSessionFacade().isCurrentShopItaly()) {
                billingAddressForm.setCodiceFiscale(customer.getUnit().getVatID());
            }
            if (isBlank(billingAddressForm.getContactPhone()) && customer.getContactAddress() != null) {
                billingAddressForm.setContactPhone(AddressFormHelper.getPhoneNumber(customer.getContactAddress()));
            }
        }
        model.addAttribute("guestAddressForm", billingAddressForm);

    }

    private void addTitles(Model model) {
        model.addAttribute("titles", getDistUserFacade().getTitles());
    }

    private void addB2BAddressForm(Model model, CustomerData customer, AddressData billingAddress) {
        B2BBillingAddressForm billingAddressForm = new B2BBillingAddressForm();
        if (billingAddress != null) {
            b2bBillingAddressFormPopulator.populate(billingAddress, billingAddressForm);
            if (isBlank(billingAddressForm.getPhoneNumber()) && customer.getContactAddress() != null) {
                billingAddressForm.setPhoneNumber(AddressFormHelper.getPhoneNumber(customer.getContactAddress()));
            }
        } else {
            billingAddressForm.setBillingAddress(Boolean.TRUE);
            billingAddressForm.setShippingAddress(Boolean.TRUE);
        }
        model.addAttribute("b2bAddressForm", billingAddressForm);
    }

    private void addB2CAddressForm(Model model, CustomerData customer, AddressData billingAddress) {
        B2CAddressForm billingAddressForm = new B2CAddressForm();
        if (billingAddress != null) {
            b2cBillingAddressFormPopulator.populate(billingAddress, billingAddressForm);
            if (isBlank(billingAddressForm.getContactPhone()) && customer.getContactAddress() != null) {
                billingAddressForm.setContactPhone(AddressFormHelper.getPhoneNumber(customer.getContactAddress()));
            }
        } else {
            billingAddressForm.setBillingAddress(Boolean.TRUE);
            billingAddressForm.setShippingAddress(Boolean.TRUE);
        }
        model.addAttribute("b2CAddressForm", billingAddressForm);
    }

    private AddressData getBillingAddress(CartData cart, CustomerData customer, List<AddressData> billingAddresses) {
        if (cart.getBillingAddress() != null) {
            return cart.getBillingAddress();
        } else if (customer.getBillingAddress() != null) {
            return customer.getBillingAddress();
        } else if (CollectionUtils.isNotEmpty(billingAddresses)) {
            return billingAddresses.iterator().next();
        }
        return null;
    }

    private AddressData getShippingAddress(CartData cart, CustomerData customer) {
        if (cart.getDeliveryAddress() != null) {
            return cart.getDeliveryAddress();
        }
        return customer.getDefaultShippingAddress();
    }

    private void addB2EAddressForm(Model model) {
        model.addAttribute(DistConstants.Session.B2E_ADDRESS_FORM,
                           getSessionService().getOrLoadAttribute(DistConstants.Session.B2E_ADDRESS_FORM, B2EAddressForm::new));
    }

    private void addDeliveryModeData(Model model, CartData cartData) {
        model.addAttribute("completeDeliveryPossible", getDistCheckoutFacade().isCompleteDeliveryPossible());
        model.addAttribute("showDeliveryModePrice", getDistCheckoutFacade().isDeliveryModePriceVisible());
        model.addAttribute("selectedDeliveryMode", cartData.getDeliveryMode());
        addScheduleDelivery(model, cartData);
        addValidDeliveryModes(model, cartData);
    }

    private void addScheduleDelivery(Model model, CartData cartData) {
        boolean isScheduledDeliveryAllowed = getDistCheckoutFacade().isScheduleDeliveryAllowed(cartData);
        if (isScheduledDeliveryAllowed) {
            model.addAttribute("showScheduleDelivery", Boolean.TRUE);
            model.addAttribute("invalidModesForScheduleDelivery", List.of(getDistCheckoutFacade().getPickupDeliveryModeCode()));
        }
    }

    private void addValidDeliveryModes(Model model, CartData cartData) {
        boolean isPickupAvailable = getDistCheckoutFacade().isPickupAvailableForCart(cartData);
        if (isPickupAvailable) {
            boolean isPickupAllowed = getDistCheckoutFacade().isPickupAllowedForCart(cartData);
            List<WarehouseData> pickupWarehouses = warehouseFacade.getPickupWarehousesAndCalculatePickupDate(cartData);
            if (isPickupAllowed && CollectionUtils.isNotEmpty(pickupWarehouses)) {
                model.addAttribute(DELIVERY_MODES, cartData.getValidDeliveryModes());
                model.addAttribute("pickupWarehouses", pickupWarehouses);
            } else {
                List<DeliveryModeData> deliveryModesWithoutPickup = cartData.getValidDeliveryModes()
                                                                            .stream()
                                                                            .filter(distDeliveryModeData -> !METHOD_PICKUP.equals(distDeliveryModeData.getCode()))
                                                                            .collect(Collectors.toList());
                model.addAttribute(DELIVERY_MODES, deliveryModesWithoutPickup);
            }
        } else {
            model.addAttribute(DELIVERY_MODES, cartData.getValidDeliveryModes());
        }
    }

    private void checkVoucherReturnCode(Model model) {
        String returnCode = getB2bCartFacade().getVoucherReturnCodeFromCurrentCart();
        if (isEmpty(returnCode)) {
            model.addAttribute("voucherErrorMessageKey", CONF_VOUCHER_ERROR_KEY_PREFIX + returnCode);
            GlobalMessages.addErrorMessage(model, "checkoutvoucherbox.voucherError.invalid");
            getB2bCartFacade().resetVoucherOnCurrentCart();
        } else {
            GlobalMessages.addConfMessage(model, "checkoutvoucherbox.voucherSuccess");
        }
        getSessionService().removeAttribute("anonymousVoucherCode");
    }

    private boolean isFastCheckoutUrl(String referalUrl) {
        return isNotEmpty(referalUrl) && distFastCheckoutUrls.stream().anyMatch(referalUrl::contains);
    }

    @PostMapping(value = "/shipping/b2c")
    public ResponseEntity<?> createB2CShippingAddress(@Valid B2CAddressForm addressForm, BindingResult bindingResult) {
        CustomerData customer = getDistCheckoutFacade().getCurrentCheckoutCustomer();
        if (!isB2CCustomer(customer)) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }

        populatePhoneNumberOnForm(addressForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return getFieldErrorResponse(bindingResult);
        }

        AddressData formAddressData = getAddressData(addressForm.getAddressId());
        populateAddressData(formAddressData, addressForm, customer.getEmail());
        AddressData addressData = createOrEditAddress(formAddressData, Boolean.TRUE);
        if (isNewAddress(addressForm)) {
            getDistCheckoutFacade().setDeliveryAddress(addressData);
        }
        return ok().body(addressData);
    }

    @PostMapping(value = "/shipping/b2b")
    public ResponseEntity<?> createB2BShippingAddress(@Valid B2BBillingAddressForm b2BBillingAddressForm, BindingResult bindingResult) {
        CustomerData customer = getDistCheckoutFacade().getCurrentCheckoutCustomer();
        if (!isB2BCustomer(customer)) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }

        populatePhoneNumberOnForm(b2BBillingAddressForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return getFieldErrorResponse(bindingResult);
        }

        AddressData formAddressData = getAddressData(b2BBillingAddressForm.getAddressId());
        populateAddressData(formAddressData, b2BBillingAddressForm, customer.getEmail());
        AddressData addressData = createOrEditAddress(formAddressData, Boolean.TRUE);
        if (isNewAddress(b2BBillingAddressForm)) {
            getDistCheckoutFacade().setDeliveryAddress(addressData);
        }
        return ok().body(addressData);
    }

    private boolean isNewAddress(AbstractDistAddressForm addressForm) {
        return isEmpty(addressForm.getAddressId());
    }

    @DeleteMapping("/shipping")
    public ResponseEntity<Void> removeShippingAddress(@RequestBody String shippingAddressId) {
        AddressData addressData = new AddressData();
        addressData.setId(shippingAddressId);
        userFacade.removeAddress(addressData);
        return ok().build();
    }

    @PutMapping(value = "/select/billing")
    public ResponseEntity<?> updateBillingAddressSelection(@RequestBody String billingAddressId) {
        AddressData address = getDistUserFacade().getAddressForCode(billingAddressId);
        if (address != null && address.isBillingAddress()) {
            getDistCheckoutFacade().setPaymentAddress(address);
            return ok().build();
        }
        return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
    }

    @PutMapping(value = "/select/shipping")
    public ResponseEntity<?> setShippingAddress(@RequestBody String shippingAddressId) {
        AddressData address = getDistUserFacade().getAddressForCode(shippingAddressId);
        if (address != null && address.isShippingAddress()) {
            getDistCheckoutFacade().setDeliveryAddress(address);
            return ok().build();
        }
        return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
    }

    @PostMapping("/select/billingAsShipping")
    public ResponseEntity<?> selectBillingAsShipping(@Valid BillingAndShippingAddressForm billingAndShippingAddress, BindingResult bindingResult) {
        CartData cart = getDistCheckoutFacade().getCheckoutCart();
        if (bindingResult.hasErrors() || isDeliveryModePickup((DeliveryModeData) cart.getDeliveryMode())) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }

        AddressData addressData = getDistUserFacade().getAddressForCode(billingAndShippingAddress.getAddressId());
        if (addressData == null || !addressData.isShippingAddress()) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }

        if (BooleanUtils.isTrue(billingAndShippingAddress.isBillingAndShippingAddress())) {
            getDistCheckoutFacade().setDeliveryAddress(addressData);
        } else if (cart.getDeliveryAddress() != null && StringUtils.equals(cart.getDeliveryAddress().getId(), addressData.getId())) {
            getDistCheckoutFacade().removeDeliveryAddress();
        }
        return ok().build();
    }

    @PutMapping(value = "/shipping/set-default-address")
    public ResponseEntity<?> setDefaultShippingAddress(@RequestBody String shippingAddressId) {
        AddressData addressData = getDistUserFacade().getAddressForCode(shippingAddressId);
        if (addressData == null) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }
        userFacade.setDefaultShippingAddress(addressData);
        return ok().build();
    }

    @PutMapping(value = "/billing/set-default-address")
    public ResponseEntity<?> setDefaultBillingAddress(@RequestBody String billingAddressId) {
        AddressData addressData = getDistUserFacade().getAddressForCode(billingAddressId);
        if (addressData == null) {
            return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
        }
        userFacade.setDefaultBillingAddress(addressData);
        return ok().build();
    }

    @GetMapping(value = "/continueCheckout")
    public String continueCheckout(final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();
        final CustomerData customer = getDistCheckoutFacade().getCurrentCheckoutCustomer();

        String validationMessage = validateDeliveryPage(cartData);
        if (isNotEmpty(validationMessage)) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList(validationMessage));
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_DELIVERY);
        }
        setDefaultsIfNotSet(cartData, customer);
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);
    }

    private void setDefaultsIfNotSet(CartData cartData, CustomerData customer) {
        if (getDistCheckoutFacade().isNotLimitedUserType()) {
            if (isDefaultDeliveryNotSetOrNotEqual(customer)) {
                getDistUserFacade().setDefaultDeliveryMode(cartData.getDeliveryMode().getCode());
            }
            getDistUserFacade().setDefaultBillingAddressIfNotSet(cartData.getBillingAddress());
            getDistUserFacade().setDefaultShippingAddressIfNotSet(cartData.getDeliveryAddress());
        }
    }

    private boolean isDefaultDeliveryNotSetOrNotEqual(CustomerData customer) {
        return customer.getDefaultDeliveryMode() == null
                || !equalsIgnoreCase(customer.getDefaultDeliveryMode(), getDistCheckoutFacade().getDefaultDeliveryMode());
    }

    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<List<GlobalMessageResponse>> handleModelNotFoundException() {
        return getGlobalErrorResponse("cart.error.recalculate");
    }

    @ExceptionHandler(ErpCommunicationException.class)
    public ResponseEntity<List<GlobalMessageResponse>> handleModelNotFoundException(ErpCommunicationException e) {
        LOG.error(ERP_COMMUNICATION_ERROR, e);
        return getGlobalErrorResponse(DEFAULT_ERROR_MESSAGE);
    }

    @Override
    protected boolean recalculateCartBeforePage() {
        return Boolean.TRUE;
    }
}
