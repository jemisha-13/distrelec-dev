package com.namics.distrelec.b2b.core.quotation.dao;

import com.namics.distrelec.b2b.core.model.DistB2BQuoteLimitModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Date;
import java.util.List;

public interface DistQuotationDao {

    DistB2BQuoteLimitModel getQuotationCount(final UserModel user);

    List<DistB2BQuoteLimitModel> getAllQuotationsBetweenTwoDates(final Date from, final Date to);

}
