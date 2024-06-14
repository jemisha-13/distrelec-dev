package com.namics.distrelec.b2b.core.quotation.dao.impl;

import com.namics.distrelec.b2b.core.jalo.DistB2BQuoteLimit;
import com.namics.distrelec.b2b.core.model.DistB2BQuoteLimitModel;
import com.namics.distrelec.b2b.core.quotation.dao.DistQuotationDao;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static com.namics.distrelec.b2b.core.constants.DistQueryParameterConstants.DateParams.DATE_FROM;
import static com.namics.distrelec.b2b.core.constants.DistQueryParameterConstants.DateParams.DATE_TO;
import static com.namics.distrelec.b2b.core.constants.DistQueryParameterConstants.UserParams.USER_PK;

public class DefaultDistQuotationDao implements DistQuotationDao {

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    private static final String GET_QUOTATION_COUNT_QUERY = "SELECT {dql.pk} from {DistB2BQuoteLimit as dql} WHERE " +
            "{dql.user} = ?"+ USER_PK +" ORDER BY createdTS DESC";

    private static final String GET_RESET_QUOTATION_COUNTER_QUERY = "SELECT {dql.pk} from {DistB2BQuoteLimit as dql} WHERE " +
            "{"+DistB2BQuoteLimit.CREATION_TIME+"} >= ?"+ DATE_FROM +" AND {"+DistB2BQuoteLimit.CREATION_TIME+"} <= ?"+ DATE_TO;

    @Override
    public DistB2BQuoteLimitModel getQuotationCount(UserModel userModel) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_QUOTATION_COUNT_QUERY);
        query.addQueryParameter(USER_PK, userModel.getPk().getLongValue());

        final List<DistB2BQuoteLimitModel> result = flexibleSearchService.<DistB2BQuoteLimitModel>search(query).getResult();
        return CollectionUtils.isEmpty(result) ? null : result.stream().findFirst().orElse(null);
    }

    @Override
    public List<DistB2BQuoteLimitModel> getAllQuotationsBetweenTwoDates(Date from, Date to) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_RESET_QUOTATION_COUNTER_QUERY);
        query.addQueryParameter(DATE_FROM, from);
        query.addQueryParameter(DATE_TO, from);
        return flexibleSearchService.<DistB2BQuoteLimitModel>search(query).getResult();
    }
}
