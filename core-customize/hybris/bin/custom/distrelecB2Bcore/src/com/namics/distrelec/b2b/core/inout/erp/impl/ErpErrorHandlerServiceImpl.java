package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.erp.ErpErrorHandlerService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCartService;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ErpErrorHandlerServiceImpl implements ErpErrorHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(ErpErrorHandlerServiceImpl.class);

    private static final String PRODUCT_STATUS_MISALIGNMENT_REGEX = "SAP ERR:V1,028 :Material (\\d+-\\d+-\\d+) has status: Inactive Our Decis";

    private static final String TEMPORARY_QUALITY_BLOCK_REGEX = "SAP ERR:V1,028 :Material (\\d+-\\d+-\\d+) has status: Temporary Q Block";

    @Autowired
    private DistCartService cartService;

    @Autowired
    private DistCommerceCartService distCommerceCartService;

    @Autowired
    private ProductService productService;

    @Override
    public boolean isProductStatusMisalignmentException(String message) {
        Matcher matcher = getMatcher(message, PRODUCT_STATUS_MISALIGNMENT_REGEX);
        return matcher.find();
    }

    @Override
    public boolean isTemporaryQualityBlockException(String message) {
        Matcher matcher = getMatcher(message, TEMPORARY_QUALITY_BLOCK_REGEX);
        return matcher.find();
    }

    @Override
    public String extractProductCodeFromMessage(String message) {
        if (isProductStatusMisalignmentException(message)) {
            return extractProductCode(message, PRODUCT_STATUS_MISALIGNMENT_REGEX);
        }

        if (isTemporaryQualityBlockException(message)) {
            return extractProductCode(message, TEMPORARY_QUALITY_BLOCK_REGEX);
        }

        return StringUtils.EMPTY;
    }

    private String extractProductCode(String message, String regex) {
        Matcher matcher = getMatcher(message, regex);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("-", "");
        }
        return StringUtils.EMPTY;
    }

    @Override
    public List<CommerceCartModification> updateProductQuantity(String code, long quantity) {
        CartModel cartModel = cartService.getSessionCart();
        ProductModel productModel = productService.getProductForCode(code);
        List<CartEntryModel> cartEntries = cartService.getEntriesForProduct(cartModel, productModel);

        return cartEntries.stream()
                          .map(cartEntry -> {
                              try {
                                  return distCommerceCartService.updateQuantityForCartEntry(createParameter(cartEntry.getEntryNumber(), quantity));
                              } catch (CommerceCartModificationException exception) {
                                  LOG.error("Error while updating cart entry for product with code " + code, exception);
                                  return null;
                              }
                          })
                          .filter(Objects::nonNull)
                          .collect(Collectors.toList());
    }

    private CommerceCartParameter createParameter(long entryNumber, long quantity) {
        final CartModel cartModel = cartService.getSessionCart();
        final CommerceCartParameter parameter = new CommerceCartParameter();
        parameter.setEnableHooks(true);
        parameter.setCart(cartModel);
        parameter.setEntryNumber(entryNumber);
        parameter.setQuantity(quantity);
        parameter.setRecalculate(Boolean.FALSE);
        return parameter;
    }

    private static Matcher getMatcher(String message, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        return matcher;
    }
}
