package com.namics.distrelec.b2b.facades.order.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderHistoryEntryData;
import de.hybris.platform.b2bacceleratorfacades.order.populators.B2BApprovalDataPopulator;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

public class DistApprovalDataPopulator extends B2BApprovalDataPopulator {

    private static final String BLANK_STRING = "";

    protected B2BOrderApprovalData createTarget() {
        return new B2BOrderApprovalData();
    }

    @Resource
    I18NService i18NService;

    @Override
    public void populate(final WorkflowActionModel source, final B2BOrderApprovalData target) {

        final OrderModel orderModel = getB2bWorkflowIntegrationService().getOrderFromAction(source);
        target.setWorkflowActionModelCode(source.getCode());
        target.setB2bOrderData(getOrderConverter().convert(orderModel));

        target.setAllDecisions(
                               new ArrayList<String>(CollectionUtils.collect(source.getDecisions(),
                                                                             new BeanToPropertyValueTransformer(WorkflowDecisionModel.QUALIFIER) {
                                                                                 @Override
                                                                                 public Object transform(final Object object) {
                                                                                     final Object original = super.transform(object);
                                                                                     if (original instanceof String) {
                                                                                         return ((String) super.transform(object)).toUpperCase();
                                                                                     } else {
                                                                                         return original;
                                                                                     }
                                                                                 }
                                                                             })));

        if (source.getSelectedDecision() != null) {
            target.setSelectedDecision(source.getSelectedDecision().getName());
        }
        target.setApprovalComments(source.getComment());
        if (WorkflowActionStatus.IN_PROGRESS.equals(source.getStatus())) {
            target.setApprovalDecisionRequired(true);
        }

        final List<B2BOrderHistoryEntryData> orderHistoryEntriesData = Converters.convertAll(orderModel.getHistoryEntries(),
                                                                                             getB2bOrderHistoryEntryConverter());
        // TODO:Add the QUOTE and MERCHANT keywords in enum as a dictionary for filtering
        target.setQuotesApprovalHistoryEntriesData(filterOrderHistoryEntriesForApprovalStage(orderHistoryEntriesData, "QUOTE"));
        target.setMerchantApprovalHistoryEntriesData(filterOrderHistoryEntriesForApprovalStage(orderHistoryEntriesData, "MERCHANT"));
        target.setOrderHistoryEntriesData(orderHistoryEntriesData);

        final PrincipalModel principalAssigned = source.getPrincipalAssigned();
        final Collection<B2BPermissionResultModel> b2bPermissionResults = orderModel.getPermissionResults();
        for (final B2BPermissionResultModel b2bPermissionResultModel : b2bPermissionResults) {
            if (b2bPermissionResultModel.getApprover().getUid().equals(principalAssigned.getUid())) {

                target.setApprovalComments(StringUtils.isNotBlank(b2bPermissionResultModel.getNote()) ? b2bPermissionResultModel.getNote()
                                                                                                      : getNoteForsupportedLocal(b2bPermissionResultModel));
            }
        }

        if (source.getStatus() != null) {
            target.setStatus(source.getStatus().getCode());
        }

        if (source.getModifiedtime() != null) {
            target.setDecisionDate(source.getModifiedtime());
        }
    }

    private String getNoteForsupportedLocal(final B2BPermissionResultModel b2bPermissionResultModel) {
        for (final Locale locale : i18NService.getSupportedLocales()) {
            if (StringUtils.isNotBlank(b2bPermissionResultModel.getNote(locale))) {
                return b2bPermissionResultModel.getNote(locale);
            }
        }

        return BLANK_STRING;
    }
}
