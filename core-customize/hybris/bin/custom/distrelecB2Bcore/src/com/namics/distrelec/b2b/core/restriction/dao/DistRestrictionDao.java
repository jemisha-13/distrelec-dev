package com.namics.distrelec.b2b.core.restriction.dao;

import java.util.Date;
import java.util.List;

import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;

public interface DistRestrictionDao {

	List<CMSTimeRestrictionModel> getEligibleRestrictions(Date fromDate, Date toDate);

}
