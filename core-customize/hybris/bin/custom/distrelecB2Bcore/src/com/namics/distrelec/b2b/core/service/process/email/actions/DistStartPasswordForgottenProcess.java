package com.namics.distrelec.b2b.core.service.process.email.actions;

import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import de.hybris.platform.commerceservices.actions.password.ForgottenPasswordAction;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static de.hybris.platform.core.model.security.PrincipalModel.UID;

public class DistStartPasswordForgottenProcess extends ForgottenPasswordAction {

    private static final Logger LOG = LoggerFactory.getLogger(DistStartPasswordForgottenProcess.class);

    @Autowired
    private DistCustomerAccountService distCustomerAccountService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserMatchingService userMatchingService;

    @Autowired
    private ProcessParameterHelper processParameterHelper;

    @Override
    public Transition executeAction(ForgottenPasswordProcessModel forgottenPasswordProcessModel) {
        if (processParameterHelper.containsParameter(forgottenPasswordProcessModel, UID)) {
            final String customerUid = (String) processParameterHelper.getProcessParameterByName(UID, forgottenPasswordProcessModel.getContextParameters()).getValue();
            sessionService.getCurrentSession().setAttribute("currentSite", forgottenPasswordProcessModel.getSite());

            try {
                final CustomerModel customerModel = userMatchingService.getUserByProperty(customerUid, CustomerModel.class);
                if (forgottenPasswordProcessModel.isInCheckout()) {
                    distCustomerAccountService.checkoutForgottenPassword(customerModel, forgottenPasswordProcessModel.getStorefrontRequest());
                } else {
                    distCustomerAccountService.forgottenPassword(customerModel, forgottenPasswordProcessModel.getStorefrontRequest());
                }
                return Transition.OK;
            } catch (final UnknownIdentifierException uie) {
                LOG.warn(String.format("User with unique property: %s does not exist in the database.", YSanitizer.sanitize(customerUid)));
                return Transition.NOK;
            }
        } else {
            LOG.warn("The field [uid] cannot be empty");
            return Transition.NOK;
        }
    }
}
