/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.ws.WebServiceException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.PaymentMethod;
import com.distrelec.webservice.sap.v1.ReadPaymentMethodsRequest;
import com.distrelec.webservice.sap.v1.ReadPaymentMethodsResponse;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.core.model.DistPaymentMethodModel;
import com.namics.distrelec.b2b.core.model.DistPaymentTypeCodeMapModel;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import static com.namics.distrelec.b2b.core.util.DistUtils.isInvoicePaymentMode;

/**
 * SAP implementation of the <code>PaymentOptionService</code>.
 * 
 * @author pbueschi, ksperner, Namics AG
 * @since Distrelec 1.0
 */
public class SapPaymentOptionService extends AbstractSapService implements PaymentOptionService {

    private static final Logger LOG = LogManager.getLogger(SapPaymentOptionService.class);

    private static final String PAYMENT_MODES_FOR_PAYMENTMETHODS_QUERY = "SELECT DISTINCT({mode." + DistPaymentModeModel.PK + "}) FROM {"
            + DistPaymentModeModel._TYPECODE + " AS mode JOIN " + DistPaymentMethodModel._TYPECODE + " AS method ON {mode."
            + DistPaymentModeModel.ERPPAYMENTMETHOD + "}={method." + DistPaymentMethodModel.PK + "}} WHERE {method." + DistPaymentMethodModel.CODE
            + "} IN (?sapCodes)";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;

    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Autowired
    private ModelService modelService;

    private SIHybrisV1Out webServiceClient;

    private SapOrderCalculationService sapOrderCalculationService;

    @Autowired
    private PaymentModeService paymentModeService;

    /**
     * Create a new instance of {@code SapPaymentOptionService}
     */
    public SapPaymentOptionService() {
        super(DistConstants.CacheName.PAYMENT_AND_SHIPPING_OPTION);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#getSupportedPaymentOptions(de.hybris.platform.core.model.order.
     * CartModel, boolean)
     */
    @Override
    public List<AbstractDistPaymentModeModel> getSupportedPaymentOptions(final CartModel cart, final boolean userHasBudget) {
        try {
            if (!BooleanUtils.isTrue(cart.getCalculated())) {
                getSapOrderCalculationService().calculate(cart, true);
            }

            if (userHasBudget && CollectionUtils.isNotEmpty(cart.getValidPaymentModes())) {
                // We allow only non hop payment methods for the approval workflow
                final List<AbstractDistPaymentModeModel> validPaymentModes = cart.getValidPaymentModes().stream()
                        .filter(paymentMode -> !BooleanUtils.isTrue(paymentMode.getHop())).collect(Collectors.toList());
                cart.setValidPaymentModes(validPaymentModes);
                if (cart.getPaymentMode() == null || !validPaymentModes.contains(cart.getPaymentMode())) {
                    cart.setPaymentMode(validPaymentModes.size() > 0 ? validPaymentModes.get(0) : null);
                }
            }

            return cart.getValidPaymentModes();
        } catch (final CalculationException e) {
            DistLogUtils.logError(LOG, "{} Error occurred during order calculation", e, ErrorLogCode.PAYMENT_ERROR);
        }

        return ListUtils.EMPTY_LIST;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#getErpPaymentModeForAbstractDistPaymentMode(com.namics.distrelec.b2b.
     * core.model.payment.AbstractDistPaymentModeModel)
     */
    @Override
    public DistCodelistModel getErpPaymentModeForAbstractDistPaymentMode(final AbstractDistPaymentModeModel paymentMode) {
        if (!(paymentMode instanceof DistPaymentModeModel)) {
            throw new IllegalStateException("The given payment mode " + paymentMode + " is no instance of DistPaymentModeModel!");
        }
        return ((DistPaymentModeModel) paymentMode).getErpPaymentMethod();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#getErpPaymentModeForAbstractDistPaymentMode(com.namics.distrelec.b2b.
     * core.model.payment.AbstractDistPaymentModeModel, de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel)
     */
    @Override
    public DistCodelistModel getErpPaymentModeForAbstractDistPaymentMode(final AbstractDistPaymentModeModel paymentMode,
            final CreditCardPaymentInfoModel ccPaymentInfo) {
        if (!(paymentMode instanceof DistPaymentModeModel)) {
            throw new IllegalStateException("The given payment mode " + paymentMode + " is no instance of DistPaymentModeModel!");
        }
        return ((DistPaymentModeModel) paymentMode).getErpPaymentMethod();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#getDeuCSCodeForCreditCardTypeCode(com.namics.distrelec.b2b.core.model.
     * payment.AbstractDistPaymentModeModel, java.lang.String)
     */
    @Override
    public String getDeuCSCodeForCreditCardTypeCode(final AbstractDistPaymentModeModel paymentMode, final String creditCardTypeCode) {
        final DistPaymentTypeCodeMapModel exampleModel = new DistPaymentTypeCodeMapModel();
        exampleModel.setCreditCardTypeCode(creditCardTypeCode);
        return getFlexibleSearchService().getModelByExample(exampleModel).getPaymentProviderCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#getAbstractDistPaymentModeForErpPaymentModeCode(java.lang.String)
     */
    @Override
    public DistPaymentModeModel getAbstractDistPaymentModeForErpPaymentModeCode(final String distPaymentMethodCode) {
        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {dpm.").append(DistPaymentModeModel.PK).append("} FROM {").append(DistPaymentModeModel._TYPECODE).append(" AS dpm JOIN ")
                .append(DistPaymentMethodModel._TYPECODE).append(" AS dpme ON {dpm.").append(DistPaymentModeModel.ERPPAYMENTMETHOD).append("}={dpme.")
                .append(DistPaymentMethodModel.PK).append("}} WHERE {dpme.").append(DistPaymentMethodModel.CODE).append("}=?distPaymentMethodCode");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter("distPaymentMethodCode", distPaymentMethodCode);
        return getFlexibleSearchService().searchUnique(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#getAbstractDistPaymentModeForCreditCardTypeCode(java.lang.String)
     */
    @Override
    public DistPaymentModeModel getAbstractDistPaymentModeForCreditCardTypeCode(final String creditCardTypeCode) {
        throw new UnsupportedOperationException("Method not supported by SAP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#updateDefaultPaymentOption(de.hybris.platform.core.model.order.
     * CartModel, com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel)
     */
    @Override
    public boolean updateDefaultPaymentOption(final CartModel cart, final AbstractDistPaymentModeModel paymentOption) {
        try {
            final B2BCustomerModel b2bCustomer = getB2BCustomerService().getCurrentB2BCustomer();
            // DISTRELEC-9331: Default selection for payment only stored in hybris, not handed over to SAP
            b2bCustomer.setDefaultPaymentMethod(paymentOption.getCode());
            getModelService().save(b2bCustomer);
            cart.setPaymentMode(paymentOption);
            getModelService().save(cart);
            if (CollectionUtils.isNotEmpty(cart.getEntries())) {
                getSapOrderCalculationService().calculate(cart, true);
            }
            evictFromCache(buildReadPaymentMethodsRequest(b2bCustomer));

        } catch (final Exception e) {
            DistLogUtils.logError(LOG, "{} {} Can not update default paymentoption {} due to exception ", e, ErrorLogCode.PAYMENT_ERROR, ErrorSource.SAP_FAULT,
                    paymentOption);
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#getSupportedPaymentOptionsForUser(de.hybris.platform.b2b.model.
     * B2BCustomerModel)
     */
    @Override
    public List<AbstractDistPaymentModeModel> getSupportedPaymentOptionsForUser(final B2BCustomerModel b2bCustomer) {
        return getSupportedPaymentOptionsForUser(b2bCustomer, true);
    }

    @Override
    public List<AbstractDistPaymentModeModel> getSupportedPaymentOptionsForUser(final B2BCustomerModel b2bCustomer, final boolean useCache) {
        ReadPaymentMethodsResponse readPaymentMethodsResponse = null;
        try {
            readPaymentMethodsResponse = getReadPaymentMethodsResponse(buildReadPaymentMethodsRequest(b2bCustomer), useCache);
        } catch (final CalculationException e) {
            DistLogUtils.logError(LOG, "{} {} Error occur while calling IF10", e, ErrorLogCode.PAYMENT_ERROR, ErrorSource.SAP_FAULT);
        }

        if (null == readPaymentMethodsResponse) {
            return ListUtils.EMPTY_LIST;
        }

        // Convert SAP payment methods to Hybris zone payment modes
        final List<DistPaymentModeModel> distPaymentModes = getDistPaymentModesForSapPaymentMethods(readPaymentMethodsResponse.getPaymentMethods());
        final boolean hasBudget = b2bCustomer.getBudget() != null;
        return distPaymentModes.stream() //
                .filter(dpm -> dpm.getActive() == Boolean.TRUE
                        && (hasBudget && isInvoicePaymentMode(dpm) || !hasBudget)) //
                .collect(Collectors.toList());
    }

    /**
     * 
     * @param sapPaymentMethods
     * @return
     */
    private List<DistPaymentModeModel> getDistPaymentModesForSapPaymentMethods(final List<PaymentMethod> sapPaymentMethods) {
        if (CollectionUtils.isEmpty(sapPaymentMethods)) {
            return Collections.<DistPaymentModeModel> emptyList();
        }

        final List<String> sapCodes = sapPaymentMethods.stream() //
                .filter(spm -> StringUtils.isNotBlank(spm.getPaymentMethodCode())) //
                .map(PaymentMethod::getPaymentMethodCode).collect(Collectors.toList());

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(PAYMENT_MODES_FOR_PAYMENTMETHODS_QUERY);
        flexibleSearchQuery.addQueryParameter("sapCodes", sapCodes);
        return getFlexibleSearchService().<DistPaymentModeModel> search(flexibleSearchQuery).getResult();
    }

    /**
     * 
     * @param b2bCustomer
     * @return
     */
    private ReadPaymentMethodsRequest buildReadPaymentMethodsRequest(final B2BCustomerModel b2bCustomer) {
        final ReadPaymentMethodsRequest readPaymentMethodsRequest = new ReadPaymentMethodsRequest();
        readPaymentMethodsRequest.setCustomerId(b2bCustomer.getDefaultB2BUnit().getErpCustomerID());
        readPaymentMethodsRequest.setSalesOrganization(getCurrentSalesOrgCode());
        return readPaymentMethodsRequest;
    }

    private String getCurrentSalesOrgCode() {
        return getDistSalesOrgService().getCurrentSalesOrg().getCode();
    }

    private ReadPaymentMethodsResponse getReadPaymentMethodsResponse(final ReadPaymentMethodsRequest request,
            boolean useCache) throws CalculationException {
        ReadPaymentMethodsResponse response = null;
        if (useCache) {
            response = getFromCache(request, ReadPaymentMethodsResponse.class);
            if (response != null && LOG.isDebugEnabled()) {
                LOG.debug("ReadPaymentMethodsResponse from cache!");
            }
        }

        if (response == null) {
            response = callReadPaymentMethodsWebService(request);
            putIntoCache(request, response);
        }

        return response;
    }

    private ReadPaymentMethodsResponse callReadPaymentMethodsWebService(final ReadPaymentMethodsRequest readPaymentMethodsRequest) {
        ReadPaymentMethodsResponse readPaymentMethodsResponse = null;
        final long startTime = System.currentTimeMillis();
        try {
            readPaymentMethodsResponse = getWebServiceClient().if10ReadPaymentMethods(readPaymentMethodsRequest);
        } catch (P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if10ReadPaymentMethods", faultMessage);
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if10ReadPaymentMethods", webServiceException);
        }
        final long endTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Call to SAP PI IF-10 Read Payment Methods took " + (endTime - startTime) + "ms");
        }
        return readPaymentMethodsResponse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService#getDefaultPaymentOptionForUser(de.hybris.platform.b2b.model.
     * B2BCustomerModel)
     */
    @Override
    public DistPaymentModeModel getDefaultPaymentOptionForUser(final B2BCustomerModel b2bCustomer) {
        return getDefaultPaymentOptionForUser(b2bCustomer, true);
    }

    @Override
    public DistPaymentModeModel getDefaultPaymentOptionForUser(final B2BCustomerModel b2bCustomer, final boolean useCache) {
        // DISTRELEC-10295: if the default payment mode in the customer settings is not empty, then we return it, otherwise we return what
        // we have in SAP.
        if (StringUtils.isNotBlank(b2bCustomer.getDefaultPaymentMethod())) {
            try {
                return (DistPaymentModeModel) getPaymentModeService().getPaymentModeForCode(b2bCustomer.getDefaultPaymentMethod());
            } catch (final Exception exp) {
                DistLogUtils.logError(LOG, "{} {} ERROR occurs while fetching the payment method: {}, {}", exp, ErrorLogCode.PAYMENT_ERROR,
                        ErrorSource.SAP_FAULT, b2bCustomer.getDefaultPaymentMethod(), exp.getMessage());
            }
        }

        ReadPaymentMethodsResponse paymentMethodsResponse = null;
        try {
            paymentMethodsResponse = getReadPaymentMethodsResponse(buildReadPaymentMethodsRequest(b2bCustomer), useCache);
            if (null == paymentMethodsResponse) {
                return null;
            }
        } catch (final CalculationException e) {
            DistLogUtils.logError(LOG, "{} {} Error occur while calling IF10", e, ErrorLogCode.PAYMENT_ERROR, ErrorSource.SAP_FAULT);
            return null;
        }

        // find default payment method
        final PaymentMethod defaultPaymentMethod = paymentMethodsResponse.getPaymentMethods().stream().filter(PaymentMethod::isDefault).findFirst()
                .orElse(null);

        if (null == defaultPaymentMethod) {
            return null;
        }

        // Convert SAP payment method to Hybris zone payment mode
        final List<DistPaymentModeModel> defaultPaymentMode = getDistPaymentModesForSapPaymentMethods(Collections.singletonList(defaultPaymentMethod));

        return defaultPaymentMode.isEmpty() ? null : defaultPaymentMode.get(0);
    }

    // Getters & Setters

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService() {
        return b2bUnitService;
    }

    public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    @Required
    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public SapOrderCalculationService getSapOrderCalculationService() {
        if (sapOrderCalculationService == null) {
            sapOrderCalculationService = (SapOrderCalculationService) SpringUtil.getBean("sap.orderCalculationService");
        }
        return sapOrderCalculationService;
    }

    public void setSapOrderCalculationService(SapOrderCalculationService sapOrderCalculationService) {
        this.sapOrderCalculationService = sapOrderCalculationService;
    }

    public B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2BCustomerService() {
        return b2BCustomerService;
    }

    public void setB2BCustomerService(B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService) {
        b2BCustomerService = b2bCustomerService;
    }

    public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
        return b2bUnitService;
    }

    public void setB2bUnitService(B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public PaymentModeService getPaymentModeService() {
        return paymentModeService;
    }

    public void setPaymentModeService(final PaymentModeService paymentModeService) {
        this.paymentModeService = paymentModeService;
    }
}
