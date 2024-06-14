package com.namics.distrelec.b2b.facades.order.converters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.distrelec.webservice.if15.v1.*;
import com.distrelec.webservice.if19.v1.ItemList;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsResponse;
import com.distrelec.webservice.if19.v1.ReturnReasons;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistOrderChannelModel;
import com.namics.distrelec.b2b.core.model.delivery.DistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.core.rma.enums.RmaMainReturnReasonTypes;
import com.namics.distrelec.b2b.core.rma.enums.RmaSubReturnReasonTypes;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;
import com.namics.distrelec.b2b.facades.customer.b2b.budget.DistB2BBudgetFacade;
import com.namics.distrelec.b2b.facades.order.data.DistAvailabilityData;
import com.namics.distrelec.b2b.facades.order.data.DistDiscountData;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.converter.ReadOrderResponseWraper;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class SapReadOrderResponseOrderHistoryConverter extends AbstractPopulatingConverter<ReadOrderResponseWraper, OrderData> {

    protected static final Logger LOG = LoggerFactory.getLogger(SapReadOrderResponseOrderHistoryConverter.class);

    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                                                                               ProductOption.PROMOTION_LABELS, ProductOption.DIST_MANUFACTURER);

    public static final String MAIN_REASON_TEXT = "cart.return.items.return.mainReason";

    public static final String NOT_RECIEVED_DEFAULT_VALUE = "011";

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
    @Qualifier("b2BCustomerConverter")
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
    @Qualifier("defaultDistRMAEntryDataConverter")
    private Converter<ItemList, DistRMAEntryData> defaultDistRMADataConverter;

    @Autowired
    protected DistrelecProductFacade productFacade;

    @Override
    public void populate(final ReadOrderResponseWraper readOrderResponseWraper, final OrderData target) {
        final ReadOrderResponseV2 readOrderResponse = readOrderResponseWraper.getReadOrderResponse();
        final RMAGetOrderItemsResponse rmaGetOrderItemsResponse = readOrderResponseWraper.getRmaGetOrderItemsResponse();
        final OrderData distOrderData = (OrderData) target;

        distOrderData.setPlacedBy(readOrderResponse.getContactName());

        final B2BCustomerModel customer = readOrderResponseWraper.getB2BCustomerModel();
        if (customer != null) {
            if (customer.getBudget() != null && distOrderData.getB2bCustomerData() != null) {
                distB2BBudgetFacade.calculateExceededBudget(distOrderData.getB2bCustomerData().getBudget(),
                                                            distOrderData.getTotalPrice());
            }
            final CustomerData b2BCustomerData = b2bCustomerConverter.convert(customer);
            if (b2BCustomerData != null) {
                distOrderData.setB2bCustomerData(b2BCustomerData);
                distOrderData.setCustomerType(b2BCustomerData.getCustomerType().getCode());
                if (distOrderData.getPlacedBy() == null) {
                    distOrderData.setPlacedBy(b2BCustomerData.getName());
                }
            }
        }

        setOrderDate(readOrderResponse, distOrderData);
        distOrderData.setCode(readOrderResponse.getOrderId());
        setOrderChannel(readOrderResponse, distOrderData);
        setOrderStatus(readOrderResponse, distOrderData);
        distOrderData.setDeliveryDate(null);
        setReturnReasons(rmaGetOrderItemsResponse, distOrderData);

        /** Line items start from here */

        final String currencyIso = readOrderResponse.getCurrencyCode().value();

        distOrderData.setTotalPrice(createPriceData(readOrderResponse.getTotal(), currencyIso));
        final List<DistDeliveryData> deliveryDataList = new ArrayList<>();
        final Deliveries deliveries = readOrderResponse.getDeliveries();
        if (null != deliveries && null != deliveries.getDelivery() && !deliveries.getDelivery().isEmpty()) {
            for (final Delivery delivery : deliveries.getDelivery()) {
                final DistDeliveryData deliveryData = new DistDeliveryData();
                final List<DistHandlingUnit> handlingUnits = new ArrayList<>();
                if (null != delivery.getHandlingUnits() && !delivery.getHandlingUnits().isEmpty()) {
                    for (final HandlingUnit handlingUnit : delivery.getHandlingUnits()) {
                        final DistHandlingUnit distHandlingUnit = new DistHandlingUnit();
                        distHandlingUnit.setUnitID(handlingUnit.getHandlingUnitID());
                        distHandlingUnit.setTrackingURL(handlingUnit.getHandlingUnitTrackingURL());
                        final List<DistHandlingUnitItem> handlingUnitItems = new ArrayList<>();
                        if (null != handlingUnit.getItems() && !handlingUnit.getItems().isEmpty()) {
                            for (final Item item : handlingUnit.getItems()) {
                                final DistHandlingUnitItem distHandlingUnitItem = new DistHandlingUnitItem();
                                distHandlingUnitItem.setArticleName(item.getArticleName());
                                distHandlingUnitItem.setManufacturer(item.getManufacturer());
                                distHandlingUnitItem.setMaterialID(item.getMaterialID());
                                distHandlingUnitItem.setQuantity(BigDecimal.valueOf(item.getQuantity()));
                                distHandlingUnitItem.setType(item.getType());
                                distHandlingUnitItem.setUnit(item.getUnit());
                                handlingUnitItems.add(distHandlingUnitItem);
                            }
                        }
                        distHandlingUnit.setHandlingUnitItem(handlingUnitItems);
                        handlingUnits.add(distHandlingUnit);
                    }
                }
                deliveryData.setDeliveryID(delivery.getDeliveryID());
                deliveryData.setHandlingUnit(handlingUnits);
                deliveryData.setDeliveryDate(null != delivery.getDeliveryDate() ? delivery.getDeliveryDate().toGregorianCalendar().getTime() : null);
                deliveryDataList.add(deliveryData);
            }
        }
        distOrderData.setDeliveryData(deliveryDataList);
        // DISTRELEC-4490: subtotal must be filled with subtotal1
        if (CollectionUtils.isNotEmpty(readOrderResponse.getOrderDiscounts())) {
            distOrderData.setSubTotal(createPriceData(readOrderResponse.getSubtotal2(), currencyIso));
        } else {
            distOrderData.setSubTotal(createPriceData(readOrderResponse.getSubtotal1(), currencyIso));
        }
        distOrderData.setTotalTax(createPriceData(readOrderResponse.getTax(), currencyIso));

        distOrderData.setPaymentCost(createPriceData(readOrderResponse.getPaymentPrice(), currencyIso));
        distOrderData.setEntries(getEntries(readOrderResponse, rmaGetOrderItemsResponse));
        distOrderData.setDeliveryAddress(createAddress(readOrderResponse.getShippingAddress(), false));
        distOrderData.setBillingAddress(createAddress(readOrderResponse.getBillingAddress(), true));
        distOrderData.setDeliveryCost(createPriceData(readOrderResponse.getShippingPrice(), currencyIso));

        if (readOrderResponse.getShippingMethodCode() != null && StringUtils.isNotBlank(readOrderResponse.getShippingMethodCode().value())) {
            try {
                final DistDeliveryModeModel distShippingMode = (DistDeliveryModeModel) shippingOptionService
                                                                                                            .getAbstractDistDeliveryModeForDistShippingMethodCode(readOrderResponse.getShippingMethodCode()
                                                                                                                                                                                   .value());

                final DeliveryModeData distDeliveryModeData = defaultDistDeliveryModeConverter.convert(distShippingMode);
                distDeliveryModeData.setShippingCost(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(readOrderResponse.getShippingPrice()),
                                                                             readOrderResponse.getCurrencyCode().name()));

                distOrderData.setDeliveryMode(distDeliveryModeData);
            } catch (final Exception e) {
                LOG.warn("Can not resolve delivery mode!", e);
            }
        }

        if (CollectionUtils.isNotEmpty(readOrderResponse.getVouchers())) {
            // Just one voucher is allowed per order
            final VoucherResponse voucher = readOrderResponse.getVouchers().get(0);
            final DistErpVoucherInfoData voucherData = new DistErpVoucherInfoData();
            voucherData.setFixedValue(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(voucher.getFixedValue()),
                                                              readOrderResponse.getCurrencyCode().name()));
            distOrderData.setErpVoucherInfoData(distVoucherToErpVoucherInfoConverter.convert(voucher, voucherData));
        }

        if (CollectionUtils.isNotEmpty(readOrderResponse.getOrderDiscounts())) {
            distOrderData.setDiscounts(readOrderResponse.getOrderDiscounts().stream().flatMap(orderDiscount -> {
                final DistDiscountData fixedDiscount = new DistDiscountData();
                fixedDiscount.setAbsolute(Boolean.TRUE);
                fixedDiscount.setValue(null != orderDiscount ? Double.valueOf(orderDiscount.getFixedValue()) : Double.valueOf(0));
                final CurrencyData currencyData = new CurrencyData();
                currencyData.setIsocode(currencyIso);
                fixedDiscount.setCurrency(currencyData);

                final DistDiscountData percentageDiscount = new DistDiscountData();
                percentageDiscount.setAbsolute(Boolean.FALSE);
                percentageDiscount.setValue(null != orderDiscount && null != orderDiscount.getPercentage() ? orderDiscount.getPercentage() : Double.valueOf(0));

                return Stream.of(fixedDiscount, percentageDiscount);
            }).collect(Collectors.toList()));
        }

        if (StringUtils.isNotBlank(readOrderResponse.getPaymentMethodCode())) {
            try {
                final DistPaymentModeModel distPaymentMode = (DistPaymentModeModel) paymentOptionService
                                                                                                        .getAbstractDistPaymentModeForErpPaymentModeCode(readOrderResponse.getPaymentMethodCode());

                final DistPaymentModeData distPaymentModeData = defaultDistPaymentModeConverter.convert(distPaymentMode);
                distPaymentModeData.setPaymentCost(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(readOrderResponse.getPaymentPrice()),
                                                                           readOrderResponse.getCurrencyCode().name()));

                distOrderData.setPaymentMode(distPaymentModeData);
            } catch (final Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        final B2BCostCenterData costCenter = new B2BCostCenterData();
        costCenter.setCode(readOrderResponse.getCostCenterId() != null ? readOrderResponse.getCostCenterId() : StringUtils.EMPTY);
        distOrderData.setCostCenter(costCenter);
        distOrderData.setProjectNumber(readOrderResponse.getCustomerReferenceHeaderLevel());
        distOrderData.setNote(readOrderResponse.getOrderNote());
        distOrderData.setPlacedBy(readOrderResponse.getContactName());

        super.populate(readOrderResponseWraper, target);
    }

    private void setReturnReasons(RMAGetOrderItemsResponse rmaGetOrderItemsResponse, OrderData distOrderData) {
        final List<ReturnReasons> returnReasons = (null != rmaGetOrderItemsResponse && null != rmaGetOrderItemsResponse.getReturnReasons()
                && null != rmaGetOrderItemsResponse.getReturnReasons().getReturnReason()) ? rmaGetOrderItemsResponse.getReturnReasons().getReturnReason()
                                                                                          : Collections.emptyList();

        List<RmaSubReturnReasonTypes> returnReasonSubTypes = returnReasons.stream()
                                                                          .map(ReturnReasons::getReturnReasonID)
                                                                          .map(RmaSubReturnReasonTypes::getByCode)
                                                                          .filter(Objects::nonNull)
                                                                          .collect(Collectors.toList());

        List<RmaMainReturnReasonTypes> returnReasonTypes = getAllMatchingMainReturnTypes(returnReasonSubTypes);

        distOrderData.setReturnReason(getDistMainReasonData(returnReasonSubTypes, returnReasonTypes));
    }

    private List<DistMainReasonData> getDistMainReasonData(List<RmaSubReturnReasonTypes> returnReasonSubTypes,
                                                           List<RmaMainReturnReasonTypes> returnReasonTypes) {
        List<DistMainReasonData> mainReasons = new ArrayList<>();

        for (RmaMainReturnReasonTypes type : returnReasonTypes) {
            DistMainReasonData mainReason = new DistMainReasonData();
            mainReason.setMainReasonId(type.getCode());
            mainReason.setMainReasonText(MAIN_REASON_TEXT + type.getCode());

            List<DistSubReasonData> subReasons;
            if (isSupportingSubReasons(type)) {
                subReasons = returnReasonSubTypes
                                                 .stream()
                                                 .filter(subType -> type.getSubTypes().contains(subType))
                                                 .map(subType -> {
                                                     DistSubReasonData subReason = new DistSubReasonData();
                                                     subReason.setSubReasonId(subType.getCode());
                                                     subReason.setSubReasonMessages(subType.getMessageKeys());
                                                     return subReason;
                                                 }).collect(Collectors.toList());
            } else {
                mainReason.setDefaultSubReasonId(NOT_RECIEVED_DEFAULT_VALUE);
                subReasons = Collections.emptyList();
            }

            mainReason.setSubReasons(subReasons);
            mainReasons.add(mainReason);
        }
        return mainReasons;
    }

    private boolean isSupportingSubReasons(RmaMainReturnReasonTypes type) {
        return !type.equals(RmaMainReturnReasonTypes.NOT_RECEIVED);
    }

    private List<RmaMainReturnReasonTypes> getAllMatchingMainReturnTypes(List<RmaSubReturnReasonTypes> returnReasonSubTypes) {
        return Stream.of(RmaMainReturnReasonTypes.values())
                     .filter(reasonType -> CollectionUtils.containsAny(reasonType.getSubTypes(), returnReasonSubTypes))
                     .collect(Collectors.toList());
    }

    private void setOrderDate(ReadOrderResponseV2 readOrderResponse, OrderData distOrderData) {
        final BigInteger orderDate = readOrderResponse.getOrderDate();
        if (orderDate != null) {
            distOrderData.setOrderDate(SoapConversionHelper.convertDate(orderDate));
        }
    }

    private void setOrderStatus(ReadOrderResponseV2 readOrderResponse, OrderData distOrderData) {
        if (StringUtils.isNotBlank(readOrderResponse.getOrderStatus())) {
            try {
                distOrderData.setStatus(codelistService.getDistOrderStatus(readOrderResponse.getOrderStatus()).getHybrisOrderStatus());
            } catch (final Exception exp) {
                LOG.debug(exp.getMessage(), exp);
            }
        }
    }

    private void setOrderChannel(ReadOrderResponseV2 readOrderResponse, OrderData distOrderData) {
        try {
            final DistOrderChannelModel orderChannel = codelistService.getDistOrderChannel(readOrderResponse.getOrderChannel());
            distOrderData.setSalesApplication(orderChannel.getName());
        } catch (final NotFoundException e) {
            distOrderData.setSalesApplication(SalesApplication.WEB.getCode());
        }
    }

    private List<OrderEntryData> getEntries(final ReadOrderResponseV2 readOrderResponse, final RMAGetOrderItemsResponse rmaGetOrderItemsResponse) {
        if (CollectionUtils.isEmpty(readOrderResponse.getOrderEntries())) {
            return Collections.emptyList();
        }
        final List<ItemList> returnItems = (null != rmaGetOrderItemsResponse && null != rmaGetOrderItemsResponse.getItems())
                                                                                                                             ? rmaGetOrderItemsResponse.getItems()
                                                                                                                                                       .getItem()
                                                                                                                             : new ArrayList<>();
        final String currencyIso = readOrderResponse.getCurrencyCode().value();

        final List<OrderEntryData> orderEntries = new ArrayList<>();
        for (final OrderReadEntry orderReadEntry : readOrderResponse.getOrderEntries()) {
            final OrderEntryData orderEntryData = new OrderEntryData();
            final BigInteger deliveryDate = orderReadEntry.getDeliveryDate();
            final BigInteger estimatedDeliveryDate = orderReadEntry.getEstimatedDeliveryDate();
            final ItemList returnItem = returnItems.stream().filter(rmaItem -> rmaItem.getItemNumber().equalsIgnoreCase(orderReadEntry.getOrderPosition()))
                                                   .findFirst().orElse(null);

            if (null != returnItem) {
                final DistRMAEntryData rmaData = defaultDistRMADataConverter.convert(returnItem);
                orderEntryData.setRmaData(rmaData);
            }
            orderEntryData.setItemPosition(orderReadEntry.getOrderPosition());
            orderEntryData.setCustomerReference(orderReadEntry.getCustomerReferenceItemLevel());
            orderEntryData.setBasePrice(createPriceData(orderReadEntry.getPrice(), currencyIso));
            orderEntryData.setEntryNumber(Integer.valueOf(orderReadEntry.getMaterialNumber()));
            orderEntryData.setQuantity(orderReadEntry.getOrderQuantity());
            if (deliveryDate != null && !deliveryDate.equals(BigInteger.ZERO)) {
                orderEntryData.setDeliveryDate(SoapConversionHelper.convertDate(orderReadEntry.getDeliveryDate()));
            }
            if (estimatedDeliveryDate != null && !estimatedDeliveryDate.equals(BigInteger.ZERO)) {
                orderEntryData.setRequestedDeliveryDateItemLevel(SoapConversionHelper.convertDate(orderReadEntry.getEstimatedDeliveryDate()));
            }
            try {
                final ProductModel product = defaultDistProductService.getProductForCode(orderReadEntry.getMaterialNumber());
                if (product != null) {
                    orderEntryData.setProduct(productFacade.getProductForCodeAndOptions(product.getCode(), PRODUCT_OPTIONS));
                    if (orderEntryData.getProduct().getManufacturer() == null && product.getManufacturer() != null) {
                        orderEntryData.getProduct().setManufacturer(product.getManufacturer().getName());
                    }
                }
            } catch (final Exception e) {
                LOG.warn(e.getMessage(), e);
            }
            if (orderReadEntry.getPendingQuantity() > 0) {
                final List<DistAvailabilityData> availabilities = new ArrayList<>();
                final DistAvailabilityData availableData = new DistAvailabilityData();
                availableData.setEstimatedDate(SoapConversionHelper.convertDate(orderReadEntry.getEstimatedDeliveryDate()));
                availableData.setQuantity(orderReadEntry.getPendingQuantity());
                availabilities.add(availableData);
                orderEntryData.setAvailabilities(availabilities);
            }
            orderEntryData.setTotalPrice(createPriceData(orderReadEntry.getTotal(), currencyIso));
            orderEntryData.setTotalListPrice(createPriceData(orderReadEntry.getTotal(), currencyIso));
            orderEntryData.setTotalAfterDiscountPrice(createPriceData(orderReadEntry.getTotalAfterDiscount(), currencyIso));
            orderEntryData.setDeliveryId(orderReadEntry.getDeliveryId());
            orderEntryData.setDeliveryQuantity(orderReadEntry.getDeliveryQuantity());
            orderEntryData.setDeliveryTrackingUrl(orderReadEntry.getDeliveryTrackingUrl());
            orderEntryData.setDiscountAmount(createPriceData(orderReadEntry.getDiscount(), currencyIso));
            orderEntries.add(orderEntryData);
        }
        return orderEntries;
    }

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
            distAddressData.setPhone(address.getPhoneNumber());

            if (StringUtils.isNotBlank(address.getCountry())) {
                final CountryModel countryModel = commonI18NService.getCountry(address.getCountry());
                final CountryData countryData = new CountryData();
                countryData.setIsocode(countryModel.getIsocode());
                countryData.setName(countryModel.getName());
                distAddressData.setCountry(countryData);
            }
        }

        distAddressData.setBillingAddress(isBillingAddress);
        distAddressData.setShippingAddress(!isBillingAddress);

        return distAddressData;
    }

    protected PriceData createPriceData(final double value, final String currencyIso) {
        final Double val = value;
        Assert.notNull(val, "Price value cannot be null.");
        return priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(val != null ? val.doubleValue() : 0), currencyIso);
    }
}
