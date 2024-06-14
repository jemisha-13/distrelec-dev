package com.namics.distrelec.b2b.core.obsolescence.dao;

import java.util.List;

import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

public interface DistObsolescenceDao {
	
	List<AbstractOrderEntryModel> findPhasedOutEntries();


}
