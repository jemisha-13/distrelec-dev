package com.namics.distrelec.b2b.core.reevoo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.reevoo.productfeed.dao.RevooProductFeedExportDao;
import com.namics.distrelec.b2b.core.reevoo.service.DistReevooService;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.product.ProductModel;

public class DefaultDistReevooService implements DistReevooService{
	
	@Autowired
	private RevooProductFeedExportDao revooProductFeedExportDao;
	
	@Autowired
	private CMSSiteService cmsSiteService;
	
	@Override
	public boolean isProductEligibleForReevoo(ProductModel product) {
		Boolean isEligible=false;
		CMSSiteModel site = cmsSiteService.getCurrentSite();
		if(site !=null && site.isReevooActivated() && revooProductFeedExportDao.isProductEligible(site, product)) {
			isEligible=true;
		}
		return isEligible;
	}

}
