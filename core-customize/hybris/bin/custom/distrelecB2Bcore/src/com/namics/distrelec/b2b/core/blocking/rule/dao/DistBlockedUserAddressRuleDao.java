package com.namics.distrelec.b2b.core.blocking.rule.dao;

import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedUserAddressRuleModel;

import java.util.List;

public interface DistBlockedUserAddressRuleDao {

    List<DistBlockedUserAddressRuleModel> findBlockedUserAddressRules();

}
