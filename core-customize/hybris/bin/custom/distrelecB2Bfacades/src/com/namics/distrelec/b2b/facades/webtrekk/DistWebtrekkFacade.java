/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import com.namics.distrelec.b2b.facades.product.data.WtAdvancedParameterData;
import com.namics.distrelec.b2b.facades.product.data.WtBasicParameterData;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;

public interface DistWebtrekkFacade {

    String getWebtrekkJsFileUrl(HttpServletRequest request);

    void prepareWebtrekkProductParams(final Model model, final CartData cartData);

    String encodeToUTF8(final String value);

    List<WtBasicParameterData> getWtBasicParameters(final AbstractPageModel page);

    Collection<WtAdvancedParameterData> getWtAdvancedParameters(final AbstractPageModel page);

    void prepareWebtrekkOrderParams(final Model model, final OrderData data);

    void addTeaserTrackingId(final Model model, final String teaserTrakingInstrument);

    String prepareActivatedEventsParam(String value);
}
