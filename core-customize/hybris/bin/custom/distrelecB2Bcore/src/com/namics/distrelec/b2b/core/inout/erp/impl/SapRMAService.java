/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.SearchRmasRequest;
import com.distrelec.webservice.sap.v1.SearchRmasResponse;
import com.namics.distrelec.b2b.core.inout.erp.RMAService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.core.model.DistRMAReasonModel;
import com.namics.distrelec.b2b.core.rma.ReturnRequestData;
import com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * {@code SapRMAService}
 * <p>
 * RMA service implementation for the SAP case.
 * </p>
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class SapRMAService implements RMAService {

    private static final Logger LOG = LogManager.getLogger(SapRMAService.class);

    @Autowired
    private UserService userService;
    private DistrelecCodelistService distrelecCodelistService;
    private SIHybrisV1Out webServiceClient;
    @Autowired
    private SapReturnRequestDao sapReturnRequestDao;

    @Override
    public String createReturnRequest(final ReturnRequestData returnRequestData) {
        return getSapReturnRequestDao().createReturnRequest((CustomerModel) getUserService().getCurrentUser(), returnRequestData);
    }

    @Override
    public List<String> createReturnRequests(final List<ReturnRequestData> returnRequestDatas) {
        return getSapReturnRequestDao().createReturnRequests((CustomerModel) userService.getCurrentUser(), returnRequestDatas);
    }

    @Override
    public List<Object> getReturnRequests() {
        return getSapReturnRequestDao().getReturnRequests((CustomerModel) userService.getCurrentUser());
    }

    @Override
    public List<Object> getReturnRequestsByOrder(final OrderModel order) {
        return getSapReturnRequestDao().getReturnRequestsByOrder((CustomerModel) userService.getCurrentUser(), order);
    }

    @Override
    public Object getReturnRequest(final String rmaCode) {
        return getSapReturnRequestDao().getReturnRequest((CustomerModel) userService.getCurrentUser(), rmaCode);
    }

    @Override
    public List<Object> getReturnRequestsFromDate(final Date fromDate) {
        return getSapReturnRequestDao().getReturnRequestsFromDate(fromDate);
    }

    @Override
    public List<DistRMAReasonModel> getAllReturnRequestReason() {
        return getDistrelecCodelistService().getAllDistRMAReasons();
    }

    @Override
    public DistCodelistModel getReturnRequestReasonForCode(String returnRequestReason) {
        return getDistrelecCodelistService().getDistRMAReason(returnRequestReason);
    }

    @Override
    public List<DistCodelistModel> getAllReturnRequestPackaging() {
        // throw new UnsupportedOperationException("Method not supported by SAP");
        return null;
    }

    @Override
    public DistCodelistModel getReturnRequestPackagingForCode(String returnRequestPackaging) {
        // throw new UnsupportedOperationException("Method not supported by SAP");
        return null;
    }

    protected SearchRmasRequest buildSearchRmasRequest(final String rmaCode, final Date fromDate, final Date toDate, final String rmaStatus,
            final String productCode, final String sortCriteria, final boolean sortAscending, final int pageSize, final int offset) {

        final SearchRmasRequest result = new SearchRmasRequest();
        result.setCustomerId(getUserService().getCurrentUser().getUid());
        result.setFilterRmaId(rmaCode);
        result.setFilterRmaRequestDateStart(SoapConversionHelper.convertDate(fromDate));
        result.setFilterRmaRequestDateEnd(SoapConversionHelper.convertDate(toDate));
        result.setFilterRmaStatus(rmaStatus);
        result.setFilterArticleNumber(productCode);

        // pagination data
        result.setSortAscending(sortAscending);
        result.setSortCriteria(sortCriteria);
        result.setResultSize(BigInteger.valueOf(pageSize));
        result.setResultOffset(BigInteger.valueOf(offset));

        return result;
    }

    protected SearchRmasResponse executeSearchRmaSOAPRequest(final SearchRmasRequest request) {
        SearchRmasResponse searchRmaResponse = null;
        final long startTime = new Date().getTime();
        try {
            searchRmaResponse = webServiceClient.if17SearchRmas(request);
        } catch (P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if17SearchRma", faultMessage);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if17SearchRma", wsEx);
        }
        final long endTime = new Date().getTime();
        LOG.debug("Call to SAP PI IF-17 if17SearchRma took " + (endTime - startTime) + "ms");
        return searchRmaResponse;
    }

    // spring

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public DistrelecCodelistService getDistrelecCodelistService() {
        return distrelecCodelistService;
    }

    public void setDistrelecCodelistService(final DistrelecCodelistService distrelecCodelistService) {
        this.distrelecCodelistService = distrelecCodelistService;
    }

    public SapReturnRequestDao getSapReturnRequestDao() {
        return sapReturnRequestDao;
    }

    public void setSapReturnRequestDao(SapReturnRequestDao sapReturnRequestDao) {
        this.sapReturnRequestDao = sapReturnRequestDao;
    }
}
