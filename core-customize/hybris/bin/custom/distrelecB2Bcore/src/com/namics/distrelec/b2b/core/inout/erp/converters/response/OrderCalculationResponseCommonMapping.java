package com.namics.distrelec.b2b.core.inout.erp.converters.response;

import static java.util.Objects.nonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.distrelec.webservice.if11.v3.DiscountResponse;
import com.distrelec.webservice.if11.v3.OrderEntryAvailabilityInfoResponse;
import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import com.distrelec.webservice.if11.v3.VoucherResponse;
import com.namics.distrelec.b2b.core.enums.BackOrderProfitability;
import com.namics.distrelec.b2b.core.inout.erp.exception.MoqConversionException;
import com.namics.distrelec.b2b.core.inout.erp.service.UpdateOrderEntryService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.availability.DistErpAvailabilityInfoModel;
import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;

import de.hybris.platform.core.model.order.*;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

public class OrderCalculationResponseCommonMapping {

    private static final Map<String, OrderEntryResponse> EMPTY_MAP = Collections.emptyMap();

    private static final Logger LOG = LoggerFactory.getLogger(OrderCalculationResponseCommonMapping.class);

    private static final String DEFAULT_LINE_NUMBER = "000000";

    private static final String QUANTITY_ORDERED_IN_CART_PK_FOR_ITEM_IS_LOWER_THAN_THE_MOQ_FROM_ERP = "Quantity ordered ([{}]) in cart (PK: {}) for item {} is lower than the MOQ from ERP ({})";

    public static final String IS_BACKORDER = "X";

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistProductService productService;

    @Autowired
    @Qualifier("orderEntryResponseConverter")
    private Converter<OrderEntryResponse, AbstractOrderEntryModel> orderEntryResponseConverter;

    @Autowired
    @Qualifier("updateOrderEntryService")
    private UpdateOrderEntryService updateOrderEntryService;

    @Autowired
    @Qualifier("subOrderEntryResponseConverter")
    private Converter<OrderEntryResponse, SubOrderEntryModel> subOrderEntryResponseConverter;

    /*
     * Order Entries mapping These entries represents the actual order entries in the shop cart. These entries have not yet been placed in
     * the ERP
     */
    public void fillOrderEntries(final List<OrderEntryResponse> sapOrderEntries, final AbstractOrderModel cart) throws CalculationException {

        final List<AbstractOrderEntryModel> cartEntries = cart.getNewOrderEntries();

        // an Empty list which will contains if any additional parent entries arrive from sap response
        final List<AbstractOrderEntryModel> parentCartEntryfromSapresponse = new ArrayList<>();

        // an Empty list which will contains if any additional child entries arrive from sap response
        final List<OrderEntryResponse> childEntryfromSapresponse = new ArrayList<>();

        // an Empty Map which will contains if any additional availabilityInfo has been reported from sap response
        // key --> materialNumber from sap response, value --> OrderEntryResponse
        final Map<String, OrderEntryResponse> additionalAvailabilityInfoMap = new HashMap<>();

        // key --> materialNumber from sap response, value --> OrderEntryResponse
        final Map<MultiKey, OrderEntryResponse> materialNumberToSapEntryMap = new HashMap<>();
        // key --> MultiKey(itemNumber,QuotationId,QuotationItem) from sap response, value --> AbstractOrderEntryModel
        final Map<MultiKey, AbstractOrderEntryModel> itemNumberToAbstractOrderEntryMap = new HashMap<>();

        for (final OrderEntryResponse sapOrderEntry : sapOrderEntries) {
            if (sapOrderEntry.getMoq() != null) {
                LOG.error(QUANTITY_ORDERED_IN_CART_PK_FOR_ITEM_IS_LOWER_THAN_THE_MOQ_FROM_ERP, sapOrderEntry.getOrderQuantity(),
                          cart.getPk().getLongValueAsString(),
                          sapOrderEntry.getMaterialNumber(), sapOrderEntry.getMoq());
                throw new MoqConversionException(sapOrderEntry.getMaterialNumber(), sapOrderEntry.getMoq());
            }

            boolean include = true;
            AbstractOrderEntryModel abstractOrderEntryModel = null;
            if (isParentEntry(sapOrderEntry)) {
                if (haveAdditionalItems(cartEntries, sapOrderEntry)) {
                    abstractOrderEntryModel = orderEntryResponseConverter.convert(sapOrderEntry);
                    abstractOrderEntryModel.setOrder(cart);
                    parentCartEntryfromSapresponse.add(abstractOrderEntryModel);
                    if (sapOrderEntry.getItemNumber() != null) {
                        itemNumberToAbstractOrderEntryMap.put(
                                                              new MultiKey(sapOrderEntry.getItemNumber(), sapOrderEntry.getQuotationId(),
                                                                           sapOrderEntry.getQuotationItem()),
                                                              abstractOrderEntryModel);
                    }
                } else {
                    if (sapOrderEntry.getItemNumber() != null) {
                        abstractOrderEntryModel = findOrderEntryModelFromCart(materialNumberToSapEntryMap, cartEntries, sapOrderEntry);
                        itemNumberToAbstractOrderEntryMap.put(
                                                              new MultiKey(sapOrderEntry.getItemNumber(), sapOrderEntry.getQuotationId(),
                                                                           sapOrderEntry.getQuotationItem()),
                                                              abstractOrderEntryModel);
                    }
                }

            } else {
                if (sapOrderEntry.getHigherLevelItemReason() != null && sapOrderEntry.getHigherLevelItemReason().equalsIgnoreCase("M")) {
                    additionalAvailabilityInfoMap.put(sapOrderEntry.getMaterialNumber(), sapOrderEntry);
                    include = !include;

                } else { // This means this is a sub Entry
                    childEntryfromSapresponse.add(sapOrderEntry);
                }
            }
            if (abstractOrderEntryModel != null) {
                abstractOrderEntryModel.setIsBackOrder(StringUtils.equalsIgnoreCase(sapOrderEntry.getBackOrderFlag(), IS_BACKORDER));
            }

            if (include && (sapOrderEntry.getHigherLevelItem() == null || DEFAULT_LINE_NUMBER.equals(sapOrderEntry.getHigherLevelItem()))) {
                String customerReference = abstractOrderEntryModel == null ? null : abstractOrderEntryModel.getCustomerReference();
                materialNumberToSapEntryMap.put(new MultiKey(sapOrderEntry.getMaterialNumber(), customerReference, sapOrderEntry.getQuotationId(),
                                                             getQuoteItemFromSapOrderEntry(sapOrderEntry)),
                                                sapOrderEntry);
            }
        }

        // merged into the single cart
        parentCartEntryfromSapresponse.addAll(cartEntries);

        // looping through parent orderEntries and look up corresponding values in map
        for (AbstractOrderEntryModel hybrisAbstractOrderEntry : parentCartEntryfromSapresponse) {
            createOrderEntry(materialNumberToSapEntryMap, hybrisAbstractOrderEntry, additionalAvailabilityInfoMap);
            modelService.save(hybrisAbstractOrderEntry);
        }

        // key --> AbstractOrderEntryModel , values --> list of SubOrderEntryModel
        Map<AbstractOrderEntryModel, List<SubOrderEntryModel>> aMap = new HashMap<>();
        // looping through child orderEntries and setting up corresponding parent orderEntries
        for (OrderEntryResponse sapOrderEntry : childEntryfromSapresponse) {
            final AbstractOrderEntryModel abstractOrderEntryModel = itemNumberToAbstractOrderEntryMap
                                                                                                     .get(new MultiKey(sapOrderEntry.getHigherLevelItem(),
                                                                                                                       sapOrderEntry.getQuotationId(),
                                                                                                                       sapOrderEntry.getQuotationItem()));
            abstractOrderEntryModel.setIsBOM(Boolean.TRUE);
            aMap.computeIfAbsent(abstractOrderEntryModel, k -> new ArrayList<>());

            final SubOrderEntryModel subOrderEntryModel = subOrderEntryResponseConverter.convert(sapOrderEntry);
            aMap.get(abstractOrderEntryModel).add(subOrderEntryModel);
            modelService.save(subOrderEntryModel);
        }

        for (AbstractOrderEntryModel abstractOrderEntryModel : parentCartEntryfromSapresponse) {
            abstractOrderEntryModel.setSubOrderEntries(aMap.get(abstractOrderEntryModel));
        }

        modelService.saveAll(parentCartEntryfromSapresponse);
        modelService.save(cart);
    }

    private boolean isParentEntry(final OrderEntryResponse sapOrderEntry) {
        if (sapOrderEntry != null && sapOrderEntry.getHigherLevelItem() != null) {
            return DEFAULT_LINE_NUMBER.equals(sapOrderEntry.getHigherLevelItem());
        }
        return true;
    }

    private boolean haveAdditionalItems(final List<AbstractOrderEntryModel> cartEntries, OrderEntryResponse orderEntryResponse) {
        for (Iterator<AbstractOrderEntryModel> iterator = cartEntries.iterator(); iterator.hasNext();) {
            if (orderEntryResponse.getMaterialNumber().equals(iterator.next().getProduct().getCode())) {
                return false;
            }
        }
        return true;
    }

    private AbstractOrderEntryModel findOrderEntryModelFromCart(final Map<MultiKey, OrderEntryResponse> materialNumberToSapEntryMap,
                                                                final List<AbstractOrderEntryModel> cartEntries, final OrderEntryResponse sapOrderEntry) {

        for (final AbstractOrderEntryModel cartEntry : cartEntries) {
            if (StringUtils.equals(cartEntry.getProduct().getCode(), sapOrderEntry.getMaterialNumber()) && //
                    StringUtils.equals(cartEntry.getQuotationId(), sapOrderEntry.getQuotationId()) && //
                    StringUtils.equals(getQuoteItemFromHybrisOrderEntry(cartEntry), sapOrderEntry.getQuotationItem()) && //
                    materialNumberToSapEntryMap.get(new MultiKey(cartEntry.getProduct().getCode(), cartEntry.getCustomerReference(), cartEntry.getQuotationId(),
                                                                 getQuoteItemFromHybrisOrderEntry(cartEntry))) == null) {
                return cartEntry;
            }
        }

        return null;
    }

    private void createOrderEntry(final Map<MultiKey, OrderEntryResponse> materialNumberToSapEntryMap, final AbstractOrderEntryModel hybrisOrderEntry,
                                  final Map<String, OrderEntryResponse> additionalAvailabilityInfoMap) {

        final OrderEntryResponse sapOrderEntry = materialNumberToSapEntryMap.get(new MultiKey(hybrisOrderEntry.getProduct().getCode(),
                                                                                              hybrisOrderEntry.getCustomerReference(),
                                                                                              hybrisOrderEntry.getQuotationId(),
                                                                                              getQuoteItemFromHybrisOrderEntry(hybrisOrderEntry)));

        final List<OrderEntryAvailabilityInfoResponse> availabilityInfo = new ArrayList<>();

        if (additionalAvailabilityInfoMap.get(sapOrderEntry.getMaterialNumber()) != null) {
            final List<OrderEntryAvailabilityInfoResponse> sapAvailabilityInfosIncuAddional = additionalAvailabilityInfoMap
                                                                                                                           .get(sapOrderEntry.getMaterialNumber())
                                                                                                                           .getAvailabilityInfo();
            if (sapAvailabilityInfosIncuAddional != null && sapAvailabilityInfosIncuAddional.size() != 0) {
                availabilityInfo.addAll(sapAvailabilityInfosIncuAddional);
            }
        }

        availabilityInfo.addAll(sapOrderEntry.getAvailabilityInfo());

        fillAvailabilityInfo(hybrisOrderEntry, availabilityInfo);
        // update entry
        if (sapOrderEntry.getHigherLevelItem() == null || DEFAULT_LINE_NUMBER.equals(sapOrderEntry.getHigherLevelItem())) {
            updateOrderEntryService.updateOrderEntry(sapOrderEntry, hybrisOrderEntry, additionalAvailabilityInfoMap);
        }
    }

    /**
     * Fill the erpAvailabilityInformations at OrderEntry level <br>
     * Each availability info represents the real available quantity in SAP for a specific date.
     */
    protected void fillAvailabilityInfo(final AbstractOrderEntryModel entry, final List<OrderEntryAvailabilityInfoResponse> sapAvailabilityInfos) {
        cleanUpOldErpAvailabilityInfos(entry);
        List<OrderEntryAvailabilityInfoResponse> availabilityInfos = removeInfosWithoutEstimatedDate(sapAvailabilityInfos);

        if (isBackorderNotProfitable(availabilityInfos)) {
            entry.setIsBackOrderProfitable(Boolean.FALSE);
            entry.setBackOrderProfitability(BackOrderProfitability.N);
            entry.setBackOrderedQuantity(entry.getQuantity() - calculateUnprofitableBackorderQuantity(availabilityInfos));
        } else {
            entry.setIsBackOrderProfitable(Boolean.TRUE);
            if (isAllowedBackorder(availabilityInfos)) {
                entry.setBackOrderProfitability(BackOrderProfitability.M);
            }
        }
        entry.setErpAvailabilityInfos(createNewAvailabilityInfos(availabilityInfos));
        modelService.save(entry);
    }

    private void cleanUpOldErpAvailabilityInfos(AbstractOrderEntryModel entry) {
        if (CollectionUtils.isNotEmpty(entry.getErpAvailabilityInfos())) {
            modelService.removeAll(entry.getErpAvailabilityInfos());
        }
    }

    private long calculateUnprofitableBackorderQuantity(List<OrderEntryAvailabilityInfoResponse> availabilityInfos) {
        return availabilityInfos.stream()
                                .filter(checkAvailability(BackOrderProfitability.N))
                                .mapToLong(OrderEntryAvailabilityInfoResponse::getAvailableQuantity)
                                .sum();
    }

    private boolean isAllowedBackorder(List<OrderEntryAvailabilityInfoResponse> availabilityInfos) {
        return availabilityInfos.stream()
                                .anyMatch(checkAvailability(BackOrderProfitability.M));
    }

    private boolean isBackorderNotProfitable(List<OrderEntryAvailabilityInfoResponse> availabilityInfos) {
        return availabilityInfos.stream()
                                .anyMatch(checkAvailability(BackOrderProfitability.N));
    }

    private Predicate<OrderEntryAvailabilityInfoResponse> checkAvailability(BackOrderProfitability backOrderProfitability) {
        return sapAvailabilityInfo -> nonNull(sapAvailabilityInfo.getProfitability())
                && StringUtils.equals(backOrderProfitability.toString(), sapAvailabilityInfo.getProfitability());
    }

    private List<DistErpAvailabilityInfoModel> createNewAvailabilityInfos(List<OrderEntryAvailabilityInfoResponse> availabilityInfos) {
        List<DistErpAvailabilityInfoModel> newAvailabilityInfos = availabilityInfos.stream()
                                                                                   .map(sapAvailabilityInfo -> {
                                                                                       DistErpAvailabilityInfoModel erpAvailabilityInfo = modelService.create(DistErpAvailabilityInfoModel.class);
                                                                                       erpAvailabilityInfo.setEstimatedDeliveryDate(SoapConversionHelper.convertDate(sapAvailabilityInfo.getEstimatedDeliveryDate()));
                                                                                       erpAvailabilityInfo.setQuantity(sapAvailabilityInfo.getAvailableQuantity());
                                                                                       return erpAvailabilityInfo;
                                                                                   })
                                                                                   .collect(Collectors.toList());
        modelService.saveAll(newAvailabilityInfos);
        return newAvailabilityInfos;
    }

    private List<OrderEntryAvailabilityInfoResponse> removeInfosWithoutEstimatedDate(List<OrderEntryAvailabilityInfoResponse> sapAvailabilityInfos) {
        return sapAvailabilityInfos
                                   .stream()
                                   .filter(sapAvailabilityInfo -> sapAvailabilityInfo.getEstimatedDeliveryDate() != null
                                           || !sapAvailabilityInfo.getEstimatedDeliveryDate().equals(BigInteger.ZERO))
                                   .collect(Collectors.toList());
    }

    /*
     * Confirmed Order Entries mapping These entries represents the existing open order entries in ERP These entries have already been
     * placed in the ERP
     */
    protected void fillConfirmedOrderEntries(final List<OrderEntryResponse> sapConfirmedEntries, final AbstractOrderModel cart) throws CalculationException {

        // remove existing confirmed entries
        removeExistingConfirmedEntries(cart);

        // manage confirmed entries
        final List<AbstractOrderEntryModel> confirmedEntries = new ArrayList<>();// getExistingConfirmedEntryInCart(cart);

        for (final OrderEntryResponse orderEntryResponse : sapConfirmedEntries) {
            final ProductModel product = productService.getProductForCode(orderEntryResponse.getMaterialNumber());

            final AbstractOrderEntryModel cartEntry = getConfirmedEntryByProduct(product, cart, confirmedEntries, orderEntryResponse);
            if (confirmedEntries.contains(cartEntry)) {
                // logically remove the existing confirmed entry
                confirmedEntries.remove(cartEntry);
            }

            cartEntry.setProduct(product);
            cartEntry.setUnit(product.getUnit());

            // first set the entry mandatory attributes
            updateOrderEntryService.updateOrderEntry(orderEntryResponse, cartEntry, EMPTY_MAP);

            // then fill the availability info
            fillAvailabilityInfo(cartEntry, orderEntryResponse.getAvailabilityInfo());
            modelService.save(cartEntry);
            confirmedEntries.add(cartEntry);
        }
        modelService.saveAll(confirmedEntries);
        modelService.save(cart);
    }

    private void removeExistingConfirmedEntries(final AbstractOrderModel cart) {
        if (CollectionUtils.isNotEmpty(cart.getConfirmedOrderEntries())) {
            modelService.removeAll(cart.getConfirmedOrderEntries());
            modelService.refresh(cart);
        }
    }

    protected AbstractOrderEntryModel getConfirmedEntryByProduct(final ProductModel product, final AbstractOrderModel cart,
                                                                 final List<AbstractOrderEntryModel> confirmedEntries,
                                                                 final OrderEntryResponse orderEntryResponse) {

        return confirmedEntries == null ? createNewConfirmedEntryForOrder(cart)
                                        : confirmedEntries.stream() //
                                                          .filter(confirmedEntry -> confirmedEntry.getProduct().equals(product) //
                                                                  && StringUtils.equals(confirmedEntry.getQuotationId(), orderEntryResponse.getQuotationId()) //
                                                                  && StringUtils.equals(getQuoteItemFromHybrisOrderEntry(confirmedEntry),
                                                                                        getQuoteItemFromSapOrderEntry(orderEntryResponse)) //
                                                          ).findFirst() // if not found create new one
                                                          .orElseGet(() -> createNewConfirmedEntryForOrder(cart));
    }

    private AbstractOrderEntryModel createNewConfirmedEntryForOrder(final AbstractOrderModel cart) {
        final AbstractOrderEntryModel newEntry = modelService.create(cart instanceof OrderModel ? OrderEntryModel.class : CartEntryModel.class);
        newEntry.setOrder(cart);
        newEntry.setConfirmed(Boolean.TRUE);
        return newEntry;
    }

    protected Double getTotalDiscountsValue(final List<DiscountResponse> discounts) {
        Double value = 0D;
        for (final DiscountResponse discount : discounts) {
            value += discount.getFixedValue();
        }
        return value;
    }

    protected void setCalculatedStatus(final AbstractOrderModel order) {
        order.setCalculated(Boolean.TRUE);
        modelService.save(order);
        final List<AbstractOrderEntryModel> entries = order.getEntries();
        if (entries != null) {
            for (final AbstractOrderEntryModel entry : entries) {
                entry.setCalculated(Boolean.TRUE);
            }
            modelService.saveAll(entries);
        }
    }

    private String getQuoteItemFromHybrisOrderEntry(final AbstractOrderEntryModel entry) {
        return entry != null && StringUtils.isNotBlank(entry.getLineNumber()) ? entry.getLineNumber() : DEFAULT_LINE_NUMBER;
    }

    private String getQuoteItemFromSapOrderEntry(final OrderEntryResponse entry) {
        return entry != null && StringUtils.isNotBlank(entry.getQuotationItem()) ? entry.getQuotationItem() : DEFAULT_LINE_NUMBER;
    }

    protected void fillVoucherInfo(final List<VoucherResponse> vouchers, final AbstractOrderModel target) {
        if (CollectionUtils.isNotEmpty(vouchers)) {
            // only one voucher is allowed, so get the first one
            final VoucherResponse soapVoucher = vouchers.get(0);

            final DistErpVoucherInfoModel voucherInfo = modelService.create(DistErpVoucherInfoModel.class);
            voucherInfo.setCode(soapVoucher.getCode());
            voucherInfo.setFixedValue(soapVoucher.getFixedValue());
            voucherInfo.setValidInERP(soapVoucher.isValid());

            final String returnCode = StringUtils.isEmpty(soapVoucher.getErrorCode()) ? "99" : soapVoucher.getErrorCode();
            voucherInfo.setReturnERPCode(soapVoucher.isValid() ? "00" : returnCode);
            voucherInfo.setCalculatedInERP(true);
            modelService.save(voucherInfo);
            target.setErpVoucherInfo(voucherInfo);

            modelService.save(target);
        } else if (target.getErpVoucherInfo() != null) {
            target.getErpVoucherInfo().setCalculatedInERP(true);
            modelService.save(target.getErpVoucherInfo());
        }
    }
}
