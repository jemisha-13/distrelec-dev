package com.namics.distrelec.b2b.core.blocking.rule.dao.impl;

import com.namics.distrelec.b2b.core.blocking.rule.dao.DistBlockedUserAddressRuleDao;
import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedUserAddressRuleModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DefaultDistBlockedUserAddressRuleDao implements DistBlockedUserAddressRuleDao {

    private static final String FIND_BLOCKED_USER_ADDRESS_RULES = "SELECT {" + DistBlockedUserAddressRuleModel.PK + "}"
                                                                    + " FROM {" + DistBlockedUserAddressRuleModel._TYPECODE + "}";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<DistBlockedUserAddressRuleModel> findBlockedUserAddressRules() {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_BLOCKED_USER_ADDRESS_RULES);
        return getFlexibleSearchService().<DistBlockedUserAddressRuleModel>search(searchQuery).getResult();
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
