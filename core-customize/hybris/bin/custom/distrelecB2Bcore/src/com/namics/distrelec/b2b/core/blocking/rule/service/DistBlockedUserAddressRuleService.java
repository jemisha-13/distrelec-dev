package com.namics.distrelec.b2b.core.blocking.rule.service;

import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedUserAddressRuleModel;

public interface DistBlockedUserAddressRuleService {

    DistBlockedUserAddressRuleModel findBlockedUserAddressRule(String postalCode, String streetName, String streetNumber, String city,
                                                               String countryIso);
}
