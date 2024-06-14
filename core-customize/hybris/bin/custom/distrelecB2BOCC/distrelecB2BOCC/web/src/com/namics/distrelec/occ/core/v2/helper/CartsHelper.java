package com.namics.distrelec.occ.core.v2.helper;

import static de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException.INVALID;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.event.DistSendToFriendEvent;
import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.core.model.process.RequestInvoicePaymentModeEmailProcessModel;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.order.exceptions.InvalidQuantityForSapCatalogException;
import com.namics.distrelec.b2b.core.service.order.exceptions.PunchoutException;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.user.DistAddressService;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.facades.misc.DistShareWithFriendsFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationProductData;
import com.namics.distrelec.b2b.facades.order.warehouse.WarehouseFacade;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;
import com.namics.distrelec.b2b.facades.pdf.DistPDFGenerationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import com.namics.distrelec.occ.core.cart.ws.dto.CalibrationFormWsDTO;
import com.namics.distrelec.occ.core.cart.ws.dto.SendToFriendFormWsDTO;
import com.namics.distrelec.occ.core.exceptions.UnsupportedDeliveryModeException;
import com.namics.distrelec.occ.core.order.ws.dto.AddQuotationRequestWsDTO;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderEntryWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@Component
public class CartsHelper extends AbstractHelper {
    private static final long DEFAULT_PRODUCT_QUANTITY = 1;

    private static final Logger LOG = LoggerFactory.getLogger(CartsHelper.class);

    @Autowired
    protected ErpStatusUtil erpStatusUtil;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistB2BCartFacade b2bCartFacade;

    @Autowired
    private DistCartService cartService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private DistShareWithFriendsFacade distShareWithFriendsFacade;

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private DistProductService productService;

    @Autowired
    private BusinessProcessService businessProcessService;

    @Autowired
    private DistrelecCMSSiteService distrelecCMSSiteService;

    @Autowired
    private DistUserFacade userFacade;

    @Autowired
    private DistAddressService addressService;

    @Autowired
    private WarehouseFacade warehouseFacade;

    @Autowired
    private DistPDFGenerationFacade distPDFGenerationFacade;

    public List<QuotationProductData> getProductQuantities(AddQuotationRequestWsDTO addQuotationRequest, String fields) {
        return CollectionUtils.isEmpty(addQuotationRequest.getProducts()) ? new ArrayList<>()
                                                                          : getDataMapper().mapAsList(addQuotationRequest.getProducts(),
                                                                                                      QuotationProductData.class, fields)
                                                                                           .stream()
                                                                                           .filter(product -> product.getQuantity() > 0
                                                                                                   && isNotBlank(product.getProductCode())
                                                                                                   && isNotBlank(product.getItemNumber()))
                                                                                           .collect(Collectors.toList());
    }

    public void resetInvalidVoucher() {
        if (isSavedVoucherInvalid()) {
            b2bCartFacade.resetVoucherOnCurrentCart();
        }
    }

    public void redeemOrResetVoucher(final String voucherCode) throws CalculationException, VoucherOperationException {
        DistErpVoucherInfoModel voucher = b2bCartFacade.getSessionCartModel().getErpVoucherInfo();
        if (voucher != null && StringUtils.length(voucher.getCode()) > 10) {
            b2bCartFacade.resetVoucherOnCurrentCart();
            throw new VoucherOperationException("cart.voucher_invalid");
        }

        if (StringUtils.isNotEmpty(voucherCode)) {
            b2bCartFacade.setVoucherCodeToRedeem(voucherCode);
            b2bCartFacade.recalculateCart();
        }

        if (isSavedVoucherInvalid()) {
            b2bCartFacade.resetVoucherOnCurrentCart();
            throw new VoucherOperationException("cart.voucher_invalid");
        }
    }

    private boolean isSavedVoucherInvalid() {
        DistErpVoucherInfoModel voucher = b2bCartFacade.getSessionCartModel().getErpVoucherInfo();
        return voucher == null || StringUtils.isEmpty(voucher.getReturnERPCode()) || BooleanUtils.isFalse(voucher.isValidInERP())
                || !StringUtils.equals("00", voucher.getReturnERPCode());
    }

    public void replace(CalibrationFormWsDTO form, int entryNumber) throws Exception {
        ProductModel source = productService.getProductForCode(form.getSource());
        ProductModel replacement = productService.getProductForCode(form.getTarget());
        if (areProductsNotBuyable(source, replacement)) {
            LOG.error("Product cannot be ordered temporarily.");
            throw new UnknownIdentifierException("Product cannot be ordered temporarily.");
        }
        AbstractOrderEntryModel replacedEntry = cartService.replace(source, replacement, entryNumber, form.getNewQty());
        if (replacedEntry != null) {
            b2bCartFacade.recalculateCart();
        } else {
            throw new UnknownIdentifierException("Product not found.");
        }
    }

    private boolean areProductsNotBuyable(ProductModel source, ProductModel replacement) {
        return Boolean.FALSE.equals(productFacade.isProductBuyable(source.getCode()))
                || Boolean.FALSE.equals(productFacade.isProductBuyable(replacement.getCode()));
    }

    public void shareCartPdfWithFriends(SendToFriendFormWsDTO form) {
        InputStream pdfStream = distPDFGenerationFacade.getPDFStreamForCart();
        distShareWithFriendsFacade.shareCartPdfWithFriends(createSendToFriendEvent(form), getSessionCart(), pdfStream);
    }

    private DistSendToFriendEvent createSendToFriendEvent(final SendToFriendFormWsDTO form) {
        final DistSendToFriendEvent distSendToFriendEvent = new DistSendToFriendEvent();
        distSendToFriendEvent.setYourName(form.getSenderName());
        distSendToFriendEvent.setYourEmail(form.getSenderEmail());
        distSendToFriendEvent.setReceiverName(form.getReceiverName());
        distSendToFriendEvent.setReceiverEmail(form.getReceiverEmail());
        distSendToFriendEvent.setMessage(form.getMessage());
        distSendToFriendEvent.setLanguage(commonI18NService.getCurrentLanguage());
        return distSendToFriendEvent;
    }

    public void requestInvoicePaymentMode() {
        CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
        if (userFacade.canRequestInvoicePaymentMode()) {
            B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;

            String processId = String.format("requestInvoicePaymentModeEmail-%s-%s", b2bCustomer.getUid(), System.currentTimeMillis());
            RequestInvoicePaymentModeEmailProcessModel processModel = businessProcessService.createProcess(
                                                                                                           processId,
                                                                                                           "requestInvoicePaymentModeEmailProcess");

            processModel.setCustomer(b2bCustomer);
            processModel.setSite(distrelecCMSSiteService.getCurrentSite());

            getModelService().save(processModel);
            businessProcessService.startProcess(processModel);

            b2bCustomer.setRequestedInvoicePaymentModeDate(new Date());
            getModelService().save(b2bCustomer);
        } else {
            LOG.warn("Customer {} is not eligible for invoice payment method request", customer.getUid());
        }
    }

    public void validateCartForPlaceOrder() {
        validateDeliveryPage();
        validatePaymentDetailsOnCart();
    }

    public void validateCartForInvoiceApproval() {
        validateCartForPlaceOrder();
        validateIsPaymentInvoice();
    }

    public void validateDeliveryPage() {
        CartModel cart = cartService.getSessionCart();
        B2BCustomerModel customer = (B2BCustomerModel) cart.getUser();

        if (isNull(cart.getDeliveryMode())) {
            throw new CartException("checkout.deliverymode_not_selected", INVALID);
        }

        if (isNull(cart.getPaymentAddress())) {
            throw new CartException("checkout.billing_not_selected", INVALID);
        }

        if (isNotPickup(cart) && isNull(cart.getDeliveryAddress())) {
            throw new CartException("checkout.delivery_not_selected", INVALID);
        }

        if (checkoutFacade.shouldAllowEditBilling(customer.getCustomerType()) && isAddressNotValid(cart.getPaymentAddress())) {
            throw new CartException("checkout.billing_invalid_address", INVALID);
        }

        if (isNotPickup(cart) && shouldAllowEditShipping(cart, customer) && isAddressNotValid(cart.getDeliveryAddress())) {
            throw new CartException("checkout.delivery_invalid_address", INVALID);
        }

        if (checkoutFacade.isMinimumOrderValueNotReached(cart)) {
            throw new CartException("checkout.invalid.mov", INVALID);
        }

        if (checkoutFacade.hasUnallowedBackorders()) {
            throw new CartException("checkout.invalid.backorder", INVALID);
        }

        Collection<PunchoutFilterResult> punchoutFilterResults = b2bCartFacade.removeProductsWithPunchout();
        if (CollectionUtils.isNotEmpty(punchoutFilterResults)) {
            throw new PunchoutException("cart.product_error_punchout", punchoutFilterResults);
        }
    }

    public String getJoinedPunchedOutProducts(Collection<PunchoutFilterResult> punchoutFilterResult) {
        return punchoutFilterResult.stream()
                                   .filter(Objects::nonNull)
                                   .map(PunchoutFilterResult::getPunchedOutProduct)
                                   .filter(Objects::nonNull)
                                   .map(ProductModel::getCode)
                                   .collect(Collectors.joining(DistConstants.Punctuation.COMMA));
    }

    public List<String> getProductCodesFromCartModification(List<CartModificationData> cartModificationData) {
        return cartModificationData
                                   .stream()
                                   .map(modification -> modification.getEntry().getProduct().getCode())
                                   .collect(Collectors.toList());
    }

    private boolean isNotPickup(CartModel cart) {
        return !StringUtils.equals(DistConstants.Shipping.METHOD_PICKUP, cart.getDeliveryMode().getCode());
    }

    private boolean shouldAllowEditShipping(CartModel cart, B2BCustomerModel customer) {
        if (areBillingAndShippingTheSame(cart)) {
            return checkoutFacade.shouldAllowEditBilling(customer.getCustomerType());
        }
        return true;
    }

    private boolean areBillingAndShippingTheSame(CartModel cart) {
        return nonNull(cart.getPaymentAddress())
                && Objects.equals(cart.getPaymentAddress(), cart.getDeliveryAddress());
    }

    private boolean isAddressNotValid(AddressModel addressData) {
        return !addressService.isAddressValid(addressData)
                || isUserAddressBlocked(addressData);
    }

    private boolean isUserAddressBlocked(AddressModel address) {
        String countryIso = address.getCountry() != null ? address.getCountry().getIsocode() : null;
        return checkoutFacade.isUserAddressBlocked(address.getPostalcode(), address.getLine1(), address.getLine2(),
                                                   address.getTown(), countryIso);
    }

    public void validatePaymentDetailsOnCart() {
        CartModel cartModel = getSessionCart();
        if (cartModel.getPaymentMode() == null) {
            throw new CartException("checkout.paymentMode.notSelected", INVALID);
        }
    }

    public void validateIsPaymentInvoice() {
        CartModel cart = getSessionCart();
        if (!InvoicePaymentInfoModel._TYPECODE.equals(cart.getPaymentMode().getPaymentInfoType().getCode())) {
            throw new CartException("checkout.paymentMode.invalid", INVALID);
        }
    }

    public CurrencyModel getCartCurrency() {
        CartModel cartModel = getSessionCart();
        return cartModel.getCurrency();
    }

    public CartModel getSessionCart() {
        return cartService.getSessionCart();
    }

    public long getOrderQuantity(OrderEntryWsDTO entry) {
        Long orderQtyMin = getOrderQtyMin(entry);
        return (nonNull(entry.getQuantity()) && entry.getQuantity() > orderQtyMin) ? entry.getQuantity() : orderQtyMin;
    }

    private long getOrderQtyMin(OrderEntryWsDTO entry) {
        return nonNull(entry.getProduct()) && isNotBlank(entry.getProduct().getCode()) ? productFacade.getMinimumOrderQty(entry.getProduct().getCode())
                                                                                       : DEFAULT_PRODUCT_QUANTITY;
    }

    public List<WarehouseData> getValidWarehouse(boolean isPickupAllowed) {
        return isPickupAllowed ? warehouseFacade.getCheckoutPickupWarehousesForCurrSite() : Collections.emptyList();
    }

    public CCPaymentInfoData applyOrRemovePaymentInfo(DistPaymentModeData paymentMode) {
        if (BooleanUtils.isTrue(paymentMode.getCreditCardPayment())) {
            checkoutFacade.setDefaultPaymentInfoForCheckout();
            return checkoutFacade.getPaymentInfo();
        }
        checkoutFacade.removePaymentInfo();
        return null;
    }

    public void validateEntryForUpdate(long entryNumber, long quantity) {
        AbstractOrderEntryModel orderEntry = b2bCartFacade.getOrderEntry(entryNumber);
        if (b2bCartFacade.isInvalidQuantityForSAPCatalogProduct(orderEntry.getProduct(), quantity)) {
            throw new InvalidQuantityForSapCatalogException("sap.catalog.order.articles.error", "Quantity requested is larger than 1 for SAP Catalog Article");
        }
    }

    public DistRegisterData createGuestRegisterData(final String email) {
        final DistRegisterData data = new DistRegisterData();
        data.setLogin(email);
        data.setEmail(email);
        final CurrencyModel currency = commonI18NService.getCurrentCurrency();
        data.setCurrencyCode(currency.getIsocode() == null ? "EUR" : currency.getIsocode());
        return data;
    }

    public void setDeliveryMode(final String deliveryModeId, final String wareHouseId) throws CalculationException, UnsupportedDeliveryModeException {
        if (StringUtils.isNotEmpty(deliveryModeId)) {
            final DeliveryModeData deliveryMode = checkoutFacade.getDeliveryModeForCode(deliveryModeId);
            if (nonNull(deliveryMode)) {
                String warehouseId = isDeliveryModeDataPickup(deliveryMode) && StringUtils.isNotEmpty(wareHouseId) ? wareHouseId : null;
                checkoutFacade.setPickupLocation(warehouseId);
                if (isDeliveryModeDataPickup(deliveryMode)) {
                    checkoutFacade.removeDeliveryAddress();
                } else if (checkoutFacade.isDeliveryAddressNotSetForCurrentCart()) {
                    checkoutFacade.setDeliveryAddressIfAvailable();
                }

                checkoutFacade.setDeliveryMode(deliveryMode.getCode());
                if (!checkoutFacade.isScheduleDeliveryAllowedForCurrentCart()) {
                    resetRequestedDeliveryDate();
                }
                checkoutFacade.calculateOrder(true);
            } else {
                throw new UnsupportedDeliveryModeException(deliveryModeId);
            }
        }
    }

    private boolean isDeliveryModeDataPickup(DeliveryModeData selectedDeliveryMode) {
        return checkoutFacade.getPickupDeliveryModeCode().equals(selectedDeliveryMode.getCode());
    }

    private void resetRequestedDeliveryDate() {
        checkoutFacade.requestDeliveryDate(null);
    }

    public String getJoinedBlockedProducts(List<CartModificationData> blockedEntries) {
        return blockedEntries.stream()
                             .filter(Objects::nonNull)
                             .map(CartModificationData::getEntry)
                             .filter(Objects::nonNull)
                             .map(OrderEntryData::getProduct)
                             .map(ProductData::getCode)
                             .collect(Collectors.joining(DistConstants.Punctuation.COMMA));
    }
}
