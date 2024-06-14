/*
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
package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Pojo for 'order approval decision' form.
 */
public class OrderApprovalDecisionForm {

    private String workFlowActionCode;
    private String approverSelectedDecision;
    private String comments;
    private String termsAndConditions;

    /**
     * Create a new instance of {@code OrderApprovalDecisionForm}
     */
    public OrderApprovalDecisionForm() {
        super();
    }

    /**
     * Create a new instance of {@code OrderApprovalDecisionForm}
     * 
     * @param workFlowActionCode
     * @param approverSelectedDecision
     * @param comments
     * @param termsAndConditions
     */
    public OrderApprovalDecisionForm(final String workFlowActionCode, final String approverSelectedDecision, final String comments,
            final String termsAndConditions) {
        this.workFlowActionCode = workFlowActionCode;
        this.approverSelectedDecision = approverSelectedDecision;
        this.comments = comments;
        this.termsAndConditions = termsAndConditions;
    }

    @NotBlank(message = "{form.approval.workflowcode.invalid}")
    public String getWorkFlowActionCode() {
        return workFlowActionCode;
    }

    public void setWorkFlowActionCode(final String workFlowActionCode) {
        this.workFlowActionCode = workFlowActionCode;
    }

    public String getApproverSelectedDecision() {
        return approverSelectedDecision;
    }

    public void setApproverSelectedDecision(final String approverSelectedDecision) {
        this.approverSelectedDecision = approverSelectedDecision;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(final String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
