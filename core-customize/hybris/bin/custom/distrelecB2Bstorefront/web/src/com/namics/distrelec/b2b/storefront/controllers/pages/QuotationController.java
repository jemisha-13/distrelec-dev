package com.namics.distrelec.b2b.storefront.controllers.pages;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.SelfServiceQuotation.ATTRIBUTE_DEFAULT_ROW_COUNT;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.list.FixedSizeList;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.core.event.DistQuoteEmailEvent;
import com.namics.distrelec.b2b.core.event.DistQuoteEmailProduct;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import com.namics.distrelec.b2b.facades.product.DistQuotationEmailFacade;
import com.namics.distrelec.b2b.facades.quotation.data.DistRequestQuoteData;
import com.namics.distrelec.b2b.facades.user.data.DistRequestQuoteRowData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;

@Controller
@RequestMapping(value = QuotationController.REQUEST_QUOTATION_PAGE_REQUEST_MAPPING)
public class QuotationController extends AbstractSearchPageController {

    public static final String REQUEST_QUOTATION_PAGE_REQUEST_MAPPING = "/request-quotation";

    private static final String REQUEST_QUOTATION_CMS_PAGE = "request-quotation";

    private static final String CURRENCY_FORMAT = "^(\\d{0,3}([,.]?\\d{3})*|(\\d+))(\\.\\d{0,2})?$";

    @Autowired
    private DistQuotationEmailFacade distQuotationEmailFacade;

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @PostMapping(value = "/cart-quotation")
    public @ResponseBody String createQuotationFromCart() {
        final CartData currentCart = getB2bCartFacade().getCurrentCart();
        List<DistQuoteEmailProduct> products = new ArrayList<>();

        currentCart.getEntries().forEach(item -> {
            DistQuoteEmailProduct product = new DistQuoteEmailProduct();
            product.setRow(item.getEntryNumber());
            product.setArticleNumber(item.getProduct().getCode());

            final ProductData productData = item.getProduct();
            product.setMpn(StringUtils.isEmpty(productData.getTypeName()) ? EMPTY : productData.getTypeName());
            product.setQuantity(item.getQuantity());
            product.setNote(StringUtils.isEmpty(item.getCustomerReference()) ? EMPTY : item.getCustomerReference());
            product.setPriceData(item.getBasePrice());
            products.add(product);
        });

        final DistRequestQuoteData data = new DistRequestQuoteData();

        final CustomerData currentCustomer = getB2bCustomerFacade().getCurrentCustomer();
        final AddressData address = currentCustomer.getContactAddress();
        data.setCompanyName(currentCustomer.getCompanyName());
        data.setTitle(currentCustomer.getTitleCode());
        data.setFirstName(currentCustomer.getFirstName());
        data.setTelephone(address.getPhone());
        data.setEmail(currentCustomer.getEmail());
        data.setLastName(currentCustomer.getLastName());
        data.setReference(EMPTY);
        return sendQuotationEmail(data, products, Boolean.TRUE);
    }

    @GetMapping
    public String getCreateQuotation(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        if (!getCurrentBaseStore().isQuotationsEnabled()) {
            return REDIRECT_PREFIX + "/forbidden";
        } else {
            model.addAttribute("quotationdata", getQuotationDataObject());
            final CustomerData currentCustomer = getB2bCustomerFacade().getCurrentCustomer();
            AddressData address = currentCustomer.getContactAddress();
            model.addAttribute("contactAddress", address);
            model.addAttribute("titleCode", currentCustomer.getTitleCode());
            model.addAttribute("companyName", currentCustomer.getCompanyName());
            model.addAttribute("customerNo", currentCustomer.getUnit().getErpCustomerId());
            model.addAttribute("titles", userFacade.getTitles());
            model.addAttribute("rowlimit", getConfigurationService().getConfiguration().getInteger("quote.row.count", 5));

            model.addAttribute("isCustomerOverQuotationLimit", !getDistProductPriceQuotationFacade().checkIfUserWithinQuoteSubmissionLimit());
            final ContentPageModel contentPage = getContentPageForLabelOrId(REQUEST_QUOTATION_CMS_PAGE);
            final Breadcrumb quotationBreadcrumbEntry = createBreadcrumb();
            model.addAttribute("breadcrumbs", Collections.singletonList(quotationBreadcrumbEntry));
            storeCmsPageInModel(model, contentPage);
            setUpMetaDataForContentPage(model, contentPage);
            addGlobalModelAttributes(model, request);

            return ControllerConstants.Views.Pages.Quotation.RequestQuotationPage;
        }
    }

    private Breadcrumb createBreadcrumb() {
        return new Breadcrumb("#", getMessageSource().getMessage("quote.request.btn", null, getI18nService().getCurrentLocale()), null);
    }

    private DistRequestQuoteData getQuotationDataObject() {
        DistRequestQuoteData quotationData = new DistRequestQuoteData();
        Integer size = getConfigurationService().getConfiguration().getInteger(ATTRIBUTE_DEFAULT_ROW_COUNT, 25);
        List<DistRequestQuoteRowData> fixed = FixedSizeList.decorate(Arrays.asList(new DistRequestQuoteRowData[size]));
        quotationData.setRows(fixed);
        quotationData.setIsTenderProcess(Boolean.FALSE);
        return quotationData;
    }

    @PostMapping
    public @ResponseBody String updateB2BQuotation(final Model model, @ModelAttribute("quotationdata") final DistRequestQuoteData distRequestQuoteData) {
        boolean isWithinLimit = getDistProductPriceQuotationFacade().checkIfUserWithinQuoteSubmissionLimit();
        model.addAttribute("isCustomerOverQuotationLimit", !isWithinLimit);

        if (isWithinLimit) {
            return sendQuotationEmail(distRequestQuoteData, getProductData(distRequestQuoteData), false);
        }
        return "failure";
    }

    private String sendQuotationEmail(final DistRequestQuoteData quoteData, final List<DistQuoteEmailProduct> productsForQuote, boolean isFromCart) {
        boolean isWithinLimit = getDistProductPriceQuotationFacade().checkIfUserWithinQuoteSubmissionLimit();

        if (isWithinLimit) {
            final DistQuoteEmailEvent quoteEmail = new DistQuoteEmailEvent.DistQuoteEmailEventBuilder(quoteData.getTitle(), quoteData.getFirstName(), quoteData
                                                                                                                                                               .getLastName(),
                                                                                                      quoteData.getEmail(), quoteData.getCompanyName(),
                                                                                                      quoteData.getTelephone())
                                                                                                                               .setQuotation(productsForQuote,
                                                                                                                                             quoteData.getRequiredDeliveryDate(),
                                                                                                                                             quoteData.getReference(),
                                                                                                                                             quoteData.getIsTenderProcess())
                                                                                                                               .setFromCart(isFromCart)
                                                                                                                               .build();
            distQuotationEmailFacade.sendQuotationEmail(quoteEmail);
            getDistProductPriceQuotationFacade().incrementQuotesRequestedCounter();
        }
        return "success";
    }

    @PostMapping(value = "/resubmit-quotation/{previousQuoteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody QuoteStatusData resubmitQuotation(@PathVariable final String previousQuoteId) {
        if (StringUtils.isNotEmpty(previousQuoteId)) {
            return getDistProductPriceQuotationFacade().resubmitQuotation(previousQuoteId);
        }
        return null;
    }

    private String normalizeCurrency(final String currency) {
        if (StringUtils.isNotEmpty(currency)) {
            String cleanedCurrency = currency.replaceAll("[^\\d,|.]+", EMPTY);
            if (cleanedCurrency.matches(CURRENCY_FORMAT)) {
                return cleanedCurrency;
            }
        }
        return null;
    }

    private List<DistQuoteEmailProduct> getProductData(final DistRequestQuoteData quoteData) {

        final List<DistRequestQuoteRowData> rows = quoteData.getRows().stream()
                                                            .filter(Objects::nonNull)
                                                            .filter(row -> row.getRowNumber() != null)
                                                            .collect(Collectors.toList());

        final B2BCustomerModel user = (B2BCustomerModel) getUserService().getCurrentUser();
        final B2BUnitModel company = user.getDefaultB2BUnit();

        List<DistQuoteEmailProduct> productDataList = new ArrayList<>();
        for (DistRequestQuoteRowData row : rows) {
            DistQuoteEmailProduct distQuoteEmailProduct = new DistQuoteEmailProduct();

            final String cleansedCurrency = normalizeCurrency(row.getPrice());
            PriceData quotationPrice = null;
            if (StringUtils.isNotEmpty(cleansedCurrency)) {
                quotationPrice = priceDataFactory.create(PriceDataType.BUY, new BigDecimal(cleansedCurrency), company.getCurrency().getIsocode());
            }

            if (quotationPrice != null) {
                distQuoteEmailProduct.setPriceData(quotationPrice);
            } else {
                distQuoteEmailProduct.setPrice(cleansedCurrency);
            }

            distQuoteEmailProduct.setRow(row.getRowNumber());
            distQuoteEmailProduct.setQuantity(row.getQuantity());
            distQuoteEmailProduct.setNote(row.getDescription());
            distQuoteEmailProduct.setMpn(row.getMpn());

            final String productNumber = super.normalizeProductCode(row.getArticle());
            distQuoteEmailProduct.setArticleNumber(productNumber != null ? productNumber : row.getArticle());
            productDataList.add(distQuoteEmailProduct);
        }
        return productDataList;
    }
}
