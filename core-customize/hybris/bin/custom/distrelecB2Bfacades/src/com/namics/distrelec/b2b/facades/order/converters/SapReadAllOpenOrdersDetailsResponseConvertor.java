package com.namics.distrelec.b2b.facades.order.converters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.distrelec.webservice.if15.v1.AddressWithId;
import com.distrelec.webservice.if15.v1.OpenOrders;
import com.distrelec.webservice.if15.v1.VoucherResponse;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.delivery.DistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.facades.customer.b2b.budget.DistB2BBudgetFacade;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

public class SapReadAllOpenOrdersDetailsResponseConvertor extends AbstractPopulatingConverter<OpenOrders, OrderData> {

    protected static final Logger LOG = Logger.getLogger(SapReadAllOpenOrdersDetailsResponseConvertor.class);

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    @Qualifier("sap.paymentOptionService")
    private PaymentOptionService paymentOptionService;

    @Autowired
    @Qualifier("sap.shippingOptionService")
    private ShippingOptionService shippingOptionService;

    @Autowired
    private DistrelecCodelistService codelistService;

    @Autowired
    @Qualifier("customerConverter")
    private Converter<UserModel, CustomerData> b2bCustomerConverter;

    @Autowired
    private DistB2BBudgetFacade distB2BBudgetFacade;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    @Qualifier("distVoucherToErpVoucherInfoConverter")
    private Converter<VoucherResponse, DistErpVoucherInfoData> distVoucherToErpVoucherInfoConverter;

    @Autowired
    @Qualifier("defaultProductService")
    private ProductService defaultDistProductService;

    @Autowired
    @Qualifier("defaultDistPaymentModeConverter")
    private Converter<DistPaymentModeModel, DistPaymentModeData> defaultDistPaymentModeConverter;

    @Autowired
    @Qualifier("defaultDistDeliveryModeConverter")
    private Converter<DistDeliveryModeModel, DeliveryModeData> defaultDistDeliveryModeConverter;

    @Autowired
    @Qualifier("productFacade")
    protected DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Override
    public void populate(final OpenOrders source, final OrderData target) {

        target.setOpenOrder(true);

        final BigInteger orderDate = source.getOrderDate();
        if (orderDate != null) {
            target.setOrderDate(SoapConversionHelper.convertDate(orderDate));
        }
        target.setCode(source.getOrderId());
        target.setOpenOrder(true);
        target.setErpOpenOrderCode(source.getOrderId());

        final BigInteger orderCloseDate = source.getOrderCloseDate();
        if (orderCloseDate != null) {
            target.setOpenOrderSelectedClosingDate(SoapConversionHelper.convertDate(orderCloseDate));
        }
        target.setOpenOrderEditableForAllContacts(source.isEditableByAllContacts());
        final String currencyIso = source.getCurrencyCode() == null ? StringUtils.EMPTY : source.getCurrencyCode().value();

        target.setTotalPrice(createPriceData(source.getTotal(), currencyIso));

        target.setSubTotal(createPriceData(source.getSubtotal2(), currencyIso));
        target.setTotalTax(createPriceData(source.getTax(), currencyIso));

        target.setPaymentCost(createPriceData(source.getPaymentPrice(), currencyIso));
        target.setEntries(getEntries(source));

        target.setDeliveryAddress(createAddress(source.getShippingAddressId(), false));
        target.setBillingAddress(createAddress(source.getBillingAddressId(), true));
        target.setDeliveryCost(createPriceData(source.getShippingPrice(), currencyIso));
        if (source.getShippingMethodCode() != null && StringUtils.isNotBlank(source.getShippingMethodCode().value())) {
            try {
                final DistDeliveryModeModel distShippingMode = (DistDeliveryModeModel) shippingOptionService
                                                                                                            .getAbstractDistDeliveryModeForDistShippingMethodCode(source.getShippingMethodCode()
                                                                                                                                                                        .value());

                final DeliveryModeData distDeliveryModeData = getDefaultDistDeliveryModeConverter().convert(distShippingMode);
                // Set the ShippingCost
                distDeliveryModeData.setShippingCost(
                                                     getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(source.getShippingPrice()),
                                                                                  source.getCurrencyCode().name()));
                target.setDeliveryMode(distDeliveryModeData);
            } catch (final Exception e) {
                LOG.warn("Can not resolve delivery mode!", e);
            }
        }

        if (CollectionUtils.isNotEmpty(source.getVouchers())) {
            final VoucherResponse voucher = source.getVouchers().get(0);
            final DistErpVoucherInfoData voucherData = new DistErpVoucherInfoData();
            voucherData.setFixedValue(
                                      getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(voucher.getFixedValue()),
                                                                   source.getCurrencyCode().name()));
            target.setErpVoucherInfoData(getDistVoucherToErpVoucherInfoConverter().convert(voucher, voucherData));
        }

        if (StringUtils.isNotBlank(source.getPaymentMethodCode())) {
            try {
                final DistPaymentModeModel distPaymentMode = (DistPaymentModeModel) getPaymentOptionService()
                                                                                                             .getAbstractDistPaymentModeForErpPaymentModeCode(source.getPaymentMethodCode());

                final DistPaymentModeData distPaymentModeData = getDefaultDistPaymentModeConverter().convert(distPaymentMode);
                // Set the PaymentCost
                distPaymentModeData.setPaymentCost(
                                                   getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(source.getPaymentPrice()),
                                                                                source.getCurrencyCode().name()));
                target.setPaymentMode(distPaymentModeData);
            } catch (final Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        final B2BCostCenterData costCenter = new B2BCostCenterData();
        costCenter.setCode(source.getCostCenterId() != null ? source.getCostCenterId() : "");
        target.setCostCenter(costCenter);
        target.setOpenOrderReference(source.getCustomerReferenceHeaderLevel());
        target.setNote(source.getOrderNote());
        super.populate(source, target);
    }

    /**
     * Convert the open order entries
     *
     * @param source
     *            the open order
     * @return a list of {@link OrderEntryData}
     */
    private List<OrderEntryData> getEntries(final OpenOrders source) {
        if (source.getOrderEntries() == null || source.getOrderEntries().isEmpty()) {
            return Collections.<OrderEntryData> emptyList();
        }

        final String currencyIso = source.getCurrencyCode().value();
        return source.getOrderEntries().stream().map(orderReadEntry -> {
            final OrderEntryData orderEntryData = new OrderEntryData();
            orderEntryData.setBasePrice(createPriceData(orderReadEntry.getPrice(), currencyIso));
            orderEntryData.setEntryNumber(Integer.valueOf(orderReadEntry.getMaterialNumber()));
            orderEntryData.setQuantity(Long.valueOf(orderReadEntry.getOrderQuantity()));
            try {
                final ProductModel product = defaultDistProductService.getProductForCode(orderReadEntry.getMaterialNumber());
                if (product != null) {
                    orderEntryData.setProduct(productFacade.getProductForCodeAndOptions(product.getCode(), Arrays.asList(ProductOption.BASIC,
                                                                                                                         ProductOption.SUMMARY,
                                                                                                                         ProductOption.DESCRIPTION,
                                                                                                                         ProductOption.PROMOTION_LABELS,
                                                                                                                         ProductOption.DIST_MANUFACTURER)));
                    if (orderEntryData.getProduct().getManufacturer() == null && product.getManufacturer() != null) {
                        orderEntryData.getProduct().setManufacturer(product.getManufacturer().getName());
                    }
                }
            } catch (final Exception e) {
                LOG.warn(e.getMessage(), e);
            }
            orderEntryData.setTotalPrice(createPriceData(orderReadEntry.getTotal(), currencyIso));
            orderEntryData.setTotalListPrice(createPriceData(orderReadEntry.getTotal(), currencyIso));

            // DISTRELEC-11538: Populating Stack And Trace Elements
            if (null != orderReadEntry.getDeliveryDate()) {
                orderEntryData.setDeliveryDate(new Date(orderReadEntry.getDeliveryDate().longValue()));
            }
            orderEntryData.setDeliveryId(orderReadEntry.getDeliveryId());
            orderEntryData.setDeliveryQuantity(orderReadEntry.getDeliveryQuantity());
            orderEntryData.setDeliveryTrackingUrl(orderReadEntry.getDeliveryTrackingUrl());
            return orderEntryData;
        }).collect(Collectors.toList());
    }

    /**
     * Create an instance of {@code AddressData} from the specified {@code AddressWithId}.
     *
     * @param address
     * @return an instance of {@code AddressData}
     */
    protected AddressData createAddress(final AddressWithId address, final boolean isBillingAddress) {
        final AddressData distAddressData = new AddressData();

        if (address != null) {
            distAddressData.setCompanyName(address.getCompanyName1());
            distAddressData.setCompanyName2(address.getCompanyName2());
            distAddressData.setLastName(address.getLastName());

            distAddressData.setFirstName(address.getFirstName());

            distAddressData.setLine1(address.getStreetName());
            distAddressData.setLine2(address.getStreetNumber());

            distAddressData.setPobox(address.getPobox());
            distAddressData.setTown(address.getTown());
            distAddressData.setPostalCode(address.getPostalCode());

            if (address.getCountry() != null) {
                final CountryModel countryModel = commonI18NService.getCountry(address.getCountry());
                final CountryData countryData = new CountryData();
                countryData.setIsocode(countryModel.getIsocode());
                countryData.setName(countryModel.getName());
                distAddressData.setCountry(countryData);
            }

            distAddressData.setPhone(address.getPhoneNumber());
        }

        distAddressData.setBillingAddress(isBillingAddress);
        distAddressData.setShippingAddress(!isBillingAddress);

        return distAddressData;
    }

    protected PriceData createPriceData(final double value, final String currencyIso) {
        final Double val = Double.valueOf(value);
        Assert.notNull(val, "Price value cannot be null.");
        return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(val != null ? val.doubleValue() : 0), currencyIso);
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public PaymentOptionService getPaymentOptionService() {
        return paymentOptionService;
    }

    public void setPaymentOptionService(final PaymentOptionService paymentOptionService) {
        this.paymentOptionService = paymentOptionService;
    }

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(final DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public Converter<UserModel, CustomerData> getB2bCustomerConverter() {
        return b2bCustomerConverter;
    }

    public void setB2bCustomerConverter(final Converter<UserModel, CustomerData> b2bCustomerConverter) {
        this.b2bCustomerConverter = b2bCustomerConverter;
    }

    public ProductService getDefaultDistProductService() {
        return defaultDistProductService;
    }

    public void setDefaultDistProductService(final ProductService defaultDistProductService) {
        this.defaultDistProductService = defaultDistProductService;
    }

    public Converter<DistPaymentModeModel, DistPaymentModeData> getDefaultDistPaymentModeConverter() {
        return defaultDistPaymentModeConverter;
    }

    public void setDefaultDistPaymentModeConverter(final Converter<DistPaymentModeModel, DistPaymentModeData> defaultDistPaymentModeConverter) {
        this.defaultDistPaymentModeConverter = defaultDistPaymentModeConverter;
    }

    public Converter<DistDeliveryModeModel, DeliveryModeData> getDefaultDistDeliveryModeConverter() {
        return defaultDistDeliveryModeConverter;
    }

    public void setDefaultDistDeliveryModeConverter(final Converter<DistDeliveryModeModel, DeliveryModeData> defaultDistDeliveryModeConverter) {
        this.defaultDistDeliveryModeConverter = defaultDistDeliveryModeConverter;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ShippingOptionService getShippingOptionService() {
        return shippingOptionService;
    }

    public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
        this.shippingOptionService = shippingOptionService;
    }

    public DistB2BBudgetFacade getDistB2BBudgetFacade() {
        return distB2BBudgetFacade;
    }

    public void setDistB2BBudgetFacade(final DistB2BBudgetFacade distB2BBudgetFacade) {
        this.distB2BBudgetFacade = distB2BBudgetFacade;
    }

    public Converter<VoucherResponse, DistErpVoucherInfoData> getDistVoucherToErpVoucherInfoConverter() {
        return distVoucherToErpVoucherInfoConverter;
    }

    public void setDistVoucherToErpVoucherInfoConverter(final Converter<VoucherResponse, DistErpVoucherInfoData> distVoucherToErpVoucherInfoConverter) {
        this.distVoucherToErpVoucherInfoConverter = distVoucherToErpVoucherInfoConverter;
    }
}
