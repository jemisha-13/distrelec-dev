package com.namics.distrelec.b2b.core.service.evaluator.impl;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.restrictions.DistPageRestrictionModel;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Evaluator for Page restriction.
 * 
 * @author Akshay
 * 
 */
public class DistPageRestrictionEvaluator implements CMSRestrictionEvaluator<DistPageRestrictionModel> {

	private static final Logger LOG = Logger.getLogger(DistPageRestrictionEvaluator.class);

	@Autowired
	private SessionService sessionService;

	private static final String cmsPage = "CurrentCMSPage";

	@Override
	public boolean evaluate(final DistPageRestrictionModel pageRestriction, final RestrictionData context) {
		final String pageLabel = sessionService.getAttribute(cmsPage);
		boolean value = false;
		final Collection<AbstractPageModel> pages = pageRestriction.getPages();

		if (CollectionUtils.isNotEmpty(pages) && pageLabel != null) {
			boolean found = false;
			for (final AbstractPageModel page : pages) {
				if (pageLabel.equals(((ContentPageModel) page).getLabel()) || pageLabel.equals(page.getUid())) {
					found = true;
					break;
				}
			}
			value = pageRestriction.isInverse() ^ found;
		} else if (LOG.isDebugEnabled()) {
			LOG.debug("Could not evaluate DistPageRestriction. Returning false.");
		}
		return value;
	}
}
