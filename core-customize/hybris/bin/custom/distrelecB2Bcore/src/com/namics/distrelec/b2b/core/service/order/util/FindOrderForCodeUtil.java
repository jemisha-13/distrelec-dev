package com.namics.distrelec.b2b.core.service.order.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class FindOrderForCodeUtil {
    private FindOrderForCodeUtil() {
    }

    public static AbstractOrderModel getAbstractOrderByCode(final String code, final FlexibleSearchService flexibleSearchService) {
        AbstractOrderModel abstractOrder = getAbstractOrderForCodeInternal(CartModel._TYPECODE, AbstractOrderModel.ERPORDERCODE, code, flexibleSearchService);
        if (abstractOrder == null) {
            abstractOrder = getAbstractOrderForCodeInternal(CartModel._TYPECODE, AbstractOrderModel.CODE, code, flexibleSearchService);
        }
        if (abstractOrder == null) {
            abstractOrder = getAbstractOrderForCodeInternal(AbstractOrderModel._TYPECODE, AbstractOrderModel.ERPORDERCODE, code, flexibleSearchService);
        }
        if (abstractOrder == null) {
            abstractOrder = getAbstractOrderForCodeInternal(AbstractOrderModel._TYPECODE, AbstractOrderModel.CODE, code, flexibleSearchService);
        }
        if (abstractOrder == null) {
            abstractOrder = getAbstractOrderForCodeInternal(OrderModel._TYPECODE, AbstractOrderModel.ERPORDERCODE, code, flexibleSearchService);
        }
        if (abstractOrder == null) {
            abstractOrder = getAbstractOrderForCodeInternal(OrderModel._TYPECODE, AbstractOrderModel.CODE, code, flexibleSearchService);
        }
        if (abstractOrder == null) {
            abstractOrder = getAbstractOrderForCodeInternal(OrderModel._TYPECODE, AbstractOrderModel.GUID, code, flexibleSearchService);
        }
        if (abstractOrder == null) {
            abstractOrder = getAbstractOrderForCodeInternal(AbstractOrderModel._TYPECODE, AbstractOrderModel.GUID, code, flexibleSearchService);
        }
        return abstractOrder;
    }

    private static <T extends AbstractOrderModel> T getAbstractOrderForCodeInternal(final String typecode, final String param, final String code,
                                                                                    final FlexibleSearchService flexibleSearchService) {
        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {pk} FROM {").append(typecode).append("} ").append("WHERE {").append(param).append("}=(?").append(param).append(")");

        final Map<String, Object> params = new HashMap<>();
        params.put(param, code);

        final SearchResult<T> searchResult = flexibleSearchService.search(queryString.toString(), params);

        return CollectionUtils.isEmpty(searchResult.getResult()) ? null : searchResult.getResult().iterator().next();
    }

    public static CartModel getCartByCode(final String code, final FlexibleSearchService flexibleSearchService) {
        CartModel cartModel = getAbstractOrderForCodeInternal(CartModel._TYPECODE, AbstractOrderModel.ERPORDERCODE, code, flexibleSearchService);
        if (cartModel == null) {
            cartModel = getAbstractOrderForCodeInternal(CartModel._TYPECODE, AbstractOrderModel.CODE, code, flexibleSearchService);
        }
        if (cartModel == null) {
            cartModel = getAbstractOrderForCodeInternal(CartModel._TYPECODE, AbstractOrderModel.GUID, code, flexibleSearchService);
        }
        return cartModel;
    }
}
