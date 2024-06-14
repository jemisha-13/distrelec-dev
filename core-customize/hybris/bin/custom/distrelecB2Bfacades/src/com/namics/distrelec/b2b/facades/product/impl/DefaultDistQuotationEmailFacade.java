package com.namics.distrelec.b2b.facades.product.impl;

import com.namics.distrelec.b2b.core.event.DistQuoteEmailEvent;
import com.namics.distrelec.b2b.facades.product.DistQuotationEmailFacade;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultDistQuotationEmailFacade implements DistQuotationEmailFacade {

	@Autowired
	private EventService eventService;

	@Autowired
	private BaseStoreService baseStoreService;

	@Autowired
	private BaseSiteService baseSiteService;

	@Autowired
	private UserService userService;

	@Override
	public void sendQuotationEmail(final DistQuoteEmailEvent event) {
		// YTODO Auto-generated method stub
		setUpAdditionalEventData(event);
		getEventService().publishEvent(event);
	}

	protected void setUpAdditionalEventData(final DistQuoteEmailEvent userEvent) {
		userEvent.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		userEvent.setSite(getBaseSiteService().getCurrentBaseSite());
		userEvent.setCustomer((CustomerModel) getUserService().getCurrentUser());
	}

	public EventService getEventService() {
		return eventService;
	}

	public void setEventService(final EventService eventService) {
		this.eventService = eventService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

}
