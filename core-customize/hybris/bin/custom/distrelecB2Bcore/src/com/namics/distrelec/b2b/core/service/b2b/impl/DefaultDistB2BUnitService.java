/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.b2b.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BUnitService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;

/**
 * DefaultDistB2BUnitService extends DefaultB2BUnitService.
 * 
 * @author DAEHUSIR, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistB2BUnitService extends DefaultB2BUnitService {

    private static final Logger LOG = LogManager.getLogger(DefaultDistB2BUnitService.class);

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public void updateBranchInSession(final Session session, final UserModel currentUser) {
        if (!(currentUser instanceof B2BCustomerModel)) {
            return;
        }

        final CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();

        final Object[] branchInfo = (Object[]) getSessionService().executeInLocalView(new SessionExecutionBody() {
            @Override
            public Object[] execute() {
                getSearchRestrictionService().disableSearchRestrictions();
                final B2BCustomerModel currentCustomer = (B2BCustomerModel) currentUser;
                final B2BUnitModel unitOfCustomer = getParent(currentCustomer);
                EnumerationValueModel userPriceGroup = null;
                if (unitOfCustomer.getUserPriceGroup() != null) {
                    userPriceGroup = getTypeService().getEnumerationValue(unitOfCustomer.getUserPriceGroup());
                } else {
                    if (cmsSiteModel != null) {
                        userPriceGroup = getTypeService().getEnumerationValue(cmsSiteModel.getUserPriceGroup());
                    } else {
                        String errMsg = "Can not set user price group, because current site is not set in session!";
                        if (LOG.isDebugEnabled()) {
                            // print exception stack trace
                            Exception ex = new IllegalStateException("No CMSSite in session");
                            LOG.error(errMsg, ex);
                        } else {
                            LOG.error(errMsg);
                        }
                    }
                }
                return new Object[] { getRootUnit(unitOfCustomer), getBranch(unitOfCustomer), unitOfCustomer, userPriceGroup };
            }
        }, currentUser);
        getSessionService().setAttribute("rootunit", branchInfo[0]);
        getSessionService().setAttribute("branch", branchInfo[1]);
        getSessionService().setAttribute("unit", branchInfo[2]);
        getSessionService().setAttribute("Europe1PriceFactory_UPG", branchInfo[3]);
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }
}
