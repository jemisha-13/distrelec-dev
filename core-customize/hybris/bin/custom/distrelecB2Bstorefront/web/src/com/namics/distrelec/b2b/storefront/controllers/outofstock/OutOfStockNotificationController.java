package com.namics.distrelec.b2b.storefront.controllers.outofstock;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUTOFSTOCK;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecOutOfStockNotificationFacade;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageController;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

@Controller
public class OutOfStockNotificationController extends AbstractPageController {

    private static final String STOCK_NOTIFY_CUSTOMER_EMAIL = "customerEmail";

    private static final String STOCK_NOTIFY_ATRICLE_NUMBER = "articleNumber";

    private static final String NOTIFY_ZERO_STOCK = "/notifyZeroStock";

    private static final String BACK_ORDER_ZERO_STOCK = "/backorder/zeroStock";

    @Autowired
    private DistrelecOutOfStockNotificationFacade outOfStockFacade;

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @ResponseBody
    @RequestMapping(value = NOTIFY_ZERO_STOCK, method = RequestMethod.POST)
    public boolean saveStockNotificationDataPDP(@RequestParam(value = STOCK_NOTIFY_CUSTOMER_EMAIL) final String customerEmail,
                                                @RequestParam(value = STOCK_NOTIFY_ATRICLE_NUMBER) final List<String> articleNumber,
                                                final Model model) {

        boolean wasSavingOosSuccessful = false;
        if (StringUtils.isNotEmpty(customerEmail) && CollectionUtils.isNotEmpty(articleNumber)) {
            wasSavingOosSuccessful = getOutOfStockFacade().saveStockNotificationDetails(customerEmail, articleNumber);
        }
        model.addAttribute("stockNotificationStatus", wasSavingOosSuccessful);
        return wasSavingOosSuccessful;
    }

    @ResponseBody
    @RequestMapping(value = BACK_ORDER_ZERO_STOCK, method = RequestMethod.POST)
    public boolean saveStockNotificationDataBackOrder(@RequestParam(value = STOCK_NOTIFY_CUSTOMER_EMAIL) final String customerEmail,
                                                      @RequestParam(value = STOCK_NOTIFY_ATRICLE_NUMBER) final List<String> articleNumber,
                                                      final Model model) {

        boolean wasSavingOosSuccessful = false;
        if (StringUtils.isNotEmpty(customerEmail) && CollectionUtils.isNotEmpty(articleNumber)) {
            final List<OrderEntryData> outOfStockCartItems = getOutOfStockItems(articleNumber);
            boolean hasDiscontinuedProduct = outOfStockCartItems
                                                                .stream()
                                                                .map(OrderEntryData::getProduct)
                                                                .filter(Objects::nonNull)
                                                                .anyMatch(e -> getErpStatusUtil()
                                                                                                 .getErpSalesStatusFromConfiguration(ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUTOFSTOCK)
                                                                                                 .contains(e.getSalesStatus()));

            if (!hasDiscontinuedProduct && CollectionUtils.isNotEmpty(outOfStockCartItems)) {
                wasSavingOosSuccessful = getOutOfStockFacade().saveBackOrderOutOfStock(customerEmail, outOfStockCartItems);
            }
        }

        model.addAttribute("stockNotificationStatus", wasSavingOosSuccessful);
        return wasSavingOosSuccessful;
    }

    private List<OrderEntryData> getOutOfStockItems(final List<String> articleNumber) {
        final CartData cart = getDistCheckoutFacade().getCheckoutCart();
        final List<OrderEntryData> entries = cart.getEntries();
        List<OrderEntryData> filteredEntries = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entries)) {
            for (OrderEntryData item : entries) {
                if (item.getProduct() != null) {
                    for (String code : articleNumber) {
                        if (item.getProduct().getCode().equalsIgnoreCase(code)) {
                            filteredEntries.add(item);
                        }
                    }
                }
            }
        }
        return filteredEntries;
    }

    public DistrelecOutOfStockNotificationFacade getOutOfStockFacade() {
        return outOfStockFacade;
    }

    public void setoutOfStockFacade(DistrelecOutOfStockNotificationFacade outOfStockFacade) {
        this.outOfStockFacade = outOfStockFacade;
    }

    public DistCheckoutFacade getDistCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(DistCheckoutFacade distCheckoutFacade) {
        this.checkoutFacade = distCheckoutFacade;
    }
}
