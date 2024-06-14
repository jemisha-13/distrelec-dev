package com.namics.distrelec.b2b.core.service.channel.model;

import com.namics.distrelec.b2b.core.model.restrictions.DistChannelRestrictionModel;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Dynamic attribute handler for channel restriction description.
 * 
 * @author Akshay
 * 
 */
public class DistChannelRestrictionDescription
		extends AbstractDynamicAttributeHandler<String, DistChannelRestrictionModel> {

	@Override
	public String get(final DistChannelRestrictionModel model) {
		final StringBuilder result = new StringBuilder();
		return result.toString();
	}

}
