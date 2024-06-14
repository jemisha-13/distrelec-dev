/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.helper;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.rma.CreateRMAOrderEntryDataForm;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.occ.core.order.ws.dto.DistReturnRequestEntryInputListWsDTO;
import com.namics.distrelec.occ.core.order.ws.dto.DistReturnRequestEntryInputWsDTO;
import com.namics.distrelec.occ.core.returns.data.ReturnRequestsData;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestWsDTO;
import de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade;
import de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

/**
 * Helper class for order returns
 */
@Component
public class OrderReturnsHelper extends AbstractHelper {
    private static final Logger LOG = LoggerFactory.getLogger(OrderReturnsHelper.class);

    @Resource(name = "omsReturnFacade")
    private OmsReturnFacade omsReturnFacade;

    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;

    @Resource(name = "productFacade")
    private ProductFacade productFacade;

    /**
     * Returns the list of order return requests for the current user
     *
     * @param currentPage
     *            The current result page requested.
     * @param pageSize
     *            he number of results returned per page.
     * @param sort
     *            Sorting method applied to the return results.
     * @param fields
     *            configuration for fields that will be returned in {@link ReturnRequestListWsDTO}
     * @return The {@link ReturnRequestListWsDTO} for the current user.
     */
    public ReturnRequestListWsDTO searchOrderReturnRequests(final int currentPage, final int pageSize, final String sort, final String fields) {
        final PageableData pageableData = createPageableData(currentPage, pageSize, sort);

        final ReturnRequestsData returnRequestsData = createReturnRequestsData(omsReturnFacade.getPagedReturnRequestsByCurrentUser(pageableData));

        return getDataMapper().map(returnRequestsData, ReturnRequestListWsDTO.class, fields);
    }

    /**
     * Returns a return request by its code
     *
     * @param orderReturnRequestCode
     *            Code of the order return request
     * @param fields
     *            configuration for fields that will be returned in {@link ReturnRequestWsDTO}
     * @return THe {@link ReturnRequestWsDTO} which the code belongs to
     */
    public ReturnRequestWsDTO getOrderReturnRequest(final String orderReturnRequestCode, final String fields) {
        final ReturnRequestData returnRequestDetails = omsReturnFacade.getReturnForReturnCode(orderReturnRequestCode);
        validateUserForOrder(returnRequestDetails.getOrder().getCode());

        populateReturnEntriesWithProductData(returnRequestDetails);

        return getDataMapper().map(returnRequestDetails, ReturnRequestWsDTO.class, fields);
    }

    /**
     * Cancels an order return request
     *
     * @param orderReturnRequestCode
     *            The code of the order return request which is requested to be cancelled
     */
    public void cancelOrderReturnRequest(final String orderReturnRequestCode) {
        final ReturnRequestData returnRequestDetails = omsReturnFacade.getReturnForReturnCode(orderReturnRequestCode);
        validateUserForOrder(returnRequestDetails.getOrder().getCode());

        final CancelReturnRequestData cancelReturnRequestData = new CancelReturnRequestData();
        cancelReturnRequestData.setCode(orderReturnRequestCode);
        cancelReturnRequestData.setCancelReason(CancelReason.CUSTOMERREQUEST);

        omsReturnFacade.cancelReturnRequest(cancelReturnRequestData);
    }

    /**
     * Creates an order return request
     *
     * @param returnRequestEntryInputListWsDTO
     *            The return request entry input list for an order
     * @param fields
     *            configuration for fields that will be returned in {@link ReturnRequestWsDTO}
     * @return The {@link ReturnRequestWsDTO} converted from the newly created return request
     */
    public ReturnRequestWsDTO createOrderReturnRequest(final ReturnRequestEntryInputListWsDTO returnRequestEntryInputListWsDTO, final String fields) {
        final String orderCode = returnRequestEntryInputListWsDTO.getOrderCode();
        validateUserForOrder(orderCode);

        final OrderData order = orderFacade.getOrderDetailsForCode(orderCode);
        final ReturnRequestData returnRequestData = prepareReturnRequestData(order, returnRequestEntryInputListWsDTO);

        final ReturnRequestData createdReturnRequestData = omsReturnFacade.createReturnRequest(returnRequestData);

        populateReturnEntriesWithProductData(createdReturnRequestData);

        return getDataMapper().map(createdReturnRequestData, ReturnRequestWsDTO.class, fields);
    }

    /**
     * Populates return entries of {@code ReturnRequestData} with {@code ProductData}
     *
     * @param returnRequestData
     *            The return request data whose return entries are populated
     */
    protected void populateReturnEntriesWithProductData(ReturnRequestData returnRequestData) {
        returnRequestData.getReturnEntries().forEach(entry -> {
            final ProductData productData = productFacade.getProductForCodeAndOptions(entry.getOrderEntry().getProduct().getCode(), null);
            entry.getOrderEntry().setProduct(productData);
        });
    }

    /**
     * Creates a new {@link ReturnRequestsData} from {@link SearchPageData}
     *
     * @param result
     *            Search results
     * @return {@link ReturnRequestsData} which contains the information provided by {@link SearchPageData}
     */

    protected ReturnRequestsData createReturnRequestsData(final SearchPageData<ReturnRequestData> result) {
        final ReturnRequestsData returnRequestsData = new ReturnRequestsData();

        returnRequestsData.setReturnRequests(result.getResults());
        returnRequestsData.setSorts(result.getSorts());
        returnRequestsData.setPagination(result.getPagination());

        return returnRequestsData;
    }

    /**
     * Validates if the current user has access to the order
     *
     * @param orderCode
     *            the order code
     * @throws NotFoundException
     *             if current user has no access to the order
     */
    protected void validateUserForOrder(final String orderCode) {
        try {
            orderFacade.getOrderDetailsForCode(orderCode);
        } catch (UnknownIdentifierException ex) {
            LOG.warn("Order not found for current user in current BaseStore", ex);
            throw new NotFoundException("Resource not found");
        }
    }

    /**
     * It prepares the {@link ReturnRequestData} object by taking the order and the map of orderentries {@link OrderEntryData} number and
     * returned quantities
     *
     * @param order
     *            order {@link OrderData} which we want to return
     * @param returnRequestEntryInputList
     *            a {@link ReturnRequestEntryInputListWsDTO} array of order entries number and the returned quantities
     * @return returnRequest {@link ReturnRequestData}
     */
    protected ReturnRequestData prepareReturnRequestData(final OrderData order, final ReturnRequestEntryInputListWsDTO returnRequestEntryInputList) {
        final ReturnRequestData returnRequest = new ReturnRequestData();

        final List<ReturnEntryData> returnEntries = returnRequestEntryInputList.getReturnRequestEntryInputs().stream().map(this::mapToReturnEntryData)
                                                                               .collect(toList());

        returnRequest.setOrder(order);
        returnRequest.setReturnEntries(returnEntries);
        returnRequest.setRefundDeliveryCost(false);

        return returnRequest;
    }

    /**
     * Maps {@link ReturnRequestEntryInputWsDTO} to {@link ReturnEntryData} along with default values necessary for a return request
     *
     * @param entryInput
     *            Return request entry input which will be mapped
     * @return The mapped {@link ReturnRequestEntryInputWsDTO}
     */
    protected ReturnEntryData mapToReturnEntryData(final ReturnRequestEntryInputWsDTO entryInput) {
        final ReturnEntryData returnEntry = new ReturnEntryData();

        final OrderEntryData oed = new OrderEntryData();
        oed.setEntryNumber(entryInput.getOrderEntryNumber());

        returnEntry.setOrderEntry(oed);
        returnEntry.setExpectedQuantity(entryInput.getQuantity());
        returnEntry.setRefundReason(RefundReason.GOODWILL);
        returnEntry.setAction(ReturnAction.HOLD);

        return returnEntry;
    }

    /**
     * This method check whether the quantity ordered in return request does not exceeds the remaining available quantity.
     *
     * @param orderEntryList
     * @param createRMAOrderEntryDataList
     * @return boolean
     */
    public boolean isReturnRaisedQuantityAvailable(final List<OrderEntryData> orderEntryList,
                                                   final List<CreateRMAOrderEntryDataForm> createRMAOrderEntryDataList) {
        boolean invalidQuantity = false;
        int index = 0;
        for (final CreateRMAOrderEntryDataForm createRMAOrderEntryDataForm : createRMAOrderEntryDataList) {
            if (!StringUtils.isEmpty(createRMAOrderEntryDataForm.getReturnReasonID())) {
                final OrderEntryData orderEntryData = orderEntryList.stream()
                                                                    .filter(orderData -> orderData.getItemPosition()
                                                                                                  .equalsIgnoreCase(createRMAOrderEntryDataForm.getItemNumber()))
                                                                    .findFirst().orElse(null);
                if (createRMAOrderEntryDataForm.getQuantity() == null
                        || (null != orderEntryData && createRMAOrderEntryDataForm.getQuantity() > orderEntryData.getRmaData().getRemainingReturnQty())) {
                    // Ordered Quantity exceeds the remaining available quantity.
                    invalidQuantity = true;
                }
            }
            index++;
        }

        return invalidQuantity;

    }

    public boolean isRmaRaised(final List<CreateRMAOrderEntryDataForm> orderItems) {

        // Check if the return request has been raised or not
        boolean isRmaRaised = false;
        for (final CreateRMAOrderEntryDataForm createRMAOrderEntryDataForm : orderItems) {
            if (createRMAOrderEntryDataForm.isRmaRaised()) {
                // Return request raised
                isRmaRaised = true;
                return isRmaRaised;
            }
        }
        return isRmaRaised;
    }

    public String convertCreateRMARequestFormToJson(@Valid CreateRMARequestForm createRMARequestForm, OrderData orderDetails) {
        createRMARequestForm.setOrderDate(orderDetails.getOrderDate());
        createRMARequestForm.setOrderItems(createRMARequestForm.getOrderItems().stream()
                                                               .filter(this::isReturnOrderItemSubmitted)
                                                               .collect(Collectors.toList()));
        return convertObjectToJson(createRMARequestForm);
    }

    private boolean isReturnOrderItemSubmitted(CreateRMAOrderEntryDataForm orderEntryDataForm) {
        return orderEntryDataForm.getArticleNumber() != null &&
                orderEntryDataForm.getQuantity() != null &&
                orderEntryDataForm.getReturnReasonID() != null;
    }

    /**
     * Convert the Java Object to JSON.
     *
     * @param obj
     * @return String
     */
    private String convertObjectToJson(final Object obj) {
        String jsonInString = StringUtils.EMPTY;
        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsonInString = mapper.writeValueAsString(obj);

        } catch (final JsonGenerationException e) {
            LOG.warn("Exception occurred during JSON generation", e);
        } catch (final JsonMappingException e) {
            LOG.warn("Exception occurred during JSON mapping", e);
        } catch (final IOException e) {
            LOG.warn("Exception occurred during conversion of object to JSON", e);
        }

        return jsonInString;
    }

    public CreateRMARequestForm mapToCreateRMARequestForm(final DistReturnRequestEntryInputListWsDTO distReturnRequestEntryInputList,
                                                          final OrderData orderDetails) {
        CreateRMARequestForm createRMARequestForm = new CreateRMARequestForm();
        createRMARequestForm.setOrderId(distReturnRequestEntryInputList.getOrderCode());
        createRMARequestForm.setOrderDate(orderDetails.getOrderDate());

        List<CreateRMAOrderEntryDataForm> entries = new ArrayList<>();
        List<DistReturnRequestEntryInputWsDTO> returnRequestEntryInputs = distReturnRequestEntryInputList.getReturnRequestEntryInputs();
        for (DistReturnRequestEntryInputWsDTO input : returnRequestEntryInputs) {
            CreateRMAOrderEntryDataForm entry = new CreateRMAOrderEntryDataForm();
            entry.setItemNumber(input.getOrderEntryNumber().toString());
            entry.setArticleNumber(input.getArticleNumber());
            entry.setQuantity(input.getQuantity());
            entry.setCustomerText(input.getCustomerComment());
            entry.setReturnReasonID(input.getMainReasonId());
            entry.setReturnSubReason(input.getSubReasonId());

            entries.add(entry);
        }
        createRMARequestForm.setOrderItems(entries);

        return createRMARequestForm;
    }
}
