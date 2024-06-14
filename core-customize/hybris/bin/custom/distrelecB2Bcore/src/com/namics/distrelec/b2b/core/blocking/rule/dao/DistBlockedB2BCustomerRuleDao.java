package com.namics.distrelec.b2b.core.blocking.rule.dao;

import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedB2BCustomerRuleModel;

import java.util.List;

public interface DistBlockedB2BCustomerRuleDao {

    List<DistBlockedB2BCustomerRuleModel> findBlockedB2BCustomerRules();

}
