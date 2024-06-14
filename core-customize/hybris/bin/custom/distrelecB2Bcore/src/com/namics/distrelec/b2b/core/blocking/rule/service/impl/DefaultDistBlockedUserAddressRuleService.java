package com.namics.distrelec.b2b.core.blocking.rule.service.impl;

import com.namics.distrelec.b2b.core.blocking.rule.dao.DistBlockedUserAddressRuleDao;
import com.namics.distrelec.b2b.core.blocking.rule.service.DistBlockedUserAddressRuleService;
import com.namics.distrelec.b2b.core.blocking.rule.util.DistSqlLikeExpressionUtil;
import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedUserAddressRuleModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

public class DefaultDistBlockedUserAddressRuleService implements DistBlockedUserAddressRuleService {

    @Autowired
    private DistBlockedUserAddressRuleDao distBlockedUserAddressRuleDao;

    @Override
    public DistBlockedUserAddressRuleModel findBlockedUserAddressRule(String postalCode, String streetName, String streetNumber, String city,
                                                                      String countryIso) {
        List<DistBlockedUserAddressRuleModel> blockedAddressRules = getDistBlockedUserAddressRuleDao().findBlockedUserAddressRules();
        return CollectionUtils.emptyIfNull(blockedAddressRules).stream()
                              .filter(address -> isAddressValueSkippedOrBlocked(address.getPostalCode(), postalCode))
                              .filter(address -> isAddressValueSkippedOrBlocked(address.getStreetNumber(), streetNumber))
                              .filter(address -> isAddressValueSkippedOrBlocked(address.getStreetName(), streetName))
                              .filter(address -> isAddressValueSkippedOrBlocked(address.getCity(), city))
                              .filter(address -> isCountryValueSkippedOrBlocked(address.getCountryIso(), countryIso))
                              .findFirst()
                              .orElse(null);
    }

    private boolean isAddressValueSkippedOrBlocked(String expression, String addressValue) {
        return StringUtils.isBlank(expression) || DistSqlLikeExpressionUtil.isValueBlocked(expression, addressValue);
    }

    private boolean isCountryValueSkippedOrBlocked(String isocode, String countryValue) {
        String trimmedValue = countryValue != null ? countryValue.trim() : null;
        return StringUtils.isBlank(isocode) || Objects.equals(isocode, trimmedValue);
    }

    public DistBlockedUserAddressRuleDao getDistBlockedUserAddressRuleDao() {
        return distBlockedUserAddressRuleDao;
    }

    public void setDistBlockedUserAddressRuleDao(final DistBlockedUserAddressRuleDao distBlockedUserAddressRuleDao) {
        this.distBlockedUserAddressRuleDao = distBlockedUserAddressRuleDao;
    }
}
