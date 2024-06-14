/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.backoffice.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.zkoss.zul.Messagebox;

import javax.annotation.Resource;
import java.util.EnumSet;

/**
 * {@code ApproveNewlyRegisteredCustomerAction}
 * 
 *
 * @author lukasz.nowakowski@sap.com
 * @date 2018-06-06
 * @since Distrelec 7.6
 */
public class ApproveNewlyRegisteredCustomerAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<CustomerModel, Object> {
    
    private static final Logger LOG = LogManager.getLogger(ApproveNewlyRegisteredCustomerAction.class);

    @Resource
    private ModelService modelService;
    @Resource
    private DistCustomerAccountService customerAccountService;

    @Override
    public ActionResult<Object> perform(final ActionContext<CustomerModel> ctx) {
        final CustomerModel source = ctx.getData();

        if (source instanceof B2BCustomerModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) source;

            try {
                if (customer.getCustomerType().equals(CustomerType.B2C)) {
                    customerAccountService.raiseRegistrationEvent(customer,
                            checkCustomerHasActiveCart(customer) ? RegistrationType.CHECKOUT_EXISTING : RegistrationType.STANDALONE_EXISTING);

                    customer.setDoubleOptInActivated(true);
                    customer.setToken(null);

                    modelService.save(customer);
                }
                else if (customer.getCustomerType().equals(CustomerType.B2B)) {
                    customerAccountService.raiseRegistrationEvent(customer,
                            checkCustomerHasActiveCart(customer) ? RegistrationType.CHECKOUT_EXISTING : RegistrationType.STANDALONE_EXISTING);

                    modelService.save(customer);
                }
                else {
                    customerAccountService.resendSetInitialPasswordEmail(customer);
                }

                Messagebox.show(ctx.getLabel("customer.approved.message"));

                final ActionResult<Object> result = new ActionResult<>(ActionResult.SUCCESS);

                result.setStatusFlags(EnumSet.of(ActionResult.StatusFlag.OBJECT_MODIFIED));

                return result;
            } catch (Exception e) {
                LOG.error("Cannot approve customer because of " + e.getMessage()/*, e*/);
            }
        }

        return new ActionResult<>(ActionResult.ERROR);
    }

    private boolean checkCustomerHasActiveCart(final B2BCustomerModel customerModel) {
        return customerModel.getCarts().stream()
                .anyMatch(cart -> !cart.isGhostOrder()); // Skip the ghost cart

    }

    @Override
    public boolean canPerform(final ActionContext<CustomerModel> ctx) {
        final CustomerModel source = ctx.getData();

        return source instanceof B2BCustomerModel && !source.isDoubleOptInActivated();
    }

    @Override
    public boolean needsConfirmation(final ActionContext<CustomerModel> ctx) {
        return false;
    }

    @Override
    public String getConfirmationMessage(final ActionContext<CustomerModel> ctx) {
        return null;
    }
}