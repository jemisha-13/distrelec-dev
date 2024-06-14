package com.namics.distrelec.b2b.facades.rma.impl;

import static com.namics.distrelec.b2b.facades.order.converters.SapReadOrderResponseOrderHistoryConverter.MAIN_REASON_TEXT;
import static com.namics.distrelec.b2b.facades.order.converters.SapReadOrderResponseOrderHistoryConverter.NOT_RECIEVED_DEFAULT_VALUE;
import static com.namics.distrelec.b2b.facades.rma.converters.DistCreateRMARequestConverter.RMA_COMMENT_SEPARATOR;
import static com.namics.distrelec.b2b.facades.rma.converters.DistCreateRMARequestConverter.RMA_SECOND_LEVEL_FIXED_LENGTH;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.if19.v1.RMACreateRequest;
import com.distrelec.webservice.if19.v1.RMACreateRespOrder;
import com.distrelec.webservice.if19.v1.RMACreateResponse;
import com.namics.distrelec.b2b.core.event.DistRmaRequestEvent;
import com.namics.distrelec.b2b.core.event.DistRmaRequestEventEntry;
import com.namics.distrelec.b2b.core.event.DistRmaRequestEventGuestEntry;
import com.namics.distrelec.b2b.core.inout.erp.RMAService;
import com.namics.distrelec.b2b.core.inout.erp.ReturnOnlineService;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.rma.GuestRMACreateRequestForm;
import com.namics.distrelec.b2b.core.rma.ReturnRequestData;
import com.namics.distrelec.b2b.core.rma.enums.RmaMainReturnReasonTypes;
import com.namics.distrelec.b2b.core.rma.enums.RmaSubReturnReasonTypes;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.facades.rma.DistReturnRequestFacade;

import de.hybris.platform.commercefacades.order.data.DistMainReasonData;
import de.hybris.platform.commercefacades.order.data.DistSubReasonData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

public class DefaultDistReturnRequestFacade implements DistReturnRequestFacade {

    @Autowired
    private RMAService rmaService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private DistrelecBaseStoreService baseStoreService;

    @Autowired
    private ReturnOnlineService returnOnlineService;

    @Autowired
    private UserService userService;

    @Autowired
    private DistProductService productService;

    @Autowired
    private EventService eventService;

    @Autowired
    private Converter<CreateRMARequestForm, RMACreateRequest> distCreateRMARequestConverter;

    @Autowired
    private Converter<RMACreateRespOrder, CreateRMAResponseData> distCreateRMAResponseDataConverter;

    @Override
    public String createReturnRequest(final ReturnRequestData returnRequestData) {
        return rmaService.createReturnRequest(returnRequestData);
    }

    @Override
    public List<Object> getReturnRequests() {
        return rmaService.getReturnRequests();
    }

    @Override
    public List<Object> getReturnRequestsByOrder(final OrderModel order) {
        return rmaService.getReturnRequestsByOrder(order);
    }

    @Override
    public Object getReturnRequest(final String rmaCode) {
        return rmaService.getReturnRequest(rmaCode);
    }

    @Override
    public CreateRMAResponseData createRMACreateRequest(final CreateRMARequestForm createRMARequestForm) {

        final RMACreateRequest rmaCreateRequest = new RMACreateRequest();

        // Converter call to create RMACreate Request from the form.
        distCreateRMARequestConverter.convert(createRMARequestForm, rmaCreateRequest);

        // Get the Create RMA response from SAP.
        final RMACreateResponse rmaCreateResponse = returnOnlineService.rmaCreate(rmaCreateRequest);
        final RMACreateRespOrder rmaCreateRespOrder = rmaCreateResponse.getRmaOrder();

        // Create output Data to be displayed on web-shop in converter.
        final CreateRMAResponseData createRMAResponseData = new CreateRMAResponseData();
        distCreateRMAResponseDataConverter.convert(rmaCreateRespOrder, createRMAResponseData);

        return createRMAResponseData;
    }

    @Override
    public void sendGuestReturnRequestEmail(GuestRMACreateRequestForm guestRMACreateRequestForm) {
        eventService.publishEvent(getRmaRequestEventForGuest(guestRMACreateRequestForm));
    }

    @Override
    public void sendUserReturnRequestEmail(UserRMARequestDataWrapper userRmaRequestDataWrapper) {
        eventService.publishEvent(getRmaRequestEvent(userRmaRequestDataWrapper));
    }

    @Override
    public List<DistMainReasonData> getReturnReasons() {
        List<DistMainReasonData> returnReasons = new ArrayList<>();

        List<RmaMainReturnReasonTypes> returnReasonTypes = Stream.of(RmaMainReturnReasonTypes.values())
                                                                 .collect(Collectors.toList());

        List<RmaSubReturnReasonTypes> returnReasonSubTypes = returnReasonTypes.stream()
                                                                              .flatMap(type -> type.getSubTypes().stream())
                                                                              .collect(Collectors.toList());

        for (RmaMainReturnReasonTypes type : returnReasonTypes) {
            DistMainReasonData mainReason = new DistMainReasonData();
            mainReason.setMainReasonId(type.getCode());
            mainReason.setMainReasonText(MAIN_REASON_TEXT + type.getCode());

            List<DistSubReasonData> subReasons = getSubReasonsForMainReason(type, returnReasonSubTypes);
            if (isEmpty(subReasons)) {
                mainReason.setDefaultSubReasonId(NOT_RECIEVED_DEFAULT_VALUE);
            }

            mainReason.setSubReasons(subReasons);
            returnReasons.add(mainReason);
        }

        return returnReasons;
    }

    @Override
    public boolean isOrderReturnDisabled() {
        return !baseSiteService.getCurrentBaseSite().isEnableProductReturn();
    }

    private List<DistSubReasonData> getSubReasonsForMainReason(RmaMainReturnReasonTypes type, List<RmaSubReturnReasonTypes> returnReasonSubTypes) {

        if (isFalse(isSupportingSubReasons(type))) {
            return emptyList();
        }

        return returnReasonSubTypes.stream()
                                   .filter(subType -> type.getSubTypes().contains(subType))
                                   .map(subType -> {
                                       DistSubReasonData subReason = new DistSubReasonData();
                                       subReason.setSubReasonId(subType.getCode());
                                       subReason.setSubReasonMessages(subType.getMessageKeys());
                                       return subReason;
                                   })
                                   .collect(Collectors.toList());
    }

    private boolean isSupportingSubReasons(RmaMainReturnReasonTypes type) {
        return !type.equals(RmaMainReturnReasonTypes.NOT_RECEIVED);
    }

    private DistRmaRequestEvent getRmaRequestEventForGuest(final GuestRMACreateRequestForm guestRMACreateRequestForm) {

        final DistRmaRequestEvent distRmaRequestEvent = new DistRmaRequestEvent();
        final List<DistRmaRequestEventEntry> rmaRequestEventEntryList = new ArrayList<>();

        // Details Regarding site, store
        distRmaRequestEvent.setSite(baseSiteService.getCurrentBaseSite());
        distRmaRequestEvent.setCreatedAt(new Date());
        distRmaRequestEvent.setBaseStore(baseStoreService.getCurrentBaseStore());
        distRmaRequestEvent.setOrderCode(guestRMACreateRequestForm.getOrderNumber());

        // Details Regarding Guests
        DistRmaRequestEventGuestEntry distRmaRequestEventGuestEntry = new DistRmaRequestEventGuestEntry();
        distRmaRequestEventGuestEntry.setCustomerName(guestRMACreateRequestForm.getCustomerName());
        distRmaRequestEventGuestEntry.setEmailAddress(guestRMACreateRequestForm.getEmailAddress());
        distRmaRequestEventGuestEntry.setPhoneNumber(guestRMACreateRequestForm.getPhoneNumber());
        distRmaRequestEvent.setGuestEntries(distRmaRequestEventGuestEntry);

        // Details Regarding Return Claim
        DistRmaRequestEventEntry rmaRequestEventEntry = new DistRmaRequestEventEntry();
        rmaRequestEventEntry.setAmount(guestRMACreateRequestForm.getQuantity().toString());
        rmaRequestEventEntry.setComment(getCustomerText(guestRMACreateRequestForm));
        rmaRequestEventEntry.setProductNumber(guestRMACreateRequestForm.getArticleNumber());
        rmaRequestEventEntry.setReturnReason(guestRMACreateRequestForm.getReturnReason());

        rmaRequestEventEntryList.add(rmaRequestEventEntry);
        distRmaRequestEvent.setEntries(rmaRequestEventEntryList);

        distRmaRequestEvent.setCustomer((CustomerModel) userService.getCurrentUser());
        distRmaRequestEvent.setLanguage(baseSiteService.getCurrentBaseSite().getDefaultLanguage());

        return distRmaRequestEvent;
    }

    private String getCustomerText(GuestRMACreateRequestForm guestRMACreateRequestForm) {
        // secondLevelReason was added here, to allow checking which was the reason of returning in ZR01 document in ERP
        // This was done because multiple 2nd level reasons share the same code and because of that they aren't
        // able to map what is the correct return reason which the customer selected
        return getFixedLengthSubReason(guestRMACreateRequestForm.getReturnSubReason()) + RMA_COMMENT_SEPARATOR + guestRMACreateRequestForm.getCustomerText();
    }

    private String getFixedLengthSubReason(String subReason) {
        return StringUtils.rightPad(subReason, RMA_SECOND_LEVEL_FIXED_LENGTH, StringUtils.EMPTY);
    }

    private DistRmaRequestEvent getRmaRequestEvent(UserRMARequestDataWrapper data) {
        CreateRMARequestForm createRMARequestForm = data.getCreateRMARequestForm();
        String rmaId = data.getRmaId();

        final DistRmaRequestEvent distRmaRequestEvent = new DistRmaRequestEvent();
        distRmaRequestEvent.setCreatedAt(new Date());
        distRmaRequestEvent.setPurchaseDate(createRMARequestForm.getOrderDate());
        distRmaRequestEvent.setSite(baseSiteService.getCurrentBaseSite());
        distRmaRequestEvent.setCustomer((CustomerModel) userService.getCurrentUser());
        distRmaRequestEvent.setBaseStore(baseStoreService.getCurrentBaseStore());
        distRmaRequestEvent.setRmaCode(rmaId);

        distRmaRequestEvent.setOrderCode(createRMARequestForm.getOrderId());
        if (CollectionUtils.isNotEmpty(createRMARequestForm.getOrderItems())) {
            distRmaRequestEvent.setEntries(createRMARequestForm.getOrderItems().stream().filter(item -> item.getQuantity() != null).map(returnEntryData -> {
                final DistRmaRequestEventEntry eventEntry = new DistRmaRequestEventEntry();

                eventEntry.setAmount(Long.toString(returnEntryData.getQuantity()));
                eventEntry.setComment(returnEntryData.getCustomerText());

                final ProductModel productModel = productService.getProductForCode(returnEntryData.getArticleNumber());
                if (productModel != null) {
                    eventEntry.setProductNumber(productModel.getCodeErpRelevant());
                    eventEntry.setProductName(productModel.getName());
                }

                eventEntry.setReplacement(returnEntryData.getRefundType());
                eventEntry.setReturnReason(returnEntryData.getReturnReasonID());
                eventEntry.setSerialNumbers(returnEntryData.getArticleNumber());

                return eventEntry;
            }).collect(Collectors.toList()));
        }

        return distRmaRequestEvent;
    }

}
