/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.event.DistSendToFriendEvent;
import com.namics.distrelec.b2b.core.inout.erp.exception.MoqUnderflowException;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Cart;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.RemoveCart;
import com.namics.distrelec.b2b.facades.adobe.datalayer.impl.DefaultDistDigitalDatalayerFacade;
import com.namics.distrelec.b2b.facades.campaigns.DistCampaignFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.export.DistExportFacade;
import com.namics.distrelec.b2b.facades.misc.DistShareWithFriendsFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import com.namics.distrelec.b2b.facades.pdf.DistPDFGenerationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.facades.reco.DistRecommendationFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.CalibrationForm;
import com.namics.distrelec.b2b.storefront.forms.PriceDiffForm;
import com.namics.distrelec.b2b.storefront.forms.SendToFriendForm;
import com.namics.distrelec.b2b.storefront.forms.UpdateQuantityForm;
import com.namics.hybris.ffsearch.data.campaign.FactFinderFeedbackTextData;
import com.namics.hybris.ffsearch.data.suggest.AutocompleteSuggestion;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller for cart page.
 */
@Controller
@RequestMapping(value = "/cart")
public class CartPageController extends AbstractPageController {

    protected static final Logger LOG = LogManager.getLogger(CartPageController.class);

    private static final String DANGEROUS_PRODUCTS = "/dangerousProducts";

    private static final String ALLOWED_ZERO_PRICE_ARTICLES_PROP = "order.sap.zero-price.allowed";

    private static final int CHARACTER_LIMIT = Integer.parseInt(DistConstants.Product.ATTRIBUTE_MAX_NAME_LENGTH);

    private static final String CART_CMS_PAGE = "cartPage";

    public static final String CART_DATA = "cartData";

    private static final String CONTINUE_URL = "continueUrl";

    public static final String PAGE_CART = "/cart";

    private final static String CONF_VOUCHER_ERROR_KEY_PREFIX = "checkoutvoucherbox.voucherError.error";

    private final static String CONF_VOUCHER_ERROR_KEY_PREFIX_01 = "checkoutvoucherbox.voucherError.error01";

    private static final String VOUCHER_ERROR_CODE_EMPTY = "98";

    public static final String PUSHED_CAMPAIGN_PRODUCTS_PATH = "/pushed-campaign-products";

    public static final String RECOMMENDED_PRODUCTS_PATH = "/recommendation";

    private static final String CAROUSEL_DATA_ATTRIBUTE_NAME = "productFFCarouselData";

    public static final String PRODUCT_CODE_PATH_VARIABLE_PATTERN = "/{productCodes:.*}";

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade cartFacade;

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("distCampaignFacade")
    private DistCampaignFacade distCampaignFacade;

    @Autowired
    private DistExportFacade distExportFacade;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    private DistShareWithFriendsFacade distShareWithFriendsFacade;

    @Autowired
    private DistCartService cartService;

    @Autowired
    @Qualifier("distRecommendationFacade")
    private DistRecommendationFacade<ProductData, FactFinderFeedbackTextData> recommendationFacade;

    @Autowired
    private DistProductService productService;

    @Autowired
    private DistCommercePriceService distCommercePriceService;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    @Qualifier("defaultDistDigitalDatalayerFacade")
    private DefaultDistDigitalDatalayerFacade defaultDistDigitalDatalayerFacade;

    @Autowired
    @Qualifier("productModelUrlResolver")
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    @Qualifier("catalogPlusProductModelUrlResolver")
    private DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver;

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private DistPDFGenerationFacade distPDFGenerationFacade;

    @GetMapping
    public String showCart(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {

        final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
        model.addAttribute("customer", customer);

        if (!model.containsAttribute(WebConstants.HAS_PUNCHED_OUT_PRODUCTS) && !model.containsAttribute(WebConstants.PRODUCTS_PUNCHED_OUT)) {
            final Collection<PunchoutFilterResult> punchoutFilterResult = getCartFacade().removeProductsWithPunchout();

            if (CollectionUtils.isNotEmpty(punchoutFilterResult)) {
                final String delimitedPunchoutProducts = getJoinedPunchedOutProductCodes(punchoutFilterResult);
                model.addAttribute(WebConstants.HAS_PUNCHED_OUT_PRODUCTS, Boolean.TRUE);
                model.addAttribute(WebConstants.PRODUCTS_PUNCHED_OUT, delimitedPunchoutProducts);
                model.addAttribute(WebConstants.HAS_PUNCHED_OUT_PRODUCTS, Boolean.TRUE);
                GlobalMessages.addErrorMessage(model, "cart.product.clearedOut.message.cart.punchout", new String[] { delimitedPunchoutProducts },
                                               DistConstants.Punctuation.PIPE);
            }
        } else {
            // Login after cart scenario
            GlobalMessages.addErrorMessage(model, "cart.product.error.punchout");
        }

        final CartData cart = getCartFacade().getSessionCartIfPresent();
        if (cart != null && CollectionUtils.isNotEmpty(cart.getEntries())) {
            boolean phaseoutproductflag = false;
            final List<String> eolProducts = new ArrayList<>();
            final List<String> phaseOutProducts = new ArrayList<>();
            final List<String> updatedMOQProducts = new ArrayList<>();
            final List<String> notforsale = erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_GENERIC);

            // Used to populate Alternative Product List for cart items
            final List<ProductData> alternativeProductList = new ArrayList<>();

            final Map<String, Object[]> stockAndSalesStatusMap = getStockAndSalesStatusForEntries(getCartService().getSessionCart().getEntries());

            for (final OrderEntryData orderEntry : cart.getEntries()) {
                final ProductData productData = orderEntry.getProduct();
                final String salesStatus = (String) stockAndSalesStatusMap.get(productData.getCode())[1];
                productData.setSalesStatus(salesStatus);
                final int stockAvailable = (Integer) (stockAndSalesStatusMap.get(productData.getCode())[0] != null
                                                                                                                   ? stockAndSalesStatusMap.get(productData.getCode())[0]
                                                                                                                   : Integer.valueOf(0));
                phaseoutproductflag = notforsale.contains(salesStatus);
                final boolean eol = getProductFacade().isEndOfLife(productData.getCode());

                final long updatedMoqQuantity = calculateNewMoqQuantity(orderEntry.getQuantity(), productData.getOrderQuantityMinimum(),
                                                                        productData.getOrderQuantityStep());
                if (updatedMoqQuantity != Long.MAX_VALUE) {
                    orderEntry.setQuantity(updatedMoqQuantity);

                    try {
                        getCartFacade().updateCartEntry(orderEntry.getEntryNumber(), updatedMoqQuantity);
                        updatedMOQProducts.add(productData.getCodeErpRelevant());
                    } catch (CommerceCartModificationException e) {
                        logError(LOG, "{} {} Could not update MOQ Quantity for product [{}], cart, [{}], customer [{}]", e,
                                 ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, productData.getCode(), cart.getCode(), customer.getContactEmail());
                        GlobalMessages.addErrorMessage(model, "basket.error.occurred");
                    }
                }

                if (eol || phaseoutproductflag) {
                    // The current quantity
                    final long currentQty = orderEntry.getQuantity();
                    // The new quantity
                    final long newQty = !eol && stockAvailable > 0 ? (currentQty > stockAvailable ? stockAvailable : currentQty) : 0;
                    if (currentQty != newQty) { // Adjust only if newQty != currentQty
                        try {
                            getCartFacade().updateCartEntry(orderEntry.getEntryNumber().longValue(), newQty);
                            if (phaseoutproductflag) {
                                phaseOutProducts.add(productData.getCodeErpRelevant());
                            }
                        } catch (final CommerceCartModificationException e) {
                            logError(LOG,
                                     "{} {} Could not remove (set qty to 0) from cart. Trying to remove because product has reached EOL status. for cart: [{}]. For Customer [{}]",
                                     e, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, cart.getCode(), customer.getContactEmail());
                            GlobalMessages.addErrorMessage(model, "basket.error.occurred");
                        }
                    }

                    if (eol) {
                        eolProducts.add(productData.getCodeErpRelevant());
                    }
                }
                if (orderEntry.getProduct() != null) {
                    final ProductModel productForOrderEntry = getProductService().getProductForCode(orderEntry.getProduct().getCode());
                    populateAlternativeProductList(productForOrderEntry, alternativeProductList);
                    productData.setName(DistUtils.truncateProductName(CHARACTER_LIMIT, productData.getName()));
                }
            }

            model.addAttribute(REPLACEMENT_PRODUCT_LIST, alternativeProductList);

            if (!phaseOutProducts.isEmpty()) {
                model.addAttribute(WebConstants.PRODUCTS_PHASE_OUT, StringUtils.collectionToDelimitedString(phaseOutProducts, "; "));
            }
            if (!eolProducts.isEmpty()) {
                model.addAttribute(WebConstants.HAS_EOL_PRODUCTS, Boolean.TRUE);
                model.addAttribute(WebConstants.PRODUCTS_EOL, StringUtils.collectionToDelimitedString(eolProducts, ", "));
            }

            model.addAttribute(WebConstants.HAS_MOQ_UPDATED_SINCE_LAST_CART_LOAD, updatedMOQProducts);
        }

        addDangerousGoodsWarningmessage(getListOfdangerousProductsfromCart(), model);
        if (customer != null && customer.getCustomerType() != null) {
            model.addAttribute("wtPageAreaCode", "ct"); // 9
            if (customer.getCustomerType() == CustomerType.OCI) {
                model.addAttribute("wtContentGroup", "oci"); // 8
                model.addAttribute("wtChannel", "oci"); // 7
            } else if (customer.getCustomerType() == CustomerType.ARIBA) {
                model.addAttribute("wtContentGroup", "arb"); // 8
                model.addAttribute("wtChannel", "arb"); // 7
                model.addAttribute(DistConstants.Ariba.SetupRequestParams.EDIT_CART, getCartEditable(request));
            }
        }

        // E-Procurement: display error message if Ariba cart could not be loaded
        if (Boolean.TRUE.equals(getSessionService().getAttribute(DistConstants.Ariba.Session.ARIBA_CART_NOT_LOADED))) {
            getSessionService().removeAttribute(DistConstants.Ariba.Session.ARIBA_CART_NOT_LOADED);
            GlobalMessages.addErrorMessage(model, "basket.error.aribacart.notfound");
        }

        try {
            // TODO: use recalculate instead of calculateOpenOrder
            getCartFacade().recalculateCart();
            // checkoutFacade.calculateOpenOrder("0000001", true);
        } catch (final CalculationException e) {
            logError(LOG, "{} {} Could not recalculate cart :[{}]. For Customer [{}]", e, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                     cart.getCode(), customer.getContactEmail());
            if (e instanceof MoqUnderflowException) {
                GlobalMessages.addErrorMessage(model, "cart.error.recalculate");
                model.addAttribute("errorMsg", "cart.error.recalculate");
            } else {
                model.addAttribute("errorMsg", "cart.error.recalculate");
            }
        }
        prepareDataForPage(model);
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        Cart ddCart = digitalDatalayer.getCart();
        if (null == ddCart) {
            ddCart = new Cart();
        }
        final HttpSession session = request.getSession(true);
        if (session.getAttribute("removeCartForDD") != null) {
            ddCart.setRemoveCart((List<RemoveCart>) session.getAttribute("removeCartForDD"));
            session.removeAttribute("removeCartForDD");
        }
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.CART,
                                                                               DigitalDatalayerConstants.PageType.CARTPAGE);
        getDistDigitalDatalayerFacade().populateFFTrackingAttribute(digitalDatalayer, Boolean.FALSE);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        return ControllerConstants.Views.Pages.Cart.CartPage;
    }

    private String getJoinedPunchedOutProductCodes(Collection<PunchoutFilterResult> punchoutFilterResult) {
        return punchoutFilterResult.stream()
                                   .filter(Objects::nonNull)
                                   .filter(result -> result.getPunchedOutProduct() != null)
                                   .map(result -> result.getPunchedOutProduct().getCode())
                                   .collect(Collectors.joining(", "));
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getPdf() {
        InputStream pdf = distPDFGenerationFacade.getPDFStreamForCart();
        if (pdf == null) {
            return notFound().build();
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Content-Disposition", distPDFGenerationFacade.getCartPdfHeaderValue());
        return ok()
                   .headers(header)
                   .contentType(MediaType.APPLICATION_PDF)
                   .body(new InputStreamResource(pdf));
    }

    private long calculateNewMoqQuantity(final Long qty, final Long minQty, final Long step) {
        if (qty != null && minQty != null && step != null) {
            long sum = (qty - minQty) % step;
            if (qty == minQty.longValue() && sum == 0 || qty - (qty % step) == qty) {
                return Long.MAX_VALUE;
            }

            if (qty > minQty) {
                return qty - (qty % step);
            }

            if (qty < minQty) {
                return minQty;
            }
        }
        return Long.MAX_VALUE;
    }

    private void populateAlternativeProductList(final ProductModel sourceProductModel, final List<ProductData> alternativeProductList) {
        if (sourceProductModel.getReplacementProduct() != null && hasValidReplacement(sourceProductModel)) {
            final ProductModel replacementProduct = sourceProductModel.getReplacementProduct();

            final ProductData productDataBasicPopulated = productFacade.getProductForCodeAndOptions(replacementProduct.getCode(), PRODUCT_OPTIONS);
            if (productFacade.getRelevantSalesUnit(productDataBasicPopulated.getCode()) != null) {
                productDataBasicPopulated.setSalesUnit(productFacade.getRelevantSalesUnit(replacementProduct.getCode()));
            }

            productDataBasicPopulated.setUrl(getProductUrlResolver(replacementProduct).resolve(replacementProduct));
            alternativeProductList.add(productDataBasicPopulated);
        }
    }

    /**
     * Return the product URL resolver for the specified product
     *
     * @param productModel
     * @return the product URL resolver.
     */
    private DistUrlResolver<ProductModel> getProductUrlResolver(final ProductModel productModel) {
        final DistUrlResolver<ProductModel> resolver = productModel.getCatPlusSupplierAID() == null ? getProductModelUrlResolver()
                                                                                                    : getCatalogPlusProductModelUrlResolver();
        return resolver;
    }

    @RequestMapping(value = DANGEROUS_PRODUCTS, method = RequestMethod.GET, produces = "application/json")
    public String getDangerousProducts(final Model model) {
        addDangerousGoodsWarningmessage(getListOfdangerousProductsfromCart(), model);
        return ControllerConstants.Views.Fragments.Cart.DangerousProductsMessage;
    }

    /**
     * @return List of dangerous Items
     */
    private List<ProductData> getListOfdangerousProductsfromCart() {
        // If the there is no session cart, the cart is empty or the current country is one of the excluded countries, then we return an
        // empty list.
        if (!getCartFacade().hasSessionCart() || //
                CollectionUtils.isEmpty(getCartFacade().getSessionCart().getEntries()) || //
                getConfigurationService().getConfiguration() //
                                         .getString("dangerous.goods.excluded.countries", org.apache.commons.lang.StringUtils.EMPTY)
                                         .contains(getStoreSessionFacade().getCurrentCountry().getIsocode())) {
            return Collections.<ProductData> emptyList();
        }

        final List<ProductData> dangerousProducts = new ArrayList<>();
        for (final OrderEntryData orderEntry : getCartFacade().getSessionCart().getEntries()) {
            final ProductData productData = orderEntry.getProduct();
            if (getProductFacade().isDangerousProduct(productData.getCode())) {
                dangerousProducts.add(productData);
            }
        }

        return dangerousProducts;
    }

    // DISTRELEC-6945: dangerousProducts message on the cart in all elfa shop
    private void addDangerousGoodsWarningmessage(final List<ProductData> dangerousProducts, final Model model) {
        if (CollectionUtils.isNotEmpty(dangerousProducts)) {
            model.addAttribute("dangerousProducts", getDangerousProductWarningMessage(dangerousProducts));
        }
    }

    /**
     * @param dangerousProducts
     * @return HTML string which has the list of dangerous items found in the cart
     */
    private String getDangerousProductWarningMessage(final List<ProductData> dangerousProducts) {
        final StringBuilder warningmessage = new StringBuilder("<ul style=\"list-style-type:square\">");
        for (final ProductData b2bProductData : dangerousProducts) {
            warningmessage.append("<li> - " + b2bProductData.getCode() + ": " + b2bProductData.getName() + "</li>");
        }
        warningmessage.append("</ul>");
        return warningmessage.toString();
    }

    @RequestMapping(value = PUSHED_CAMPAIGN_PRODUCTS_PATH, method = RequestMethod.GET, produces = "application/json")
    public String pushedCampaignProducts(final Model model) throws CMSItemNotFoundException {
        final List<ProductData> pushedCampaignProducts = new ArrayList<>();
        final List<String> productCodes = new ArrayList<>();
        final CartData cart = getCartFacade().getSessionCart();
        if (CollectionUtils.isNotEmpty(cart.getEntries())) {
            for (final OrderEntryData orderEntry : cart.getEntries()) {
                final ProductData productData = orderEntry.getProduct();
                productCodes.add(productData.getCode());
            }
        }
        pushedCampaignProducts.addAll(distCampaignFacade.getCartCampaignProducts(productCodes));
        model.addAttribute(CAROUSEL_DATA_ATTRIBUTE_NAME, pushedCampaignProducts);
        return ControllerConstants.Views.Fragments.Product.CarouselProducts;
    }

    @RequestMapping(value = RECOMMENDED_PRODUCTS_PATH, method = RequestMethod.GET, produces = "application/json")
    public String recommendedProducts(final Model model) throws CMSItemNotFoundException {
        final List<ProductData> recommendedProducts = new ArrayList<>();
        final List<String> productCodes = new ArrayList<>();
        final CartData cart = getCartFacade().getSessionCart();
        if (CollectionUtils.isNotEmpty(cart.getEntries())) {
            for (final OrderEntryData orderEntry : cart.getEntries()) {
                final ProductData productData = orderEntry.getProduct();
                productCodes.add(productData.getCode());
            }
        }
        recommendedProducts.addAll(recommendationFacade.getCartRecommendedProducts(productCodes));
        model.addAttribute(CAROUSEL_DATA_ATTRIBUTE_NAME, recommendedProducts);
        return ControllerConstants.Views.Fragments.Product.CarouselProducts;
    }

    private Boolean getCartEditable(final HttpServletRequest request) {
        final Object value = request.getSession().getAttribute(DistConstants.Ariba.SetupRequestParams.EDIT_CART);
        return value != null ? Boolean.valueOf(value.toString()) : Boolean.FALSE;
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String cartCheck(final HttpServletRequest request) {
        String message = request.getParameter(DistConstants.Checkout.MESSAGE_PARAMETER);
        if (!StringUtils.isEmpty(message)) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/checkout" + DistConstants.Punctuation.QUESTION_MARK
                                                     + DistConstants.Checkout.MESSAGE_PARAMETER + "=" + message);
        }
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/checkout");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateCartQuantities(@RequestParam("entryNumber") final long entryNumber, final Model model, @Valid final UpdateQuantityForm form,
                                       final BindingResult bindingResult, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

        if (bindingResult.hasErrors()) {
            for (final ObjectError error : bindingResult.getAllErrors()) {
                if (error.getCode().equals("typeMismatch")) {
                    GlobalMessages.addErrorMessage(model, "basket.error.quantity.invalid");
                } else {
                    GlobalMessages.addErrorMessage(model, error.getDefaultMessage());
                }
            }
        } else if (getCartFacade().getSessionCart().getEntries() != null) {
            try {
                if (!validSAPCatalogQty(entryNumber, form.getQuantity().longValue())) {
                    model.addAttribute("errorMsg", "sap.catalog.order.articles.error");
                    return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
                }
                final CartModificationData cartModification = getCartFacade().updateCartEntry(entryNumber, form.getQuantity().longValue(), false);
                if (cartModification.getQuantity() == form.getQuantity().longValue()) {
                    // Success
                    if (cartModification.getQuantity() == 0) {
                        // Success in removing entry
                        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("basket.page.message.remove"));
                    } else {
                        // Success in update quantity
                        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("basket.page.message.update"));
                        model.addAttribute("updateQuantity", Long.valueOf(cartModification.getQuantityAdded()));
                        model.addAttribute("updatedEntry", cartModification.getEntry().getEntryNumber());
                    }
                } else {
                    // Less than successful
                    if (form.getQuantity().longValue() == 0) {
                        // Failed to remove entry
                        final String errorMessage = "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode();
                        redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList(errorMessage));
                        model.addAttribute("errorMsg", errorMessage);
                    } else {
                        final String errorMessage = "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode();
                        // Failed to update quantity
                        redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList(errorMessage));
                        model.addAttribute("errorMsg", errorMessage);
                    }
                }

                // Redirect to the cart page on update success so that the browser doesn't re-post again
                // return REDIRECT_PREFIX + PAGE_CART;
            } catch (final CommerceCartModificationException ex) {
                LOG.warn("Couldn't update product with the entry number: {}.", entryNumber, ex);
            }
        }

        prepareDataForPage(model);
        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());
        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    @RequestMapping(value = "/update/reference", method = RequestMethod.POST)
    public String setReferenceText(final Model model, @RequestParam(value = "customerReference") final String customerReference,
                                   @RequestParam(value = "entryNumber") final long entryNumber) throws CMSItemNotFoundException {
        try {
            getCartFacade().updateCartEntry(entryNumber, customerReference);
        } catch (final CommerceCartModificationException e) {
            logError(LOG, "{} {} Could not update customer reference text on order entry with entryNumber {}", e, ErrorLogCode.CART_CALCULATION_ERROR,
                     ErrorSource.HYBRIS, entryNumber);
            model.addAttribute("errorMsg", "cart.error.customerReference");
        }
        prepareDataForPage(model);
        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());
        return ControllerConstants.Views.Fragments.Cart.CartJson;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public String removeProductFromCart(final Model model, @RequestParam(value = "entryNumber") final long entryNumber, final HttpServletRequest request) {
        removeFromCart(model, entryNumber, request);
        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());
        return ControllerConstants.Views.Fragments.Cart.CartJson;
    }

    private void removeFromCart(Model model, @RequestParam("entryNumber") long entryNumber, HttpServletRequest request) {
        CartModificationData cartData = new CartModificationData();
        try {
            cartData = getCartFacade().updateCartEntry(entryNumber, 0, false);
        } catch (final CommerceCartModificationException e) {
            logError(LOG, "{} {} Couldn't remove product with the entry number: {}. ", e, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, entryNumber);
            model.addAttribute("errorMsg", "basket.error.occurred");
        }
        final List<RemoveCart> removeCartList = getDistDigitalDatalayerFacade().populateCartRemoval(cartData);
        final HttpSession session = request.getSession();
        session.setAttribute("removeCartForDD", removeCartList);
        try {
            getCartFacade().recalculateCart();
        } catch (final CalculationException e) {
            model.addAttribute("errorMsg", "cart.error.recalculate");
        }
        final String returnCode = getVoucherReturnCodeFromCart(getCartFacade().getSessionCartIfPresent());
        if (StringUtils.isEmpty(returnCode)) {
            // reset the voucher. This is needed otherwise a possible old voucher is still there.
            getCartFacade().resetVoucherOnCurrentCart();
        }
    }

    @RequestMapping(value = "/removequotation", method = { RequestMethod.POST, RequestMethod.GET })
    public String removeQuotationFromCart(@RequestParam(value = "quotationId", required = true) final String quotationId, final Model model) {
        try {
            getCartFacade().removeQuotationFromCart(quotationId);
        } catch (final CommerceCartModificationException e) {
            logError(LOG, "{} {} Couldn't remove quotationId: {} from cart", e, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, quotationId);
            model.addAttribute("errorMsg", "basket.error.occurred");
        }
        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());
        return ControllerConstants.Views.Fragments.Cart.CartJson;
    }

    /**
     * @param productCode
     * @param model
     * @return
     */
    @RequestMapping(value = "/online-price", method = { RequestMethod.GET })
    public String onlinePrice(@RequestParam("pcode") final String productCode, final Model model) {
        String message = null;
        Boolean error = Boolean.FALSE;
        try {
            final String pcode = normalizeProductCode(productCode);
            model.addAttribute("productCode", pcode);
            if (!getProductFacade().isProductBuyable(pcode)) {
                message = "product.notBuyable.temporarly.message";
                error = Boolean.TRUE;
            } else {
                final PriceInformation priceInfo = getDistCommercePriceService().getWebPriceForProduct(getProductService().getProductForCode(pcode), true,
                                                                                                       true);
                if (priceInfo != null) {
                    model.addAttribute("priceData", createPriceData(PriceDataType.BUY, priceInfo));
                }
            }

        } catch (final UnknownIdentifierException uiexp) {
            message = "product.notFound";
            error = Boolean.TRUE;
        } catch (final Exception exp) {
            error = Boolean.TRUE;
        } finally {
            model.addAttribute("errorMsg", message);
            model.addAttribute("error", error);
        }

        return ControllerConstants.Views.Fragments.Cart.ProductOnlinePrice;
    }

    /**
     * Calculate the price difference between the two products {@code source} and {@code target}
     *
     * @param priceDiffForm
     *            the form containing the {@code source} and {@code target} product codes
     * @param bindingResult
     * @param model
     */
    @RequestMapping(value = "/price-diff", method = { RequestMethod.GET, RequestMethod.POST })
    public String priceDiff(@Valid final PriceDiffForm priceDiffForm, final BindingResult bindingResult, final Model model) {
        String errorCode = null;
        if (bindingResult.hasErrors()) {
            errorCode = "form.global.error";
        } else {
            try {
                final ProductModel source = getProductService().getProductForCode(normalizeProductCode(priceDiffForm.getSource()));
                final ProductModel target = getProductService().getProductForCode(normalizeProductCode(priceDiffForm.getTarget()));

                if (!getProductFacade().isProductBuyable(source.getCode()) || !getProductFacade().isProductBuyable(target.getCode())) {
                    errorCode = "product.notBuyable.temporarly.message";
                } else {

                    final Map<String, PriceInformation> priceInfos = getDistCommercePriceService().getWebPriceForProducts(Arrays.asList(source, target), true,
                                                                                                                          true);

                    final PriceInformation priceInfoS = priceInfos.get(source.getCode());
                    final PriceInformation priceInfoT = priceInfos.get(target.getCode());
                    if (priceInfoS != null && priceInfoT != null) {
                        model.addAttribute("priceData", createPriceData(PriceDataType.BUY, //
                                                                        priceInfoS.getPriceValue().getValue() - priceInfoT.getPriceValue().getValue(), //
                                                                        priceInfoS.getPriceValue().getCurrencyIso()));
                    } else {
                        errorCode = "product.price.notavailable";
                    }
                }
            } catch (final UnknownIdentifierException uiexp) {
                errorCode = "product.notFound";
            }
        }

        model.addAttribute("priceDiffForm", priceDiffForm);

        if (errorCode != null) {
            model.addAttribute("error", Boolean.TRUE);
            model.addAttribute("errorMsg", errorCode);
        }

        return ControllerConstants.Views.Fragments.Cart.CartPriceDiff;
    }

    /**
     * Replace the {@code source} product by the {@code target} in the cart entry at position {@code entryNumber}
     *
     * @param calibrationForm
     *            the form containing the {@code source} and {@code target} product codes and the {@code entryNumber}
     * @param bindingResult
     * @param model
     */
    @RequestMapping(value = "/replace", method = { RequestMethod.GET, RequestMethod.POST })
    public String replace(@Valid final CalibrationForm calibrationForm, final BindingResult bindingResult, final Model model) {
        String errorCode = null;
        if (bindingResult.hasErrors()) {
            errorCode = "form.global.error";
        } else {
            try {
                final ProductModel source = getProductService().getProductForCode(normalizeProductCode(calibrationForm.getSource()));
                final ProductModel replacement = getProductService().getProductForCode(normalizeProductCode(calibrationForm.getTarget()));
                if (!getProductFacade().isProductBuyable(source.getCode()) || !getProductFacade().isProductBuyable(replacement.getCode())) {
                    errorCode = "product.notBuyable.temporarly.message";
                } else {
                    AbstractOrderEntryModel replacedEntry = getCartService().replace(source, replacement, calibrationForm.getEntryNumber(),
                                                                                     calibrationForm.getNewQty());
                    if (replacedEntry != null) {
                        getCartFacade().recalculateCart();
                        model.addAttribute("replacedEntry", Long.valueOf(replacedEntry.getEntryNumber()));
                    } else {
                        errorCode = "basket.error.unknown";
                    }
                }
            } catch (final UnknownIdentifierException uiexp) {
                errorCode = "product.notFound";
            } catch (final CalculationException ce) {
                errorCode = "basket.error.unknown";
            }
        }

        if (getCartFacade().hasSessionCart()) {
            model.addAttribute("cartData", getCartFacade().getSessionCart());
        }

        if (errorCode != null) {
            model.addAttribute("errorMsg", errorCode);
        }

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    /**
     * Remove all cart entries
     *
     * @param model
     * @return a JSON response
     */
    @RequestMapping(value = "/empty-cart", method = { RequestMethod.GET, RequestMethod.POST })
    public String emptyCart(final Model model) {
        try {
            // Remove all entries from the cart
            getCartFacade().emptySessionCart();
            // Recalculate the cart
            getCartFacade().recalculateCart();
        } catch (final SystemException se) {
            LOG.warn("Couldn't remove all products from the cart with code : {}", getCartFacade().getSessionCart().getCode(), se);
            model.addAttribute("errorMsg", "basket.error.occurred");
        } catch (final CalculationException ce) {
            logError(LOG, "{} {} Could not recalculate cart with code : {}", ce, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                     getCartFacade().getSessionCart().getCode());
            model.addAttribute("errorMsg", "cart.error.recalculate");
        }

        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());
        return ControllerConstants.Views.Fragments.Cart.CartJson;
    }

    /**
     * Remove all cart entries
     *
     * @param model
     * @return a JSON response
     */
    @RequestMapping(value = "/restore", method = { RequestMethod.GET, RequestMethod.POST })
    public String restoreCart(final Model model) {
        try {
            // Remove all entries from the cart
            getCartFacade().restoreSessionCart();
            // Recalculate the cart
            getCartFacade().recalculateCart();
        } catch (final SystemException se) {
            LOG.warn("Couldn't restore previous cart with code : {}", getCartFacade().getSessionCart().getCode(), se);
            model.addAttribute("errorMsg", "basket.error.occurred");
        } catch (final CalculationException ce) {
            logError(LOG, "{} {} Could not recalculate cart with code : {}", ce, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                     getCartFacade().getSessionCart().getCode());
            model.addAttribute("errorMsg", "cart.error.recalculate");
        }

        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());
        return ControllerConstants.Views.Fragments.Cart.CartJson;
    }

    @RequestMapping(value = "/json", method = RequestMethod.GET, produces = "application/json")
    public String getCartJson(final Model model) {
        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());

        return ControllerConstants.Views.Fragments.Cart.CartJson;
    }

    @RequestMapping(value = "/recalculate", method = RequestMethod.POST, produces = "application/json")
    public String recalculateCartJson(final Model model) {

        try {
            getCartFacade().recalculateCart();
        } catch (final CalculationException e) {
            logError(LOG, "{} {} Could not recalculate cart with code : {}", e, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                     getCartFacade().getSessionCart().getCode());
            model.addAttribute("errorMsg", "cart.error.recalculate");
        }
        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());

        return ControllerConstants.Views.Fragments.Cart.CartRecalculateJson;
    }

    @RequestMapping(value = "/directOrder/search", method = RequestMethod.GET)
    public String searchForDirectOrder(final Model model, @RequestParam(value = "term") final String term) {
        // DISTRELEC-6543 We should support the ELFA case by removing the '-' characters
        final AutocompleteSuggestion suggestion = productSearchFacade.getDirectOrderAutocompleteSuggestions(term.replaceAll("-", ""), null);
        model.addAttribute("productSuggestions", suggestion.getRes().getProds().getList());
        return ControllerConstants.Views.Fragments.Cart.DirectOrderSearch;
    }

    @RequestMapping("/download/{exportFormat:.*}/*")
    public void getDownloadFile(@PathVariable("exportFormat") final String exportFormat, final HttpServletResponse response) {
        final String fileNamePrefix = getConfigurationService().getConfiguration().getString("distrelec.exportCartFileNamePrefix");
        final File downloadFile = getDistExportFacade().exportCart(getCartFacade().getSessionCart(), exportFormat, fileNamePrefix);

        try {
            setUpDownloadFile(response, downloadFile, exportFormat);
        } catch (final IOException e) {
            logError(LOG, "{} Could not set up file {} for download", e, ErrorLogCode.CART_CALCULATION_ERROR, downloadFile.getPath());
        }
    }

    @RequestMapping(value = "/sendToFriend", method = RequestMethod.POST)
    public String sendCompareListToFriend(@Valid final SendToFriendForm form, final BindingResult bindingResults, final HttpServletRequest request,
                                          final Model model) {
        if (bindingResults.hasErrors()) {
            model.addAttribute("error", "form.global.error");
        } else if (!getCaptchaUtil().validateReCaptcha(request)) {
            model.addAttribute("error", "form.captcha.error");
        } else {
            final DistSendToFriendEvent sendToFriendEvent = getSendToFriendEvent(form);
            InputStream pdfStream = distPDFGenerationFacade.getPDFStreamForCart();
            getDistShareWithFriendsFacade().shareCartPdfWithFriends(sendToFriendEvent, getCartService().getSessionCart(), pdfStream);
        }
        return ControllerConstants.Views.Fragments.Cart.SendToFriendJson;
    }

    @RequestMapping(value = { "/update" }, method = RequestMethod.GET)
    public String fallBack() {
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/cart");
    }

    protected void createProductList(final Model model) throws CMSItemNotFoundException {
        final CartData cartData = getCartFacade().getSessionCart();

        if (cartData.getEntries() != null && !cartData.getEntries().isEmpty()) {
            for (final OrderEntryData entry : cartData.getEntries()) {
                final UpdateQuantityForm uqf = new UpdateQuantityForm();
                model.addAttribute("updateQuantityForm" + entry.getProduct().getCode(), uqf);
            }
        }

        model.addAttribute(CART_DATA, cartData);

        final ContentPageModel contentPageModel = getContentPageForLabelOrId(CART_CMS_PAGE);
        storeCmsPageInModel(model, contentPageModel);
        setUpMetaDataForContentPage(model, contentPageModel);
    }

    protected void prepareDataForPage(final Model model) throws CMSItemNotFoundException {
        final String continueUrl = (String) getSessionService().getAttribute(WebConstants.CONTINUE_URL);
        model.addAttribute(CONTINUE_URL, (continueUrl == null || continueUrl.isEmpty()) ? ROOT : continueUrl);
        createProductList(model);
        final ContentPageModel contentPage = getContentPageForLabelOrId(CART_CMS_PAGE);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                           getSimpleBreadcrumbBuilder().getBreadcrumbs(contentPage.getTitle(), contentPage.getTitle(Locale.ENGLISH)));
        model.addAttribute("pageType", PageType.Cart);

        // Webtrekk
        prepareWebtrekkParams(model, null);

        // Digital Data Layer
        DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        CartData cartData = getCartFacade().getSessionCartIfPresent();
        if (null != cartData) {
            defaultDistDigitalDatalayerFacade.setVoucherInfo(cartData, digitalDatalayer);
            if (!hasReachedMovAmount()) {
                model.addAttribute(WebConstants.MOV_MISSING_VALUE, cartData.getMissingMov());
                model.addAttribute(WebConstants.MOV_LIMIT_VALUE, cartData.getMovLimit());
                model.addAttribute(WebConstants.MOV_CART_VALUE, cartData.getMovLimit() - cartData.getMissingMov());
            }
        }
        getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
    }

    @RequestMapping(value = "/accessories", method = RequestMethod.GET, produces = "application/json")
    public String getCartProductAccessories(@RequestParam(value = "offset", required = true, defaultValue = "0") final int offset,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") final int size, final Model model) {

        List<ProductData> products = Collections.<ProductData> emptyList();

        if (getCartFacade().hasSessionCart()) {
            final CartModel sessionCart = getCartService().getSessionCart();
            if (CollectionUtils.isNotEmpty(sessionCart.getEntries())) {
                final List<AbstractOrderEntryModel> entries = new ArrayList<>(sessionCart.getEntries());
                // Sort the cart by price DESC
                Collections.sort(entries, (o1, o2) -> o2.getBaseNetPrice().compareTo(o1.getBaseNetPrice()));

                // Pick only the top 5 products
                final List<ProductModel> sources = new ArrayList<>();
                for (final AbstractOrderEntryModel entry : entries) {
                    sources.add(entry.getProduct());
                    if (sources.size() >= 5) {
                        break;
                    }
                }

                products = getProductFacade().getProductsReferences(sources, Arrays.asList(ProductReferenceTypeEnum.DIST_ACCESSORY), offset, size);
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("offset", Integer.valueOf(offset < 0 ? 0 : offset));
        model.addAttribute("maxSize", Integer.valueOf(size));

        return ControllerConstants.Views.Fragments.Product.ProductAccessories;
    }

    boolean validSAPCatalogQty(final long entryNumber, final long quantity) {
        final AbstractOrderEntryModel entryModel = getCartFacade().getOrderEntry(entryNumber);
        final List<String> sapCatalogProducts = Arrays
                                                      .asList(getConfigurationService().getConfiguration().getString("sap.catalog.order.articles", "")
                                                                                       .split(","));
        if (entryModel != null && sapCatalogProducts.contains(entryModel.getProduct().getCode())) {
            if (quantity == 1) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Creates quotation from cart which then creates inquiries in ERP and if its within threshold it creates quotation at the same time.
     * Uses saleorg, customerNumber, contactNumber, customerName, articleNumber, quantity, and entryLevelRef from cart
     *
     * @param model
     * @return quoteCreationStatus
     */
    @RequestMapping(value = "requestQuote", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public QuoteStatusData submitquote(final Model model) {

        QuoteStatusData quoteResponse = null;
        if (getCartFacade().hasSessionCart()) {
            quoteResponse = getDistProductPriceQuotationFacade().createCartQuotation();

        }
        return quoteResponse;
    }

    /**
     * Calculate the stock levels and the sales statuses for the products, given by their codes. This method is an optimization to avoid
     * calling the availability and sales status for each product separately.
     *
     * @param entries
     *            the list of product codes.
     * @return a {@link Map} containing the product codes as {@code key}s and an array with the total stock level and sales status
     */
    protected Map<String, Object[]> getStockAndSalesStatusForEntries(final List<AbstractOrderEntryModel> entries) {
        if (entries == null || entries.isEmpty()) {
            return MapUtils.EMPTY_MAP;
        }

        final Map<String, String> salesStatuses = getProductFacade().getSalesStatusForEntries(entries);

        final Map<String, Object[]> map = new HashMap<>() {
            @Override
            public Object[] get(final Object key) {
                final Object[] value = super.get(key);
                return value != null ? value : new Object[] { Integer.valueOf(0), salesStatuses.get(key) };
            }
        };

        final List<ProductAvailabilityData> availabilities = getProductFacade().getAvailabilityForEntries(entries);
        // We need to be sure that we didn't had connection problem with the ERP.
        if (CollectionUtils.isNotEmpty(availabilities)) {
            for (final ProductAvailabilityData availability : availabilities) {
                if (!map.containsKey(availability.getProductCode())) {
                    map.put(availability.getProductCode(),
                            new Object[] { availability.getStockLevelTotal(), salesStatuses.get(availability.getProductCode()) });
                }
            }
        }

        return map;
    }

    protected PriceData createPriceData(final PriceDataType priceType, final PriceInformation priceInfo) {
        return getPriceDataFactory().create(priceType, BigDecimal.valueOf(priceInfo.getPriceValue().getValue()),
                                            priceInfo.getPriceValue().getCurrencyIso());
    }

    protected PriceData createPriceData(final PriceDataType priceType, final double value, final String currencyIso) {
        return getPriceDataFactory().create(priceType, BigDecimal.valueOf(value), currencyIso);
    }

    // -------------------------- Voucher Implementation Start --------------------------

    /**
     * This method restricts the customer if they redeems empty voucher with empty code.
     *
     * @param model
     * @param request
     * @return String
     * @throws CMSItemNotFoundException
     * @throws InvalidCartException
     */
    @RequestMapping(value = "/redeemVoucher", method = RequestMethod.POST)
    public String redeemVoucherEmpty(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException, InvalidCartException {

        // Add Global Model attributes
        addGlobalModelAttributes(model, request);

        model.addAttribute("voucherErrorMessageKey", CONF_VOUCHER_ERROR_KEY_PREFIX + VOUCHER_ERROR_CODE_EMPTY);
        model.addAttribute("isVoucherEmpty", Boolean.TRUE);

        // reset the voucher. This is needed otherwise a possible old voucher is still there.
        getCartFacade().resetVoucherOnCurrentCart();

        prepareDataForPage(model);
        return ControllerConstants.Views.Pages.Cart.CartPage;
    }

    /**
     * @param voucherCode
     * @param model
     * @param request
     * @return String
     * @throws CMSItemNotFoundException
     * @throws InvalidCartException
     */
    @RequestMapping(value = "/redeemVoucher/{voucherCode:.*}", method = RequestMethod.POST)
    public String redeemVoucher(@PathVariable("voucherCode") final String voucherCode, final Model model,
                                final HttpServletRequest request) throws CMSItemNotFoundException, InvalidCartException {

        // Add Global Model attributes
        addGlobalModelAttributes(model, request);
        if (validVoucher()) {
            if (!getUserService().isAnonymousUser(getUserService().getCurrentUser())) {

                model.addAttribute("isVoucherEmpty", Boolean.FALSE);

                // Redeem the voucher code.
                redeemVoucher(voucherCode);

                try {
                    getCartFacade().recalculateCart();
                } catch (final CalculationException e) {
                    model.addAttribute("errorMsg", "cart.error.recalculate");
                }

                final String returnCode = getVoucherReturnCodeFromCart(getCartFacade().getSessionCartIfPresent());
                if (StringUtils.isEmpty(returnCode)) {
                    model.addAttribute("voucherErrorMessageKey", CONF_VOUCHER_ERROR_KEY_PREFIX + returnCode);
                    GlobalMessages.addErrorMessage(model, "checkoutvoucherbox.voucherError.invalid");
                    // reset the voucher. This is needed otherwise a possible old voucher is still there.
                    getCartFacade().resetVoucherOnCurrentCart();
                } else {
                    GlobalMessages.addConfMessage(model, "checkoutvoucherbox.voucherSuccess");
                }

                prepareDataForPage(model);
                return ControllerConstants.Views.Pages.Cart.CartPage;
            } else {
                getSessionService().setAttribute("anonymousVoucherCode", voucherCode);
                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/checkout");
            }
        } else {
            model.addAttribute("voucherErrorMessageKey", CONF_VOUCHER_ERROR_KEY_PREFIX_01);
            GlobalMessages.addErrorMessage(model, "checkoutvoucherbox.voucherError.invalid");
            // reset the voucher. This is needed otherwise a possible old voucher is still there.
            getCartFacade().resetVoucherOnCurrentCart();
            prepareDataForPage(model);
            return ControllerConstants.Views.Pages.Cart.CartPage;
        }
    }

    protected boolean validVoucher() {

        boolean validVoucher = Boolean.TRUE;
        final CartData cartData = getCartFacade().getSessionCartIfPresent();
        final DistErpVoucherInfoData voucher = cartData.getErpVoucherInfoData();
        if (voucher != null && voucher.getCode().length() > 10) {
            validVoucher = Boolean.FALSE;
            return validVoucher;
        }
        return validVoucher;
    }

    /**
     * Reset the voucher code.
     *
     * @param model
     * @param request
     * @return String
     * @throws CMSItemNotFoundException
     * @throws InvalidCartException
     */
    @RequestMapping(value = "/resetVoucher", method = RequestMethod.POST)
    public String resetVoucher(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException, InvalidCartException {

        // Add Global Attributes
        addGlobalModelAttributes(model, request);

        getCartFacade().resetVoucherOnCurrentCart();
        try {
            getCartFacade().recalculateCart();
        } catch (final CalculationException e) {
            model.addAttribute("errorMsg", "cart.error.recalculate");
        }
        prepareDataForPage(model);
        return ControllerConstants.Views.Pages.Cart.CartPage;
    }

    /**
     * Redeem the voucher code.
     *
     * @param voucherCode
     */
    private void redeemVoucher(final String voucherCode) {
        if (!StringUtils.isEmpty(voucherCode)) {
            getCartFacade().setVoucherCodeToRedeem(voucherCode);
        }
    }

    /**
     * This method checks if the voucher code is applicable or not.
     *
     * @param cart
     * @return String
     */
    private String getVoucherReturnCodeFromCart(final CartData cart) {
        final DistErpVoucherInfoData erpVoucherInfoData = cart.getErpVoucherInfoData();
        if (erpVoucherInfoData != null && erpVoucherInfoData.getReturnERPCode() != null) {
            if ("00".equals(erpVoucherInfoData.getReturnERPCode()) && Boolean.TRUE.equals(erpVoucherInfoData.getValid())) {
                return erpVoucherInfoData.getReturnERPCode();
            }
        }
        return null;
    }

    // -------------------------- Voucher Implementation End --------------------------

    /**
     * This method allows saves the reevooeligible for checkout.
     *
     * @param reevooeligible
     * 
     */
    @RequestMapping(value = "/reevooeligible", method = RequestMethod.POST)
    @ResponseBody
    public String setReevooEligibleFlag(@RequestParam(value = "reevooEligible") final Boolean reevooEligible) {
        getCheckoutFacade().saveReevooEligibleFlag(reevooEligible);
        return SUCCESS;
    }

    public DistCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(final DistCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public DistB2BCartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(final DistB2BCartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    @Override
    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    @Override
    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public DistExportFacade getDistExportFacade() {
        return distExportFacade;
    }

    public void setDistExportFacade(final DistExportFacade distExportFacade) {
        this.distExportFacade = distExportFacade;
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(final SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }

    public DistShareWithFriendsFacade getDistShareWithFriendsFacade() {
        return distShareWithFriendsFacade;
    }

    public void setDistShareWithFriendsFacade(final DistShareWithFriendsFacade distShareWithFriendsFacade) {
        this.distShareWithFriendsFacade = distShareWithFriendsFacade;
    }

    public DistCartService getCartService() {
        return cartService;
    }

    public void setCartService(final DistCartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public DistProductService getProductService() {
        return productService;
    }

    public void setProductService(final DistProductService productService) {
        this.productService = productService;
    }

    public DistCommercePriceService getDistCommercePriceService() {
        return distCommercePriceService;
    }

    public void setDistCommercePriceService(final DistCommercePriceService distCommercePriceService) {
        this.distCommercePriceService = distCommercePriceService;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public DistUrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(final DistUrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public DistUrlResolver<ProductModel> getCatalogPlusProductModelUrlResolver() {
        return catalogPlusProductModelUrlResolver;
    }

    public void setCatalogPlusProductModelUrlResolver(final DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver) {
        this.catalogPlusProductModelUrlResolver = catalogPlusProductModelUrlResolver;
    }
}
