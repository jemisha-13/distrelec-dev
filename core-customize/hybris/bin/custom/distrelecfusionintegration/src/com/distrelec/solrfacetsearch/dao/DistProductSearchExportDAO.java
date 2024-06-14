package com.distrelec.solrfacetsearch.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

public interface DistProductSearchExportDAO {
    double getTaxFactor(ProductModel product, CMSSiteModel cmsSite, DistSalesOrgProductModel saleOrgProduct);

    DistSalesOrgProductModel getDistSalesOrgProductModels(ProductModel product, CMSSiteModel cmsSite);

    Optional<ProductCountryModel> getProductCountry(ProductModel product, CountryModel country);

    long getTotalStockForProduct(ProductModel product, Set<WarehouseModel> warehouses);

    boolean hasActivePunchOutFilter(CMSSiteModel cmsSite, CountryModel country, ProductModel product);

    List<EnumerationValueModel> getChannelsWithPunchOutFilters(ProductModel product);

    boolean isVisibleInSalesOrg(ProductModel product, DistSalesOrgModel salesOrg);

}
