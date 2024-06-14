package com.namics.distrelec.b2b.core.obsolescence.dao.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.obsolescence.dao.DistObsolescenceDao;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


public class DefaultDistObsolescenceDao implements DistObsolescenceDao{

	private static final int pastSixMonths = -6;

	private static final int pastOneMonths = -1;
	
	private static final String FIND_PHASED_OUT_ORDER_ENTRIES = "SELECT DISTINCT {orderentry:" + OrderEntryModel.PK + "} FROM {" + ProductModel._TYPECODE
    		+ " AS p JOIN "+DistSalesOrgProductModel._TYPECODE+" AS dsop ON {dsop.product} = {p.pk} JOIN "+DistSalesStatusModel._TYPECODE+" AS dss ON {dsop.salesStatus} = {dss.pk} "
    		+ " JOIN "+OrderEntryModel._TYPECODE+" AS orderentry ON {orderentry.product}= {p.pk}} WHERE {dsop:"+DistSalesOrgProductModel.MODIFIEDTIME+"} > ?modifiedDate AND "
		    + "{orderentry:"+OrderEntryModel.CREATIONTIME+"} > ?creationDate AND {dss:code} IN (?codelist) ";
	
 
    @Autowired
    private FlexibleSearchService flexibleSearchService;
    
    @Autowired
    private ConfigurationService configurationService;
	

	@Override
	public List<AbstractOrderEntryModel> findPhasedOutEntries() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, pastSixMonths);
		Date creationDate = calendar.getTime();

		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.MONTH, pastOneMonths);
		Date modifiedDate = calendar1.getTime();
		 Configuration configuration = getConfigurationService().getConfiguration();
		final String ignoredObsolCategoryCodesConfig = configuration.getString("obsolescence.phasedoutlist");
		List<String> codesList=Arrays.asList(ignoredObsolCategoryCodesConfig.split(","));
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_PHASED_OUT_ORDER_ENTRIES);
	    searchQuery.addQueryParameter("creationDate", creationDate);
	    searchQuery.addQueryParameter("modifiedDate", modifiedDate);
	    searchQuery.addQueryParameter("codelist", codesList);
	    return getFlexibleSearchService().<AbstractOrderEntryModel> search(searchQuery).getResult();
	}
	
	
    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }
    
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
    
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


}
