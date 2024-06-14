package com.namics.distrelec.b2b.core.cms.daos;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSSiteDao;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

public interface DistCMSSiteDao extends CMSSiteDao
{
	List<CMSSiteModel> findCMSSitesProductIsAvailableOn(ProductModel product);
}
