package com.namics.distrelec.b2b.core.service.pricerow.dao;

import java.util.List;

import com.namics.distrelec.b2b.core.model.DistPriceRowModel;

import de.hybris.platform.servicelayer.internal.dao.Dao;

public interface DistPriceRowDao extends Dao {

    List<DistPriceRowModel> findOrphanedPriceRowsWithLimit(int limit);

}
