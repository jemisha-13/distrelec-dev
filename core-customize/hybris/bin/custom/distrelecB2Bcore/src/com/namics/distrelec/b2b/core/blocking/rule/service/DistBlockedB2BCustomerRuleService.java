package com.namics.distrelec.b2b.core.blocking.rule.service;

import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedB2BCustomerRuleModel;

public interface DistBlockedB2BCustomerRuleService {

    DistBlockedB2BCustomerRuleModel findBlockedB2BCustomerRule(String userUid);
}
