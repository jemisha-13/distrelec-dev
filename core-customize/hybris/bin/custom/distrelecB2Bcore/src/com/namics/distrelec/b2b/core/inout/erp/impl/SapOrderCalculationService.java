/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static com.namics.distrelec.b2b.core.service.process.payment.PaymentNotifyProcessHelper.buildProcessErpCodeEventCode;
import static com.namics.distrelec.b2b.core.util.DistUtils.isInvoicePaymentMode;
import static com.namics.distrelec.b2b.core.util.PaymentUtils.*;
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.ws.WebServiceException;

import com.namics.distrelec.b2b.core.inout.erp.exception.TemporaryQualityBlockException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.if11.v3.*;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.ErpErrorHandlerService;
import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;
import com.namics.distrelec.b2b.core.inout.erp.converters.request.OpenOrderCalculationRequestConverter;
import com.namics.distrelec.b2b.core.inout.erp.converters.request.OrderCalculationRequestConverter;
import com.namics.distrelec.b2b.core.inout.erp.converters.response.OpenOrderCalculationResponsePopulator;
import com.namics.distrelec.b2b.core.inout.erp.converters.response.OrderCalculationResponsePopulator;
import com.namics.distrelec.b2b.core.inout.erp.exception.MoqConversionException;
import com.namics.distrelec.b2b.core.inout.erp.exception.ProductStatusMisalignmentException;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.service.order.exceptions.CustomerBlockedInErpException;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.core.util.DistXmlUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

/**
 * SAP implementation of the <code>OrderCalculationService</code>.
 *
 * @author pbueschi, ksperner, Namics AG
 * @since Distrelec 1.0
 */
public class SapOrderCalculationService extends AbstractSapService implements OrderCalculationService {

    private static final Logger LOG = LogManager.getLogger(SapOrderCalculationService.class);

    private static final String SERIALIZE_XML_KEY = "distrelec.order.calculation.serializeResponse";

    private static final String SAP_SIMULATE_ERROR = "Could not get order simulation from SAP PI";

    private static final String CUSTOMER_BLOCKED_IN_SAP = "CUSTBLOCK";

    private OrderCalculationRequestConverter orderCalculationRequestConverter;

    private OrderCalculationResponsePopulator orderCalculationResponsePopulator;

    private OpenOrderCalculationRequestConverter<AbstractOrderModel> openOrderCalculationRequestConverter;

    private OpenOrderCalculationResponsePopulator openOrderCalculationResponsePopulator;

    private SIHybrisIF11V1Out webServiceClientIF11;

    private SIHybrisV1Out webServiceClient;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private B2BCustomerService b2bCustomerService;

    @Autowired
    private BusinessProcessService businessProcessService;

    @Autowired
    private ErpErrorHandlerService erpErrorHandlerService;

    public SapOrderCalculationService() {
        super(DistConstants.CacheName.ORDER_CALCULATION);
    }

    @Override
    public void calculate(final AbstractOrderModel abstractOrder, final boolean simulate) throws CalculationException {
        if (abstractOrder != null && abstractOrder.isOpenOrder() && StringUtils.isNotEmpty(abstractOrder.getErpOpenOrderCode())) {
            calculateOpenOrder(abstractOrder, simulate, null);
        } else {
            calculateOrder(abstractOrder, simulate);
        }
    }

    @Override
    public void calculateOpenOrder(final AbstractOrderModel abstractOrder, final boolean simulate, final String openOrderCode)
                                                                                                                               throws CalculationException {

        final OpenOrderCalculationRequest request = openOrderCalculationRequestConverter.convert(abstractOrder);
        OpenOrderCalculationResponse response = null;

        try {
            if (isRealOrder(simulate)) {
                LOG.info("Going to place an openorder which Hybris OrderId is = {}", abstractOrder.getCode());
            }

            request.setSimulateOrder(simulate);
            // request.setOrderId(openOrderCode);

            response = executeOpenOrderCalculationRequest(request);

            if (isRealOrder(simulate)) {
                LOG.info("finished with placing sap openorder. Hybris OrderId = {} and Sap openorder id = {}", abstractOrder.getCode(), response.getOrderId());
            }

            openOrderCalculationResponsePopulator.populate(response, abstractOrder);

            if (isRealOrder(simulate)) {
                // set the ERP Order code
                abstractOrder.setErpOrderCode(response.getOrderId());
                // always save response xml
                if (saveXML()) {
                    abstractOrder.setXmlOrderCalculationResult(DistXmlUtils.soapToString(response, OpenOrderCalculationResponse.class));
                }
                modelService.save(abstractOrder);
                // Trigger the ERP code event
                triggerErpCodeEvent(abstractOrder, response.getOrderId());
            }
        } catch (final Exception exec) {
            DistLogUtils.logError(LOG, "{} {} Error occurred while placing order into sap. See below the request XML, Hybris OrderId is = {}, {}", exec,
                                  ErrorLogCode.PLACEORDER_ERROR, ErrorSource.SAP_FAULT, abstractOrder.getCode());
            LOG.error("\n request: {}\n response: {}", DistXmlUtils.soapToString(request, OpenOrderCalculationRequest.class),
                      DistXmlUtils.soapToString(response, OpenOrderCalculationResponse.class));
            // DISTRELEC-7746: save order Calculation request xml in case of these error
            if (saveXML()) {
                modelService.save(abstractOrder);
            }
            throw new CalculationException(SAP_SIMULATE_ERROR, exec);
        }

    }

    private boolean saveXML() {
        return configurationService.getConfiguration().getBoolean(SERIALIZE_XML_KEY, false);
    }

    private boolean isRealOrder(final boolean simulate) {
        return !simulate;
    }

    private void calculateOrder(final AbstractOrderModel abstractOrder, final boolean simulate) throws CalculationException {
        // build the request
        final OrderCalculationRequest request = buildCalculationRequest(abstractOrder);
        try {
            if (abstractOrder != null && CollectionUtils.isNotEmpty(abstractOrder.getEntries())) {

                request.setSimulateOrder(simulate);
                final B2BCustomerModel customer = (B2BCustomerModel) b2bCustomerService.getCurrentB2BCustomer();
                final Boolean isMultiCountryAribaCustomer = sessionService.getAttribute(DistConstants.Ariba.Session.ARIBA_MULTI_COUNTRY_CUSTOMER);

                if (BooleanUtils.isTrue(isMultiCountryAribaCustomer)) {
                    request.setCurrency((null != commonI18NService.getCurrentCurrency()) ? commonI18NService.getCurrentCurrency().getIsocode()
                                                                                         : getB2BUnitCurrency(customer));
                } else if (null != abstractOrder.getCurrency()) {
                    request.setCurrency(abstractOrder.getCurrency().getIsocode());
                }
                if (isRealOrder(simulate)) {
                    LOG.info("Going to place an order which hybris OrderId is = {}", abstractOrder.getCode());
                    request.setWebshopOrderCode(abstractOrder.getCode());
                    if (!isInvoicePaymentMode(abstractOrder.getPaymentMode())) {
                        request.setEvoPayID(getEvoPayID(abstractOrder));
                        request.setEvoRefNR(getEvoRefID(abstractOrder));
                        request.setBillingToken(getBillingToken(abstractOrder));
                        request.setAuthorizedAmount(getAuthorizedAmount(abstractOrder));
                    }

                    // DISTRELEC-10490
                    if (abstractOrder.getDeliveryCost() != null && abstractOrder.getDeliveryCost() > 0) {
                        final List<String> catalogs = Arrays
                                                            .asList(configurationService.getConfiguration().getString("sap.catalog.order.articles", "")
                                                                                        .split(","));
                        if (!catalogs.isEmpty()) {
                            final boolean triggerVoucher = !CollectionUtils.exists(abstractOrder.getEntries(), object -> {
                                final AbstractOrderEntryModel entry = (AbstractOrderEntryModel) object;
                                return !catalogs.contains(entry.getProduct().getCode());
                            });

                            if (triggerVoucher) {
                                final VoucherGeneration voucherGeneration = new VoucherGeneration();
                                voucherGeneration.setValue(abstractOrder.getDeliveryCost());
                                final Date today = new Date();
                                voucherGeneration.setValidFrom(SoapConversionHelper.convertDate(today));
                                voucherGeneration.setValidUntil(SoapConversionHelper.convertDate(DateUtils.addDays(today, 30)));
                                request.setFreeVoucherPromotion(voucherGeneration);
                            }
                        }
                    }
                }

                // execute the soap call
                final OrderCalculationResponse response = getOrderCalculationResponse(abstractOrder, request);

                // manage the response
                orderCalculationResponsePopulator.populate(response, abstractOrder);
                if (isRealOrder(simulate)) {
                    LOG.info("finished with placing sap order. Hybris OrderId = {} and Sap OrderId = {}", abstractOrder.getCode(), response.getOrderId());
                    // set the orderCalculationResponse response xml
                    if (saveXML()) {
                        abstractOrder.setXmlOrderCalculationResult(DistXmlUtils.soapToString(response, OrderCalculationResponse.class));
                        abstractOrder.setXmlOrderCalculationRequest(DistXmlUtils.soapToString(request, OrderCalculationRequest.class));
                    }
                    modelService.save(abstractOrder);
                    // Trigger the ERP code event
                    triggerErpCodeEvent(abstractOrder, response.getOrderId());
                }
            }
        } catch (ProductStatusMisalignmentException exception) {
            handleProductStatusMisalignmentException(exception);
        } catch (TemporaryQualityBlockException exception) {
            handleTemporaryQualityBlockException(exception);
        } catch (CustomerBlockedInErpException exception) {
            throw exception;
        } catch (MoqConversionException exception) {
            handleMoqConversionException(exception);
        } catch (Exception excep) {
            DistLogUtils.logError(LOG, "{} {} Error occurred while placing order into sap. See bellow the request xml, hybris OrderId is = {}, {}", excep,
                                  ErrorLogCode.PLACEORDER_ERROR, ErrorSource.SAP_FAULT, abstractOrder.getCode());
            LOG.error(DistXmlUtils.soapToString(request, OrderCalculationRequest.class));
            // DISTRELEC-7746: save order Calculation request XML in case of these error
            if (saveXML()) {
                abstractOrder.setXmlOrderCalculationRequest(DistXmlUtils.soapToString(request, OrderCalculationRequest.class));
                if (null != excep.getStackTrace() && excep.getStackTrace().length > 0 && (null == abstractOrder.getXmlOrderCalculationResult() || StringUtils
                                                                                                                                                             .isEmpty(abstractOrder.getXmlOrderCalculationResult()))) {
                    final int lineCount = configurationService.getConfiguration().getInt("error.log.lines.ordercalulation", 0);
                    abstractOrder.setXmlOrderCalculationResult(
                                                               Arrays.stream(excep.getStackTrace()).limit(lineCount).map(StackTraceElement::toString)
                                                                     .collect(Collectors.joining("\n")));
                }

                modelService.save(abstractOrder);
            }
            throw new CalculationException(SAP_SIMULATE_ERROR, excep);
        }
    }

    private void handleMoqConversionException(MoqConversionException exception) {
        erpErrorHandlerService.updateProductQuantity(exception.getCode(), exception.getMoq());
        throw exception;
    }

    private void handleTemporaryQualityBlockException(TemporaryQualityBlockException exception) {
        String code = erpErrorHandlerService.extractProductCodeFromMessage(exception.getMessage());
        erpErrorHandlerService.updateProductQuantity(code, 0);
        throw new TemporaryQualityBlockException(code);
    }

    private void handleProductStatusMisalignmentException(ProductStatusMisalignmentException exception) {
        String code = erpErrorHandlerService.extractProductCodeFromMessage(exception.getMessage());
        erpErrorHandlerService.updateProductQuantity(code, 0);
        throw new ProductStatusMisalignmentException(code);
    }

    private String getB2BUnitCurrency(B2BCustomerModel customer) {
        return (null != customer.getDefaultB2BUnit() && null != customer.getDefaultB2BUnit().getCurrency()) ? customer.getDefaultB2BUnit().getCurrency()
                                                                                                                      .getIsocode()
                                                                                                            : null;
    }

    @Override
    public void calculateWishList(final Wishlist2Model wishlist) throws CalculationException {
        if (wishlist == null) {
            return;
        }

        if (BooleanUtils.isFalse(wishlist.getCalculated()) && CollectionUtils.isNotEmpty(wishlist.getEntries())) {

            setDefaultValueToEmptyEntries(wishlist);
            final OrderCalculationRequest request = buildCalculationRequest(wishlist);
            // execute the soap call
            final OrderCalculationResponse response = getOrderCalculationResponse(request);
            if (null == response) {
                throw new CalculationException(SAP_SIMULATE_ERROR);
            }

            wishlist.setTotalPrice(response.getTax() + response.getSubtotal2());
            wishlist.setTotalTax(response.getTax());
            wishlist.setSubTotal(response.getSubtotal2());
            wishlist.setCalculated(Boolean.TRUE);
            modelService.save(wishlist);
        }
    }

    private void setDefaultValueToEmptyEntries(Wishlist2Model wishlist) {
        wishlist.getEntries()
                .stream()
                .filter(entry -> entry.getDesired() == null)
                .forEach(entry -> {
                    entry.setDesired(1);
                    modelService.save(entry);
                });
    }

    /**
     * When we receive the ERP order code, we trigger an event to unlock the business process and proceed with mail sending. The event has
     * an expiration period of 180 second.
     *
     * @param order
     *            the target order
     * @param erpCode
     *            the ERP code.
     */
    private void triggerErpCodeEvent(final AbstractOrderModel order, final String erpCode) {
        if (order != null && StringUtils.isNotBlank(erpCode)) {
            LOG.info("Triggering Business Process Erp Code Event " + buildProcessErpCodeEventCode(order));
            businessProcessService.triggerEvent(buildProcessErpCodeEventCode(order), new Date(System.currentTimeMillis() + 180_000));
        }
    }

    private OrderCalculationRequest buildCalculationRequest(final AbstractOrderModel abstractOrder) {
        if (abstractOrder != null && CollectionUtils.isNotEmpty(abstractOrder.getEntries())) {
            return orderCalculationRequestConverter.convert(abstractOrder);
        }
        return null;
    }

    private OrderCalculationRequest buildCalculationRequest(final Wishlist2Model wishlist) {
        final OrderCalculationRequest orderCalculationRequest = new OrderCalculationRequest();
        orderCalculationRequest.setSalesOrganization(distSalesOrgService.getCurrentSalesOrg().getCode());
        orderCalculationRequest.setSimulateOrder(true);
        final B2BCustomerModel customer = (B2BCustomerModel) wishlist.getUser();
        if (StringUtils.isNotBlank(customer.getErpContactID()) && StringUtils.isNotBlank(customer.getDefaultB2BUnit().getErpCustomerID())) {
            orderCalculationRequest.setCustomerData(new OrderCustomer());
            orderCalculationRequest.getCustomerData().setContactId(customer.getErpContactID());
            orderCalculationRequest.getCustomerData().setCustomerId(customer.getDefaultB2BUnit().getErpCustomerID());
            final Boolean isMultiCountryAribaCustomer = sessionService.getAttribute(DistConstants.Ariba.Session.ARIBA_MULTI_COUNTRY_CUSTOMER);
            if (BooleanUtils.isTrue(isMultiCountryAribaCustomer)) {
                orderCalculationRequest.setCurrency((null != commonI18NService.getCurrentCurrency()) ? commonI18NService.getCurrentCurrency().getIsocode()
                                                                                                     : getB2BUnitCurrency(customer));
            } else if (null != customer.getDefaultB2BUnit() && null != customer.getDefaultB2BUnit().getCurrency()) {
                orderCalculationRequest.setCurrency(customer.getDefaultB2BUnit().getCurrency().getIsocode());
            }
        }
        for (final Wishlist2EntryModel wishlistEntry : wishlist.getEntries()) {
            final OrderEntryRequest requestOrderEntry = new OrderEntryRequest();
            requestOrderEntry.setMaterialNumber(wishlistEntry.getProduct().getCode());
            requestOrderEntry.setQuantity(wishlistEntry.getDesired());
            requestOrderEntry.setFreeGiftPromotion(false);
            requestOrderEntry.setCustomerReferenceItemLevel(wishlistEntry.getComment());
            orderCalculationRequest.getOrderEntries().add(requestOrderEntry);
        }

        return orderCalculationRequest;
    }

    /*
     * Method responsible to retrieve the value from cache or execute soap call
     */
    private OrderCalculationResponse getOrderCalculationResponse(final OrderCalculationRequest request) throws CalculationException {
        OrderCalculationResponse response = null;
        if (request.isSimulateOrder() && (response = getFromCache(request, OrderCalculationResponse.class)) != null) {
            LOG.debug("OrderCalculationResponse from cache!");
        }

        if (response == null) {
            response = executeOrderCalculationRequest(request);
        }

        if (response != null && getCache() != null) {
            putIntoCache(request, response);
        }

        return response;
    }

    /*
     * Method responsible to retrieve the value from cache or execute soap call
     */
    private OrderCalculationResponse getOrderCalculationResponse(final AbstractOrderModel order, final OrderCalculationRequest request)
                                                                                                                                        throws CalculationException {
        OrderCalculationResponse response = null;
        if (request.isSimulateOrder() && (response = getFromCache(request, OrderCalculationResponse.class)) != null) {
            LOG.debug("OrderCalculationResponse from cache!");
        }

        if (response == null) {
            response = executeOrderCalculationRequest(order, request);
        }

        if (response != null && getCache() != null) {
            putIntoCache(request, response);
        }

        return response;
    }

    private OrderCalculationResponse executeOrderCalculationRequest(final AbstractOrderModel order, final OrderCalculationRequest request) {
        OrderCalculationResponse orderCalculationResponse = null;

        final long startTime = System.currentTimeMillis();
        try {
            orderCalculationResponse = webServiceClientIF11.if11V1OrderCalculation(request);
        } catch (com.distrelec.webservice.if11.v3.P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if11OrderCalculation", faultMessage);
            if (null != faultMessage.getFaultInfo()) {
                order.setXmlOrderCalculationResult(getXmlOrderCalculationResult(faultMessage));
                if (StringUtils.equals(faultMessage.getFaultInfo().getFaultId(), CUSTOMER_BLOCKED_IN_SAP)) {
                    throw new CustomerBlockedInErpException(faultMessage.getFaultInfo().getFaultText());
                }

                if (erpErrorHandlerService.isProductStatusMisalignmentException(faultMessage.getFaultInfo().getFaultText())) {
                    throw new ProductStatusMisalignmentException(faultMessage.getFaultInfo().getFaultText());
                }

                if (erpErrorHandlerService.isTemporaryQualityBlockException(faultMessage.getFaultInfo().getFaultText())) {
                    throw new TemporaryQualityBlockException(faultMessage.getFaultInfo().getFaultText());
                }
            }
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if11OrderCalculation", webServiceException);
            if (null != webServiceException.getStackTrace() && webServiceException.getStackTrace().length > 0) {
                final int lineCount = configurationService.getConfiguration().getInt("error.log.lines.ordercalulation", 0);
                order.setXmlOrderCalculationResult(
                                                   Arrays.stream(webServiceException.getStackTrace()).limit(lineCount).map(StackTraceElement::toString)
                                                         .collect(Collectors.joining("\n")));
            }
        }

        LOG.debug("Call to SAP PI IF-11 Order Calculation took {} ms", System.currentTimeMillis() - startTime);

        return orderCalculationResponse;
    }

    private String getXmlOrderCalculationResult(P1FaultMessage faultMessage) {
        StringBuilder orderCalculationResult = new StringBuilder();
        if (nonNull(faultMessage.getFaultInfo().getFaultName())) {
            orderCalculationResult.append(faultMessage.getFaultInfo().getFaultName());
        }
        if (nonNull(faultMessage.getFaultInfo().getFaultText())) {
            orderCalculationResult.append(faultMessage.getFaultInfo().getFaultText());
        }
        if (nonNull(faultMessage.getFaultInfo().getFaultDetail())) {
            orderCalculationResult.append(faultMessage.getFaultInfo().getFaultDetail());
        }
        return orderCalculationResult.toString();
    }

    private OrderCalculationResponse executeOrderCalculationRequest(final OrderCalculationRequest request) {
        OrderCalculationResponse orderCalculationResponse = null;

        final long startTime = System.currentTimeMillis();
        try {
            orderCalculationResponse = webServiceClientIF11.if11V1OrderCalculation(request);
        } catch (com.distrelec.webservice.if11.v3.P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if11OrderCalculation", faultMessage);
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if11OrderCalculation", webServiceException);
        }

        LOG.debug("Call to SAP PI IF-11 Order Calculation took {} ms", System.currentTimeMillis() - startTime);

        return orderCalculationResponse;
    }

    private OpenOrderCalculationResponse executeOpenOrderCalculationRequest(final OpenOrderCalculationRequest request) {
        OpenOrderCalculationResponse openOrderCalculationResponse = null;
        final long startTime = System.currentTimeMillis();
        try {
            openOrderCalculationResponse = webServiceClientIF11.if11V1OpenOrderCalculation(request);
        } catch (com.distrelec.webservice.if11.v3.P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if11OpenOrderCalculation", faultMessage);
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if11OpenOrderCalculation", webServiceException);
        }

        LOG.debug("Call to SAP PI IF-11 Open Order Calculation took {} ms", System.currentTimeMillis() - startTime);
        return openOrderCalculationResponse;
    }

    public void setOrderCalculationRequestConverter(final OrderCalculationRequestConverter orderCalculationRequestConverter) {
        this.orderCalculationRequestConverter = orderCalculationRequestConverter;
    }

    public void setOrderCalculationResponsePopulator(final OrderCalculationResponsePopulator orderCalculationResponsePopulator) {
        this.orderCalculationResponsePopulator = orderCalculationResponsePopulator;
    }

    public void setOpenOrderCalculationRequestConverter(final OpenOrderCalculationRequestConverter openOrderCalculationRequestConverter) {
        this.openOrderCalculationRequestConverter = openOrderCalculationRequestConverter;
    }

    public void setOpenOrderCalculationResponsePopulator(final OpenOrderCalculationResponsePopulator openOrderCalculationResponsePopulator) {
        this.openOrderCalculationResponsePopulator = openOrderCalculationResponsePopulator;
    }

    public void setWebServiceClientIF11(SIHybrisIF11V1Out webServiceClientIF11) {
        this.webServiceClientIF11 = webServiceClientIF11;
    }

    @Required
    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }
}
