package com.namics.distrelec.b2b.core.inout.erp.converters;

import com.distrelec.webservice.if15.v1.AddressWithId;
import com.distrelec.webservice.if15.v1.OpenOrders;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;
import com.namics.distrelec.b2b.core.service.user.DistAddressService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.daos.CountryDao;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class ErpOpenOrderExtModelConvertor extends AbstractPopulatingConverter<OpenOrders, ErpOpenOrderExtModel> {

    private static final Logger LOG = Logger.getLogger(ErpOpenOrderExtModelConvertor.class);
    private UserService userService;
    private CountryDao countryDao;
    private DistAddressService addressService;
    private PaymentOptionService paymentOptionService;
    private ShippingOptionService shippingOptionService;

    @Override
    protected ErpOpenOrderExtModel createTarget() {
        return new ErpOpenOrderExtModel();
    }

    @Override
    public void populate(final OpenOrders source, final ErpOpenOrderExtModel target) {
        target.setEditableByAllContacts(source.isEditableByAllContacts());

        target.setErpCustomerId(source.getCustomerId());
        target.setErpContactId(source.getContactId());
        target.setOrderStatus(source.getOrderStatus());
        target.setErpOrderId(source.getOrderId());

        target.setOrderCloseDate(SoapConversionHelper.convertDate(source.getOrderCloseDate()));
        target.setOrderDate(SoapConversionHelper.convertDate(source.getOrderDate()));

        final String erpBillingAddressId = source.getBillingAddressId() == null ? null : source.getBillingAddressId().getAddressId();
        final String erpShippingAddressId = source.getShippingAddressId() == null ? null : source.getShippingAddressId().getAddressId();

        target.setErpBillingAddressId(erpBillingAddressId);
        target.setErpShippingAddressId(erpShippingAddressId);

        if (erpBillingAddressId.equalsIgnoreCase(erpShippingAddressId)) {
            final AddressModel address = getAddressFromResponse(source.getBillingAddressId(), source.getOrderId(), Boolean.TRUE, Boolean.TRUE);
            target.setErpBillingAddress(address);
            target.setErpShippingAddress(address);
        } else {
            target.setErpBillingAddress(getAddressFromResponse(source.getBillingAddressId(), source.getOrderId(), Boolean.TRUE, Boolean.FALSE));
            target.setErpShippingAddress(getAddressFromResponse(source.getShippingAddressId(), source.getOrderId(), Boolean.FALSE, Boolean.TRUE));
        }

        if (StringUtils.isNotBlank(source.getPaymentMethodCode())) {
            target.setErpPaymentMode(getPaymentModeForErpCode(source.getPaymentMethodCode()));
        }

        if (source.getShippingMethodCode() != null & StringUtils.isNotBlank(source.getShippingMethodCode().value())) {
            target.setErpDeliveryMode(getDeliveryModeForErpCode(source.getShippingMethodCode().value()));
        }

        target.setOrderReferenceHeaderLevel(source.getCustomerReferenceHeaderLevel());
        // target.setPossibleClosingDates(OpenOrderHelper.getOpenOrderClosingDates((B2BCustomerModel) getUserService().getCurrentUser()));

    }

    private AddressModel getAddressFromResponse(final AddressWithId responseAddress, final String orderId, final Boolean isBilling, final Boolean isShipping) {
        if (responseAddress == null) {
            return null;
        }
        final String addressId = getAddressID(responseAddress, orderId);
        final AddressModel address = getAddressOrCreate(addressId);
        address.setDuplicate(Boolean.TRUE);
        address.setBillingAddress(isBilling);
        address.setShippingAddress(isShipping);

        address.setErpAddressID(addressId);
        address.setCompany(responseAddress.getCompanyName1());
        address.setCompanyName2(responseAddress.getCompanyName2());
        address.setCompanyName3(responseAddress.getCompanyName3());
        // address.setTitle(getTitleFromResponse(responseAddress.getTitle()));
        address.setFirstname(responseAddress.getFirstName());
        address.setLastname(responseAddress.getLastName());
        address.setStreetname(responseAddress.getStreetName());
        address.setStreetnumber(responseAddress.getStreetNumber());
        address.setPobox(responseAddress.getPobox());
        address.setPostalcode(responseAddress.getPostalCode());
        address.setTown(responseAddress.getTown());
        // address.setRegion(value);
        address.setCountry(getCountryFromResponse(responseAddress.getCountry()));
        address.setPhone1(responseAddress.getPhoneNumber());
        return address;
    }

    private String getAddressID(final AddressWithId responseAddress, final String orderId) {
        final StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.isBlank(orderId) ? "" : orderId);
        sb.append(StringUtils.isBlank(orderId) ? "" : "-");
        sb.append(responseAddress.getAddressId());
        return sb.toString();

    }

    private AddressModel getAddressOrCreate(final String addressId) {
        final AddressModel address = addressService.getAddressByErpAddressId(addressId, Boolean.TRUE);
        return address == null ? new AddressModel() : address;
    }

    private CountryModel getCountryFromResponse(final String code) {
        if (StringUtils.isNotBlank(code)) {
            final List<CountryModel> results = countryDao.findCountriesByCode(code);
            return CollectionUtils.isEmpty(results) ? null : results.get(0);
        }
        return null;
    }

    private AbstractDistPaymentModeModel getPaymentModeForErpCode(final String erpCode) {
        try {
            return StringUtils.isNotBlank(erpCode) ? getPaymentOptionService().getAbstractDistPaymentModeForErpPaymentModeCode(erpCode) : null;
        } catch (final ModelNotFoundException e) {
            LOG.warn("Can not find hybris payment mode for SAP payment code " + erpCode);
        }
        return null;
    }

    private AbstractDistDeliveryModeModel getDeliveryModeForErpCode(final String erpCode) {
        try {
            return StringUtils.isNotBlank(erpCode) ? getShippingOptionService().getAbstractDistDeliveryModeForDistShippingMethodCode(erpCode) : null;
        } catch (final ModelNotFoundException mnfex) {
            LOG.warn("Can not find hybris delivery mode for SAP delivery code " + erpCode);
        }
        return null;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public CountryDao getCountryDao() {
        return countryDao;
    }

    public void setCountryDao(final CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    public DistAddressService getAddressService() {
        return addressService;
    }

    public void setAddressService(final DistAddressService addressService) {
        this.addressService = addressService;
    }

    public PaymentOptionService getPaymentOptionService() {
        return paymentOptionService;
    }

    public void setPaymentOptionService(final PaymentOptionService paymentOptionService) {
        this.paymentOptionService = paymentOptionService;
    }

    public ShippingOptionService getShippingOptionService() {
        return shippingOptionService;
    }

    public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
        this.shippingOptionService = shippingOptionService;
    }

}
