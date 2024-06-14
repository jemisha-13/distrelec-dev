package com.namics.distrelec.b2b.core.blocking.rule.dao.impl;

import com.namics.distrelec.b2b.core.blocking.rule.dao.DistBlockedB2BCustomerRuleDao;
import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedB2BCustomerRuleModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DefaultDistBlockedB2BCustomerRuleDao implements DistBlockedB2BCustomerRuleDao {

    private static final String FIND_BLOCKED_B2B_CUSTOMER_RULES = "SELECT {" + DistBlockedB2BCustomerRuleModel.PK + "}"
                                                                    + " FROM {" + DistBlockedB2BCustomerRuleModel._TYPECODE + "}";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<DistBlockedB2BCustomerRuleModel> findBlockedB2BCustomerRules() {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_BLOCKED_B2B_CUSTOMER_RULES);
        return getFlexibleSearchService().<DistBlockedB2BCustomerRuleModel>search(searchQuery).getResult();
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
