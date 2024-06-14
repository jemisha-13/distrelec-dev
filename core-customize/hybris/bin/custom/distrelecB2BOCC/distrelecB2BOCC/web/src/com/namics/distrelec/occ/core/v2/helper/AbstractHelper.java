/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.helper;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.SearchPageableData;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.occ.core.v2.enums.ShowMode;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

@Component
public abstract class AbstractHelper {

    private static final int DEFAULT_SEARCH_MAX_SIZE = 100;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelService modelService;

    @Autowired
    @Qualifier("b2bCustomerFacade")
    private DistCustomerFacade b2bCustomerFacade;

    protected PageableData createPageableData(final int currentPage, final int pageSize, final String sort) {
        final SearchPageableData pageable = new SearchPageableData();
        pageable.setCurrentPage(currentPage);
        pageable.setPageSize(pageSize);
        pageable.setSort(sort);
        return pageable;
    }

    public boolean isCompanyAdminUser(B2BCustomerModel currentUser) {
        boolean isAdminUser = getUserService().isMemberOfGroup(currentUser, getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP));
        return (getCurrentSalesOrg().isAdminManagingSubUsers() && isAdminUser);
    }

    public boolean isCompanyInvoiceAdminUser() {
        final B2BCustomerModel currentUser = getCurrentUser();
        return isCompanyAdminUser(currentUser) || getCurrentSalesOrg().isInvoiceVisibleToAll() || currentUser.isShowAllOrderhistory();
    }

    public B2BCustomerModel getCurrentUser() {
        return (B2BCustomerModel) getUserService().getCurrentUser();
    }

    public boolean isCompanyOrderAdminUser() {
        final B2BCustomerModel currentUser = getCurrentUser();
        return isCompanyAdminUser(currentUser) || getCurrentSalesOrg().isOrderVisibleToAll() || currentUser.isShowAllOrderhistory();
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the specified data
     *
     * @param pageNumber
     *            the page number
     * @param pageSize
     *            the page size
     * @param sortCode
     *            the sort code
     * @param showMode
     *            the show mode
     * @param sortType
     * @return a new instance of {@code DistOrderHistoryPageableData}
     */
    protected DistOrderHistoryPageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode, final ShowMode showMode,
                                                              final String sortType) {

        final int actualPageSize = ShowMode.ALL == showMode ? DEFAULT_SEARCH_MAX_SIZE : pageSize;

        return new DistOrderHistoryPageableData(pageNumber, actualPageSize, sortCode, sortType);
    }

    public SalesOrgData getCurrentSalesOrg() {
        // Since the current salesOrg does not change, then it may be stored in
        // the session
        return getSessionService().getOrLoadAttribute("currentSalesOrg", () -> getStoreSessionFacade().getCurrentSalesOrg());
    }

    protected ModelService getModelService() {
        return modelService;
    }

    public CustomerData getUser() {
        return getB2bCustomerFacade().getCurrentCustomer();
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    protected DataMapper getDataMapper() {
        return dataMapper;
    }

    protected void setDataMapper(final DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    public DistCustomerFacade getB2bCustomerFacade() {
        return b2bCustomerFacade;
    }

    public void setB2bCustomerFacade(DistCustomerFacade b2bCustomerFacade) {
        this.b2bCustomerFacade = b2bCustomerFacade;
    }
}
