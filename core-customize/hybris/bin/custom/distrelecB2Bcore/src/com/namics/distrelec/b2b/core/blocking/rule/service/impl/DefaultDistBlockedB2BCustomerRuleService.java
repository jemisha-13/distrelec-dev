package com.namics.distrelec.b2b.core.blocking.rule.service.impl;

import com.namics.distrelec.b2b.core.blocking.rule.dao.DistBlockedB2BCustomerRuleDao;
import com.namics.distrelec.b2b.core.blocking.rule.service.DistBlockedB2BCustomerRuleService;
import com.namics.distrelec.b2b.core.blocking.rule.util.DistSqlLikeExpressionUtil;
import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedB2BCustomerRuleModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DefaultDistBlockedB2BCustomerRuleService implements DistBlockedB2BCustomerRuleService {

    @Autowired
    private DistBlockedB2BCustomerRuleDao distBlockedB2BCustomerRuleDao;

    @Override
    public DistBlockedB2BCustomerRuleModel findBlockedB2BCustomerRule(String userUid) {
        List<DistBlockedB2BCustomerRuleModel> blockedCustomerRules = getDistBlockedB2BCustomerRuleDao().findBlockedB2BCustomerRules();
        return CollectionUtils.emptyIfNull(blockedCustomerRules).stream()
                              .filter(user -> isUserUidBlocked(user.getCustomerId(), userUid))
                              .findFirst()
                              .orElse(null);
    }

    private boolean isUserUidBlocked(String expression, String userUid) {
        return StringUtils.isNotBlank(expression) && DistSqlLikeExpressionUtil.isValueBlocked(expression, userUid);
    }

    public DistBlockedB2BCustomerRuleDao getDistBlockedB2BCustomerRuleDao() {
        return distBlockedB2BCustomerRuleDao;
    }

    public void setDistBlockedB2BCustomerRuleDao(final DistBlockedB2BCustomerRuleDao distBlockedB2BCustomerRuleDao) {
        this.distBlockedB2BCustomerRuleDao = distBlockedB2BCustomerRuleDao;
    }
}
