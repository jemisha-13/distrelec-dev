package com.namics.distrelec.b2b.facades.export.impl;

import static com.namics.distrelec.b2b.facades.product.converters.populator.ProductVolumePricesPopulator.LIST;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.service.export.DistExportService;
import com.namics.distrelec.b2b.core.service.export.data.AbstractDistExportData;
import com.namics.distrelec.b2b.core.service.export.data.DistCartExportData;
import com.namics.distrelec.b2b.core.service.export.data.DistOrderExportData;
import com.namics.distrelec.b2b.core.service.export.data.DistProductExportData;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.product.model.StockLevelData;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.export.DistExportFacade;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * Default implementation of {@link DistExportFacade}
 *
 * @author pbueschi, Namics AG
 */
public class DefaultDistExportFacade implements DistExportFacade {

    private static final Logger LOG = Logger.getLogger(DefaultDistExportFacade.class);

    private static final int ZERO = 0;

    @Autowired
    @Qualifier("distExportService")
    private DistExportService distExportService;

    @Autowired
    @Qualifier("erp.availabilityService")
    private AvailabilityService availabilityService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Override
    public File exportProducts(final List<NamicsWishlistEntryData> wishlistEntries, final String exportFormat, final String exportFileNamePrefix) {
        if (CollectionUtils.isNotEmpty(wishlistEntries)) {
            final List<AbstractDistExportData> productExportDataList = new ArrayList<>();
            for (final NamicsWishlistEntryData entry : wishlistEntries) {
                final AbstractDistExportData exportData = getProductExportData(entry.getProduct(), true, entry.getDesired().longValue());
                exportData.setReference(entry.getComment());
                productExportDataList.add(exportData);
            }

            return distExportService.getDownloadExportFile(productExportDataList, exportFormat, exportFileNamePrefix);
        }

        return null;
    }

    @Override
    public File exportCart(final CartData cartData, final String exportFormat, final String exportFileNamePrefix) {
        if (cartData != null && cartData.getEntries() != null) {
            final List<DistCartExportData> exportCartDataList = new ArrayList<>();

            for (final OrderEntryData entryData : cartData.getEntries()) {
                final DistCartExportData cartExportData = new DistCartExportData();
                final ProductData productData = entryData.getProduct();

                try {
                    BeanUtils.copyProperties(cartExportData, getProductExportData(productData, false, entryData.getQuantity()));
                    BeanUtils.copyProperty(cartExportData, "reference", entryData.getCustomerReference() != null ? entryData.getCustomerReference() : EMPTY);
                    BeanUtils.copyProperty(cartExportData, "quantity", entryData.getQuantity());
                    BeanUtils.copyProperty(cartExportData, "mySinglePrice", getFormattedPrice(entryData.getBasePrice()));
                    BeanUtils.copyProperty(cartExportData, "mySubtotal", getFormattedPrice(entryData.getTotalPrice()));
                    BeanUtils.copyProperty(cartExportData, "listSinglePrice", getFormattedPrice(entryData.getBaseListPrice()));
                    BeanUtils.copyProperty(cartExportData, "listSubtotal", getFormattedPrice(entryData.getTotalListPrice()));
                } catch (final IllegalAccessException | InvocationTargetException iae) {
                    LOG.error("Failed copy bean properties", iae);
                }

                exportCartDataList.add(cartExportData);
            }

            return distExportService.getDownloadExportFile(exportCartDataList, exportFormat, exportFileNamePrefix);
        }

        return null;
    }

    @Override
    public File exportOrder(final OrderData orderData, final String exportFormat, final String exportFileNamePrefix) {
        if (orderData != null) {
            final List<DistOrderExportData> exportOrderDataList = new ArrayList<>();
            for (final OrderEntryData entryData : orderData.getEntries()) {
                final DistOrderExportData orderExportData = new DistOrderExportData();
                final ProductData productData = entryData.getProduct();

                try {
                    BeanUtils.copyProperties(orderExportData, getProductExportData(productData, false, entryData.getQuantity()));
                    BeanUtils.copyProperty(orderExportData, "orderCode", orderData.getCode());
                    BeanUtils.copyProperty(orderExportData, "quantity", entryData.getQuantity());
                    BeanUtils.copyProperty(orderExportData, "mySinglePrice", getFormattedPrice(entryData.getBasePrice()));
                    BeanUtils.copyProperty(orderExportData, "mySubtotal", getFormattedPrice(entryData.getTotalPrice()));
                } catch (final IllegalAccessException | InvocationTargetException iae) {
                    LOG.error("Failed copy bean properties", iae);
                }

                exportOrderDataList.add(orderExportData);
            }

            return distExportService.getDownloadExportFile(exportOrderDataList, exportFormat, exportFileNamePrefix);
        }

        return null;
    }

    private AbstractDistExportData getProductExportData(final ProductData productData, final boolean copyVolumePrices, final long quantity) {
        final DistProductExportData productExportData = new DistProductExportData();

        try {
            BeanUtils.copyProperty(productExportData, "code", productData.getCodeErpRelevant());
            BeanUtils.copyProperty(productExportData, "manufacturerName", getManufacturerName(productData.getDistManufacturer()));
            BeanUtils.copyProperty(productExportData, "typeName", productData.getTypeName());
            BeanUtils.copyProperty(productExportData, "quantity", quantity);
            BeanUtils.copyProperty(productExportData, "name", productData.getName());
            final Map<String, Integer> availabilityMap = getAvailability(productData.getCode(), quantity);
            BeanUtils.copyProperty(productExportData, "availability", availabilityMap.keySet().iterator().next());
            BeanUtils.copyProperty(productExportData, "stockLevel", availabilityMap.values().iterator().next());
            BeanUtils.copyProperty(productExportData, "salesUnit", getFormattedString(productData.getSalesUnit()));
            if (copyVolumePrices && MapUtils.isNotEmpty(productData.getVolumePricesMap())) {
                BeanUtils.copyProperty(productExportData, "volumePrices", getVolumePricesForExport(productData.getVolumePricesMap()));
            }
            BeanUtils.copyProperty(productExportData, "expiredOn", getFormattedDate(productData.getEndOfLifeDate()));
            BeanUtils.copyProperty(productExportData, "replacementReason", productData.getReplacementReason());
        } catch (final IllegalAccessException | InvocationTargetException iae) {
            LOG.error("Failed copy bean properties", iae);
        }

        return productExportData;
    }

    private String getFormattedPrice(final PriceData price) {
        if (price == null || price.getValue() == null) {
            return EMPTY;
        }

        if (StringUtils.isNotEmpty(price.getCurrencyIso()) && price.getValue() != null) {
            return price.getCurrencyIso() + SPACE + price.getValue();
        }

        return price.getFormattedValue();
    }

    private String getManufacturerName(final DistManufacturerData distManufacturer) {
        return distManufacturer == null ? EMPTY : distManufacturer.getName();
    }

    private Map<String, Integer> getAvailability(final String productCode, final Long quantity) {
        final Map<String, Integer> availabilityMap = new HashMap<>();
        final List<ProductAvailabilityExtModel> availabilities = availabilityService.getAvailability(Collections.singletonList(productCode), Boolean.FALSE);

        if (CollectionUtils.isNotEmpty(availabilities)) {
            final ProductAvailabilityExtModel availability = availabilities.get(0);
            for (final StockLevelData sld : availability.getStockLevels()) {
                if ((sld.getAvailable() > 0) && (sld.getAvailable() >= quantity.intValue())) {
                    availabilityMap.put(sld.getDeliveryTime(), sld.getAvailable());
                    return availabilityMap;
                }
            }
            availabilityMap.put(availability.getDeliveryTimeBackorder(), ZERO);
            return availabilityMap;
        }

        availabilityMap.put(EMPTY, ZERO);
        return availabilityMap;
    }

    private String getFormattedString(final String value) {
        return StringUtils.isNotBlank(value) ? value : EMPTY;
    }

    private Map<Long, String> getVolumePricesForExport(final Map<Long, Map<String, PriceData>> priceDataMap) {
        final Map<Long, String> volumePrices = new HashMap<>();
        for (final Map.Entry<Long, Map<String, PriceData>> priceData : priceDataMap.entrySet()) {
            if (priceData.getValue().get(LIST) != null) {
                volumePrices.put(priceData.getKey(), priceData.getValue().get(LIST).getFormattedValue());
            }
        }
        return volumePrices;
    }

    private String getFormattedDate(final Date date) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, i18NService.getCurrentLocale());
        return date == null ? EMPTY : dateFormat.format(date);
    }

}
