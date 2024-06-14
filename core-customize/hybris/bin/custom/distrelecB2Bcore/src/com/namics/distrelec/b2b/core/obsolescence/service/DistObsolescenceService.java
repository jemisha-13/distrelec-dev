package com.namics.distrelec.b2b.core.obsolescence.service;

import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.facades.user.data.ObsolescenceTempData;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.CustomerModel;

public interface DistObsolescenceService {

	Map<String, List<AbstractOrderEntryModel>> getPhasedOutOrderEntries();

	void sendEmail(Map<String, List<AbstractOrderEntryModel>> pahsedOutOrderEntries);

	void unsubscribeObsolescence(String customerUid);

	List<ObsolescenceTempData> changeObsolPreference(List<ObsolescenceTempData> obsoleCategories);

	void saveObsolescenceCategoriesForCustomer(CustomerModel customer, boolean selected);
}
