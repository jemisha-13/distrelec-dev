package com.namics.distrelec.b2b.core.bomtool.impl;

import com.namics.distrelec.b2b.core.bomtool.BomToolProductAvailabilityStrategy;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.platform.core.model.product.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBomToolAvailabilityStrategy implements BomToolProductAvailabilityStrategy {

    @Autowired
    private DistSalesOrgService salesOrgService;

    @Autowired
    private DistSalesOrgProductService salesOrgProductService;

    @Override
    public boolean isAvailableForSale(ProductModel product) {
        final DistSalesOrgProductModel salesOrgProduct = getSalesOrgProduct(product);
        final DistSalesStatusModel salesStatus = salesOrgProduct != null ? salesOrgProduct.getSalesStatus() : null;

        return isSalesStatusAvailableForSale(salesStatus);
    }

    @Override
    public boolean isAvailableForSaleAfterStockIsDepleted(final ProductModel product) {
        final DistSalesOrgProductModel salesOrgProduct = getSalesOrgProduct(product);
        final DistSalesStatusModel salesStatus = salesOrgProduct != null ? salesOrgProduct.getSalesStatus() : null;

        return isSalesStatusAvailableForSaleAfterStockIsDepleted(salesStatus);
    }

    private DistSalesOrgProductModel getSalesOrgProduct(ProductModel product) {
        DistSalesOrgModel currentSalesOrg = salesOrgService.getCurrentSalesOrg();
        return salesOrgProductService.getSalesOrgProduct(product, currentSalesOrg);
    }

    protected abstract boolean isSalesStatusAvailableForSale(final DistSalesStatusModel salesStatus);
    protected abstract boolean isSalesStatusAvailableForSaleAfterStockIsDepleted(final DistSalesStatusModel salesStatus);
}
