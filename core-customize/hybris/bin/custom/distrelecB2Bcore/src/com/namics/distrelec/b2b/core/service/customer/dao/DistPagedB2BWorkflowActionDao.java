/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.dao;

import java.util.List;

import de.hybris.platform.b2bacceleratorservices.dao.PagedB2BWorkflowActionDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;

/**
 * Customized version of {@link PagedB2BWorkflowActionDao}.
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec CH
 * @since Namics Extensions 1.0
 * 
 */
public interface DistPagedB2BWorkflowActionDao extends PagedB2BWorkflowActionDao {

    /**
     * Look for the {@code WorkflowActionModel}s assigned to the specified user, having one of the specified {@code WorkflowActionType}s and
     * having one of the specified {@code WorkflowActionStatus}s
     * 
     * @param user
     *            the user
     * @param actionTypes
     *            the list of workflow actions
     * @param actionStatus
     *            the list of the Workflow action states
     * @param pageableData
     * @return an instance of {@code SearchPageData} containing the fetched results
     */
    SearchPageData<WorkflowActionModel> findPagedWorkflowActionsByUserAndActionTypes(UserModel user, WorkflowActionType[] actionTypes,
            WorkflowActionStatus[] actionStatus, PageableData pageableData);

    /**
     * Look for the {@code WorkflowActionModel}s assigned to the specified user, having one of the specified {@code WorkflowActionType}s and
     * having one of the specified {@code WorkflowActionStatus}s
     * 
     * @param user
     *            the user
     * @param actionTypes
     *            the list of workflow actions
     * @param actionStatus
     *            the list of the Workflow action states
     * @return a list of {@code WorkflowActionModel}.
     */
    List<WorkflowActionModel> findWorkflowActionsByUserAndActionTypes(UserModel user, WorkflowActionType[] actionTypes, WorkflowActionStatus[] actionStatus);

    /**
     * Find the {@code WorkflowActionModel}s started by the specified user, having one of the specified {@code WorkflowActionType}s and
     * having one of the specified {@code WorkflowActionStatus}s
     * 
     * @param user
     *            the user
     * @param actionTypes
     *            the list of workflow actions
     * @param actionStatus
     *            the list of the Workflow action states
     * @param pageableData
     * @return an instance of {@code SearchPageData} containing the fetched results
     */
    SearchPageData<WorkflowActionModel> findPagedWorkflowActionsForSubUser(UserModel user, WorkflowActionType[] actionTypes,
            WorkflowActionStatus[] actionStatus, PageableData pageableData);

    /**
     * Find the {@code WorkflowActionModel}s started by the specified user, having one of the specified {@code WorkflowActionType}s and
     * having one of the specified {@code WorkflowActionStatus}s
     * 
     * @param user
     *            the user
     * @param actionTypes
     *            the list of workflow actions
     * @param actionStatus
     *            the list of the Workflow action states
     * @return an instance of {@code SearchPageData} containing the fetched results
     */
    List<WorkflowActionModel> findWorkflowActionsForSubUser(UserModel user, WorkflowActionType[] actionTypes, WorkflowActionStatus[] actionStatus);


    /**
     * Count the number of the order approval requests assigned to the specified user with the specified workflow action types and workflow
     * action status.
     * 
     * @param user
     *            the user
     * @param actionTypes
     *            the list of action types
     * @param actionStatus
     *            the list of action status
     * @return the number of order approval requests
     */
    int getApprovalRequestsCountForApprover(UserModel user, WorkflowActionType[] actionTypes, WorkflowActionStatus[] actionStatus);

    /**
     * Count the number of the order approval requests started by the specified user with the specified workflow action types and workflow
     * action status.
     * 
     * @param user
     *            the user
     * @param actionTypes
     *            the list of action types
     * @param actionStatus
     *            the list of action status
     * @return the number of order approval requests
     */
    int getApprovalRequestsCountForSubUser(UserModel user, WorkflowActionType[] actionTypes, WorkflowActionStatus[] actionStatus);
}
