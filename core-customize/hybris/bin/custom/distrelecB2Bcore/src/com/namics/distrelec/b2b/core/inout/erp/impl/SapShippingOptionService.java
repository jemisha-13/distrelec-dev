/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.ws.WebServiceException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.sap.v1.*;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.ShippingOptionService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.model.DistShippingMethodModel;
import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.delivery.DistDeliveryModeModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.DistLogUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * SAP implementation of the <code>ShippingOptionService</code>.
 *
 * @author pbueschi, ksperner, Namics AG
 * @since Distrelec 1.0
 */
public class SapShippingOptionService extends AbstractSapService implements ShippingOptionService {

    private static final Logger LOG = LogManager.getLogger(SapShippingOptionService.class);

    private static final String DELIVERY_MODES_FOR_SHIPPINGMETHODS_QUERY = "SELECT DISTINCT ({ddm." + DistDeliveryModeModel.PK + "}) FROM {"
                                                                           + DistDeliveryModeModel._TYPECODE + " AS ddm JOIN "
                                                                           + DistShippingMethodModel._TYPECODE + " AS dsm ON {ddm."
                                                                           + DistDeliveryModeModel.ERPDELIVERYMETHOD + "}={dsm." + DistShippingMethodModel.PK
                                                                           + "} } WHERE {dsm." + DistShippingMethodModel.CODE
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

    /**
     * Create a new instance of {@code SapPaymentOptionService}
     */
    public SapShippingOptionService() {
        super(DistConstants.CacheName.PAYMENT_AND_SHIPPING_OPTION);
    }

    @Override
    public String getPickupDeliveryModeCode() {
        return DistConstants.Shipping.METHOD_PICKUP;
    }

    @Override
    public DistDeliveryModeModel getAbstractDistDeliveryModeForDistShippingMethodCode(final String distShippingMethodCode) {
        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {ddm.").append(DistDeliveryModeModel.PK).append("} FROM {").append(DistDeliveryModeModel._TYPECODE).append(" AS ddm JOIN ")
                   .append(DistShippingMethodModel._TYPECODE).append(" AS dsm ON {ddm.").append(DistDeliveryModeModel.ERPDELIVERYMETHOD).append("}={dsm.")
                   .append(DistShippingMethodModel.PK).append("}} WHERE {dsm.").append(DistShippingMethodModel.CODE).append("}=?distShippingMethodCode");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter("distShippingMethodCode", distShippingMethodCode);
        return flexibleSearchService.searchUnique(query);
    }

    @Override
    public boolean updateDefaultShippingOption(final AbstractDistDeliveryModeModel deliveryMode) {

        final ShippingMethodCode shippingMethodCode = convertDeliveryModeToSapFormat(deliveryMode);
        if (null == shippingMethodCode) {
            return false;
        }

        final String customerID = findCustomerID();
        if (null == customerID) {
            LOG.error("could not find customer ID");
            return false;
        }

        final UpdateDefaultShippingMethodRequest updateRequest = buildUpdateDefaultShippingMethodRequest(shippingMethodCode, customerID);

        final UpdateDefaultShippingMethodResponse updateResponse = callUpdateShippingMethodWebService(updateRequest);

        if (null == updateResponse) {
            return false;
        }

        final boolean callWasSuccessful = updateResponse.isSuccessful();
        if (!callWasSuccessful) {
            LOG.error("{} {} Result of call to SAP PI IF-09 Update Default Shipping Method failed!", ErrorLogCode.SHIPPING_METHOD_ERROR, ErrorSource.SAP_FAULT);
        }

        evictFromCache(buildReadShippingMethodsRequest(customerID));
        return callWasSuccessful;
    }

    private ShippingMethodCode convertDeliveryModeToSapFormat(final AbstractDistDeliveryModeModel deliveryMode) {
        if (!(deliveryMode instanceof DistDeliveryModeModel)) {
            LOG.error("{} {} The given delivery mode '{}' is not an instance of DistDeliveryModeModel!", ErrorLogCode.DELIVERY_MODE_ERROR,
                      ErrorSource.SAP_FAULT, deliveryMode);
            return null;
        }
        final DistDeliveryModeModel distDeliveryMode = (DistDeliveryModeModel) deliveryMode;
        final DistShippingMethodModel erpDeliveryMethod = distDeliveryMode.getErpDeliveryMethod();
        if (null == erpDeliveryMethod) {
            LOG.error("{} {} ErpDeliveryMethod of deliverMode '{}' is null", ErrorLogCode.DELIVERY_MODE_ERROR, ErrorSource.SAP_FAULT, deliveryMode);
            return null;
        }
        final String deliveryMethodCode = erpDeliveryMethod.getCode();
        if (null == deliveryMethodCode) {
            LOG.error("{} {} Code of erpDeliveryMethod of deliverMode '{}' is null", ErrorLogCode.DELIVERY_MODE_ERROR, ErrorSource.SAP_FAULT, deliveryMode);
            return null;
        }
        ShippingMethodCode shippingMethodCode;
        try {
            shippingMethodCode = ShippingMethodCode.fromValue(deliveryMethodCode);
        } catch (IllegalArgumentException e) {
            LOG.error("{} {} The delivery method code {} is not a valid shipping method code", ErrorLogCode.SHIPPING_METHOD_ERROR, ErrorSource.SAP_FAULT,
                      deliveryMethodCode);
            return null;
        }
        return shippingMethodCode;
    }

    private String findCustomerID() {
        final B2BCustomerModel b2bCustomer = b2BCustomerService.getCurrentB2BCustomer();
        if (null == b2bCustomer) {
            return null;
        }
        final B2BUnitModel b2bUnit = b2bUnitService.getParent(b2bCustomer);
        if (null == b2bUnit) {
            return null;
        }
        return b2bUnit.getErpCustomerID();
    }

    private UpdateDefaultShippingMethodRequest buildUpdateDefaultShippingMethodRequest(final ShippingMethodCode shippingMethodCode, final String customerID) {
        final UpdateDefaultShippingMethodRequest updateRequest = new UpdateDefaultShippingMethodRequest();
        updateRequest.setCustomerId(customerID);
        updateRequest.setSalesOrganization(getCurrentSalesOrgCode());
        updateRequest.setShippingMethodCode(shippingMethodCode);
        return updateRequest;
    }

    private String getCurrentSalesOrgCode() {
        return distSalesOrgService.getCurrentSalesOrg().getCode();
    }

    private UpdateDefaultShippingMethodResponse callUpdateShippingMethodWebService(final UpdateDefaultShippingMethodRequest updateRequest) {
        UpdateDefaultShippingMethodResponse updateResponse = null;
        final long startTime = new Date().getTime();
        try {
            updateResponse = getWebServiceClient().if09UpdateDefaultShippingMethod(updateRequest);
            if (updateResponse.isSuccessful()) {
                evictFromCache(buildReadShippingMethodsRequest(updateRequest.getCustomerId()));
            }

        } catch (P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if09UpdateDefaultShippingMethod", faultMessage);
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if09UpdateDefaultShippingMethod", webServiceException);
        }
        final long endTime = new Date().getTime();
        LOG.debug("Call to SAP PI IF-09 Update Default Shipping Method took {}ms", endTime - startTime);
        return updateResponse;
    }

    @Override
    public List<AbstractDistDeliveryModeModel> getSupportedShippingOptionsForUser(final B2BCustomerModel b2bCustomer, final List<String> ignoredOptions) {

        ReadShippingMethodsResponse readShippingMethodsResponse = null;
        try {
            readShippingMethodsResponse = getReadShippingMethodsResponse(buildReadShippingMethodsRequest(b2bCustomer));
        } catch (final CalculationException e) {
            DistLogUtils.logError(LOG, "{} {} Error occur while calling IF09", e, ErrorLogCode.SHIPPING_METHOD_ERROR, ErrorSource.SAP_FAULT);
        }

        if (null == readShippingMethodsResponse) {
            return ListUtils.EMPTY_LIST;
        }

        // Convert SAP shipping methods to Hybris zone delivery modes
        return getDistDeliveryModesForShippingMethods(readShippingMethodsResponse.getShippingMethods())
                                                                                                       .stream()
                                                                                                       .filter(distDeliveryMode -> Boolean.TRUE == distDeliveryMode.getActive()
                                                                                                               && !ignoredOptions.contains(distDeliveryMode.getCode()))
                                                                                                       .collect(Collectors.toList());
    }

    @Override
    public DistDeliveryModeModel getDefaultShippingOptionForUser(final B2BCustomerModel b2bCustomer) {
        ReadShippingMethodsResponse readShippingMethodsResponse = null;
        try {
            readShippingMethodsResponse = getReadShippingMethodsResponse(buildReadShippingMethodsRequest(b2bCustomer));
        } catch (final CalculationException e) {
            DistLogUtils.logError(LOG, "{} {} Error occur while calling IF09", e, ErrorLogCode.SHIPPING_METHOD_ERROR,
                                  ErrorSource.SAP_FAULT.getCode("if09ReadShippingMethods"));
        }

        if (null == readShippingMethodsResponse) {
            return null;
        }

        // find default shipping method
        final ShippingMethods defaultShippingMethod = readShippingMethodsResponse.getShippingMethods()
                                                                                 .stream().filter(ShippingMethods::isDefault).findFirst().orElse(null);

        if (null == defaultShippingMethod) {
            return null;
        }

        // convert SAP shipping method to hybris zone delivery mode
        final List<DistDeliveryModeModel> defaultDeliveryMode = getDistDeliveryModesForShippingMethods(Collections.singletonList(defaultShippingMethod));

        if (defaultDeliveryMode.isEmpty()) {
            return null;
        }

        return defaultDeliveryMode.get(0);
    }

    @Override
    public void setErpDefaultShippingOptionForUser(B2BCustomerModel customer) {
        AbstractDistDeliveryModeModel defaultDeliveryForUnit = getDefaultShippingOptionForUser(customer);
        if (defaultDeliveryForUnit != null) {
            customer.setDefaultDeliveryMethod(defaultDeliveryForUnit.getCode());
        }
        modelService.save(customer);
    }

    private ReadShippingMethodsRequest buildReadShippingMethodsRequest(final B2BCustomerModel b2bCustomer) {
        return buildReadShippingMethodsRequest(b2bCustomer.getDefaultB2BUnit().getErpCustomerID());
    }

    private ReadShippingMethodsRequest buildReadShippingMethodsRequest(final String customerId) {
        final ReadShippingMethodsRequest readShippingMethodsRequest = new ReadShippingMethodsRequest();
        readShippingMethodsRequest.setCustomerId(customerId);
        readShippingMethodsRequest.setSalesOrganization(getCurrentSalesOrgCode());
        return readShippingMethodsRequest;
    }

    /*
     * Method responsible to retrieve the value from cache or execute soap call
     */
    private ReadShippingMethodsResponse getReadShippingMethodsResponse(final ReadShippingMethodsRequest request) throws CalculationException {
        ReadShippingMethodsResponse response = null;
        if ((response = getFromCache(request, ReadShippingMethodsResponse.class)) != null && LOG.isDebugEnabled()) {
            LOG.debug("ReadShippingMethodsResponse from cache!");
        }

        if (response == null) {
            response = callReadShippingMethodsWebService(request);
        }

        if (response != null && getCache() != null) {
            putIntoCache(request, response);
        }

        return response;
    }

    /**
     * Make a real call to SAP to get the shipping methods
     *
     * @param readShippingMethodsRequest
     * @return #ReadShippingMethodsResponse
     */
    private ReadShippingMethodsResponse callReadShippingMethodsWebService(final ReadShippingMethodsRequest readShippingMethodsRequest) {
        ReadShippingMethodsResponse readShippingMethodsResponse = null;
        final long startTime = System.currentTimeMillis();
        try {
            readShippingMethodsResponse = getWebServiceClient().if09ReadShippingMethods(readShippingMethodsRequest);
        } catch (P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if09ReadShippingMethods", faultMessage);
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if09ReadShippingMethods", webServiceException);
        }

        final long endTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Call to SAP PI IF-09 Read Shipping Methods took  {}ms", endTime - startTime);
        }
        return readShippingMethodsResponse;
    }

    /**
     * Fetching the Hybris delivery modes having the correspondent SAP shipping methods
     *
     * @param shippingMethods
     * @return a list of #DistDeliveryModeModel
     */
    private List<DistDeliveryModeModel> getDistDeliveryModesForShippingMethods(final List<ShippingMethods> shippingMethods) {
        if (CollectionUtils.isEmpty(shippingMethods)) {
            return Collections.<DistDeliveryModeModel> emptyList();
        }

        final List<String> sapCodes = shippingMethods.stream()
                                                     .filter(shippingMethod -> shippingMethod.getShippingMethodCode() != null)
                                                     .map(shippingMethod -> shippingMethod.getShippingMethodCode().value())
                                                     .collect(Collectors.toList());

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(DELIVERY_MODES_FOR_SHIPPINGMETHODS_QUERY);
        flexibleSearchQuery.addQueryParameter("sapCodes", sapCodes);
        return flexibleSearchService.<DistDeliveryModeModel> search(flexibleSearchQuery).getResult();
    }

    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    @Required
    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }
}
