/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import com.namics.distrelec.b2b.core.service.product.model.PickupStockLevelExtModel;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.product.model.StockLevelData;
import com.namics.distrelec.b2b.facades.product.converters.populator.PickupStockLevelPopulator;
import com.namics.distrelec.b2b.facades.product.data.PickupStockLevelData;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Custom Converter for ProductAvailabilityModel.
 *
 * @author pnueesch, Namics AG
 *
 */
public class ProductAvailabilityConverter extends AbstractPopulatingConverter<ProductAvailabilityExtModel, ProductAvailabilityData> {

    @Autowired
    private PickupStockLevelPopulator<PickupStockLevelExtModel, PickupStockLevelData> productAvailabilityPopulator;

    @Override
    protected ProductAvailabilityData createTarget() {
        return new ProductAvailabilityData();
    }

    @Override
    public void populate(final ProductAvailabilityExtModel productAvailabilityModel, final ProductAvailabilityData productAvailabilityData) {
        Assert.notNull(productAvailabilityModel, "Parameter productAvailabilityModel cannot be null.");
        Assert.notNull(productAvailabilityData, "Parameter productAvailabilityData cannot be null.");
        productAvailabilityData.setProductCode(productAvailabilityModel.getProductCode());
        productAvailabilityData.setDetailInfo(productAvailabilityModel.getDetailInfo());
        productAvailabilityData.setBackorderQuantity(productAvailabilityModel.getBackorderQuantity());
        productAvailabilityData.setBackorderDeliveryDate(productAvailabilityModel.getBackorderDeliveryDate());
        productAvailabilityData.setStockLevelTotal(productAvailabilityModel.getStockLevelTotal());
        productAvailabilityData.setRequestedQuantity(productAvailabilityModel.getRequestedQuantity());
        productAvailabilityData.setDeliveryTimeBackorder(productAvailabilityModel.getDeliveryTimeBackorder());
        productAvailabilityData.setLeadTimeErp(productAvailabilityModel.getLeadTimeErp());

        // Pickup
        if (productAvailabilityModel.getStockLevelPickup() != null) {
            final List<PickupStockLevelData> pickupStockLevel = new ArrayList<PickupStockLevelData>();
            for (final PickupStockLevelExtModel pickupStockLevelModel : productAvailabilityModel.getStockLevelPickup()) {
                final PickupStockLevelData pickupStockLevelData = new PickupStockLevelData();
                getProductAvailabilityPopulator().populate(pickupStockLevelModel, pickupStockLevelData);
                pickupStockLevel.add(pickupStockLevelData);
            }
            productAvailabilityData.setStockLevelPickup(pickupStockLevel);
        }

        productAvailabilityData.setStockLevels(productAvailabilityModel.getStockLevels());
        String statusLabel = null;

        if (null != productAvailabilityData.getStockLevels()) {
            for (final StockLevelData sld : productAvailabilityData.getStockLevels()) {
                if (sld.getAvailable().intValue() > 0) {
                    if (Objects.nonNull(sld.getDeliveryTime())) {
                        statusLabel = sld.getDeliveryTime();
                    } else {
                        statusLabel = "0";
                    }
                    break;
                }
            }
        }

        if (StringUtils.isEmpty(statusLabel)) {
            statusLabel = productAvailabilityData.getDeliveryTimeBackorder();
        } else if (productAvailabilityData.getBackorderQuantity().intValue() > 0) {
            statusLabel = statusLabel.compareTo(productAvailabilityData.getDeliveryTimeBackorder()) <= 0 ? statusLabel
                    : productAvailabilityData.getDeliveryTimeBackorder();
        }

        productAvailabilityData.setStatusLabel(statusLabel);

        super.populate(productAvailabilityModel, productAvailabilityData);
    }

    public PickupStockLevelPopulator<PickupStockLevelExtModel, PickupStockLevelData> getProductAvailabilityPopulator() {
        return productAvailabilityPopulator;
    }

    public void setProductAvailabilityPopulator(final PickupStockLevelPopulator<PickupStockLevelExtModel, PickupStockLevelData> productAvailabilityPopulator) {
        this.productAvailabilityPopulator = productAvailabilityPopulator;
    }

}
