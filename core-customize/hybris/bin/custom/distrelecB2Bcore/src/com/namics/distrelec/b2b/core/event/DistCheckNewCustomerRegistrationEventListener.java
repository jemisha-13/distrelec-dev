/*getget
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.Months;

import com.namics.distrelec.b2b.core.model.process.ExistingRegistrationEmailProcessModel;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Event listener for registration functionality.
 */
public class DistCheckNewCustomerRegistrationEventListener extends AbstractEventListener<DistCheckNewCustomerRegistrationEvent> {
    
    private static final int NUMBER_OF_MONTHS=6;
    private static final int ADMIN_EMAIL_COUNT=3;
    
    public BusinessProcessService getBusinessProcessService() {
        return SpringUtil.getBean("businessProcessService", BusinessProcessService.class);
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }

    @Override
    protected void onEvent(final DistCheckNewCustomerRegistrationEvent checkNewCustomerRegistrationEvent) {
        int i = 0;
        final B2BCustomerModel customer = (B2BCustomerModel) checkNewCustomerRegistrationEvent.getCustomer();
        final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
        if (b2bUnit != null && b2bUnit.getSalesOrg() != null && b2bUnit.getSalesOrg().isAdminManagingSubUsers()
                && CollectionUtils.isNotEmpty(b2bUnit.getMembers())) {
            for (final B2BCustomerModel b2bCustomer : getFilteredCustomerList(b2bUnit)) {
                    if (i < ADMIN_EMAIL_COUNT) {
                        final ExistingRegistrationEmailProcessModel storeFrontCustomerProcessModel = (ExistingRegistrationEmailProcessModel) getBusinessProcessService()
                                .createProcess("newExistingCustomerRegistrationEmailProcess" + System.currentTimeMillis(),
                                        "newExistingCustomerRegistrationEmailProcess");
                        storeFrontCustomerProcessModel.setSite(checkNewCustomerRegistrationEvent.getSite());
                        storeFrontCustomerProcessModel.setCustomer(b2bCustomer);
                        storeFrontCustomerProcessModel.setRegistrationType(checkNewCustomerRegistrationEvent.getRegistrationType());
                        storeFrontCustomerProcessModel.setCompanyName(b2bUnit.getName());
                        storeFrontCustomerProcessModel.setRegisteredUserName(customer.getEmail());
                        storeFrontCustomerProcessModel.setStorefrontRequest(checkNewCustomerRegistrationEvent.isStorefrontRequest());
                        getModelServiceViaLookup().save(storeFrontCustomerProcessModel);
                        getBusinessProcessService().startProcess(storeFrontCustomerProcessModel);
                        i++;
                    }
            }
        }
    }
    
    private List<B2BCustomerModel> getFilteredCustomerList(B2BUnitModel b2bUnit){
        List<B2BCustomerModel> adminCustomers= new ArrayList<B2BCustomerModel>();
        for (final Object member : b2bUnit.getMembers()) {
            if (member instanceof B2BCustomerModel) {
                B2BCustomerModel  b2bCustomer=(B2BCustomerModel)member;
                if(isActiveB2BAdminUser(b2bCustomer) && hasUserLogedIn6Months(b2bCustomer)) {
                    adminCustomers.add(b2bCustomer);
                }
            }
        }
        adminCustomers.sort(Comparator.nullsLast(Comparator.comparing(B2BCustomerModel::getCreationtime)));
        return adminCustomers; 
    }
    
    private boolean hasUserLogedIn6Months(B2BCustomerModel customer) {
        DateTime now = new DateTime();
        DateTime then = new DateTime(customer.getLastLogin());
        if(customer.getLastLogin()!=null && Math.abs(Months.monthsBetween(now, then).getMonths()) < NUMBER_OF_MONTHS){
            return true;
        }
        return false;
    }
    
    private boolean isActiveB2BAdminUser(final B2BCustomerModel b2bCustomer) {
        return getUserService().isMemberOfGroup(b2bCustomer, getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP)) //
                && BooleanUtils.isTrue(b2bCustomer.getActive());
    }

    public UserService getUserService() {
        return SpringUtil.getBean("userService", UserService.class);
    }

}
