package com.namics.distrelec.b2b.core.reevoo.productfeed.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.reevoo.DistProductSendToReevooModel;
import com.namics.distrelec.b2b.core.reevoo.productfeed.dao.RevooProductFeedExportDao;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

public class DefaultRevooProductFeedExportDao extends AbstractItemDao  implements RevooProductFeedExportDao {
	
	private String PRODUCT_FOR_REEEVOO = "SELECT {p." + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + " AS p JOIN "
            + DistSalesOrgProductModel._TYPECODE + " AS dsop ON {dsop.product}={p.pk} JOIN " + DistSalesStatusModel._TYPECODE
            + " AS dss ON {dss.pk}={dsop." + DistSalesOrgProductModel.SALESSTATUS + "}} WHERE {dsop." + DistSalesOrgProductModel.SALESORG + "}=?salesOrg AND  {p." + ProductModel.REEVOOAVAILABLE + "}=?reevooAvailable AND {dss:code} IN (?codelist) AND not exists ({{select 1 from {" + DistProductSendToReevooModel._TYPECODE + " As reevooProduct} where {reevooProduct."+DistProductSendToReevooModel.SITE+"}=(?cmssite) and {reevooProduct."+DistProductSendToReevooModel.PRODUCT+"}={p.pk} }})";
	
	private String PRODUCT_ELIGIBLE_FOR_REEEVOO = "select {reevooProduct." + DistProductSendToReevooModel.PRODUCT + "} from {" + DistProductSendToReevooModel._TYPECODE + " As reevooProduct} where {reevooProduct."+DistProductSendToReevooModel.SITE+"}=(?cmssite) AND {reevooProduct."+DistProductSendToReevooModel.PRODUCT+"}=(?product) ";
	
	 @Autowired
	    private ConfigurationService configurationService;
	 
	 
	@Override
	public List<ProductModel> getEligilbleProducts(CMSSiteModel cmsSite) {
		 Configuration configuration = getConfigurationService().getConfiguration();
		final String ignoredObsolCategoryCodesConfig = configuration.getString("reevoo.product.eligible.status");
		List<String> codesList=Arrays.asList(ignoredObsolCategoryCodesConfig.split(","));
		 final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCT_FOR_REEEVOO);
	     searchQuery.addQueryParameter("cmssite", cmsSite);
	     searchQuery.addQueryParameter("codelist", codesList);
	     searchQuery.addQueryParameter("reevooAvailable", Boolean.TRUE);
	     searchQuery.addQueryParameter("salesOrg", cmsSite.getSalesOrg());
		return  getFlexibleSearchService().<ProductModel> search(searchQuery) !=null? getFlexibleSearchService().<ProductModel> search(searchQuery).getResult(): Collections.<ProductModel>emptyList();
	}
	
	@Override
	public Boolean isProductEligible(CMSSiteModel cmsSite,ProductModel product) {
		 final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCT_ELIGIBLE_FOR_REEEVOO);
	     searchQuery.addQueryParameter("cmssite", cmsSite);
	     searchQuery.addQueryParameter("product", product);
		return  getFlexibleSearchService().<ProductModel> search(searchQuery) !=null? getFlexibleSearchService().<ProductModel> search(searchQuery).getTotalCount()>0: Boolean.FALSE;
	}
	
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	

}
