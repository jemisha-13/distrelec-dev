/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.dao.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;

import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2bacceleratorservices.dao.impl.DefaultPagedB2BWorkflowActionDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

/**
 * Customized implementation of {@link de.hybris.platform.b2bacceleratorservices.dao.impl.DefaultPagedB2BWorkflowActionDao}.
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec CH
 * @since Namics Extensions 1.0
 * 
 */
public class DefaultDistPagedB2BWorkflowActionDao extends DefaultPagedB2BWorkflowActionDao implements DistPagedB2BWorkflowActionDao {

    // Admin queries
    private static final String SORT_BY_DATE = " ORDER BY {" + WorkflowActionModel.CREATIONTIME + "} DESC, {" + WorkflowActionModel.PK + "}";

    private static final String SORT_BY_CODE = " ORDER BY {" + WorkflowActionModel.CODE + "}, {" + WorkflowActionModel.CREATIONTIME + "} DESC, {"
            + WorkflowActionModel.PK + "}";

    private static final String FIND_WORKFLOW_ACTIONS_BY_USER_QUERY = "SELECT  {" + WorkflowActionModel.PK + "} FROM {" + WorkflowActionModel._TYPECODE
            + "} WHERE { " + WorkflowActionModel.PRINCIPALASSIGNED + "} IN (?principalAssigned)" + " AND {" + WorkflowActionModel.ACTIONTYPE
            + " } IN (?actionType) AND {" + WorkflowActionModel.STATUS + "} IN (?status)";

    // Sub-user queries
    private static final String FIND_WORKFLOWS_FROM_ORDERS_BY_USER = "SELECT {" + OrderModel.WORKFLOW + "} FROM {" + OrderModel._TYPECODE + "} WHERE {"
            + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.ORIGINALVERSION + "} IS NULL AND {" + OrderModel.WORKFLOW + "} IS NOT NULL AND {"
            + OrderModel.USER + "} =?" + OrderModel.USER;

    private static final String ORDER_BY_DATE_SUB = " ORDER BY {" + OrderModel.CREATIONTIME + "} ";
    private static final String ORDER_BY_STATUS_SUB = " ORDER BY {" + OrderModel.STATUS + "} ";
    private static final String ORDER_BY_TOTALPRICE_SUB = " ORDER BY {" + OrderModel.TOTALPRICE + "} ";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    /**
     * Create a new instance of {@code DefaultDistPagedB2BWorkflowActionDao}
     * 
     * @param code
     *            the DAO code
     */
    public DefaultDistPagedB2BWorkflowActionDao(final String code) {
        super(code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao#findPagedWorkflowActionsByUserAndActionTypes(de.
     * hybris.platform.core.model.user.UserModel, de.hybris.platform.workflow.enums.WorkflowActionType[],
     * de.hybris.platform.workflow.enums.WorkflowActionStatus[], de.hybris.platform.commerceservices.search.pagedata.PageableData)
     */
    @Override
    public SearchPageData<WorkflowActionModel> findPagedWorkflowActionsByUserAndActionTypes(final UserModel user, final WorkflowActionType[] actionTypes,
            final WorkflowActionStatus[] actionStatus, final PageableData pageableData) {
        final Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(WorkflowActionModel.PRINCIPALASSIGNED, user);
        queryParams.put(WorkflowActionModel.ACTIONTYPE, Arrays.asList(actionTypes));
        queryParams.put(WorkflowActionModel.STATUS, Arrays.asList(actionStatus));

        final List<SortQueryData> sortQueries = Arrays.asList(createSortQueryData("byDate", FIND_WORKFLOW_ACTIONS_BY_USER_QUERY + SORT_BY_DATE),
                createSortQueryData("byOrderNumber", FIND_WORKFLOW_ACTIONS_BY_USER_QUERY + SORT_BY_CODE));

        final SearchPageData<WorkflowActionModel> searchPageData = getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
        return getSearchPageResult(searchPageData, pageableData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao#findWorkflowActionsByUserAndActionTypes(de.hybris.
     * platform.core.model.user.UserModel, de.hybris.platform.workflow.enums.WorkflowActionType[],
     * de.hybris.platform.workflow.enums.WorkflowActionStatus[])
     */
    @Override
    public List<WorkflowActionModel> findWorkflowActionsByUserAndActionTypes(final UserModel user, final WorkflowActionType[] actionTypes,
            final WorkflowActionStatus[] actionStatus) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_WORKFLOW_ACTIONS_BY_USER_QUERY);
        searchQuery.addQueryParameter(WorkflowActionModel.PRINCIPALASSIGNED, user);
        searchQuery.addQueryParameter(WorkflowActionModel.ACTIONTYPE, Arrays.asList(actionTypes));
        searchQuery.addQueryParameter(WorkflowActionModel.STATUS, Arrays.asList(actionStatus));
        return getFlexibleSearchService().<WorkflowActionModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao#findPagedWorkflowActionsForSubUser(de.hybris.
     * platform.core.model.user.UserModel, de.hybris.platform.workflow.enums.WorkflowActionType[],
     * de.hybris.platform.workflow.enums.WorkflowActionStatus[], de.hybris.platform.commerceservices.search.pagedata.PageableData)
     */
    @Override
    public SearchPageData<WorkflowActionModel> findPagedWorkflowActionsForSubUser(final UserModel user, final WorkflowActionType[] actionTypes,
            final WorkflowActionStatus[] actionStatus, final PageableData pageableData) {
        final SearchPageData<WorkflowActionModel> searchResult = new SearchPageData<WorkflowActionModel>();
        searchResult.setResults(doSearchForSubUser(user, actionTypes, actionStatus, pageableData));
        searchResult.setPagination(createPaginationData(searchResult.getResults(), pageableData));
        return searchResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao#findWorkflowActionsForSubUser(de.hybris.platform.
     * core.model.user.UserModel, de.hybris.platform.workflow.enums.WorkflowActionType[],
     * de.hybris.platform.workflow.enums.WorkflowActionStatus[])
     */
    @Override
    public List<WorkflowActionModel> findWorkflowActionsForSubUser(final UserModel user, final WorkflowActionType[] actionTypes,
            final WorkflowActionStatus[] actionStatus) {
        return doSearchForSubUser(user, actionTypes, actionStatus, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao#getApprovalRequestsCountForApprover(de.hybris.
     * platform.core.model.user.UserModel, de.hybris.platform.workflow.enums.WorkflowActionType[],
     * de.hybris.platform.workflow.enums.WorkflowActionStatus[])
     */
    @Override
    public int getApprovalRequestsCountForApprover(final UserModel user, final WorkflowActionType[] actionTypes, final WorkflowActionStatus[] actionStatus) {
        final List<WorkflowActionModel> results = findWorkflowActionsByUserAndActionTypes(user, actionTypes, actionStatus);
        return getFilteredResults(results).size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao#getApprovalRequestsCountForSubUser(de.hybris.
     * platform.core.model.user.UserModel, de.hybris.platform.workflow.enums.WorkflowActionType[],
     * de.hybris.platform.workflow.enums.WorkflowActionStatus[])
     */
    @Override
    public int getApprovalRequestsCountForSubUser(final UserModel user, final WorkflowActionType[] actionTypes, final WorkflowActionStatus[] actionStatus) {
        return findWorkflowActionsForSubUser(user, actionTypes, actionStatus).size();
    }

    /**
     * Retrieve the list of workflow actions started by the specified sub-user
     * 
     * @param user
     *            the sub-user
     * @param actionTypes
     *            the workflow action types
     * @param actionStatus
     *            the workflow action statuses
     * @param pageableData
     *            the pagination informations
     * @return a list of workflow actions
     */
    private List<WorkflowActionModel> doSearchForSubUser(final UserModel user, final WorkflowActionType[] actionTypes,
            final WorkflowActionStatus[] actionStatus, final PageableData pageableData) {
        final SearchResult<WorkflowModel> searchResult = getFlexibleSearchService().search(createSubUserQuery(user, pageableData));
        final List<WorkflowActionModel> workflowActions = searchResult.getResult().stream() //
                .map(workflow -> getWorkflowActionFromWorkflow(workflow, Arrays.asList(actionTypes), Arrays.asList(actionStatus))) //
                .filter(workflowAction -> workflowAction != null) //
                .filter(workflowAction -> CollectionUtils.find(workflowAction.getAttachmentItems(), PredicateUtils.instanceofPredicate(B2BApprovalProcessModel.class)) != null) //
                .collect(Collectors.toList());

        return workflowActions;
    }

    /**
     * Retrieve the {@link WorkflowActionModel} with the specified action type and status from the {@link WorkflowModel} . A
     * {@link WorkflowActionModel} is considered as valid if its status if there exist no {@link WorkflowActionModel} with name
     * {@literal APPROVED} and status {@literal COMPLETED}.
     * 
     * @param workflow
     *            the worflow
     * @param workflowActionTypes
     *            the workflow action types
     * @param workflowActionStatus
     *            the workflow action statuses
     * @return the {@link WorkflowActionModel} that belongs to the specified {@link WorkflowModel} and having the right action type anda
     *         status.
     */
    private WorkflowActionModel getWorkflowActionFromWorkflow(final WorkflowModel workflow, final List<WorkflowActionType> workflowActionTypes,
            final List<WorkflowActionStatus> workflowActionStatus) {
        if (workflow == null) {
            return null;
        }
        WorkflowActionModel workflowAction = null;

        for (final WorkflowActionModel wfam : workflow.getActions()) {
            if (workflowActionTypes.contains(wfam.getActionType()) && workflowActionStatus.contains(wfam.getStatus())) {
                workflowAction = wfam;
            }
            if ("APPROVED".equalsIgnoreCase(wfam.getName()) && wfam.getStatus() == WorkflowActionStatus.COMPLETED) {
                return null;
            }
        }

        return workflowAction;
    }

    /**
     * Create the query used for retrieving the list of workflow action list started by a sub-user
     * 
     * @param pageableData
     *            informations about sorting, pagination, etc.
     * @return the query String
     */
    private FlexibleSearchQuery createSubUserQuery(final UserModel user, final PageableData pageableData) {
        final StringBuilder queryBuilder = new StringBuilder(FIND_WORKFLOWS_FROM_ORDERS_BY_USER);

        if (pageableData instanceof DistOrderHistoryPageableData) {
            final DistOrderHistoryPageableData distPageableData = (DistOrderHistoryPageableData) pageableData;
            if ("byTotalPrice".equalsIgnoreCase(distPageableData.getSort())) {
                queryBuilder.append(ORDER_BY_TOTALPRICE_SUB);
            } else if ("byStatus".equalsIgnoreCase(distPageableData.getSort())) {
                queryBuilder.append(ORDER_BY_STATUS_SUB);
            } else {
                queryBuilder.append(ORDER_BY_DATE_SUB);
            }

            if ("asc".equalsIgnoreCase(distPageableData.getSortType()) || "desc".equalsIgnoreCase(distPageableData.getSortType())) {
                queryBuilder.append(distPageableData.getSortType());
            } else {
                queryBuilder.append("DESC");
            }
        }

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
        query.addQueryParameter(OrderModel.USER, user);
        query.setNeedTotal(true);

        return query;
    }
    
    private SearchPageData<WorkflowActionModel> getSearchPageResult(final SearchPageData<WorkflowActionModel> searchPageData, final PageableData pageableData) {
        final SearchPageData<WorkflowActionModel> searchResult = new SearchPageData<WorkflowActionModel>();
        searchResult.setResults(getFilteredResults(searchPageData.getResults()));
        searchResult.setPagination(createPaginationData(searchResult.getResults(), pageableData));
        return searchResult;
    }

    private List<WorkflowActionModel> getFilteredResults(final List<WorkflowActionModel> results) {
        return results.stream()
                      .filter(res -> CollectionUtils.find(res.getAttachmentItems(), PredicateUtils.instanceofPredicate(B2BApprovalProcessModel.class)) != null)
                      .collect(Collectors.toList());
    }

    private PaginationData createPaginationData(final List<WorkflowActionModel> workflowActions, final PageableData pageableData) {
        final PaginationData pagination = new PaginationData();
        pagination.setTotalNumberOfResults(workflowActions.size());
        pagination.setCurrentPage(pageableData.getCurrentPage());
        pagination.setPageSize(pageableData.getPageSize());
        pagination.setSort(pageableData.getSort());
        pagination.setNumberOfPages((int) Math.ceil((double) workflowActions.size() / pageableData.getPageSize()));

        return pagination;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
