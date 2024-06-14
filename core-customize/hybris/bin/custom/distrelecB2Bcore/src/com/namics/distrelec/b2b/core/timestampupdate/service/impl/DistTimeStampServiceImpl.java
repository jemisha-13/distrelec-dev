package com.namics.distrelec.b2b.core.timestampupdate.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;

import com.namics.distrelec.b2b.core.timestampupdate.service.DistTimeStampService;
import com.namics.distrelec.b2b.core.timestampupdate.dao.DistTimeStampDao;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

public class DistTimeStampServiceImpl extends AbstractBusinessService implements DistTimeStampService {
	
	protected DistTimeStampDao distTimeStampDao;
	
	@Override
    public ProductModel getProductInfoPimId(final String pimId) {
    	ProductModel productModel=getDistTimeStampDao().getProductInfoPimId(pimId);
	    return productModel != null ? productModel : null;
    }
    
	@Override
    public boolean updateProductFirstAppearanceDate() {
    	return getDistTimeStampDao().updateProductFirstAppearanceDate();
    }
	
	@Override
    public List<List<String>> searchProductsForExport() {
		final List<List<String>> prodiuctmodels =  getDistTimeStampDao().searchProductsForExport();
		if (CollectionUtils.isNotEmpty(prodiuctmodels)) {
            return prodiuctmodels;
        }
		return ListUtils.EMPTY_LIST;
    }

	public DistTimeStampDao getDistTimeStampDao() {
		return distTimeStampDao;
	}

	public void setDistTimeStampDao(DistTimeStampDao distTimeStampDao) {
		this.distTimeStampDao = distTimeStampDao;
	}

}
