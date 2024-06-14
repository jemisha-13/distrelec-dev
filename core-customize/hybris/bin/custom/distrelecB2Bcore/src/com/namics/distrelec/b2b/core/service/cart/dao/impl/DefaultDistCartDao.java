/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.cart.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.namics.distrelec.b2b.core.service.cart.dao.DistCartDao;

import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * {@code DefaultDistCartDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.5
 */
public class DefaultDistCartDao extends DefaultGenericDao<CartModel> implements DistCartDao {

    private static final List<Class<String>> R_CLASS_SLIST = Arrays.asList(String.class);

    private static final String PRODUCT_IN_CART_QUERY = "SELECT {p." + ProductModel.CODE + "} FROM {" + CartEntryModel._TYPECODE + " AS ce JOIN "
                                                        + ProductModel._TYPECODE + " AS p ON {ce." + CartEntryModel.PRODUCT + "}={p." + ProductModel.PK
                                                        + "}} WHERE {p." + ProductModel.CODE + "} IN (?"
                                                        + CartEntryModel.PRODUCT + ") AND {ce." + CartEntryModel.ORDER + "}=?cart";

    private static final String FIND_CART_FOR_CODE_QUERY = "SELECT {" + CartModel.PK + "} FROM {" + CartModel._TYPECODE + "!} WHERE {" + CartModel.CODE + "}=?"
                                                           + CartModel.CODE;

    /**
     * Create a new instance of {@code DefaultDistCartDao}
     */
    public DefaultDistCartDao() {
        super(CartModel._TYPECODE);
    }

    @Override
    public List<String> productsInCart(final CartModel cart, final String... productCodes) {
        validateParameterNotNull(cart, "Cart must not be null!");
        validateParameterNotNull(productCodes, "Product codes must not be null!");
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCT_IN_CART_QUERY);
        searchQuery.addQueryParameter(CartEntryModel.PRODUCT, Arrays.asList(productCodes));
        searchQuery.addQueryParameter("cart", cart);
        searchQuery.setResultClassList(R_CLASS_SLIST);
        return getFlexibleSearchService().<String> search(searchQuery).getResult();
    }

    @Override
    public Optional<CartModel> getCartForCode(final String code) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_CART_FOR_CODE_QUERY);
        searchQuery.addQueryParameter(CartModel.CODE, code);
        final SearchResult<CartModel> searchResult = getFlexibleSearchService().<CartModel> search(searchQuery);
        return Optional.ofNullable(CollectionUtils.isNotEmpty(searchResult.getResult()) ? searchResult.getResult().get(0) : null);
    }
}
