package com.namics.distrelec.b2b.core.cms.daos.impl;

import java.util.*;

import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.impl.DefaultCMSSiteDao;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang.time.DateUtils;

public class DefaultDistCMSSiteDao extends DefaultCMSSiteDao implements DistCMSSiteDao
{

	private final static String findCMSSiteForProduct_old = "select distinct {site.pk} from {CMSSite as site " +
			"join DistPriceRow as price on {site.userPriceGroup}={price.ug} " +
			"join DistSalesOrg as salesOrg on {site.salesOrg}={salesOrg.pk} " +
			"join DistSalesOrgProduct as salesOrgProduct on {salesOrg.pk}={salesOrgProduct.salesOrg} " +
			"join DistSalesStatus as salesStatus ON {salesOrgProduct.salesStatus}={salesStatus.pk}} " +

			"WHERE {price.product}=?product AND {salesStatus.visibleInShop} = 1 " +
			"AND {salesOrgProduct.product}=?product";


	private final static String findCMSSiteForProduct = " select distinct {site.pk} from {CMSSite as site " +
					"join DistPriceRow as price on {site.userPriceGroup}={price.ug} " +
					"join DistSalesOrg as salesOrg on {site.salesOrg}={salesOrg.pk} " +
					"join DistSalesOrgProduct as salesOrgProduct on {salesOrg.pk}={salesOrgProduct.salesOrg} " +
					"join DistSalesStatus as salesStatus ON {salesOrgProduct.salesStatus}={salesStatus.pk} } " +
				"WHERE {price.product}=?product " +
					"AND {salesStatus.visibleInShop}=1 " +
					"AND {salesOrgProduct.product}=?product       " +
					"AND ({price.startTime} IS NULL OR {price.startTime} <= ?date) " +
					"AND ({price.endTime} IS NULL OR {price.endTime} >= ?date) " +
					"AND NOT EXISTS ({{ " +
						"SELECT 1 FROM {DistCOPunchOutFilter AS pof} " +
							"WHERE {pof.product} = ?product " +
								"AND {pof.validFromDate} <= ?date " +
								"AND {pof.validUntilDate} >= ?date " +
								"AND {pof.salesOrg} = {salesOrgProduct.salesOrg} " +
								"AND {pof.country} = {site.country}" +
						"}})" +
					"AND NOT EXISTS ({{" +
						"SELECT 1 FROM {DistCOPunchOutFilter AS pof} " +
							"WHERE {pof.productHierarchy} = ?productHierarchy " +
								"AND {pof.validFromDate} <= ?date " +
								"AND {pof.validUntilDate} >= ?date " +
								"AND {pof.salesOrg} = {salesOrgProduct.salesOrg}" +
								"AND {pof.country} = {site.country}" +
					"}}) ";
	

	@Override
	public List<CMSSiteModel> findCMSSitesById(String siteUid)
	{
		List<CMSSiteModel> cmsSites = super.findCMSSitesById(siteUid);

		if (cmsSites.isEmpty() && DistUtils.containsMinus(siteUid))
		{
			String convertedSiteUid = DistUtils.revertSiteUidMinusToUnderscore(siteUid);
			cmsSites = super.findCMSSitesById(convertedSiteUid);
		}

		return cmsSites;
	}

	@Override
	public List<CMSSiteModel> findCMSSitesProductIsAvailableOn(ProductModel product)
	{
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("product", product);
		Date truncate = DateUtils.truncate(new Date(), Calendar.HOUR);
		params.put("date", truncate);
		params.put("productHierarchy", product.getProductHierarchy() == null ? "" : product.getProductHierarchy());

		SearchResult<CMSSiteModel> search = getFlexibleSearchService().search(findCMSSiteForProduct, params);
		return search.getResult();
	}
}


