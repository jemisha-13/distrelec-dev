package com.namics.distrelec.b2b.facades.product;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.List;

public interface DistrelecOutOfStockNotificationFacade {

    boolean saveStockNotificationDetails(final String customerEmail, final List<String> articleNumbers);

    boolean saveBackOrderOutOfStock(final String customerEmail, final List<OrderEntryData> outOfStockCartItems);
}
