package com.namics.distrelec.b2b.core.reevoo.purchaserfeed.export;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.OrderModel;

public interface DistReevooPurchaserFeedDao {
	
	List<OrderModel> getEligibleOrderEntry(DistSalesOrgModel salesOrg,CMSSiteModel site,Date startDate,Date endDate);
	
}
