package com.namics.distrelec.b2b.core.missingorders.service.impl;

import com.distrelec.webservice.if15.v1.*;
import com.google.common.base.Preconditions;
import com.namics.distrelec.b2b.core.inout.erp.CheckoutService;
import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.missingorders.dao.DistMissingOrdersDao;
import com.namics.distrelec.b2b.core.missingorders.service.CreateMissingOrdersResult;
import com.namics.distrelec.b2b.core.missingorders.service.DistMissingOrdersService;
import com.namics.distrelec.b2b.core.missingorders.service.MissingOrdersMatchResult;
import com.namics.distrelec.b2b.core.model.process.DistSapMissingOrdersReportEmailProcessModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.Instant;
import java.util.*;

public class DefaultDistMissingOrdersService implements DistMissingOrdersService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistMissingOrdersService.class);

    @Autowired
    DistMissingOrdersDao distMissingOrdersDao;

    @Autowired
    private OrderService sapOrderService;

    @Autowired
    @Qualifier("erp.checkoutService")
    private CheckoutService checkoutService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private BusinessProcessService businessProcessService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private UserService userService;

    @Override
    public MissingOrdersMatchResult matchMissingOrders(Integer numberOfDays, boolean fetchOrdersByDays, Date orderFromDate, Date orderToDate) {
        List<OrderModel> missingOrders = null;

        if(fetchOrdersByDays){
            Preconditions.checkArgument(numberOfDays != null, "numberOfDays must not be null when fetchOrderByDays is true");
            missingOrders = distMissingOrdersDao.findMissingOrders(numberOfDays);
        }else{
            Preconditions.checkArgument(orderFromDate != null, "orderFromDate must not be null when fetchOrderByDays is false");
            Preconditions.checkArgument(orderToDate != null, "orderToDate must not be null when fetchOrderByDays is false");
            missingOrders = distMissingOrdersDao.findMissingOrders(orderFromDate, orderToDate);
        }

        List<OrderModel> matchedOrdersInErp = new ArrayList<>();
        List<OrderModel> missingOrdersInErp = new ArrayList<>();

        for(OrderModel missingOrder : missingOrders){
            if(matchOrderInErp(missingOrder)){
                matchedOrdersInErp.add(missingOrder);
            }else{
                missingOrdersInErp.add(missingOrder);
            }
        }

        return new MissingOrdersMatchResult(matchedOrdersInErp, missingOrdersInErp);
    }

    private boolean matchOrderInErp(OrderModel order) {
        final B2BCustomerModel orderCustomer = (B2BCustomerModel) order.getUser();
        final B2BUnitModel orderCustomerUnit = orderCustomer.getDefaultB2BUnit();
        final String customerSalesOrg = orderCustomerUnit.getSalesOrg().getCode();
        final CMSSiteModel cmsSite = (CMSSiteModel) order.getSite();
        getCmsSiteService().setCurrentSite(cmsSite);

        ReadOrderRequestV2 readOrderRequest = new ReadOrderRequestV2();
        readOrderRequest.setCustomerId(orderCustomerUnit.getErpCustomerID());
        readOrderRequest.setSalesOrganization(customerSalesOrg);
        readOrderRequest.setOrderId(order.getCode());
        readOrderRequest.setWebshopOrderFlag(true);

        ReadOrderResponseV2 readOrderResponse = sapOrderService.readOrder(readOrderRequest);
        if(readOrderResponse != null){
            order.setErpOrderCode(readOrderResponse.getOrderId());
            modelService.save(order);
            LOG.info("Found matching order in ERP for missing order {} with ERP order id {}", order.getCode(), order.getErpOrderCode());
            return true;
        }else {
            return false;
        }
    }

    @Override
    public CreateMissingOrdersResult createSapOrders(List<OrderModel> orders) {
        List<OrderModel> successfullyCreatedOrders = new ArrayList<>();
        List<OrderModel> failedOrders = new ArrayList<>();

        for (OrderModel order : orders) {
            try {
                final CMSSiteModel cmsSite = (CMSSiteModel) order.getSite();
                getCmsSiteService().setCurrentSite(cmsSite);
                getCheckoutService().exportOrder(order);
                if(order.getErpOrderCode() != null) {
                    successfullyCreatedOrders.add(order);
                    LOG.info("Created missing order {} in ERP", order.getCode());
                }else {
                    failedOrders.add(order);
                    LOG.error("Failed to create missing order {} in ERP. Order code is still missing", order.getCode());
                }
            }catch (Exception e){
                failedOrders.add(order);
                LOG.error("Failed to create missing order {} in ERP. Caused by: {}", order.getCode(), e.getMessage());
            }
        }

        return new CreateMissingOrdersResult(successfullyCreatedOrders, failedOrders);
    }

    @Override
    public boolean sendReportEmail(List<OrderModel> matchedOrders, List<OrderModel> createdOrders, List<OrderModel> failedOrders) {
        try {
            DistSapMissingOrdersReportEmailProcessModel businessProcess = businessProcessService.createProcess("missingOrdersReportEmailProcess" + Instant.now().toEpochMilli(),
                    "missingOrdersReportEmailProcess");
            businessProcess.setSite(cmsSiteService.getSites().stream()
                    .filter(site -> site.getUid().equals("distrelec_CH"))
                    .findFirst().get());
            businessProcess.setCustomer(userService.getAnonymousUser());
            businessProcess.setLanguage(commonI18NService.getLanguage(Locale.ENGLISH.getLanguage()));
            businessProcess.setMatchedOrders(new HashSet<>(matchedOrders));
            businessProcess.setCreatedOrders(new HashSet<>(createdOrders));
            businessProcess.setFailedOrders(new HashSet<>(failedOrders));
            modelService.save(businessProcess);
            businessProcessService.startProcess(businessProcess);
            return true;
        }catch (Exception e){
            LOG.error("Failed to send missing orders report email. Cause: {}", e.getMessage());
            return false;
        }
    }

    public OrderService getSapOrderService() {
        return sapOrderService;
    }

    public void setSapOrderService(final OrderService sapOrderService) {
        this.sapOrderService = sapOrderService;
    }


    public CheckoutService getCheckoutService() {
        return checkoutService;
    }

    public void setCheckoutService(final CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
