/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.hazardous.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.hybris.platform.regioncache.region.CacheRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistHazardStatementModel;
import com.namics.distrelec.b2b.core.model.DistSupplementalHazardInfoModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.facades.hazard.data.HazardStatementData;
import com.namics.distrelec.b2b.facades.hazard.data.SupplementalHazardInfoData;
import com.namics.distrelec.b2b.facades.hazardous.DistHazardousFacade;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class DefaultDistHazardousFacade implements DistHazardousFacade {

	private static final String HAZARD_SUPP_INFO_CACHE_REGION = "hazardSuppInfoCacheRegion";
	private static final String HAZARD_STATEMENT_CACHE_REGION = "hazardStatementCacheRegion";

	@Autowired
	@Qualifier(HAZARD_SUPP_INFO_CACHE_REGION)
	private CacheRegion hazardSuppInfoCacheRegion;

	@Autowired
	@Qualifier(HAZARD_STATEMENT_CACHE_REGION)
	private CacheRegion hazardStatementCacheRegion;

	@Autowired
	private CommonI18NService i18NService;

	@Autowired
	private DistrelecCodelistService codelistService;

	private final ConcurrentMap<CacheKey, Object> hazardStmtCalculationLocks = new ConcurrentHashMap<>();

	@Override
	public List<HazardStatementData> getAllDistHazardStatement() {
		final LanguageModel currentLang = i18NService.getCurrentLanguage();
		final Tenant tenant = Registry.getCurrentTenant();
		final CacheKey cacheKey = new DisthazardStatementCacheKey(tenant, currentLang);
		List<HazardStatementData> cachedHazardStatementData = (List<HazardStatementData>) hazardStatementCacheRegion.get(cacheKey);
		if (cachedHazardStatementData != null) {
			return cachedHazardStatementData;
		}

		Object newHazardStmtLockObject = new Object();
		Object hazardStmtLockObject = hazardStmtCalculationLocks.putIfAbsent(cacheKey, newHazardStmtLockObject);
		if (hazardStmtLockObject == null) {
			hazardStmtLockObject = newHazardStmtLockObject;
		}

		synchronized (hazardStmtLockObject) {
			cachedHazardStatementData = (List<HazardStatementData>) hazardStatementCacheRegion.get(cacheKey);
			if (cachedHazardStatementData != null) {
				return cachedHazardStatementData;
			}

			final List<DistHazardStatementModel> hazartstatements = codelistService.getAllDistHazardStatement();
			final List<HazardStatementData> datas = new ArrayList<HazardStatementData>();
			for (final DistHazardStatementModel hazardModel : hazartstatements) {
				final HazardStatementData data = new HazardStatementData();
				data.setCode(hazardModel.getCode());
				data.setDescription(hazardModel.getDescription());
				datas.add(data);
			}
			return (List<HazardStatementData>) hazardStatementCacheRegion.getWithLoader(cacheKey, value -> datas);
		}
	}

	@Override
	public List<SupplementalHazardInfoData> getAllDistSupplementalHazardInfo() {
		final LanguageModel currentLang = i18NService.getCurrentLanguage();
		final Tenant tenant = Registry.getCurrentTenant();
		final CacheKey cacheKey = new DisthazardSuppInfoCacheKey(tenant, currentLang);
		final List<SupplementalHazardInfoData> cachedSuppInfoData = (List<SupplementalHazardInfoData>) hazardSuppInfoCacheRegion.get(cacheKey);
		if (cachedSuppInfoData != null) {
			return cachedSuppInfoData;
		}
		final List<DistSupplementalHazardInfoModel> suppInfos = codelistService.getAllDistSupplementalHazardInfo();
		final List<SupplementalHazardInfoData> datas = new ArrayList<SupplementalHazardInfoData>();

		for (final DistSupplementalHazardInfoModel suppInfoModel : suppInfos) {
			final SupplementalHazardInfoData data = new SupplementalHazardInfoData();
			data.setCode(suppInfoModel.getCode());
			data.setDescription(suppInfoModel.getDescription());
			datas.add(data);
		}
		return (List<SupplementalHazardInfoData>) hazardSuppInfoCacheRegion.getWithLoader(cacheKey, value -> datas);
	}

}
