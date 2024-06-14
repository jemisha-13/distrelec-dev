/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static com.namics.distrelec.occ.core.security.SecuredAccessConstants.*;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.rma.GuestRMACreateRequestForm;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.rma.DistReturnRequestFacade;
import com.namics.distrelec.occ.core.order.ws.dto.DistCreateRMAResponseWsDTO;
import com.namics.distrelec.occ.core.order.ws.dto.DistGuestRMACreateRequestFormWsDTO;
import com.namics.distrelec.occ.core.order.ws.dto.DistMainReasonWsDTO;
import com.namics.distrelec.occ.core.order.ws.dto.DistReturnRequestEntryInputListWsDTO;
import com.namics.distrelec.occ.core.v2.annotations.OrderReturnsRestriction;
import com.namics.distrelec.occ.core.v2.helper.OrderReturnsHelper;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ReturnRequestStatusWsDTOType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/order-returns")
@Tag(name = "Return Requests")
public class OrderReturnsController extends BaseCommerceController {
    private static final Logger LOG = LoggerFactory.getLogger(OrderReturnsController.class);

    @Autowired
    private OrderReturnsHelper orderReturnsHelper;

    @Autowired
    private OrderHistoryFacade orderHistoryFacade;

    @Autowired
    private DistReturnRequestFacade distReturnRequestFacade;

    @Resource
    private Validator returnRequestEntryInputListDTOValidator;

    @Resource
    private Validator distReturnRequestEntryInputListDTOValidator;

    @Resource
    private Validator distGuestRMACreateRequestFormWsDTOValidator;

    @Secured({ ROLE_CUSTOMERGROUP, ROLE_TRUSTED_CLIENT, ROLE_CUSTOMERMANAGERGROUP })
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @Operation(operationId = "getReturnRequests", summary = "Gets the user's return requests history", description = "Returns order return request data associated with a specified user for a specified base store.")
    @OrderReturnsRestriction
    @ApiBaseSiteIdAndUserIdParam
    public ReturnRequestListWsDTO getReturnRequests(
                                                    @Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
                                                    @Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = "0") final int pageSize,
                                                    @Parameter(description = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
                                                    @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        return orderReturnsHelper.searchOrderReturnRequests(currentPage, getPageSizeOrDefault(pageSize), sort, fields);
    }

    @Secured({ ROLE_CUSTOMERGROUP, ROLE_TRUSTED_CLIENT, ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{returnRequestCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @Operation(operationId = "getReturnRequest", summary = "Get the details of a return request.", description = "Returns specific order return request details based on a specific return request code. The response contains detailed order return request information.")
    @OrderReturnsRestriction
    @ApiBaseSiteIdAndUserIdParam
    public ReturnRequestWsDTO getReturnRequest(@Parameter(description = "Order return request code", required = true) @PathVariable final String returnRequestCode,
                                               @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        return orderReturnsHelper.getOrderReturnRequest(returnRequestCode, fields);
    }

    @Secured({ ROLE_CUSTOMERGROUP, ROLE_TRUSTED_CLIENT, ROLE_CUSTOMERMANAGERGROUP })
    @Operation(operationId = "updateReturnRequest", summary = "Updates the order return request.", description = "Updates the order return request. Only cancellation of the request is supported by setting the attribute status to CANCELLING. Cancellation of the return request cannot be reverted")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/{returnRequestCode}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON)
    @OrderReturnsRestriction
    @ApiBaseSiteIdAndUserIdParam
    public void updateReturnRequest(@Parameter(description = "Order return request code", required = true) @PathVariable final String returnRequestCode,
                                    @Parameter(description = "Return request modification object.", required = true) @RequestBody final ReturnRequestModificationWsDTO returnRequestModification) {
        if (returnRequestModification.getStatus() == ReturnRequestStatusWsDTOType.CANCELLING) {
            orderReturnsHelper.cancelOrderReturnRequest(returnRequestCode);
        }
    }

    @Secured({ ROLE_CUSTOMERGROUP, ROLE_TRUSTED_CLIENT, ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "createReturnRequest", summary = "Create an order return request.", description = "Creates an order return request.")
    @OrderReturnsRestriction
    @ApiBaseSiteIdAndUserIdParam
    public ReturnRequestWsDTO createReturnRequest(
                                                  @Parameter(description = "Return request input list for the current order.", required = true) @RequestBody final ReturnRequestEntryInputListWsDTO returnRequestEntryInputList,
                                                  @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {
        validate(returnRequestEntryInputList, "returnRequestEntryInputList", returnRequestEntryInputListDTOValidator);
        return orderReturnsHelper.createOrderReturnRequest(returnRequestEntryInputList, fields);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({ UnknownIdentifierException.class })
    public ErrorListWsDTO handleNotFoundExceptions(final Exception ex) {
        LOG.debug("Unknown identifier error", ex);
        return handleErrorInternal(NotFoundException.class.getSimpleName(), ex.getMessage());
    }

    @Secured({ ROLE_CLIENT, ROLE_TRUSTED_CLIENT, ROLE_ANONYMOUS, ROLE_CUSTOMERMANAGERGROUP, ROLE_CUSTOMERGROUP })
    @RequestMapping(value = "/guest-return", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "guestReturns", summary = "Create Guest Return.")
    @OrderReturnsRestriction
    @ApiBaseSiteIdAndUserIdParam
    public void createGuestReturn(@Valid @RequestBody final DistGuestRMACreateRequestFormWsDTO distGuestRMACreateRequestFormWsDTO,
                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        validate(distGuestRMACreateRequestFormWsDTO, "distGuestRMACreateRequestFormWsDTO", distGuestRMACreateRequestFormWsDTOValidator);
        distReturnRequestFacade.sendGuestReturnRequestEmail(getDataMapper().map(distGuestRMACreateRequestFormWsDTO, GuestRMACreateRequestForm.class));
    }

    @Secured({ ROLE_CLIENT, ROLE_TRUSTED_CLIENT, ROLE_CUSTOMERGROUP })
    @RequestMapping(value = "/create-return", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "distCreateReturnRequest", summary = "Create an order return request.")
    @OrderReturnsRestriction
    @ApiBaseSiteIdAndUserIdParam
    public DistCreateRMAResponseWsDTO distCreateReturnRequest(
                                                              @Parameter(description = "Return request input list for the current order.", required = true) @RequestBody final DistReturnRequestEntryInputListWsDTO distReturnRequestEntryInputList,
                                                              @ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields) {

        validate(distReturnRequestEntryInputList, "distReturnRequestEntryInputList", distReturnRequestEntryInputListDTOValidator);

        DistCreateRMAResponseWsDTO responseDTO = new DistCreateRMAResponseWsDTO();
        try {
            final OrderData orderDetails = orderHistoryFacade.getOrderForCode(distReturnRequestEntryInputList.getOrderCode());
            if (orderDetails == null) {
                LOG.warn("No Order found with code {}", distReturnRequestEntryInputList.getOrderCode());
                responseDTO.setErrorMsgKey("lightboxreturnrequest.process.notfound");
                return responseDTO;
            }

            CreateRMARequestForm createRMARequestForm = orderReturnsHelper.mapToCreateRMARequestForm(distReturnRequestEntryInputList, orderDetails);

            if (orderDetails.getStatus() != OrderStatus.ERP_STATUS_SHIPPED && orderDetails.getStatus() != OrderStatus.ERP_STATUS_RECIEVED) {
                LOG.warn("The order {} is not yet shipped", createRMARequestForm.getOrderId());
                responseDTO.setErrorMsgKey("lightboxreturnrequest.process.notshipped");
                return responseDTO;
            }

            final boolean isRmaRaised = orderReturnsHelper.isRmaRaised(createRMARequestForm.getOrderItems());
            if (isRmaRaised) {
                LOG.error("RMA already raised");
                responseDTO.setIsRmaRaised(isRmaRaised);
                return responseDTO;
            }

            if (BooleanUtils.isFalse(orderDetails.getValidForReturn())) {
                LOG.error("Date to raise return request is expired.");
                responseDTO.setIsValidReturnClaim(BooleanUtils.toBoolean(orderDetails.getValidForReturn()));
                return responseDTO;
            }

            final boolean invalidQuantity = orderReturnsHelper.isReturnRaisedQuantityAvailable(orderDetails.getEntries(), createRMARequestForm.getOrderItems());
            if (invalidQuantity) {
                LOG.error("Return Quantity entered is greater than available Quantity.");
                responseDTO.setInvalidQuantity(invalidQuantity);
                return responseDTO;
            }

            final CreateRMAResponseData createRMAResponseData = distReturnRequestFacade.createRMACreateRequest(createRMARequestForm);
            if (createRMAResponseData != null) {
                DistReturnRequestFacade.UserRMARequestDataWrapper userRMARequestDataWrapper = new DistReturnRequestFacade.UserRMARequestDataWrapper();
                createRMARequestForm.setOrderDate(orderDetails.getOrderDate());
                userRMARequestDataWrapper.setCreateRMARequestForm(createRMARequestForm);
                userRMARequestDataWrapper.setRmaId(createRMAResponseData.getRmaNumber());
                distReturnRequestFacade.sendUserReturnRequestEmail(userRMARequestDataWrapper);
            } else {
                responseDTO.setErrorMsgKey("lightboxreturnrequest.process.error");
                return responseDTO;
            }
            final String createRMARequestJson = orderReturnsHelper.convertCreateRMARequestFormToJson(createRMARequestForm, orderDetails);
            responseDTO.setCreateRMAResponseData(createRMAResponseData);
            responseDTO.setCreateRMARequestJson(createRMARequestJson);
        } catch (final Exception wse) {
            LOG.error("An error has occur while creating the RMA object", wse);
            responseDTO.setErrorMsgKey("lightboxreturnrequest.process.error");
            return responseDTO;
        }
        responseDTO.setIsRmaCreated(true);
        return responseDTO;
    }

    @Secured({ ROLE_CLIENT, ROLE_TRUSTED_CLIENT, ROLE_ANONYMOUS, ROLE_CUSTOMERGROUP })
    @RequestMapping(value = "/return-reasons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @Operation(operationId = "getReturnReasons", summary = "Get return reasons for RMA Guest returns", description = "Get the list of return reasons for RMA Guest returns")
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @Cacheable(value = "returnReasonsCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true, true, 'getReturnReasons', #fields)")
    @OrderReturnsRestriction
    @ApiBaseSiteIdAndUserIdParam
    public List<DistMainReasonWsDTO> getReturnReasons(@PathVariable String baseSiteId,
                                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        return getDataMapper().mapAsList(distReturnRequestFacade.getReturnReasons(), DistMainReasonWsDTO.class, fields);
    }

}
