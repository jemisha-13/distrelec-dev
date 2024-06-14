/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.store.BaseStoreModel;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerRegistrationService;
import com.namics.distrelec.b2b.core.util.DistUtils;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.site.BaseSiteService;

public class DefaultDistCustomerRegistrationService implements DistCustomerRegistrationService {

	@Autowired
	private BaseSiteService baseSiteService;

	@Override
	public Collection<CountryModel> getCountriesForRegistration(final SiteChannel channel) {
		final CMSSiteModel currentSite = (CMSSiteModel) baseSiteService.getCurrentBaseSite();
		Collection<CountryModel> countries = new ArrayList<>();

		final Optional<BaseStoreModel> baseStore = currentSite.getStores()
				.stream()
				.filter(store -> store.getChannel().equals(channel))
				.findFirst();

		if (currentSite.getUid().equalsIgnoreCase("distrelec_EX") && baseStore.isPresent()) {
			countries = baseStore.get().getRegisterCountries();
		} else {
			if(baseStore.isPresent()) {
				countries = baseStore.get().getDeliveryCountries();
			}
		}

		return DistUtils.sortCountries(countries);
	}
}
