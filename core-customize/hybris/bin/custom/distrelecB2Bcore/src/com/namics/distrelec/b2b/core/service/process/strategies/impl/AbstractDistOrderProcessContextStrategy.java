
package com.namics.distrelec.b2b.core.service.process.strategies.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * Abstract strategy to impersonate site and initialize session context from a process models that has a reference to an AbstractOrderModel.
 */
public abstract class AbstractDistOrderProcessContextStrategy<T extends AbstractOrderModel> extends AbstractDistProcessContextStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDistOrderProcessContextStrategy.class);

	@Override
	public BaseSiteModel getCmsSite(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG);

		return getOrderModel(businessProcess).map(T::getSite).orElse(null);
	}

	@Override
	protected CurrencyModel computeCurrency(final BusinessProcessModel businessProcess)
	{
		final T order = getOrder(businessProcess);

		CurrencyModel currency = getCurrency(order);

		LOG.debug("Context Order currency for business process [{}] is [{}]", businessProcess, currency);

		if (currency == null)
		{
			currency = getCurrency(getCustomer(businessProcess));

			LOG.debug("Context Customer currency for business process [{}] is [{}]", businessProcess, currency);
		}

		return currency;
	}

	@Override
	protected LanguageModel computeLanguage(final BusinessProcessModel businessProcess)
	{
		final T order = getOrder(businessProcess);

		LanguageModel language = getLanguage(order);

		LOG.debug("Context Order language for business process [{}] is [{}]", businessProcess, language);

		if (language == null)
		{
			language = getLanguage(getCustomer(businessProcess));

			LOG.debug("Context Customer language for business process [{}] is [{}]", businessProcess, language);
		}

		return language;
	}

	protected T getOrder(final BusinessProcessModel businessProcess)
	{
		return getOrderModel(businessProcess).orElse(null);
	}

	protected CurrencyModel getCurrency(final AbstractOrderModel order)
	{
		return order == null ? null : order.getCurrency();
	}

	@Override
	protected CustomerModel getCustomer(final BusinessProcessModel businessProcess)
	{
		return getOrderModel(businessProcess).map(T::getUser).map(user -> (CustomerModel) user).orElse(null);
	}

	protected LanguageModel getLanguage(final T abstractOrderModel)
	{
		return Optional.of(abstractOrderModel).filter(order -> order instanceof OrderModel).map(order -> (OrderModel) order)
				.map(OrderModel::getLanguage).orElse(null);
	}

	protected abstract Optional<T> getOrderModel(BusinessProcessModel businessProcessModel);

}
