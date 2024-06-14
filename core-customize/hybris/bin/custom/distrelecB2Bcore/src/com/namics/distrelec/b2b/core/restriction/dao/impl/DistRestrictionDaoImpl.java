package com.namics.distrelec.b2b.core.restriction.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.restriction.dao.DistRestrictionDao;

import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DistRestrictionDaoImpl extends AbstractItemDao implements DistRestrictionDao {

	private static final String ELIGIBLE_RESTRICTIONS = "select {rest.pk} from {CMSTimeRestriction as rest} where (({rest.activeFrom} >= ?startDate and {rest.activeFrom} <= ?endDate ) OR ({rest.activeuntil} >= ?startDate and {rest.activeuntil} <= ?endDate ))";

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Override
	public List<CMSTimeRestrictionModel> getEligibleRestrictions(Date startDate, Date endDate) {
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(ELIGIBLE_RESTRICTIONS);
		fQuery.addQueryParameter("startDate", startDate);
		fQuery.addQueryParameter("endDate", endDate);

		final SearchResult<CMSTimeRestrictionModel> result = flexibleSearchService.search(fQuery);
		return result.getResult();

	}

}
