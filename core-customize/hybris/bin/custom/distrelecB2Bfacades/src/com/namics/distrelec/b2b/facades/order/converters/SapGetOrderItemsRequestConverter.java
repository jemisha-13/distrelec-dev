package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if19.v1.RMAGetOrderItemsRequest;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.user.UserService;

public class SapGetOrderItemsRequestConverter
		extends AbstractPopulatingConverter<DistOrderHistoryPageableData, RMAGetOrderItemsRequest> {

	private UserService userService;
	private DistSalesOrgService distSalesOrgService;

	@Override
	public void populate(final DistOrderHistoryPageableData source, final RMAGetOrderItemsRequest target) {

		final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
		final B2BUnitModel currentUnit = currentUser.getDefaultB2BUnit();

		target.setCustomerId(currentUnit.getErpCustomerID());
		target.setOrderId(source.getOrderNumber());
		target.setSalesOrganization(getDistSalesOrgService().getCurrentSalesOrg().getCode());
		target.setSessionLanguage(currentUser.getSessionLanguage().getIsocode());

	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	public DistSalesOrgService getDistSalesOrgService() {
		return distSalesOrgService;
	}

	public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
		this.distSalesOrgService = distSalesOrgService;
	}

}
