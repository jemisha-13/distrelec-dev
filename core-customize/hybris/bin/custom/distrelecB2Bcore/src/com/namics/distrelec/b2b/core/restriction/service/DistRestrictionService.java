package com.namics.distrelec.b2b.core.restriction.service;

import java.util.Date;
import java.util.Set;

public interface DistRestrictionService {

	Set<String> getCDNSitesForClearingCache(Date startDate);
}
