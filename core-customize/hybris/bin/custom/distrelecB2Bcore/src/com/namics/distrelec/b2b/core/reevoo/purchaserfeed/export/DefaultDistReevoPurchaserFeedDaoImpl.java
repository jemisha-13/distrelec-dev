package com.namics.distrelec.b2b.core.reevoo.purchaserfeed.export;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.reevoo.DistProductSendToReevooModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

public class DefaultDistReevoPurchaserFeedDaoImpl extends AbstractItemDao implements DistReevooPurchaserFeedDao {

	private static final Logger LOG = LogManager.getLogger(DefaultDistReevoPurchaserFeedDaoImpl.class);
	
	private static final String REEVOO_ELIGIBLE_ORDER = "SELECT DISTINCT{" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ " As order JOIN  " + OrderEntryModel._TYPECODE + " AS orderEntry ON {order.pk}={orderEntry.order} JOIN "
			+ ProductModel._TYPECODE + " As p ON {orderEntry.Product}={p.pk} JOIN " + DistSalesOrgProductModel._TYPECODE
			+ " AS dsop ON {dsop.product}={p.pk} JOIN " + DistSalesStatusModel._TYPECODE
			+ " AS dss ON {dss.pk}={dsop.salesStatus} JOIN " + CustomerModel._TYPECODE
			+ " AS cust ON {cust.pk}={order.user}} WHERE {dsop." + DistSalesOrgProductModel.SALESORG
			+ "}=?salesOrg AND {dss:code} IN (?codelist) AND {order.site}=?site AND {p." + ProductModel.REEVOOAVAILABLE + "}=?reevooAvailable AND {cust." + CustomerModel.ELIGIBLEFORREEVOO + "}=?eligibleForReevoo AND {order.creationtime} > ?startDate AND {order.creationtime} < ?endDate AND EXISTS ({{select 1 from {" + DistProductSendToReevooModel._TYPECODE + " As sendToReevoo} where {sendToReevoo.site}=?site and {sendToReevoo.product}={p.pk}}})";
	
	private static final String REEVOO_ELIGIBLE_ORDER_DE = "SELECT DISTINCT{" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ " As order JOIN  " + OrderEntryModel._TYPECODE + " AS orderEntry ON {order.pk}={orderEntry.order} JOIN "
			+ ProductModel._TYPECODE + " As p ON {orderEntry.Product}={p.pk} JOIN " + DistSalesOrgProductModel._TYPECODE
			+ " AS dsop ON {dsop.product}={p.pk} JOIN " + DistSalesStatusModel._TYPECODE
			+ " AS dss ON {dss.pk}={dsop.salesStatus} JOIN " + CustomerModel._TYPECODE
			+ " AS cust ON {cust.pk}={order.user}} WHERE {dsop." + DistSalesOrgProductModel.SALESORG
			+ "}=?salesOrg AND {dss:code} IN (?codelist) AND {order.reevooEligible}=?reevooEligible AND {order.site}=?site AND {p." + ProductModel.REEVOOAVAILABLE + "}=?reevooAvailable AND {cust." + CustomerModel.ELIGIBLEFORREEVOO + "}=?eligibleForReevoo AND {order.creationtime} > ?startDate AND {order.creationtime} < ?endDate AND EXISTS ({{select 1 from {" + DistProductSendToReevooModel._TYPECODE + " As sendToReevoo} where {sendToReevoo.site}=?site and {sendToReevoo.product}={p.pk}}})";

	@Autowired
	private ConfigurationService configurationService;

	@Override
	public List<OrderModel> getEligibleOrderEntry(DistSalesOrgModel salesOrg,CMSSiteModel site,Date startDate,Date endDate) {

		try {
			Configuration configuration = getConfigurationService().getConfiguration();
			final String ignoredObsolCategoryCodesConfig = configuration.getString("reevoo.product.eligible.status");
			List<String> codesList = Arrays.asList(ignoredObsolCategoryCodesConfig.split(","));
			if(site !=null && "distrelec_DE".equalsIgnoreCase(site.getUid())) {
				final FlexibleSearchQuery searchProductQuery = new FlexibleSearchQuery(REEVOO_ELIGIBLE_ORDER_DE);
				searchProductQuery.addQueryParameter("salesOrg", salesOrg);
				searchProductQuery.addQueryParameter("codelist", codesList);
				searchProductQuery.addQueryParameter("site", site);
				searchProductQuery.addQueryParameter("startDate", startDate);
				searchProductQuery.addQueryParameter("endDate", endDate);
				searchProductQuery.addQueryParameter("eligibleForReevoo", Boolean.TRUE);
				searchProductQuery.addQueryParameter("reevooAvailable", Boolean.TRUE);
				searchProductQuery.addQueryParameter("reevooEligible", Boolean.TRUE);
				return getFlexibleSearchService().<OrderModel>search(searchProductQuery).getResult();
			}else {
				final FlexibleSearchQuery searchProductQuery = new FlexibleSearchQuery(REEVOO_ELIGIBLE_ORDER);
				searchProductQuery.addQueryParameter("salesOrg", salesOrg);
				searchProductQuery.addQueryParameter("codelist", codesList);
				searchProductQuery.addQueryParameter("site", site);
				searchProductQuery.addQueryParameter("startDate", startDate);
				searchProductQuery.addQueryParameter("endDate", endDate);
				searchProductQuery.addQueryParameter("eligibleForReevoo", Boolean.TRUE);
				searchProductQuery.addQueryParameter("reevooAvailable", Boolean.TRUE);
				return getFlexibleSearchService().<OrderModel>search(searchProductQuery).getResult();
			}
		} catch (Exception ex) {
			LOG.error("Error in fetching Eligible Order Entries", ex);
		}
		return Collections.<OrderModel>emptyList();
	}

	protected ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
