/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.occ.core.security.SecuredAccessConstants;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoDatas;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.PK.PKException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/payment/details")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@Tag(name = "Payment Details")
public class PaymentDetailsController extends BaseCommerceController {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentDetailsController.class);

    private static final String OBJECT_NAME_PAYMENT_DETAILS = "paymentDetails";

    @Resource(name = "paymentDetailsDTOValidator")
    private Validator paymentDetailsDTOValidator;

    @Resource(name = "ccPaymentInfoValidator")
    private Validator ccPaymentInfoValidator;

    @Secured({ SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_CUSTOMERGROUP, })
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getPaymentDetailsList", summary = "Get customer's credit card payment details list.", description = "Return customer's credit card payment details list.")
    @ApiBaseSiteIdAndUserIdParam
    public PaymentDetailsListWsDTO getPaymentDetailsList(
                                                         @Parameter(description = "Type of payment details.") @RequestParam(defaultValue = "false") final boolean saved,
                                                         @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        LOG.debug("getPaymentDetailsList");
        final CCPaymentInfoDatas paymentInfoDataList = new CCPaymentInfoDatas();
        if (!isEShopGroup()) {
            paymentInfoDataList.setPaymentInfos(getUserFacade().getCCPaymentInfos(saved));
        }

        return getDataMapper().map(paymentInfoDataList, PaymentDetailsListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_CUSTOMERGROUP, })
    @RequestMapping(value = "/{paymentDetailsId}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getPaymentDetails", summary = "Get customer's credit card payment details.", description = "Returns a customer's credit card payment details for the specified paymentDetailsId.")
    @ApiBaseSiteIdAndUserIdParam
    public PaymentDetailsWsDTO getPaymentDetails(@Parameter(description = "Payment details identifier.", required = true) @PathVariable final String paymentDetailsId,
                                                 @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        return getDataMapper().map(getPaymentInfo(paymentDetailsId), PaymentDetailsWsDTO.class, fields);
    }

    public CCPaymentInfoData getPaymentInfo(final String paymentDetailsId) {
        LOG.debug("getPaymentInfo : id = {}", sanitize(paymentDetailsId));
        try {
            final CCPaymentInfoData paymentInfoData = getUserFacade().getCCPaymentInfoForCode(paymentDetailsId);
            if (paymentInfoData == null) {
                throw new RequestParameterException("Payment details [" + sanitize(paymentDetailsId) + "] not found.",
                                                    RequestParameterException.UNKNOWN_IDENTIFIER, "paymentDetailsId");
            }
            return paymentInfoData;
        } catch (final PKException e) {
            throw new RequestParameterException("Payment details [" + sanitize(paymentDetailsId) + "] not found.", RequestParameterException.UNKNOWN_IDENTIFIER,
                                                "paymentDetailsId", e);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_CUSTOMERGROUP, })
    @RequestMapping(value = "/{paymentDetailsId}", method = RequestMethod.DELETE)
    @Operation(operationId = "removePaymentDetails", summary = "Deletes customer's credit card payment details.", description = "Deletes a customer's credit card payment details based on a specified paymentDetailsId.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseStatus(HttpStatus.OK)
    public void removePaymentDetails(@Parameter(description = "Payment details identifier.", required = true) @PathVariable final String paymentDetailsId) {
        LOG.debug("removePaymentDetails: id = {}", sanitize(paymentDetailsId));
        getPaymentInfo(paymentDetailsId);
        getUserFacade().removeCCPaymentInfo(paymentDetailsId);
    }

    @Secured({ SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_CUSTOMERGROUP, })
    @RequestMapping(value = "/{paymentDetailsId}", method = RequestMethod.PATCH, consumes = { MediaType.APPLICATION_JSON_VALUE,
                                                                                              MediaType.APPLICATION_XML_VALUE })
    @Operation(operationId = "updatePaymentDetails", summary = "Updates existing customer's credit card payment details.", description = "Updates an existing customer's credit card payment details based "
                                                                                                                                 + "on the specified paymentDetailsId. Only those attributes provided in the request will be updated.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseStatus(HttpStatus.OK)
    public void updatePaymentDetails(@Parameter(description = "Payment details identifier.", required = true) @PathVariable final String paymentDetailsId,
                                     @Parameter(description = "Payment details object", required = true) @RequestBody final PaymentDetailsWsDTO paymentDetails) {
        final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);
        final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();

        getDataMapper()
                       .map(paymentDetails, paymentInfoData,
                            "accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId,defaultPaymentInfo,saved,"
                                                             + "billingAddress(firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress)",
                            false);
        validate(paymentInfoData, OBJECT_NAME_PAYMENT_DETAILS, ccPaymentInfoValidator);

        getUserFacade().updateCCPaymentInfo(paymentInfoData);
        if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo()) {
            getUserFacade().setDefaultPaymentInfo(paymentInfoData);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_CUSTOMERGROUP, })
    @RequestMapping(value = "/{paymentDetailsId}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @Operation(operationId = "replacePaymentDetails", summary = "Updates existing customer's credit card payment info.", description = "Updates existing customer's credit card payment info based on the "
                                                                                                                               + "payment info ID. Attributes not given in request will be defined again (set to null or default).")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseStatus(HttpStatus.OK)
    public void replacePaymentDetails(@Parameter(description = "Payment details identifier.", required = true) @PathVariable final String paymentDetailsId,
                                      @Parameter(description = "Payment details object.", required = true) @RequestBody final PaymentDetailsWsDTO paymentDetails) {
        final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentDetailsId);
        final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();

        validate(paymentDetails, OBJECT_NAME_PAYMENT_DETAILS, paymentDetailsDTOValidator);
        getDataMapper().map(paymentDetails, paymentInfoData,
                            "accountHolderName,cardNumber,cardType,issueNumber,startMonth,expiryMonth,startYear,expiryYear,subscriptionId,defaultPaymentInfo,saved,billingAddress"
                                                             + "(firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),defaultAddress)",
                            true);

        getUserFacade().updateCCPaymentInfo(paymentInfoData);
        if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo()) {
            getUserFacade().setDefaultPaymentInfo(paymentInfoData);
        }
    }
}
