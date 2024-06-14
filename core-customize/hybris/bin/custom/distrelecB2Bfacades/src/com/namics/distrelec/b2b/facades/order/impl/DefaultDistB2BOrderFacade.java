/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.event.AbstractDistrelecCustomerEvent;
import com.namics.distrelec.b2b.core.event.OrderCancellationEvent;
import com.namics.distrelec.b2b.core.event.SapVoucherEvent;
import com.namics.distrelec.b2b.core.service.customer.dao.DistPagedB2BWorkflowActionDao;
import com.namics.distrelec.b2b.core.service.order.DistOrderService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.DistCancelledOrderPrepayment;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BOrderFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;

public class DefaultDistB2BOrderFacade extends DefaultB2BOrderFacade implements DistB2BOrderFacade {
    
    private static final Logger LOG = LogManager.getLogger(DefaultDistB2BOrderFacade.class);
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private BaseSiteService baseSiteService;
    
    @Autowired
    private DistOrderService distOrderService;
    
    @Autowired
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
    
    @Autowired
    private ConfigurationService configurationService;
	
    @Autowired
    private CommonI18NService commonI18NService;

    @Override
    public SearchPageData<B2BOrderApprovalData> getPagedOrdersForApproval(final WorkflowActionType[] actionTypes, final WorkflowActionStatus[] status,
                                                                          final PageableData pageableData) {
        
        final SearchPageData<WorkflowActionModel> actions;
        
        final UserModel currentUser = getUserService().getCurrentUser();
        if (getUserService().isMemberOfGroup(currentUser, getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP))) {
            actions = getDistPagedB2BWorkflowActionDao().findPagedWorkflowActionsByUserAndActionTypes(currentUser, actionTypes, status, pageableData);
            final SearchPageData<B2BOrderApprovalData> searchPageData = convertPageData(actions, getB2bOrderApprovalDataConverter());
            boolean ASC = false;
            if (pageableData instanceof DistOrderHistoryPageableData) {
                final DistOrderHistoryPageableData distPageableData = (DistOrderHistoryPageableData) pageableData;
                ASC = "ASC".equalsIgnoreCase(distPageableData.getSortType());
            }
            
            Collections.sort(searchPageData.getResults(), getComparator(pageableData.getSort(), ASC));
            
            return searchPageData;
        } else {
            actions = getDistPagedB2BWorkflowActionDao().findPagedWorkflowActionsForSubUser(currentUser, actionTypes, status, pageableData);
            return convertPageData(actions, getB2bOrderApprovalDataConverter());
        }
    }

    @Override
    protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter) {
        final SearchPageData<T> result = new SearchPageData<>();
        result.setPagination(createPaginationData(source.getPagination()));
        result.setSorts(source.getSorts());
        result.setResults(Converters.convertAll(source.getResults(), converter));
        return result;
    }
    
    private DistPaginationData createPaginationData(final PaginationData paginationData) {
        final DistPaginationData distPaginationData = new DistPaginationData();
        BeanUtils.copyProperties(paginationData, distPaginationData);
        return distPaginationData;
    }

    @Override
    public void setCustomerClientID(final HttpServletRequest request, final String cartCode) {
        distOrderService.setCustomerClientID(request, cartCode);
    }
    
    @Override
    public boolean isNumberOfGuestSuccessfulPurchasesExceeded(final String email) {
        final int maxPurchasesNumber = configurationService.getConfiguration().getInt("distrelec.guest.maxPurchases.number", 3);
        return distOrderService.findAllOrdersForGivenUserEmail(email).size() >= maxPurchasesNumber;
    }
    
    @Override
    public void sendOrderCancellationPrepaymentMail(DistCancelledOrderPrepayment cancelledOrderPrepayment) {
        getEventService().publishEvent(initializeOrderCancellationEvent(new OrderCancellationEvent(), cancelledOrderPrepayment));
    }

    private AbstractEvent initializeOrderCancellationEvent(final OrderCancellationEvent orderCancellationEvent,
                                                           final DistCancelledOrderPrepayment cancelledOrderPrepayment) {
        orderCancellationEvent.setBaseStore(getBaseStoreService().getCurrentBaseStore());
        orderCancellationEvent.setSite(getBaseSiteService().getCurrentBaseSite());
        final CustomerModel customerModel = getUserService().getUserForUID(cancelledOrderPrepayment.getUid(), CustomerModel.class);
        orderCancellationEvent.setCustomer(customerModel);
        orderCancellationEvent.setLanguage(commonI18NService.getCurrentLanguage());
        orderCancellationEvent.setCurrency(commonI18NService.getCurrentCurrency());
        orderCancellationEvent.setOrderNumber(cancelledOrderPrepayment.getOrderNumber());
        orderCancellationEvent.setArticleNumbers(cancelledOrderPrepayment.getArticleNumbers());
        orderCancellationEvent.setProductNames(cancelledOrderPrepayment.getProductNames());
        return orderCancellationEvent;
    }

    @Override
    public int getApprovalRequestsCount() {
        
        final UserModel user = getUserService().getCurrentUser();
        
        if (getUserService().isAnonymousUser(user)) {
            return 0;
        }
        final WorkflowActionType[] wfActionTypes = new WorkflowActionType[] { WorkflowActionType.START };
        final WorkflowActionStatus[] wfActionStatus;
        final List<WorkflowActionStatus> workflowActionStatusList = new ArrayList<>(Arrays.asList(WorkflowActionStatus.values()));
        
        if (getUserService().isMemberOfGroup(user, getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP))) {
            workflowActionStatusList.remove(WorkflowActionStatus.COMPLETED);
            wfActionStatus = workflowActionStatusList.toArray(new WorkflowActionStatus[workflowActionStatusList.size()]);

            return getDistPagedB2BWorkflowActionDao().getApprovalRequestsCountForApprover(user, wfActionTypes, wfActionStatus);
        } else {
            wfActionStatus = new WorkflowActionStatus[] { WorkflowActionStatus.ENDED_THROUGH_END_OF_WORKFLOW, WorkflowActionStatus.IN_PROGRESS,
                                                          WorkflowActionStatus.PENDING };
            return getDistPagedB2BWorkflowActionDao().getApprovalRequestsCountForSubUser(user, wfActionTypes, wfActionStatus);
        }
    }
    
    @Override
    public B2BOrderApprovalData getOrderApprovalDetailsForCode(final String code) {
        final WorkflowActionModel workflowActionModel = getB2bWorkflowIntegrationService().getActionForCode(code);
        if (workflowActionModel == null) {
            throw new UnknownIdentifierException("Can not find workflow action for code " + code);
        }
        return getB2bOrderApprovalDataConverter().convert(workflowActionModel);
    }
    
    @Override
    public void sendVoucherEmail(final String code, final String pattern) {
        try {
            final OrderModel orderModel = getB2bOrderService().getOrderForCode(code);
            if (orderModel == null || orderModel.getGeneratedVoucher() == null) {
                LOG.warn("Either the target order is null or it does not have any generated Sap Voucher!");
                return;
            }
            final SapVoucherEvent event = new SapVoucherEvent(orderModel, pattern);
            setUpAdditionalEventData(event);
            event.setCustomer((CustomerModel) orderModel.getUser());
            getEventService().publishEvent(event);
        } catch (final Exception exp) {
            DistLogUtils.logError(LOG, "{} {} Unknown error while sending the voucher email for Order: {} \n{}", exp, ErrorLogCode.EMAIL_ERROR,
                                  ErrorSource.HYBRIS, code,
                                  exp.getMessage());
        }
    }
    
    @Override
    public OrderModel getOrderModel(final String code) {
        OrderModel order;
        if (getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            order = getCustomerAccountService().getOrderDetailsForGUID(code, getBaseStoreService().getCurrentBaseStore());
        } else {
            order = getB2bOrderService().getOrderForCode(code);
        }
        if (order == null) {
            throw new UnknownIdentifierException("Order with code " + code + " not found for current user in current BaseStore");
        }
        return order;
    }
    
    protected void setUpAdditionalEventData(final AbstractDistrelecCustomerEvent<BaseSiteModel> event) {
        event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
        event.setSite(getBaseSiteService().getCurrentBaseSite());
        event.setCustomer((CustomerModel) getUserService().getCurrentUser());
    }
    
    protected Comparator<B2BOrderApprovalData> getComparator(final String sortCode, final boolean ASC) {
        if ("byTotalPrice".equalsIgnoreCase(sortCode)) {
            return new Comparator<>() {
                
                @Override
                public int compare(final B2BOrderApprovalData obj1, final B2BOrderApprovalData obj2) {
                    final int result = obj1.getB2bOrderData().getTotalPrice().getValue().compareTo(obj2.getB2bOrderData().getTotalPrice().getValue());
                    return ASC ? result : -result;
                }
            };
        } else if ("byStatus".equalsIgnoreCase(sortCode)) {
            return new Comparator<>() {
                
                @Override
                public int compare(final B2BOrderApprovalData obj1, final B2BOrderApprovalData obj2) {
                    final int result = obj1.getB2bOrderData().getStatus().getCode().compareTo(obj2.getB2bOrderData().getStatus().getCode());
                    return ASC ? result : -result;
                }
            };
        } else {
            return new Comparator<>() {
                
                @Override
                public int compare(final B2BOrderApprovalData obj1, final B2BOrderApprovalData obj2) {
                    final int result = obj1.getB2bOrderData().getCreated().compareTo(obj2.getB2bOrderData().getCreated());
                    return ASC ? result : -result;
                }
            };
        }
    }

    @Override
    public OrderData getOrderDetailsForCode(final String orderCode) {
        if (!getCheckoutCustomerStrategy().isAnonymousCheckout()) {
            final B2BCustomerModel currentMasterUser = (B2BCustomerModel) getUserService().getCurrentUser();
            final B2BUnitModel b2bUnit = b2bUnitService.getParent(currentMasterUser);
            if (b2bUnit != null && b2bUnit.getSalesOrg() != null && b2bUnit.getSalesOrg().isAdminManagingSubUsers()
                    && CollectionUtils.isNotEmpty(b2bUnit.getMembers())) {
                return getOrderDetailsForCodeWithSubUsers(orderCode, b2bUnit.getMembers());
            }
        }
        return super.getOrderDetailsForCode(orderCode);
    }
    
    @Override
    public OrderData getOrderDetailsForCodeWithSubUsers(final String orderCode, final Set<PrincipalModel> members) {
        final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
        final List<UserModel> b2bUsersList = new ArrayList<>();
        for (final PrincipalModel principalModel : members) {
            b2bUsersList.add((UserModel) principalModel);
        }
        final OrderModel orderModel = distOrderService.getOrderForCodeWithSubUsers(orderCode, baseStoreModel,
                                                                                   b2bUsersList);
        if (orderModel == null) {
            throw new UnknownIdentifierException(
                                                 "Order with orderGUID " + orderCode + " not found in current BaseStore");
        }
        return getOrderConverter().convert(orderModel);
        
    }
    
    public DistPagedB2BWorkflowActionDao getDistPagedB2BWorkflowActionDao() {
        return (DistPagedB2BWorkflowActionDao) getPagedB2BWorkflowActionDao();
    }
    
    public EventService getEventService() {
        return eventService;
    }
    
    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }
    
    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }
    
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }
}
