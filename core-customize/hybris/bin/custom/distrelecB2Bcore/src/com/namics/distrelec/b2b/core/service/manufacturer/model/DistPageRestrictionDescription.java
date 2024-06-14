package com.namics.distrelec.b2b.core.service.manufacturer.model;

import com.namics.distrelec.b2b.core.model.restrictions.DistPageRestrictionModel;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Dynamic attribute handler for page restriction description.
 * 
 * @author Akshay
 * 
 */
public class DistPageRestrictionDescription extends AbstractDynamicAttributeHandler<String, DistPageRestrictionModel> {

	@Override
	public String get(final DistPageRestrictionModel model) {
		final StringBuilder result = new StringBuilder();
		return result.toString();
	}

}
