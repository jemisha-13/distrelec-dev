/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.customer.converters.populator;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.security.DistCryptographyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.model.ObsolescenceCategoryModel;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.facades.customer.data.DistUserProfileData;
import com.namics.distrelec.b2b.facades.user.data.ObsolescenceCategoryData;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BCustomerPopulator;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Populates {@link CustomerData} with data form {@link B2BCustomerModel}
 * .
 */
public class DistB2BCustomerPopulator extends B2BCustomerPopulator {

    protected static final String B2B_ADMIN_GROUP = "b2badmingroup";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    @Qualifier("creditCardPaymentInfoConverter")
    private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;

    @Autowired
    @Qualifier("addressConverter")
    private Converter<AddressModel, AddressData> addressConverter;

    @Autowired
    @Qualifier("b2bBudgetConverter")
    private Converter<DistB2BBudgetModel, B2BBudgetData> b2bBudgetConverter;

    @Autowired
    private DistB2BCommerceBudgetService distB2BCommerceBudgetService;

    @Autowired
    private DistUserService userService;

    @Autowired
    @Qualifier("languageConverter")
    private Converter<LanguageModel, LanguageData> languageConverter;

    @Autowired
    @Qualifier("countryConverter")
    private Converter<CountryModel, CountryData> countryConverter;

    @Autowired
    @Qualifier("defaultCustomerPopulator")
    private Populator<CustomerModel, CustomerData> customerPopulator;

    @Autowired
    private Converter<B2BPermissionModel, B2BPermissionData> b2bPermissionConverter;

    @Autowired
    private Converter<ObsolescenceCategoryModel, ObsolescenceCategoryData> obsolescenceCategoryConverter;

    @Autowired
    private DistWebtrekkFacade distWebtrekkFacade;

    @Autowired
    private DistCryptographyService distCryptographyService;

    @Override
    public void populate(final CustomerModel source, final CustomerData target) {
        if (!(source instanceof B2BCustomerModel)) {
            customerPopulator.populate(source, target);
        } else {
            populate((B2BCustomerModel) source, target);
        }
    }

    public void populate(final B2BCustomerModel source, final CustomerData target) {
        super.populate(source, target);
        target.setCustomerType(userService.getCustomerType(source));
        target.setContactId(source.getErpContactID());
        target.setEmail(source.getEmail());
        target.setNewsletter(source.isNewsletter());
        target.setSubscribePhoneMarketing(source.isPhoneMarketingConsent());
        target.setRegisteredAsGuest(source.isRegisteredAsGuest());
        target.setRsCustomer(source.isRsCustomer());
        target.setErpSelectedCustomer(source.isErpSelectedCustomer());
        target.setAllowedToPlaceOpenOrders(source.getDefaultB2BUnit().isAllowedToPlaceOpenOrder());
        target.setLanguage(languageConverter.convert(!userService.isAnonymousUser(source) && source.getSessionLanguage() != null
                                                                                                                                 ? source.getSessionLanguage()
                                                                                                                                 : getCommonI18NService().getCurrentLanguage()));
        target.setDoubleOptinActivated(source.isDoubleOptInActivated());
        target.setLoginDisabled(!source.getActive());
        target.setAdminUser(userService.isMemberOfGroup(source, userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)));
        target.setCreatedDate(source.getCreationtime());
        target.setConsentConditionRequired(BooleanUtils.isTrue(source.getConsentConditionRequired()));
        if (source.getOptedForObsolescence() != null) {
            target.setOptedForObsolescence(source.getOptedForObsolescence());
        }

        if (source.getAllObsolCatSelected() != null) {
            target.setAllObsolCatSelected(source.getAllObsolCatSelected());
        }

        if (source.getObsolescenceCategories() != null) {
            target.setCategories(obsolescenceCategoryConverter.convertAll(source.getObsolescenceCategories()));
        }

        if (source.getVat4() != null) {
            target.setVat4(source.getVat4());
        }

        if (source.getLegalEmail() != null) {
            target.setLegalEmail(source.getLegalEmail());
        }

        final CMSSiteModel currentSite = cmsSiteService.getCurrentSite();
        if (currentSite != null && currentSite.getChannel() != null) {
            target.setChannelType(currentSite.getChannel().getCode());
        }

        if (source.getSessionCurrency() != null) {
            target.setCurrency(getCurrencyConverter().convert(source.getSessionCurrency()));
        }

        if (source.getContactAddress() != null) {
            target.setContactAddress(addressConverter.convert(source.getContactAddress()));

            // If a contact address is available the first and last name is
            // taken from the address, because it is more safe to resolve the
            // names with the address fields than the CustomerNameStrategy
            target.setFirstName(source.getContactAddress().getFirstname());
            target.setLastName(source.getContactAddress().getLastname());
        }

        if (source.getDefaultPaymentAddress() != null) {
            AddressData defaultAddress = addressConverter.convert(source.getDefaultPaymentAddress());
            target.setBillingAddress(defaultAddress);
            target.setDefaultBillingAddress(defaultAddress);

            // If a payment address is available the first and last name is not
            // yet set, its taken from the payment address
            if (StringUtils.isBlank(target.getFirstName())) {
                target.setFirstName(source.getDefaultPaymentAddress().getFirstname());
            }
            if (StringUtils.isBlank(target.getLastName())) {
                target.setLastName(source.getDefaultPaymentAddress().getLastname());
            }
        }

        if (CollectionUtils.isNotEmpty(source.getPermissions())) {
            target.setPermissions(source.getPermissions().stream()
                                        .map(permission -> b2bPermissionConverter.convert(permission))
                                        .collect(Collectors.toList()));
        }

        if (source.getUserprofile() != null) {
            DistUserProfileData userProfileData = new DistUserProfileData();
            userProfileData.setUserProfileName(source.getUserprofile().getUserProfileName());
            target.setUserProfileData(userProfileData);
        }
        if (source.getDistFunction() != null) {
            target.setFunctionCode(source.getDistFunction().getCode());
        }
        if (source.getDistFunction() != null) {
            target.setFunctionName(source.getDistFunction().getName());
            target.setFunctionNameEN(source.getDistFunction().getName(Locale.ENGLISH));
        }

        if (CollectionUtils.isNotEmpty(source.getPaymentInfos())) {
            final List<CCPaymentInfoData> ccPaymentInfos = source.getPaymentInfos().stream()
                                                                 .filter(CreditCardPaymentInfoModel.class::isInstance)
                                                                 .map(paymentInfo -> creditCardPaymentInfoConverter.convert((CreditCardPaymentInfoModel) paymentInfo))
                                                                 .collect(Collectors.toList());

            // Set the default credit card info in CustomerData.
            final PaymentInfoModel paymentInfoModel = source.getDefaultPaymentInfo();
            if (null != paymentInfoModel) {
                ccPaymentInfos.stream()
                              .filter(ccpInfo -> ccpInfo.getId().equalsIgnoreCase(paymentInfoModel.getPk().toString()))
                              .findFirst().get().setDefaultPaymentInfo(true);
            }

            target.setCcPaymentInfos(ccPaymentInfos);

            // Default Payment Method
            target.setDefaultPaymentMode(source.getDefaultPaymentMethod());
            for (final CCPaymentInfoData ccPaymentInfoData : ccPaymentInfos) {
                if (ccPaymentInfoData.isDefaultPaymentInfo()) {
                    target.setDefaultPaymentInfo(ccPaymentInfoData);
                    break;
                }
            }

        }

        if (source.getDefaultDeliveryMethod() != null) {
            target.setDefaultDeliveryMode(source.getDefaultDeliveryMethod());
        }

        final DistB2BBudgetModel budget = distB2BCommerceBudgetService.getActiveBudget(source);
        if (budget != null) {
            target.setBudget(b2bBudgetConverter.convert(budget));
        }

        if (source.getDefaultB2BUnit() != null) {
            target.setCompanyName(source.getDefaultB2BUnit().getBillingAddress() != null ? source.getDefaultB2BUnit().getBillingAddress().getCompany()
                                                                                         : source.getDefaultB2BUnit().getName());
        }

        populateApprovers(source, target);
        populateContactAddress(source, target);
        target.setEncryptedUserID(distWebtrekkFacade.encodeToUTF8(distCryptographyService.encryptString(source.getCustomerID(),
                                                                                                 DistCryptography.WEBTREKK_KEY)));
    }

    void populateContactAddress(final B2BCustomerModel source, final CustomerData target) {
        if (source.getContactAddress() != null) {
            target.setContactAddress(addressConverter.convert(source.getContactAddress()));
        }
    }

    @Override
    protected void populateUnit(final B2BCustomerModel source, final CustomerData target) {
        super.populateUnit(source, target);
        if (target.getUnit() != null) {
            final B2BUnitModel parent = getB2bUnitService().getParent(source);
            target.getUnit().setName(parent.getName());
            target.getUnit().setVatID(parent.getVatID());
            target.getUnit().setErpCustomerId(parent.getErpCustomerID());
            target.getUnit().setCountry(parent.getCountry() == null ? null : countryConverter.convert(parent.getCountry()));
        }
    }

    protected void populateApprovers(final B2BCustomerModel customer, final CustomerData target) {
        if (customer != null && target != null && CollectionUtils.isNotEmpty(customer.getApprovers())) {
            final List<CustomerData> approversData = customer.getApprovers().stream()
                                                             .map(approver -> populateApprover(approver, new CustomerData())).collect(Collectors.toList());
            target.setApprovers(approversData);
        }
    }

    protected CustomerData populateApprover(final B2BCustomerModel approver, final CustomerData target) {
        if (approver != null) {
            target.setName(approver.getName());
            target.setEmail(approver.getEmail());
            target.setUid(approver.getUid());
            populateRoles(approver, target);
        }
        return target;
    }
}
