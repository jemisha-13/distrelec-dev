/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.converters.request;

import com.distrelec.webservice.if11.v3.*;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistPaymentMethodModel;
import com.namics.distrelec.b2b.core.model.DistShippingMethodModel;
import com.namics.distrelec.b2b.core.model.delivery.DistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Converts an {@link AbstractOrderModel} for the SAP call to an {@link OrderCalculationRequest}
 *
 * @author fbersani, Distrelec
 * @since Distrelec 2.0
 */
public class OrderCalculationRequestConverter extends AbstractPopulatingConverter<AbstractOrderModel, OrderCalculationRequest> {

    private static final Logger LOG = LogManager.getLogger(OrderCalculationRequestConverter.class);
    private static final List<String> CEE_SHOPS = Arrays.asList("distrelec_CZ", "distrelec_RO", "distrelec_HU", "distrelec_SK");
    private static final String CEE_DUMMY_SALES_ORG = "7321";

    private DistSalesOrgService distSalesOrgService;
    private UserService userService;
    private CommonI18NService commonI18NService;

    @Autowired private I18NService i18NService;

    private NamicsCommonI18NService namicsCommonI18NService;

    private ModelService modelService;

    @Autowired private ConfigurationService configurationService;
    @Autowired private CMSSiteService cmsSiteService;
    @Autowired private SessionService sessionService;
    // The SOAP Requests object factory
    @Autowired private ObjectFactory sapObjectFactoryIF11;

    @Override protected OrderCalculationRequest createTarget() {
        return sapObjectFactoryIF11.createOrderCalculationRequest();
    }

    @Override public void populate(final AbstractOrderModel source, final OrderCalculationRequest target) {
        target.setSalesOrganization(getCurrentSalesOrgCode(source));
        // build the header of the request
        buildOrderHeaderRequest(source, target);
        // build the customer
        buildCustomerDataRequest(source, target);
        // build the entries
        buildOrderEntriesRequest(source, target);
    }

    private void buildOrderHeaderRequest(final AbstractOrderModel source, final OrderCalculationRequest request) {
        request.setSelectedShippingMethodCode(getShippingMethodCode(source));
        request.setSelectedPaymentMethodCode(getPaymentMethodCode(source));

        // not needed for Elfa
        request.setFreeShippingPromotion(false);
        //isDeliveryModePickUp

        if (getShippingMethodCode(source) != null && DistConstants.Shipping.METHOD_PICKUP.contains(getShippingMethodCode(source).value())) {
            request.setPickupLocation(source.getPickupLocation() == null ? null : source.getPickupLocation().getCode());
        } else {
            request.setPickupLocation(null);
        }

        // not needed for Elfa
        request.setFixedDiscountPromotion(null);
        request.setOrderNote(source.getNote());

        // DISTRELEC-6854 add the requestedDeliveryDateHeaderLevel
        request.setReqDeliveryDateHeaderLevel(
                source.getRequestedDeliveryDate() == null ? null : SoapConversionHelper.convertDate(source.getRequestedDeliveryDate()));

        buildRedeemVoucher(source, request);

        // OPEN ORDER CASE
        request.setOrderCloseDate(source.getOrderCloseDate() == null ? null : SoapConversionHelper.convertDate(source.getOrderCloseDate()));

        request.setEditableByAllContacts(source.getEditableByAllContacts() == null ? Boolean.FALSE : source.getEditableByAllContacts().booleanValue());

        request.setCompleteDelivery(source.getCompleteDelivery());
        if (source.getCodiceCIG() != null)
            request.setCodiceCIG(source.getCodiceCIG());

        if (source.getCodiceCUP() != null)
            request.setCodiceCUP(source.getCodiceCUP());

        // DISTRELEC-9496 always include country and language

        if (((CMSSiteModel) source.getSite()).getCountry() != null) {
            request.setShopCountry(((CMSSiteModel) source.getSite()).getCountry().getIsocode());
        }
    }

    private void buildRedeemVoucher(final AbstractOrderModel source, final OrderCalculationRequest request) {
        final DistErpVoucherInfoModel erpVoucherInfo = source.getErpVoucherInfo();
        if (erpVoucherInfo != null) {

            // this check is needed to remove the voucher information in case of
            // wrong voucher selected
            if (!erpVoucherInfo.isValidInERP() && erpVoucherInfo.isCalculatedInERP()) {
                source.setErpVoucherInfo(null);
                getModelService().save(source);
                getModelService().remove(erpVoucherInfo);
                getModelService().refresh(source);
            }

            request.getVoucherCodes().add(erpVoucherInfo.getCode());
        }
    }

    private void buildCustomerDataRequest(final AbstractOrderModel order, final OrderCalculationRequest orderCalculationRequest) {

        final UserModel user = getUser(order);

        // Check if customer is anonymous
        if (getUserService().isAnonymousUser(user)) {
            // in this case do nothing.
            if (LOG.isDebugEnabled()) {
                LOG.debug("No customer data needs to be filled in for anonymous customer!");
            }

            try {
                orderCalculationRequest.setSessionLanguage(i18NService.getCurrentLocale().getLanguage().toUpperCase());
            } catch (final Exception exp) {
                LOG.warn("Could not retrieve the session language");
            }

            return;
        }

        final B2BUnitModel company = ((B2BCustomerModel) user).getDefaultB2BUnit();
        final B2BCustomerModel contact = (B2BCustomerModel) user;

        if (isGuestCustomer(contact)) {
            // Anonymous user case
            if (CollectionUtils.isNotEmpty(company.getAddresses())) {
                final AddressModel anonymousAddress = company.getAddresses().iterator().next();
                addGuestAddress(order, anonymousAddress, company, orderCalculationRequest);
            }
        } else {
            // B2B, B2E and B2C case
            final OrderCustomer soapCustomer = new OrderCustomer();
            soapCustomer.setCustomerId(company.getErpCustomerID());
            soapCustomer.setContactId(contact.getErpContactID());

            if (isB2E(contact)) { // DISTRELEC-10347 for B2E customer we add guest address instead of billing and shipping address IDs
                addGuestAddress(order, order.getPaymentAddress() != null ? order.getPaymentAddress() : order.getDeliveryAddress(), company,
                        orderCalculationRequest);
            } else {
                // set billing address
                final AddressModel billingAddress = order.getPaymentAddress();
                soapCustomer.setBillingAddressId(billingAddress == null || billingAddress.getErpAddressID() == null ? null : billingAddress.getErpAddressID());

                // set shipping address
                final AddressModel shippingAddress = order.getDeliveryAddress() == null ? company.getShippingAddress() : order.getDeliveryAddress();
                soapCustomer
                        .setShippingAddressId(shippingAddress == null || shippingAddress.getErpAddressID() == null ? null : shippingAddress.getErpAddressID());
            }
            if (isB2BCustomer(contact)) {
                orderCalculationRequest.setCostCenterId(order.getCostCenter());
                orderCalculationRequest.setCustomerReferenceHeaderLevel(order.getProjectNumber());
            }

            orderCalculationRequest.setCustomerData(soapCustomer);
        }

        if (contact.getSessionLanguage() != null) {
            orderCalculationRequest.setSessionLanguage(contact.getSessionLanguage().getIsocode());
        }
    }

    private void buildOrderEntriesRequest(final AbstractOrderModel order, final OrderCalculationRequest orderCalculationRequest) {
        final List<String> sapCatalogProducts = Arrays
                .asList(getConfigurationService().getConfiguration().getString("sap.catalog.order.articles", "").split(","));

        for (final AbstractOrderEntryModel orderEntry : order.getEntries()) {
            createOrderEntry(orderCalculationRequest, sapCatalogProducts, orderEntry);
        }
    }

    private void createOrderEntry(final OrderCalculationRequest orderCalculationRequest, final List<String> sapCatalogProducts,
            final AbstractOrderEntryModel orderEntry) {
        boolean catalogArticle = true;

        if (catalogArticle && !sapCatalogProducts.contains(orderEntry.getProduct().getCode())) {
            catalogArticle = false;
        }
        final OrderEntryRequest requestOrderEntry = new OrderEntryRequest();
        requestOrderEntry.setMaterialNumber(orderEntry.getProduct().getCode());
        requestOrderEntry.setQuantity(orderEntry.getQuantity().longValue());
        requestOrderEntry.setFreeGiftPromotion(orderEntry.getGiveAway() == null ? false : orderEntry.getGiveAway().booleanValue());
        requestOrderEntry.setCustomerReferenceItemLevel(truncateReferenceIfNecessary(orderEntry.getCustomerReference()));

        if (BooleanUtils.isTrue(orderEntry.getQuotation())) {
            // This means this orderEntry is of Quotation type
            requestOrderEntry.setQuotationId(orderEntry.getQuotationId());
            requestOrderEntry.setQuotationItem(orderEntry.getLineNumber());
        }

        orderCalculationRequest.getOrderEntries().add(requestOrderEntry);
        orderCalculationRequest.setFreeShippingPromotion(
                catalogArticle && getConfigurationService().getConfiguration().getBoolean("sap.catalog.order.articles.freeShipping", false));
    }

    protected void addGuestAddress(final AbstractOrderModel order, final AddressModel address, final B2BUnitModel company,
            final OrderCalculationRequest orderCalculationRequest) {
        if (address == null) {
            return;
        }

        final GuestAddress soapGuestAddress = new GuestAddress();

        if (address.getTitle() != null && address.getTitle().getSapCode() != null) {
            soapGuestAddress.setTitle(Title.fromValue(address.getTitle().getSapCode()));
        } else {
            // fallback, title is mandatory
            soapGuestAddress.setTitle(Title.MR_AND_MRS);
        }
        soapGuestAddress.setFirstName(address.getFirstname());
        soapGuestAddress.setLastName(address.getLastname());
        soapGuestAddress.setAdditionalName(address.getAdditionalAddressCompany());
        soapGuestAddress.setCommunicationLanguage(getLocaleFromOrderOrSession(order));
        soapGuestAddress.setPobox(address.getPobox());
        soapGuestAddress.setPostalCode(address.getPostalcode());
        soapGuestAddress.setStreetName(address.getStreetname());
        soapGuestAddress.setStreetNumber(address.getStreetnumber());
        soapGuestAddress.setTown(address.getTown());
        soapGuestAddress.setCountry(address.getCountry() == null ? null : address.getCountry().getIsocode());
        soapGuestAddress.setEmail(address.getEmail());
        soapGuestAddress.setPhoneNumber(StringUtils.isNotEmpty(address.getCellphone()) ? address.getCellphone() : address.getPhone1());
        soapGuestAddress.setVATRegNo(company != null ? company.getVatID() : null);
        orderCalculationRequest.setGuestAddress(soapGuestAddress);
    }

    protected String truncateReferenceIfNecessary(final String customerReference) {
        if (StringUtils.isNotBlank(customerReference)) {
            return customerReference.substring(0, Math.min(customerReference.length(), 35));
        }
        return customerReference;
    }

    protected boolean isB2BCustomer(final B2BCustomerModel contact) {
        return !CustomerType.B2C.equals(contact.getCustomerType());
    }

    protected boolean isGuestCustomer(final B2BCustomerModel contact) {
        return CustomerType.GUEST.equals(contact.getCustomerType());
    }

    protected boolean isB2E(final B2BCustomerModel contact) {
        return getUserService().isMemberOfGroup(contact, getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID));
    }

    protected boolean isOCI(final UserModel contact) {
        return getUserService().isMemberOfGroup(contact, getUserService().getUserGroupForUID(DistConstants.User.OCICUSTOMERGROUP_UID));
    }

    protected UserModel getUser(final AbstractOrderModel order) {
        return isOCI(getUserService().getCurrentUser()) ? getUserService().getCurrentUser() : order.getUser();
    }

    protected String getCurrentSalesOrgCode(final AbstractOrderModel order) {
        final UserModel user = getUser(order);
        if (isCEEShop() && (getUserService().isAnonymousUser(user) || isGuestCustomer((B2BCustomerModel) user))) {
            return getConfigurationService().getConfiguration()
                    .getString("sap.ordercalculation.cee.guest.salesorg", getDistSalesOrgService().getCurrentSalesOrg().getCode());
        }

        return getDistSalesOrgService().getCurrentSalesOrg().getCode();
    }

    protected boolean isCEEShop() {
        return getConfigurationService().getConfiguration().getString("sap.ordercalculation.cee.webshops", "")
                .contains(getCmsSiteService().getCurrentSite().getUid());
    }

    protected String getPaymentMethodCode(AbstractOrderModel source) {
        final DistPaymentModeModel paymentMode = (DistPaymentModeModel) source.getPaymentMode();
        DistPaymentMethodModel erpPaymentMethod = paymentMode == null ? null : paymentMode.getErpPaymentMethod();
        return erpPaymentMethod == null ? null : erpPaymentMethod.getCode();
    }

    protected ShippingMethodCode getShippingMethodCode(AbstractOrderModel source) {
        final DistDeliveryModeModel deliveryMode = (DistDeliveryModeModel) source.getDeliveryMode();
        final DistShippingMethodModel erpDeliveryMethod = deliveryMode == null ? null : deliveryMode.getErpDeliveryMethod();
        return erpDeliveryMethod == null ? null : ShippingMethodCode.fromValue(erpDeliveryMethod.getCode());
    }

    protected String getLocaleFromOrderOrSession(AbstractOrderModel order) {
        String language = null;
        // try to get the language from the order
        if (order instanceof OrderModel && ((OrderModel) order).getLanguage() != null) {
            language = ((OrderModel) order).getLanguage().getIsocode();
        } else if (order instanceof CartModel && ((CartModel) order).getLanguage() != null) {
            language = ((CartModel) order).getLanguage().getIsocode();
        }

        // use the language from the session as fallback
        if (language == null) {
            language = i18NService.getCurrentLocale() != null ? i18NService.getCurrentLocale().getLanguage() : "en";
        }

        return LocaleUtils.toLocale(language).getLanguage().toUpperCase();
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public NamicsCommonI18NService getNamicsCommonI18NService() {
        return namicsCommonI18NService;
    }

    public void setNamicsCommonI18NService(final NamicsCommonI18NService namicsCommonI18NService) {
        this.namicsCommonI18NService = namicsCommonI18NService;
    }
}
